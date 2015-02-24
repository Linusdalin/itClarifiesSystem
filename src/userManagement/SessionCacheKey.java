package userManagement;

import cache.ServiceCache;
import dataRepresentation.DBTimeStamp;
import log.PukkaLogger;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-16
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class SessionCacheKey {

    private String sessionToken;
    private PortalUser user;
    private String ipAddress;
    private DBTimeStamp ts;
    private boolean exists = false;

    public SessionCacheKey(PortalUser user, String ipAddress, DBTimeStamp ts){

        this.user = user;
        this.ipAddress = ipAddress;
        this.ts = ts;
    }

    public SessionCacheKey(String sessionToken){

        try{

            if(sessionToken == null)
                return;

            this.sessionToken = sessionToken;

            ServiceCache cache = new ServiceCache("Token");
            String usageTimestamp = cache.lookup(sessionToken);

            System.out.println(" *** Cache UsageTimestamp: " + usageTimestamp );

            if(usageTimestamp == null)
                return;

            int timeStart = usageTimestamp.indexOf("@") + 1;
            int ipStart   = usageTimestamp.indexOf("#") + 1;

            if(timeStart <  0 || ipStart < 0)
                throw new BackOfficeException(BackOfficeException.AccessError, " Error parsing usageTimestamp \""+ usageTimestamp+"\" from cache. ");

            this.user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), usageTimestamp.substring(0, timeStart - 1))));
            this.ipAddress = usageTimestamp.substring(ipStart);
            this.ts = new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, usageTimestamp.substring(timeStart, ipStart - 1));

            exists = true;

        }catch(BackOfficeException e){

            PukkaLogger.log( e );

        }


    }


    public PortalUser getUser() {
        return user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public DBTimeStamp getTs() {
        return ts;
    }

    public String getUsageTimestamp(){

        return user.getName() + "@" + ts.getSQLTime().toString() + "#" + ipAddress;
    }

    public void setAccess(DBTimeStamp newAccess) {
        this.ts = newAccess;
    }

    public boolean exists() {
        return exists;
    }

    public void remove() throws BackOfficeException{

        ServiceCache cache = new ServiceCache("Token");
        cache.remove(sessionToken);


    }

    public void store() throws BackOfficeException{

        ServiceCache cache = new ServiceCache("Token");
        cache.store(sessionToken, getUsageTimestamp(), "New Session");
    }

    public SessionCacheKey forToken(String token) {
        this.sessionToken = token;
        return this;
    }
}
