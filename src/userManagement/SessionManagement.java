package userManagement;

import contractManagement.Contract;
import contractManagement.ContractTable;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.acs.IPAccessList;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.password.PasswordManager;

import java.util.HashMap;
import java.util.Map;

/****************************************************************************
 *
 *
 *          Session handling functionality
 *
 *
 */

public class SessionManagement {

    private static final int SESSION_TIME = 180;    // Default session time

    private PortalUser sessionUser = null;          // The user
    private String sessionToken = null;

    private static PortalUser system = null;
    private static IPAccessList internalIPAccess = null;

    public SessionManagement(){

        if(internalIPAccess == null){
            internalIPAccess = new IPAccessList();
            internalIPAccess.allow("0.*.*.*");

            internalIPAccess.allow("8.34.208.*");
            internalIPAccess.allow("8.35.192.*");
            internalIPAccess.allow("8.35.200.*");
            internalIPAccess.allow("23.236.48.*");
            internalIPAccess.allow("23.251.128.*");
            internalIPAccess.allow("107.167.160.*");
            internalIPAccess.allow("107.178.192.*");
            internalIPAccess.allow("108.170.192.*");
            internalIPAccess.allow("108.170.208.*");
            internalIPAccess.allow("108.170.216.*");
            internalIPAccess.allow("108.170.220.*");
            internalIPAccess.allow("108.170.222.*");
            internalIPAccess.allow("108.59.80.*");
            internalIPAccess.allow("130.211.4*");
            internalIPAccess.allow("146.148.16.*");
            internalIPAccess.allow("146.148.2.*");
            internalIPAccess.allow("146.148.32.*");
            internalIPAccess.allow("146.148.4.*");
            internalIPAccess.allow("146.148.64.*");
            internalIPAccess.allow("146.148.8.*");
            internalIPAccess.allow("162.216.148.*");
            internalIPAccess.allow("162.222.176.*");
            internalIPAccess.allow("173.255.112.*");
            internalIPAccess.allow("192.158.28.*");
            internalIPAccess.allow("199.192.112.*");
            internalIPAccess.allow("199.223.232.*");
            internalIPAccess.allow("199.223.236.*");
        }
    }


    /*******************************************************************************
     *
     *          Close the session
     *          (used for logout)
     *
     *
     * @param sessionToken - the token
     * @return             - status message
     * @throws BackOfficeException
     *
     *
     *
     */

