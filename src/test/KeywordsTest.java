package test;

import analysis.DocumentAnalysisException;
import backend.ItClarifies;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import junit.framework.Assert;
import junit.framework.TestCase;
import language.LanguageCode;
import log.PukkaLogger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import system.Analyser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */


public class KeywordsTest {

    /******************************************************************
     *
     *      //TODO: Use mockito to mock the httpservlet request/response
     *
     */

    private static LocalServiceTestHelper helper;
    private static BackOfficeInterface bo = new ItClarifies();


    @AfterClass
    public static void tearDown() {

        helper.tearDown();
    }


    @BeforeClass
    public static void preAmble(){

        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();
        bo.createDb();
        bo.populateValues(true);

        PukkaLogger.setLogLevel(PukkaLogger.Level.DEBUG);


    }



    @Test
    public void basicTest()throws BackOfficeException{


       try{

           Analyser analyser = new Analyser(new LanguageCode("EN"), "web/models");

           Contract contract = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Google Analytics")));

           int count = new ProjectTable().getCount();

           PukkaLogger.log(PukkaLogger.Level.INFO, "count :" + count);

           ContractVersionInstance lastVersion = contract.getHeadVersion();


           PukkaLogger.log(PukkaLogger.Level.INFO, " -> \"Last\" version: " + lastVersion.getVersion());

               ContractFragmentTable allFragments = new ContractFragmentTable(new LookupItem()
                       .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), lastVersion.getKey()))
                       .addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));

           for(DataObjectInterface object : allFragments.getValues()){

               ContractFragment fragment = (ContractFragment)object;

               PukkaLogger.log(PukkaLogger.Level.INFO, "F: " + fragment.getText());


           }

       }catch(DocumentAnalysisException e){

           e.printStackTrace();
           assertThat(true, is(false));

       }catch(BackOfficeException e){

           e.logError("In keyword gen");
           assertThat(true, is(false));
       }
    }

}

