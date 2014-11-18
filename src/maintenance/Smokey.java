package maintenance;

import java.util.Arrays;

/**
 *
 *          The smokey manager handles smokey mode
 */

public class Smokey {

    //Users that shall have access also in smokey mode

    private static final String[] smokeyUsers = {"admin"};


    private static boolean inSmokey = false;

    public static boolean isInSmokey(){

        return isInSmokey("public user");
    }


    public static boolean isInSmokey(String username){

        if(!inSmokey)
            return false;

        if(Arrays.asList(smokeyUsers).contains(username))
            return false;

        return true;

    }

    public static void enterSmokey(){

        inSmokey = true;
    }

    public static void exitSmokey(){

        inSmokey = false;
    }




}
