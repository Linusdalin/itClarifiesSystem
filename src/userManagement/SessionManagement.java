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

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-05-26
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 *
 *
 *      //TODO: findExisting and create new uses similar validation of user/pwd. Refactor this into one
 *
 */

public class SessionManagement {

    private static final int SESSION_TIME = 180;
    private PortalUser sessionUser = null;
    private static PortalUser system = null;
    private String sessionToken = null;

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


    //private Map<String, String > orgAccess = new HashMap<String, String>();

    public String close(String sessionToken) throws BackOfficeException {

            // Lookup the session

            PortalSession session = new PortalSession(new LookupItem()
                    .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken)));

            String status = "closed";

            if(!session.exists()){

                status = "unknown session"; //TODO: Log this.

            }else if(session.getStatus().equals(SessionStatus.gettimeout())){

                status = "implicit";
            }else{

                // Close session

                session.setStatus(SessionStatus.getclosed());
                session.update();

            }

            return status;
    }


    /*******************************************************************
     *
     *      Validate the session token that is received in all service requests
     *      The token should uniquely identify the user
     *
     *
     *
     * @param sessionToken - token from the web service call
     * @param ipAddress
     * @return - true if the session is active
     *
     *
     *      // TODO: Add more error codes
     */


    public boolean validate(String sessionToken, String ipAddress) throws BackOfficeException {

        // Lookup the session

       //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 1");

       PortalSession session = new PortalSession(new LookupItem()
                    .addFilter(new ColumnFilter(PortalSessionTable.Columns.Token.name(), sessionToken))
                    .addSorting(new Sorting(PortalSessionTable.Columns.Start.name(), Ordering.LAST)));

       //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 2");

       if(!session.exists()){

           PukkaLogger.log(PukkaLogger.Level.INFO, "No session exists for token " + sessionToken);
            return false;
       }

        //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 3");

        if(!internal(ipAddress) && !session.getIP().equals(ipAddress)){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Access attempt on "+ session.getUser().getName()+" account from another IP address. (Login: " + session.getIP() + " access: " + ipAddress + ")");
            return false;

        }



        // Check if the session is open and not expired

       boolean isActive =(session.getStatus().equals(SessionStatus.getopen()) && !expired(session));

        //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 4");

       if(isActive){

           sessionUser = session.getUser();
           session.setLatest(new DBTimeStamp());
           session.update();
           PukkaLogger.log(PukkaLogger.Level.INFO, "Request user is " + sessionUser.getName() + "( "+ sessionUser.getKey()+" )");

       }
        else{

           if(expired(session))
                PukkaLogger.log(PukkaLogger.Level.INFO, "Session for token " + sessionToken + " has expired");
           else
               PukkaLogger.log(PukkaLogger.Level.INFO, "Session for token " + sessionToken + " has status " + session.getStatus().getName());


       }

        //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 5");

        // Store the system user

        if(system == null)
            system = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), "itClarifies")));

        this.sessionToken = sessionToken;

        //PukkaLogger.log(PukkaLogger.Level.INFO, "Validate 6");

        return isActive;


    }

    private boolean internal(String ip) {



        boolean isInternal = internalIPAccess.check(ip);
        //System.out.println("Check internal: " + isInternal + " for ip: " + ip);
        //System.out.println("List of internal: " + internalIPAccess.toString());
        return isInternal;

    }

    /*

    private String lookupGrant(Contract document) throws BackOfficeException {


        try{

            AccessGrant grant = new AccessGrant(new LookupItem()
                .addFilter(new ReferenceFilter(AccessGrantTable.Columns.Document.name(), document.getKey()))
                .addFilter(new ReferenceFilter(AccessGrantTable.Columns.Visibility.name(), Visibility.getOrg().getKey())));



            if(grant.exists()){

                String access = grant.getAccessRight().getName();
                PukkaLogger.log(PukkaLogger.Level.INFO, "Got grant " + access + " for document " + document.getKey());

                orgAccess.put(document.getKey().toString(), access);

                return access;
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found no access for document " + document.getName());
            }

      }catch(Exception e){

            PukkaLogger.log( e );
      }

        return "no";

    }

    */

    /*******************************************************************
     *
     *      An expired session is when latest + 60 min is before now.
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


    /********************************************************************************************************
     *
     *          Lookup the granted access to a document
     *
     *
     * @param document - the document access is put upon
     * @return - AccessRight to the document for the session user
     *
     *
     *          Special cases:  If I am the owner I have rwd access
     *                          If I am not in the same organization as the owner, I will have no access
     *
     *      TODO: The system user access is too LAX here. Should be generated by the backoffice
     *


    public AccessRight getGrantedAccess(Contract document) throws BackOfficeException{

        PortalUser owner = document.getOwner();
        PortalUser user = getUser();

        if(sessionUser.equals(system)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "System User getting access to document "+ document.getName() + "...");
            return AccessRight.getrwd();
        }


        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Getting access to document "+ document.getName()+" for user " + user.getName() + "...");

        if(owner.equals(user)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "owner - rwd access");
            return AccessRight.getrwd();

        }
        if(!user.getOrganizationId().equals(owner.getOrganizationId())){

            PukkaLogger.log(PukkaLogger.Level.INFO, "not the same org. No access");
            return AccessRight.getno();
        }

        String grant = orgAccess.get(document.getKey().toString());

        if(grant == null){

            grant = lookupGrant(document);
        }

        if(grant.equals("no"))
            return AccessRight.getno();
        if(grant.equals("ro"))
            return AccessRight.getro();
        if(grant.equals("rwd"))
            return AccessRight.getrwd();
        if(grant.equals("rc"))
            return AccessRight.getrc();

        PukkaLogger.log(PukkaLogger.Level.ERROR, "Could not get access right to document " + document.getName());
        return AccessRight.getno();

    }


    public AccessGrant getGrantForDocument(Contract document) throws BackOfficeException{

        AccessGrantTable allGrants = new AccessGrantTable(new LookupList(new Sorting(AccessGrantTable.Columns.Time.name(), Ordering.LAST))
                .addFilter(new ReferenceFilter(AccessGrantTable.Columns.Document.name(), document.getKey())));

        if(allGrants.getValues().size() == 0)
            return new AccessGrant();

        return (AccessGrant)allGrants.getValues().get(0);
    }


    public String getToken() {

        return sessionToken;
    }

     */

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
}
