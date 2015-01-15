package cache;

import dataRepresentation.DataObjectInterface;
import log.PukkaLogger;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import java.util.Collections;
import java.util.List;

/******************************************************************
 *
 *          basic caching functionality based on the JCache in appengine
 *
 *
 */

public class ServiceCache {

    // The service name is used as part of the key for the cache. (Different services have different results

    private Cache cache;
    private String serviceQualifier;
    private String serviceName;


    public ServiceCache(String serviceName) throws BackOfficeException{

        this(serviceName, null);
    }

    public ServiceCache(String serviceName, PortalUser user) throws BackOfficeException{

        this.serviceName = serviceName;
        this.serviceQualifier = getServiceQualifier(serviceName, user);

        try{
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());

        } catch (CacheException e) {

            throw new BackOfficeException(BackOfficeException.CacheError, "Error creating cache (" + e.getMessage() + ")");
        }
    }

    /********************************************************************
     *
     *          Loookup a key in the cache.
     *
     *
     * @param key - the service specific key (normally the primary key from the request)
     * @return  - the stored (json) string or null if not found
     * @throws BackOfficeException
     */

    public String lookup(String key) throws BackOfficeException{

        String cacheKey = createKey(serviceQualifier, key);

        if(!cache.containsKey(cacheKey)){

            PukkaLogger.log(PukkaLogger.Level.INFO, "No in cache for key (" + cacheKey + " )(keys: " + cache.size() + ")");
            return null;
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Hit in cache for key (" + cacheKey + ") (keys: " + cache.size() + ")");
        return (String)cache.get(cacheKey);

    }


    /**************************************************************'
     *
     *          Store something into the cache
     *
     * @param key - the request key
     * @param value - json value from the database
     * @param comment - string comment for tracing errors
     * @throws BackOfficeException
     *
     *      NOTE: This uses the service qualifier from the constructor
     */

    public void store(String key, String value, String comment) throws BackOfficeException{


        try{

            String cacheKey = createKey(serviceQualifier, key);

            cache.put(cacheKey, value);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Store value for key (" + cacheKey + ") in cache. (keys: " + cache.size() + ")");

        }catch(Exception e){

            // Failed to store in cache. Log this as Fatal error for knowledge, but continue

            PukkaLogger.log(e, " Error in " + comment);

        }


    }

    /***************************************************************''
     *
     *      Remove something from the cache. Should be used whenever data is
     *      updated.
     *
     * @param key - the request key
     * @throws BackOfficeException
     *
     *      NOTE: This uses the service qualifier from the constructor
     */


    public void remove(String key) throws BackOfficeException{

        cache.remove(createKey(serviceQualifier, key));

    }


    public void clearAll(){

        cache.clear();

    }


    /*************************************************
     *
     *      Create a key from the service request and the service qualifier.
     *
     * @param key - the request key
     * @return - the compete cache key
     *
     *
     */


    private String createKey(String qualifier, String key){

        return qualifier +"-"+ key;
    }

    public String getServiceQualifier(String serviceName, PortalUser user ){

        String qualifier = serviceName;

        // Add user to the qualifier

        if(user != null)
            qualifier += "-" + user.getKey().toString();

        return qualifier;
    }


    public void clearKeyForAllQualifiers(String key) {


        List<DataObjectInterface> all = new PortalUserTable(new LookupList()).getValues();

        for(DataObjectInterface object : all){

            PortalUser user = (PortalUser)object;
            String cacheObjectKey = createKey(getServiceQualifier(serviceName, user), key);

            if( cache.remove(cacheObjectKey) != null)
                PukkaLogger.log(PukkaLogger.Level.INFO, "Clear Qualified value for key (" + cacheObjectKey + ") in cache. (keys: " + cache.size() + ")");

        }

        // Also clear the key for non qualified cache key (e.g. fragment
        clearKey(key);

    }

    public void clearKey(String key){

        String cacheObjectKey = createKey(serviceName, key);

        if( cache.remove(cacheObjectKey) != null)
            PukkaLogger.log(PukkaLogger.Level.INFO, "Clear value for key (" + cacheObjectKey + ") in cache. (keys: " + cache.size() + ")");


    }

}
