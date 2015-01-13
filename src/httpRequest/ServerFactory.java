package httpRequest;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;


/*************************************************************************'
 *
 *
 *          Handling of allocating the correct servers
 *
 *
 *          The login server handles the login for all systems
 *
 *
 **/
public class ServerFactory {


    public static String getLoginServer(){

        String applicationID = getApplicationID();


        if(SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
            if(applicationID.startsWith("itclarifiesapi") && !applicationID.contains("stage"))
                return "https://itclarifieslogin.appspot.com";
            else
                return "https://itclarifiesloginstage.appspot.com";
        }
        else
            return "http://localhost:8081";


    }

    /************************************************************************'
     *
     *
     *          The name of the local system
     *
     * @return
     */

    public static String getLocalSystem(){

        String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();
        System.out.println("serviceAccountName = " + serviceAccountName);

        String applicationID = getApplicationID();

        if(SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
            return "https://"+ applicationID+".appspot.com";
        else
            return "http://localhost:8080";


    }



    public static String getApplicationID(){

        String ID = SystemProperty.applicationId.get();
        System.out.println("ID = " + ID);

        return ID;
    }


}
