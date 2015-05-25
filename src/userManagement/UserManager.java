package userManagement;

import dataRepresentation.DBTimeStamp;
import httpRequest.RequestHandler;
import httpRequest.ServerFactory;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;

/*******************************************************************************************''
 *
 *
 *              Class to handle creation and update of users and organizations
 *
 *              As the login is in a separate service, we need to send updates to there too.
 *              This class is intended to abstract this separation  with all operations on users
 *              to allow for future changes
 *
 */


public class UserManager {

    String loginServer = ServerFactory.getLoginServer();

    private String activationCode = null;

    public UserManager(){

    }


    /**********************************************************************************
     *
     *          Note: This does not check for duplicates
     *          Note: It will create the user as active. Implementing email validation probably should make the user INACTIVE at first
     *
     *
     * @param name                - name of the user
     * @param password            - password selected by user
     * @param email               - email selected by user
     * @param organization        - The organization (either from a parent user or just created by user)
     * @param isAdmin             - Shall the user have admin rights to the
     *
     * @return                    - the user
     *
     * @throws BackOfficeException
     */

    public PortalUser createUser(String name, String password, String email, Organization organization, boolean isAdmin, boolean isActive) throws BackOfficeException{

        DBTimeStamp registrationDate = new DBTimeStamp();


        RequestHandler requestHandler = new RequestHandler(loginServer + "/User");
        String output = requestHandler.excutePost(
                "user=" + name +
                "&password=" + password +
                "&organization="+ organization.getName() +
                "&active="+ isActive +
                "&session="+ organization.getToken());

        PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from login server" + output);

        JSONObject response = new JSONObject(output);

        if(response.has("error")){

            String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
            throw new BackOfficeException(BackOfficeException.AccessError, "Error from login: " + errorMessage );
        }

        int userId = response.getInt("user");
        this.activationCode = response.getString("activation");

        PortalUser newUser = new PortalUser(name, userId, PortalUser.Type.REGISTERED.name(), email, registrationDate.getISODate(), organization.getKey(), isActive, isAdmin);
        newUser.store();


        return newUser;

    }

    /**************************************************************************************'
     *
     *
     *
     * @param name
     * @return
     */


    /******************************************************************************************
     *
     *
     *          Create a new organization both locally and remotely in the login server
     *
     * @param name                      - name of the organization
     * @param description               - description text
     * @return                          - the organization object
     * @throws BackOfficeException      - upon failing. Handle this and pass message back to user
     */

    public Organization createOrganization(String name, String description) throws BackOfficeException{

        String loginServer = ServerFactory.getLoginServer();
        String thisServer = ServerFactory.getLocalSystem();
        DBTimeStamp registrationDate = new DBTimeStamp();

        // Organization names have to be unique

        Organization existingOrganization = new Organization(new LookupItem().addFilter(new ColumnFilter(OrganizationTable.Columns.Name.name(), name)));
        if(existingOrganization.exists()){

            throw new BackOfficeException(BackOfficeException.Usage, "Error: Organization with name " + name + " already exists");

        }

        RequestHandler requestHandler = new RequestHandler(loginServer + "/Organization");
        String output = requestHandler.excutePost("name=" + name + "&description=" + description + "&link="+ thisServer);

        PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from login server" + output);

        JSONObject response = new JSONObject(output);

        if(response.has("error")){

            String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
            throw new BackOfficeException(BackOfficeException.Usage, "Error: Got error from login service " + errorMessage);
        }

        String token = response.getString("token");


        // Create a new Organization config

        OrganizationConf newConfig = new OrganizationConf(name);
        newConfig.store();

        Organization newOrganization = new Organization(name, registrationDate.getISODate(), description, token, newConfig.getKey());
        newOrganization.store();

        // Create default users for the organization

        PortalUser system = new PortalUser("itClarifies",   0, PortalUser.Type.SYSTEM.name(),   "no email", registrationDate.getISODate(), newOrganization.getKey(), true, false);
        system.store();
        PortalUser empty = new PortalUser("<< not set >>",  0, PortalUser.Type.EMPTY.name(),    "no email", registrationDate.getISODate(), newOrganization.getKey(), true, false);
        empty.store();
        PortalUser external = new PortalUser("External",    0, PortalUser.Type.EXTERNAL.name(), "no email", registrationDate.getISODate(), newOrganization.getKey(), true, false);
        external.store();



        return newOrganization;
    }

    /***************************************************************************
     *
     *              Update a user both locally and potentially in teh login server
     *
     *
     * @param user              - existing user object
     * @param name              - new or same
     * @param email             - new or same
     * @param password          - password
     * @param wsAdmin           - set the user as admin
     * @param isActive          - set the user as active
     * @return                  - the same user object, now updated
     * @throws BackOfficeException
     */

    public PortalUser updateUser(PortalUser user, String name, String email, String password, boolean wsAdmin, boolean isActive) throws BackOfficeException{

        RequestHandler requestHandler = new RequestHandler(loginServer + "/User");
        String output = requestHandler.excutePost(
                "id=" + user.getUserId() +
                "&user=" + name +
                "&password=" + password +
                "&session=SystemSessionToken");

        PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from login server" + output);

        JSONObject response = new JSONObject(output);

        if(response.has("error")){

            String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
            throw new BackOfficeException(BackOfficeException.AccessError, "Error from login: " + errorMessage );
        }

        // Also update the local object

        user.setName(name);
        user.setEmail(email);
        user.setWSAdmin(wsAdmin);
        user.setActive(isActive);

        user.update();

        return user;


    }

    /******************************************************************************************
     *
     *          Check the activation code and activate the user
     *
     * @param user                   - the user to activation
     * @param activationCode         - code as received in the request
     * @return                       - did the activation succeed
     *
     */


    public boolean activate(PortalUser user, String activationCode) {


        try{

            RequestHandler requestHandler = new RequestHandler(loginServer + "/Activate");
            String output = requestHandler.excutePost("activation=" + activationCode);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from login server" + output);

            JSONObject response = new JSONObject(output);

            if(response.has("error")){

                String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
                throw new BackOfficeException(BackOfficeException.Usage, "Error: Got error from login service " + errorMessage);
            }

            return true;

        }catch(BackOfficeException e){

            PukkaLogger.log( e );
            return false;

        }


    }

    public String getActivationCode(){

        return activationCode;
    }

}
