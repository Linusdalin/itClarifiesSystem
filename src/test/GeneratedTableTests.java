package test;

import dataRepresentation.DBTimeStamp;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import pukkaBO.condition.LookupByKey;
import databaseLayer.DBKeyInterface;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.backOffice.BackOfficeInterface;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
/* Unit tests... */

public class GeneratedTableTests{

    private static LocalServiceTestHelper helper;
    private static DBKeyInterface dummyKey;


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();
        try {

            BackOfficeInterface bo = null;
            bo.init();
            dummyKey = null;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


}