    public String close(String sessionToken) throws BackOfficeException {

            // Lookup the last session for the user

            PortalSession session = new PortalSession(new LookupItem()
                    .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken))
                    .addSorting(new Sorting(PortalSessionTable.Columns.Latest.name(), Ordering.LAST)));


            if(!session.exists()){

                PukkaLogger.log(PukkaLogger.Level.WARNING, "Trying to access non existing session with token " + sessionToken );
                return "unknown session";

            }else if(session.getStatus().equals(SessionStatus.gettimeout())){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Session implicitly closed through timeout" + sessionToken );
                return "implicit";
            }else{

                // Close session

                session.setStatus(SessionStatus.getclosed());
                session.update();
                PukkaLogger.log(PukkaLogger.Level.INFO, "Session closed" + sessionToken );

            }

            return "closed";
    }


    /*******************************************************************
     *
     *      Validate the session token that is received in all service requests
     *      The token should uniquely identify the user
     *
     *
     *
     * @param sessionToken - token from the web service call
     * @param ipAddress    - the ip address from the request
     * @return - true if the session is active
     *
     *     <datastore-index kind="PortalSession" ancestor="false" source="manual">
                 <property name="Token" direction="asc"/>
                 <property name="Latest" direction="desc"/>
             </datastore-index>

     *
     */


    public boolean validate(String sessionToken, String ipAddress) throws BackOfficeException {

        // Lookup the session

        PortalSession session = new PortalSession(new LookupItem()
                    .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken))
                    .addSorting(new Sorting(PortalSessionTable.Columns.Latest.name(), Ordering.LAST)));


        if(!session.exists()){

           PukkaLogger.log(PukkaLogger.Level.INFO, "No session exists for token " + sessionToken);
            return false;
        }


        if(!internal(ipAddress) && !session.getIP().equals(ipAddress)){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Access attempt on "+ session.getUser().getName()+" account from another IP address. (Login: " + session.getIP() + " access: " + ipAddress + ")");
            return false;

        }


        // Check if the session is open and not expired

       boolean isActive =(session.getStatus().equals(SessionStatus.getopen()) && !expired(session));

       if(isActive){

           this.sessionUser = session.getUser();
           this.sessionToken = sessionToken;
           session.setLatest(new DBTimeStamp());
           session.update();
           PukkaLogger.log(PukkaLogger.Level.INFO, "Validated user " + sessionUser.getName() + "( "+ sessionUser.getKey()+" ) in request");


       }
        else{

           if(expired(session))
                PukkaLogger.log(PukkaLogger.Level.INFO, "Session for token " + sessionToken + " has expired");
           else
               PukkaLogger.log(PukkaLogger.Level.INFO, "Session for token " + sessionToken + " has status " + session.getStatus().getName());


       }

        // Store the system user

        if(system == null){
            system = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "itClarifies")));
            System.out.println("Validate create system user");

        }
        return isActive;


    }

    private boolean internal(String ip) {

        return internalIPAccess.check(ip);
    }


    /*******************************************************************
     *
     *      An expired session is when latest + sessionTime min is before now.
     *
     * @param session - the session
     * @return - true if the session is expired
     *
     *      NOTE: If we fail to lookup a session, we close it.
     */

    private boolean expired(PortalSession session) {

        try {
            DBTimeStamp latestUpdate = session.getLatest();
            DBTimeStamp startTime = session.getStart();
            DBTimeStamp endTime = latestUpdate.addMinutes(SESSION_TIME);
            DBTimeStamp now = new DBTimeStamp();

            return endTime.isBefore(now) && startTime.isBefore(now);

        } catch (BackOfficeException e) {
            e.logError("Error looking for time for session. Fail = expire");
            return true;
        }
    }



    public PortalUser getUser() throws BackOfficeException{

        if(sessionUser == null)
            throw new BackOfficeException(BackOfficeException.AccessError, "Session not validated before accessed");

        return sessionUser;
    }



    public boolean getReadAccess(Contract document) throws BackOfficeException{

        AccessRight grantedAccess = getAccess(document);

        return(grantedAccess != null && !grantedAccess.equals(AccessRight.getno()));

    }


    public boolean getCommentAccess(Contract document) throws BackOfficeException{

        AccessRight grantedAccess = getAccess(document);

        return(grantedAccess != null && (grantedAccess.equals(AccessRight.getrc()) || grantedAccess.equals(AccessRight.getrwd())));

    }

    public boolean getRenameDeleteAccess(Contract document) throws BackOfficeException{

        AccessRight grantedAccess = getAccess(document);

        return(grantedAccess != null && grantedAccess.equals(AccessRight.getrwd()));

    }



    /*****************************************************************************************
     *
     * @param document
     * @return
     * @throws BackOfficeException
     */


    public AccessRight getAccess(Contract document) throws BackOfficeException{

        PortalUser owner = document.getOwner();
        PortalUser user = getUser();

        if(sessionUser.equals(system)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "System User getting access to document "+ document.getName() + "...");
            return AccessRight.getrwd();
        }

        if(owner.equals(getUser())){

            PukkaLogger.log(PukkaLogger.Level.INFO, "owner - rwd access");
            return AccessRight.getrwd();

        }
        if(!user.getOrganizationId().equals(owner.getOrganizationId())){

            PukkaLogger.log(PukkaLogger.Level.INFO, "not the same org as the document owner. No access");
            return AccessRight.getno();
        }

        return document.getAccess();


    }

    public String getToken() {
        return sessionToken;
    }
}
