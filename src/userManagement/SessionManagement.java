package userManagement;

import cache.ServiceCache;
import contractManagement.Contract;
import dataRepresentation.DBTimeStamp;
import log.PukkaLogger;
import pukkaBO.acs.IPAccessList;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;

/****************************************************************************
 *
 *
 *          Session handling functionality
 *
 *
 */

public class SessionManagement {

    private static final int SESSION_TIME = 60;    // Default session time
    public static final String AnyIP = "*.*.*.*";

    private PortalUser sessionUser = null;          // The user
    private String sessionToken = null;

    private static PortalUser system = null;
    private static IPAccessList internalIPAccess = null;

    public static final String MagicKey = "d1s7i55tD3bs7NS8fxx";  //TODO: This is just a simple static password. Implement better solution here
    private boolean allowMagicKeyAccess;


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
            internalIPAccess.allow("107.178.200.*");
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


    /********************************************************************************************************'
     *
     *          Creating a session for the user given the reply from the login service
     *
     *
     * @param token             - the generated session token
     * @param userId            - the user logging in (provided by the login service)
     * @param ipAddress         - login ip to be stored in the session
     *
     * @return                  - The user looked up
     * @throws BackOfficeException
     */


    public PortalUser createSessionForUser(String token, int userId, String ipAddress) throws BackOfficeException {

        PortalUser user = new PortalUser(new LookupItem()
                .addFilter(new ColumnFilter(PortalUserTable.Columns.UserId.name(), userId)));

        if(!user.exists()){

            // The user does not exist. It is probably a user from another instance of the application

            PukkaLogger.log(PukkaLogger.Level.INFO, "User with id " + userId + "does not exist in this application instance.");
            return user;


        }

        // Check if there is an active session

        if(!validate(token, ipAddress)){

            //new PortalSessionTable().createNewSession(user, token, ipAddress);

            SessionCacheKey newSessionKey = new SessionCacheKey(user, ipAddress, new DBTimeStamp()).forToken(token);
            newSessionKey.store();


            PukkaLogger.log(PukkaLogger.Level.INFO, "Creating new session for user " + user.getName() + "( id: " + userId + ")");
        }
        else{

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found existing session for user " + user.getName() + "( id: " + userId + ")");
        }


        return user;


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

            //PortalSession session = new PortalSession(new LookupItem()
            //        .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken))
            //        .addSorting(new Sorting(PortalSessionTable.Columns.Latest.name(), Ordering.LAST)));


        SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken);

            if(!sessionCacheKey.exists()){

                PukkaLogger.log(PukkaLogger.Level.WARNING, "Trying to access non existing session with token " + sessionToken );
                return "unknown session";

            }else{

                // Close session

                //session.setStatus(SessionStatus.getclosed());
                //session.update();

                sessionCacheKey.remove();
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

        //PortalSession session = new PortalSession(new LookupItem()
        //            .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken))
        //            .addSorting(new Sorting(PortalSessionTable.Columns.Latest.name(), Ordering.LAST)));

        SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken);

        if(!sessionCacheKey.exists()){

           PukkaLogger.log(PukkaLogger.Level.INFO, "No session exists for token " + sessionToken);
            return false;
        }



        System.out.println(" *** Access details: (" + sessionCacheKey.getUser().getName() + ", " +sessionCacheKey.getTs().getSQLTime().toString() + ", " + sessionCacheKey.getIpAddress() + ")" );



        if(!internal(ipAddress) && !ipAddress.equals(AnyIP) && !sessionCacheKey.getIpAddress().equals(ipAddress)){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Access attempt on "+ sessionCacheKey.getUser().getName()+" account from another IP address. (Login: " + sessionCacheKey.getIpAddress() +
                    " access: " + ipAddress + ")");
            return false;

        }




        // Check if the session is open and not expired

       boolean isActive = !expired(sessionCacheKey);

       if(isActive){

           this.sessionUser = sessionCacheKey.getUser();
           this.sessionToken = sessionToken;
           DBTimeStamp newAccess = new DBTimeStamp();
           sessionCacheKey.setAccess(newAccess);

           PukkaLogger.log(PukkaLogger.Level.INFO, "Validated user " + sessionUser.getName() + "( "+ sessionUser.getKey()+" ) in request");


       }
        else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Session for token " + sessionToken + " has expired");

       }

        // Store the system user

        if(system == null){
            system = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "itClarifies")));
            System.out.println("Validate create system user");

        }
        return isActive;


    }

    public void allowBOAccess(){

        allowMagicKeyAccess = true;
    }



    // Todo: Add IP restriction on magic key access


    public boolean validateMagicKey(String magicKey, String ipAddress) throws BackOfficeException {

        if(!allowMagicKeyAccess){

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Unauthorized magic key access attempt" + magicKey + " from " + ipAddress);
            return false;

        }


        if(!magicKey.equals(MagicKey)){

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Wrong magic key" + magicKey);
            return false;

        }


       this.sessionUser = PortalUser.getSystemUser();
       PukkaLogger.log(PukkaLogger.Level.INFO, "Validated magic key access. user = " + this.sessionUser.getName());

       // Store the system user

        if(system == null){
            system = PortalUser.getSystemUser();

        }

        return true;
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

    private boolean expired(SessionCacheKey session) {

            DBTimeStamp endTime = session.getTs().addMinutes(SESSION_TIME);
            DBTimeStamp now = new DBTimeStamp();

            return endTime.isBefore(now);

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


    /**********************************************************************************
     *
     *              Session keep-alive. Used in the ping service.
     *
     * @param sessionToken
     * @param ipAddress
     * @return
     * @throws BackOfficeException
     */


    public boolean keepAlive(String sessionToken, String ipAddress) throws BackOfficeException{

        SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken);

        if(!sessionCacheKey.exists()){

           PukkaLogger.log(PukkaLogger.Level.INFO, "No session exists for token " + sessionToken);
            return false;
        }

        if(!internal(ipAddress) && !sessionCacheKey.getIpAddress().equals(ipAddress)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Cant keep session alive from another ip. Session ip " + sessionCacheKey.getIpAddress() + "!= " + ipAddress);
            return false;

        }

        if(expired(sessionCacheKey)){


            PukkaLogger.log(PukkaLogger.Level.INFO, "Cant keep expired session alive. Session for user " + sessionCacheKey.getUser() + " expired. (Usage timestamp: " + sessionCacheKey.getUsageTimestamp() + ")");
            return false;

        }

        DBTimeStamp newAccess = new DBTimeStamp();
        sessionCacheKey.setAccess(newAccess);
        sessionCacheKey.store();

        return true;

    }
}
