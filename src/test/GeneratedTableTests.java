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

            BackOfficeInterface bo = new backend.ItClarifies()
;            bo.init();
            dummyKey = null;

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

         @Test
         public void testFragmentClass(){
     
             try{
     
                 classification.FragmentClass table1 = new classification.FragmentClass("text 1", "text 2", "text 3", "text 4", dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getType(), is("text 2"));
                 assertThat(table1.getKeywords(), is("text 3"));
                 assertThat(table1.getDescription(), is("text 4"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));

                 table1.store();
                 classification.FragmentClass table2 = new classification.FragmentClass();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getType(), is("text 2"));
                 assertThat(table2.getKeywords(), is("text 3"));
                 assertThat(table2.getDescription(), is("text 4"));
                 assertThat(table2.getOrganizationId(), is(dummyKey));
                 assertThat(table2.getOrganization().exists(), is(false));

                 table1.setName("text 11");
                 table1.setType("text 12");
                 table1.setKeywords("text 13");
                 table1.setDescription("text 14");
                 table1.setOrganization( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getType(), is("text 12"));
                 assertThat(table1.getKeywords(), is("text 13"));
                 assertThat(table1.getDescription(), is("text 14"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table FragmentClass");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(classification.FragmentClass.getReq1(), is(classification.FragmentClass.getReq1()));
                assertThat(classification.FragmentClass.getReq2(), is(classification.FragmentClass.getReq2()));
                assertThat(classification.FragmentClass.getReq3(), is(classification.FragmentClass.getReq3()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table FragmentClass");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testGroup(){
     
             try{
     
                 userManagement.Group table1 = new userManagement.Group("text 1", "text 2", dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDescription(), is("text 2"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));

                 table1.store();
                 userManagement.Group table2 = new userManagement.Group();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDescription(), is("text 2"));
                 assertThat(table2.getOrganizationId(), is(dummyKey));
                 assertThat(table2.getOrganization().exists(), is(false));

                 table1.setName("text 11");
                 table1.setDescription("text 12");
                 table1.setOrganization( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDescription(), is("text 12"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Group");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.Group.getUser(), is(userManagement.Group.getUser()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Group");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testOrganization(){
     
             try{
     
                 userManagement.Organization table1 = new userManagement.Organization("text 1", "2012-01-02", "text 3", "text 4", dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDate().getISODate(), is("2012-01-02"));
                 assertThat(table1.getDescription(), is("text 3"));
                 assertThat(table1.getToken(), is("text 4"));
                 assertThat(table1.getConfigId(), is(dummyKey));
                 assertThat(table1.getConfig().exists(), is(false));

                 table1.store();
                 userManagement.Organization table2 = new userManagement.Organization();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDate().getISODate(), is("2012-01-02"));
                 assertThat(table2.getDescription(), is("text 3"));
                 assertThat(table2.getToken(), is("text 4"));
                 assertThat(table2.getConfigId(), is(dummyKey));
                 assertThat(table2.getConfig().exists(), is(false));

                 table1.setName("text 11");
                 table1.setDate(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-12"));
                 table1.setDescription("text 13");
                 table1.setToken("text 14");
                 table1.setConfig( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDate().getISODate(), is("2012-01-12"));
                 assertThat(table1.getDescription(), is("text 13"));
                 assertThat(table1.getToken(), is("text 14"));
                 assertThat(table1.getConfigId(), is(dummyKey));
                 assertThat(table1.getConfig().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Organization");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.Organization.getnone(), is(userManagement.Organization.getnone()));
                assertThat(userManagement.Organization.getitClarifies(), is(userManagement.Organization.getitClarifies()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Organization");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testOrganizationConf(){
     
             try{
     
                 userManagement.OrganizationConf table1 = new userManagement.OrganizationConf("text 1");

                 assertThat(table1.getName(), is("text 1"));

                 table1.store();
                 userManagement.OrganizationConf table2 = new userManagement.OrganizationConf();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));

                 table1.setName("text 11");
                 assertThat(table1.getName(), is("text 11"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table OrganizationConf");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.OrganizationConf.getnoOrg(), is(userManagement.OrganizationConf.getnoOrg()));
                assertThat(userManagement.OrganizationConf.getitClarifies(), is(userManagement.OrganizationConf.getitClarifies()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table OrganizationConf");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testPortalUser(){
     
             try{
     
                 userManagement.PortalUser table1 = new userManagement.PortalUser("text 1", 2, "text 3", "text 4", "2012-01-05", dummyKey, true, false);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getUserId(), is((long)2));
                 assertThat(table1.getType(), is("text 3"));
                 assertThat(table1.getEmail(), is("text 4"));
                 assertThat(table1.getRegistration().getISODate(), is("2012-01-05"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));
                 assertThat(table1.getActive(), is(true));
                 assertThat(table1.getWSAdmin(), is(false));

                 table1.store();
                 userManagement.PortalUser table2 = new userManagement.PortalUser();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getUserId(), is((long)2));
                 assertThat(table2.getType(), is("text 3"));
                 assertThat(table2.getEmail(), is("text 4"));
                 assertThat(table2.getRegistration().getISODate(), is("2012-01-05"));
                 assertThat(table2.getOrganizationId(), is(dummyKey));
                 assertThat(table2.getOrganization().exists(), is(false));
                 assertThat(table2.getActive(), is(true));
                 assertThat(table2.getWSAdmin(), is(false));

                 table1.setName("text 11");
                 table1.setUserId(12);
                 table1.setType("text 13");
                 table1.setEmail("text 14");
                 table1.setRegistration(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-15"));
                 table1.setOrganization( dummyKey);
                 table1.setActive(true);
                 table1.setWSAdmin(false);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getUserId(), is((long)12));
                 assertThat(table1.getType(), is("text 13"));
                 assertThat(table1.getEmail(), is("text 14"));
                 assertThat(table1.getRegistration().getISODate(), is("2012-01-15"));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));
                 assertThat(table1.getActive(), is(true));
                 assertThat(table1.getWSAdmin(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table PortalUser");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.PortalUser.getSystemUser(), is(userManagement.PortalUser.getSystemUser()));
                assertThat(userManagement.PortalUser.getExternalUser(), is(userManagement.PortalUser.getExternalUser()));
                assertThat(userManagement.PortalUser.getNoUser(), is(userManagement.PortalUser.getNoUser()));
                assertThat(userManagement.PortalUser.getSuper(), is(userManagement.PortalUser.getSuper()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table PortalUser");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testPortalSession(){
     
             try{
     
                 userManagement.PortalSession table1 = new userManagement.PortalSession(dummyKey, "text 2", "text 3", "2012-01-04 00:00:00.0", "2012-01-05 00:00:00.0", new userManagement.SessionStatusTable().getDummyConstantValue( ));

                 assertThat(table1.getUserId(), is(dummyKey));
                 assertThat(table1.getUser().exists(), is(false));
                 assertThat(table1.getToken(), is("text 2"));
                 assertThat(table1.getIP(), is("text 3"));
                 assertThat(table1.getStart().getSQLTime().toString(), is("2012-01-04 00:00:00.0"));
                 assertThat(table1.getLatest().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));
                 assertThat(table1.getStatus(), is(new userManagement.SessionStatusTable().getDummyConstantValue( )));

                 table1.store();
                 userManagement.PortalSession table2 = new userManagement.PortalSession();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getUserId(), is(dummyKey));
                 assertThat(table2.getUser().exists(), is(false));
                 assertThat(table2.getToken(), is("text 2"));
                 assertThat(table2.getIP(), is("text 3"));
                 assertThat(table2.getStart().getSQLTime().toString(), is("2012-01-04 00:00:00.0"));
                 assertThat(table2.getLatest().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));
                 assertThat(table2.getStatus(), is(new userManagement.SessionStatusTable().getDummyConstantValue( )));

                 table1.setUser( dummyKey);
                 table1.setToken("text 12");
                 table1.setIP("text 13");
                 table1.setStart(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-14 00:00:00.0"));
                 table1.setLatest(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-15 00:00:00.0"));
                 table1.setStatus(new userManagement.SessionStatusTable().getDummyConstantValue( ));
                 assertThat(table1.getUserId(), is(dummyKey));
                 assertThat(table1.getUser().exists(), is(false));
                 assertThat(table1.getToken(), is("text 12"));
                 assertThat(table1.getIP(), is("text 13"));
                 assertThat(table1.getStart().getSQLTime().toString(), is("2012-01-14 00:00:00.0"));
                 assertThat(table1.getLatest().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));
                 assertThat(table1.getStatus(), is(new userManagement.SessionStatusTable().getDummyConstantValue( )));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table PortalSession");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.PortalSession.getlongLifeSystem(), is(userManagement.PortalSession.getlongLifeSystem()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table PortalSession");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContract(){
     
             try{
     
                 contractManagement.Contract table1 = new contractManagement.Contract("text 1", "text 2", 3, new contractManagement.ContractTypeTable().getDummyConstantValue( ), new contractManagement.ContractStatusTable().getDummyConstantValue( ), "text 6", "text 7", dummyKey, dummyKey, "2012-01-10", "text 11", new userManagement.AccessRightTable().getDummyConstantValue( ));

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getFile(), is("text 2"));
                 assertThat(table1.getOrdinal(), is((long)3));
                 assertThat(table1.getType(), is(new contractManagement.ContractTypeTable().getDummyConstantValue( )));
                 assertThat(table1.getStatus(), is(new contractManagement.ContractStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getMessage(), is("text 6"));
                 assertThat(table1.getDescription(), is("text 7"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOwnerId(), is(dummyKey));
                 assertThat(table1.getOwner().exists(), is(false));
                 assertThat(table1.getCreation().getISODate(), is("2012-01-10"));
                 assertThat(table1.getLanguage(), is("text 11"));
                 assertThat(table1.getAccess(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));

                 table1.store();
                 contractManagement.Contract table2 = new contractManagement.Contract();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getFile(), is("text 2"));
                 assertThat(table2.getOrdinal(), is((long)3));
                 assertThat(table2.getType(), is(new contractManagement.ContractTypeTable().getDummyConstantValue( )));
                 assertThat(table2.getStatus(), is(new contractManagement.ContractStatusTable().getDummyConstantValue( )));
                 assertThat(table2.getMessage(), is("text 6"));
                 assertThat(table2.getDescription(), is("text 7"));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getOwnerId(), is(dummyKey));
                 assertThat(table2.getOwner().exists(), is(false));
                 assertThat(table2.getCreation().getISODate(), is("2012-01-10"));
                 assertThat(table2.getLanguage(), is("text 11"));
                 assertThat(table2.getAccess(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));

                 table1.setName("text 11");
                 table1.setFile("text 12");
                 table1.setOrdinal(13);
                 table1.setType(new contractManagement.ContractTypeTable().getDummyConstantValue( ));
                 table1.setStatus(new contractManagement.ContractStatusTable().getDummyConstantValue( ));
                 table1.setMessage("text 16");
                 table1.setDescription("text 17");
                 table1.setProject( dummyKey);
                 table1.setOwner( dummyKey);
                 table1.setCreation(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-20"));
                 table1.setLanguage("text 21");
                 table1.setAccess(new userManagement.AccessRightTable().getDummyConstantValue( ));
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getFile(), is("text 12"));
                 assertThat(table1.getOrdinal(), is((long)13));
                 assertThat(table1.getType(), is(new contractManagement.ContractTypeTable().getDummyConstantValue( )));
                 assertThat(table1.getStatus(), is(new contractManagement.ContractStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getMessage(), is("text 16"));
                 assertThat(table1.getDescription(), is("text 17"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOwnerId(), is(dummyKey));
                 assertThat(table1.getOwner().exists(), is(false));
                 assertThat(table1.getCreation().getISODate(), is("2012-01-20"));
                 assertThat(table1.getLanguage(), is("text 21"));
                 assertThat(table1.getAccess(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Contract");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testProject(){
     
             try{
     
                 contractManagement.Project table1 = new contractManagement.Project("text 1", "text 2", dummyKey, dummyKey, "2012-01-05 00:00:00.0", "text 6");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDescription(), is("text 2"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));
                 assertThat(table1.getCreationTime().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));
                 assertThat(table1.getAccess(), is("text 6"));

                 table1.store();
                 contractManagement.Project table2 = new contractManagement.Project();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDescription(), is("text 2"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getOrganizationId(), is(dummyKey));
                 assertThat(table2.getOrganization().exists(), is(false));
                 assertThat(table2.getCreationTime().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));
                 assertThat(table2.getAccess(), is("text 6"));

                 table1.setName("text 11");
                 table1.setDescription("text 12");
                 table1.setCreator( dummyKey);
                 table1.setOrganization( dummyKey);
                 table1.setCreationTime(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-15 00:00:00.0"));
                 table1.setAccess("text 16");
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDescription(), is("text 12"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getOrganizationId(), is(dummyKey));
                 assertThat(table1.getOrganization().exists(), is(false));
                 assertThat(table1.getCreationTime().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));
                 assertThat(table1.getAccess(), is("text 16"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Project");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractVersionInstance(){
     
             try{
     
                 contractManagement.ContractVersionInstance table1 = new contractManagement.ContractVersionInstance("text 1", dummyKey, "text 3", dummyKey, "2012-01-05 00:00:00.0");

                 assertThat(table1.getVersion(), is("text 1"));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getFileHandler(), is("text 3"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getCreation().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));

                 table1.store();
                 contractManagement.ContractVersionInstance table2 = new contractManagement.ContractVersionInstance();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getVersion(), is("text 1"));
                 assertThat(table2.getDocumentId(), is(dummyKey));
                 assertThat(table2.getDocument().exists(), is(false));
                 assertThat(table2.getFileHandler(), is("text 3"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getCreation().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));

                 table1.setVersion("text 11");
                 table1.setDocument( dummyKey);
                 table1.setFileHandler("text 13");
                 table1.setCreator( dummyKey);
                 table1.setCreation(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-15 00:00:00.0"));
                 assertThat(table1.getVersion(), is("text 11"));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getFileHandler(), is("text 13"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getCreation().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractVersionInstance");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testSnapshot(){
     
             try{
     
                 versioning.Snapshot table1 = new versioning.Snapshot("text 1", dummyKey, "text 3", dummyKey, "2012-01-05 00:00:00.0");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getDescription(), is("text 3"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getTimestamp().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));

                 table1.store();
                 versioning.Snapshot table2 = new versioning.Snapshot();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getDescription(), is("text 3"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getTimestamp().getSQLTime().toString(), is("2012-01-05 00:00:00.0"));

                 table1.setName("text 11");
                 table1.setProject( dummyKey);
                 table1.setDescription("text 13");
                 table1.setCreator( dummyKey);
                 table1.setTimestamp(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-15 00:00:00.0"));
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getDescription(), is("text 13"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getTimestamp().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Snapshot");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testChecklist(){
     
             try{
     
                 actions.Checklist table1 = new actions.Checklist("text 1", "text 2", "text 3", dummyKey, dummyKey, "2012-01-06");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDescription(), is("text 2"));
                 assertThat(table1.getId(), is("text 3"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOwnerId(), is(dummyKey));
                 assertThat(table1.getOwner().exists(), is(false));
                 assertThat(table1.getCreated().getISODate(), is("2012-01-06"));

                 table1.store();
                 actions.Checklist table2 = new actions.Checklist();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDescription(), is("text 2"));
                 assertThat(table2.getId(), is("text 3"));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getOwnerId(), is(dummyKey));
                 assertThat(table2.getOwner().exists(), is(false));
                 assertThat(table2.getCreated().getISODate(), is("2012-01-06"));

                 table1.setName("text 11");
                 table1.setDescription("text 12");
                 table1.setId("text 13");
                 table1.setProject( dummyKey);
                 table1.setOwner( dummyKey);
                 table1.setCreated(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-16"));
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDescription(), is("text 12"));
                 assertThat(table1.getId(), is("text 13"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOwnerId(), is(dummyKey));
                 assertThat(table1.getOwner().exists(), is(false));
                 assertThat(table1.getCreated().getISODate(), is("2012-01-16"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Checklist");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testChecklistItem(){
     
             try{
     
                 actions.ChecklistItem table1 = new actions.ChecklistItem(1, 2, "text 3", "text 4", "text 5", dummyKey, dummyKey, dummyKey, dummyKey, "text 10", new actions.ActionStatusTable().getDummyConstantValue( ), "2012-01-12");

                 assertThat(table1.getIdentifier(), is((long)1));
                 assertThat(table1.getParent(), is((long)2));
                 assertThat(table1.getName(), is("text 3"));
                 assertThat(table1.getDescription(), is("text 4"));
                 assertThat(table1.getComment(), is("text 5"));
                 assertThat(table1.getChecklistId(), is(dummyKey));
                 assertThat(table1.getChecklist().exists(), is(false));
                 assertThat(table1.getSourceId(), is(dummyKey));
                 assertThat(table1.getSource().exists(), is(false));
                 assertThat(table1.getCompletionId(), is(dummyKey));
                 assertThat(table1.getCompletion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getTagReference(), is("text 10"));
                 assertThat(table1.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getCompleted().getISODate(), is("2012-01-12"));

                 table1.store();
                 actions.ChecklistItem table2 = new actions.ChecklistItem();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getIdentifier(), is((long)1));
                 assertThat(table2.getParent(), is((long)2));
                 assertThat(table2.getName(), is("text 3"));
                 assertThat(table2.getDescription(), is("text 4"));
                 assertThat(table2.getComment(), is("text 5"));
                 assertThat(table2.getChecklistId(), is(dummyKey));
                 assertThat(table2.getChecklist().exists(), is(false));
                 assertThat(table2.getSourceId(), is(dummyKey));
                 assertThat(table2.getSource().exists(), is(false));
                 assertThat(table2.getCompletionId(), is(dummyKey));
                 assertThat(table2.getCompletion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getTagReference(), is("text 10"));
                 assertThat(table2.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table2.getCompleted().getISODate(), is("2012-01-12"));

                 table1.setIdentifier(11);
                 table1.setParent(12);
                 table1.setName("text 13");
                 table1.setDescription("text 14");
                 table1.setComment("text 15");
                 table1.setChecklist( dummyKey);
                 table1.setSource( dummyKey);
                 table1.setCompletion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setTagReference("text 20");
                 table1.setStatus(new actions.ActionStatusTable().getDummyConstantValue( ));
                 table1.setCompleted(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-22"));
                 assertThat(table1.getIdentifier(), is((long)11));
                 assertThat(table1.getParent(), is((long)12));
                 assertThat(table1.getName(), is("text 13"));
                 assertThat(table1.getDescription(), is("text 14"));
                 assertThat(table1.getComment(), is("text 15"));
                 assertThat(table1.getChecklistId(), is(dummyKey));
                 assertThat(table1.getChecklist().exists(), is(false));
                 assertThat(table1.getSourceId(), is(dummyKey));
                 assertThat(table1.getSource().exists(), is(false));
                 assertThat(table1.getCompletionId(), is(dummyKey));
                 assertThat(table1.getCompletion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getTagReference(), is("text 20"));
                 assertThat(table1.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getCompleted().getISODate(), is("2012-01-22"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ChecklistItem");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testAction(){
     
             try{
     
                 actions.Action table1 = new actions.Action(1, "text 2", "text 3", "text 4", dummyKey, dummyKey, dummyKey, dummyKey, dummyKey, dummyKey, 11, new actions.ActionStatusTable().getDummyConstantValue( ), "2012-01-13", "2012-01-14", "2012-01-15");

                 assertThat(table1.getId(), is((long)1));
                 assertThat(table1.getName(), is("text 2"));
                 assertThat(table1.getDescription(), is("text 3"));
                 assertThat(table1.getPattern(), is("text 4"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getComplyId(), is(dummyKey));
                 assertThat(table1.getComply().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getIssuerId(), is(dummyKey));
                 assertThat(table1.getIssuer().exists(), is(false));
                 assertThat(table1.getAssigneeId(), is(dummyKey));
                 assertThat(table1.getAssignee().exists(), is(false));
                 assertThat(table1.getPriority(), is((long)11));
                 assertThat(table1.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getCreated().getISODate(), is("2012-01-13"));
                 assertThat(table1.getDue().getISODate(), is("2012-01-14"));
                 assertThat(table1.getCompleted().getISODate(), is("2012-01-15"));

                 table1.store();
                 actions.Action table2 = new actions.Action();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getId(), is((long)1));
                 assertThat(table2.getName(), is("text 2"));
                 assertThat(table2.getDescription(), is("text 3"));
                 assertThat(table2.getPattern(), is("text 4"));
                 assertThat(table2.getFragmentId(), is(dummyKey));
                 assertThat(table2.getFragment().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getComplyId(), is(dummyKey));
                 assertThat(table2.getComply().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getIssuerId(), is(dummyKey));
                 assertThat(table2.getIssuer().exists(), is(false));
                 assertThat(table2.getAssigneeId(), is(dummyKey));
                 assertThat(table2.getAssignee().exists(), is(false));
                 assertThat(table2.getPriority(), is((long)11));
                 assertThat(table2.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table2.getCreated().getISODate(), is("2012-01-13"));
                 assertThat(table2.getDue().getISODate(), is("2012-01-14"));
                 assertThat(table2.getCompleted().getISODate(), is("2012-01-15"));

                 table1.setId(11);
                 table1.setName("text 12");
                 table1.setDescription("text 13");
                 table1.setPattern("text 14");
                 table1.setFragment( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setComply( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setIssuer( dummyKey);
                 table1.setAssignee( dummyKey);
                 table1.setPriority(21);
                 table1.setStatus(new actions.ActionStatusTable().getDummyConstantValue( ));
                 table1.setCreated(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-23"));
                 table1.setDue(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-24"));
                 table1.setCompleted(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-25"));
                 assertThat(table1.getId(), is((long)11));
                 assertThat(table1.getName(), is("text 12"));
                 assertThat(table1.getDescription(), is("text 13"));
                 assertThat(table1.getPattern(), is("text 14"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getComplyId(), is(dummyKey));
                 assertThat(table1.getComply().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getIssuerId(), is(dummyKey));
                 assertThat(table1.getIssuer().exists(), is(false));
                 assertThat(table1.getAssigneeId(), is(dummyKey));
                 assertThat(table1.getAssignee().exists(), is(false));
                 assertThat(table1.getPriority(), is((long)21));
                 assertThat(table1.getStatus(), is(new actions.ActionStatusTable().getDummyConstantValue( )));
                 assertThat(table1.getCreated().getISODate(), is("2012-01-23"));
                 assertThat(table1.getDue().getISODate(), is("2012-01-24"));
                 assertThat(table1.getCompleted().getISODate(), is("2012-01-25"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Action");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractFragmentType(){
     
             try{
     
                 contractManagement.ContractFragmentType table1 = new contractManagement.ContractFragmentType("text 1", "text 2");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDescription(), is("text 2"));

                 table1.store();
                 contractManagement.ContractFragmentType table2 = new contractManagement.ContractFragmentType();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDescription(), is("text 2"));

                 table1.setName("text 11");
                 table1.setDescription("text 12");
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDescription(), is("text 12"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractFragmentType");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(contractManagement.ContractFragmentType.getText(), is(contractManagement.ContractFragmentType.getText()));
                assertThat(contractManagement.ContractFragmentType.getHeadline(), is(contractManagement.ContractFragmentType.getHeadline()));
                assertThat(contractManagement.ContractFragmentType.getSub(), is(contractManagement.ContractFragmentType.getSub()));
                assertThat(contractManagement.ContractFragmentType.getBList(), is(contractManagement.ContractFragmentType.getBList()));
                assertThat(contractManagement.ContractFragmentType.getIList(), is(contractManagement.ContractFragmentType.getIList()));
                assertThat(contractManagement.ContractFragmentType.getNList(), is(contractManagement.ContractFragmentType.getNList()));
                assertThat(contractManagement.ContractFragmentType.getDocTitle(), is(contractManagement.ContractFragmentType.getDocTitle()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractFragmentType");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testStructureItem(){
     
             try{
     
                 contractManagement.StructureItem table1 = new contractManagement.StructureItem("text 1", 2, dummyKey, dummyKey, 5, "text 6", 7);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getTopElement(), is((long)2));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOrdinal(), is((long)5));
                 assertThat(table1.getType(), is("text 6"));
                 assertThat(table1.getIndentation(), is((long)7));

                 table1.store();
                 contractManagement.StructureItem table2 = new contractManagement.StructureItem();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getTopElement(), is((long)2));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getOrdinal(), is((long)5));
                 assertThat(table2.getType(), is("text 6"));
                 assertThat(table2.getIndentation(), is((long)7));

                 table1.setName("text 11");
                 table1.setTopElement(12);
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setOrdinal(15);
                 table1.setType("text 16");
                 table1.setIndentation(17);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getTopElement(), is((long)12));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getOrdinal(), is((long)15));
                 assertThat(table1.getType(), is("text 16"));
                 assertThat(table1.getIndentation(), is((long)17));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table StructureItem");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractClause(){
     
             try{
     
                 contractManagement.ContractClause table1 = new contractManagement.ContractClause("text 1", dummyKey, 3);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getNumber(), is((long)3));

                 table1.store();
                 contractManagement.ContractClause table2 = new contractManagement.ContractClause();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getNumber(), is((long)3));

                 table1.setName("text 11");
                 table1.setVersion( dummyKey);
                 table1.setNumber(13);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getNumber(), is((long)13));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractClause");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractFragment(){
     
             try{
     
                 contractManagement.ContractFragment table1 = new contractManagement.ContractFragment("text 1", dummyKey, dummyKey, 4, 5, "text 6", 7, "text 8", new risk.ContractRiskTable().getDummyConstantValue( ), 10, 11, 12, 13, 14, 15, 16, "text 17", "text 18");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getStructureNo(), is((long)4));
                 assertThat(table1.getOrdinal(), is((long)5));
                 assertThat(table1.getText(), is("text 6"));
                 assertThat(table1.getIndentation(), is((long)7));
                 assertThat(table1.getType(), is("text 8"));
                 assertThat(table1.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getAnnotationCount(), is((long)10));
                 assertThat(table1.getReferenceCount(), is((long)11));
                 assertThat(table1.getClassificatonCount(), is((long)12));
                 assertThat(table1.getActionCount(), is((long)13));
                 assertThat(table1.getxPos(), is((long)14));
                 assertThat(table1.getyPos(), is((long)15));
                 assertThat(table1.getwidth(), is((long)16));
                 assertThat(table1.getdisplay(), is("text 17"));
                 assertThat(table1.getImage(), is("text 18"));

                 table1.store();
                 contractManagement.ContractFragment table2 = new contractManagement.ContractFragment();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getStructureNo(), is((long)4));
                 assertThat(table2.getOrdinal(), is((long)5));
                 assertThat(table2.getText(), is("text 6"));
                 assertThat(table2.getIndentation(), is((long)7));
                 assertThat(table2.getType(), is("text 8"));
                 assertThat(table2.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table2.getAnnotationCount(), is((long)10));
                 assertThat(table2.getReferenceCount(), is((long)11));
                 assertThat(table2.getClassificatonCount(), is((long)12));
                 assertThat(table2.getActionCount(), is((long)13));
                 assertThat(table2.getxPos(), is((long)14));
                 assertThat(table2.getyPos(), is((long)15));
                 assertThat(table2.getwidth(), is((long)16));
                 assertThat(table2.getdisplay(), is("text 17"));
                 assertThat(table2.getImage(), is("text 18"));

                 table1.setName("text 11");
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setStructureNo(14);
                 table1.setOrdinal(15);
                 table1.setText("text 16");
                 table1.setIndentation(17);
                 table1.setType("text 18");
                 table1.setRisk(new risk.ContractRiskTable().getDummyConstantValue( ));
                 table1.setAnnotationCount(20);
                 table1.setReferenceCount(21);
                 table1.setClassificatonCount(22);
                 table1.setActionCount(23);
                 table1.setxPos(24);
                 table1.setyPos(25);
                 table1.setwidth(26);
                 table1.setdisplay("text 27");
                 table1.setImage("text 28");
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getStructureNo(), is((long)14));
                 assertThat(table1.getOrdinal(), is((long)15));
                 assertThat(table1.getText(), is("text 16"));
                 assertThat(table1.getIndentation(), is((long)17));
                 assertThat(table1.getType(), is("text 18"));
                 assertThat(table1.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getAnnotationCount(), is((long)20));
                 assertThat(table1.getReferenceCount(), is((long)21));
                 assertThat(table1.getClassificatonCount(), is((long)22));
                 assertThat(table1.getActionCount(), is((long)23));
                 assertThat(table1.getxPos(), is((long)24));
                 assertThat(table1.getyPos(), is((long)25));
                 assertThat(table1.getwidth(), is((long)26));
                 assertThat(table1.getdisplay(), is("text 27"));
                 assertThat(table1.getImage(), is("text 28"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractFragment");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractView(){
     
             try{
     
                 contractManagement.ContractView table1 = new contractManagement.ContractView("text 1", dummyKey, "text 3", "text 4");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getContractId(), is(dummyKey));
                 assertThat(table1.getContract().exists(), is(false));
                 assertThat(table1.getSearch(), is("text 3"));
                 assertThat(table1.getComment(), is("text 4"));

                 table1.store();
                 contractManagement.ContractView table2 = new contractManagement.ContractView();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getContractId(), is(dummyKey));
                 assertThat(table2.getContract().exists(), is(false));
                 assertThat(table2.getSearch(), is("text 3"));
                 assertThat(table2.getComment(), is("text 4"));

                 table1.setName("text 11");
                 table1.setContract( dummyKey);
                 table1.setSearch("text 13");
                 table1.setComment("text 14");
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getContractId(), is(dummyKey));
                 assertThat(table1.getContract().exists(), is(false));
                 assertThat(table1.getSearch(), is("text 13"));
                 assertThat(table1.getComment(), is("text 14"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractView");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractAnnotation(){
     
             try{
     
                 contractManagement.ContractAnnotation table1 = new contractManagement.ContractAnnotation("text 1", dummyKey, 3, "text 4", dummyKey, dummyKey, "text 7", 8, "2012-01-09 00:00:00.0");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getOrdinal(), is((long)3));
                 assertThat(table1.getDescription(), is("text 4"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 7"));
                 assertThat(table1.getPatternPos(), is((long)8));
                 assertThat(table1.getTime().getSQLTime().toString(), is("2012-01-09 00:00:00.0"));

                 table1.store();
                 contractManagement.ContractAnnotation table2 = new contractManagement.ContractAnnotation();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getFragmentId(), is(dummyKey));
                 assertThat(table2.getFragment().exists(), is(false));
                 assertThat(table2.getOrdinal(), is((long)3));
                 assertThat(table2.getDescription(), is("text 4"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getPattern(), is("text 7"));
                 assertThat(table2.getPatternPos(), is((long)8));
                 assertThat(table2.getTime().getSQLTime().toString(), is("2012-01-09 00:00:00.0"));

                 table1.setName("text 11");
                 table1.setFragment( dummyKey);
                 table1.setOrdinal(13);
                 table1.setDescription("text 14");
                 table1.setCreator( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setPattern("text 17");
                 table1.setPatternPos(18);
                 table1.setTime(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-19 00:00:00.0"));
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getOrdinal(), is((long)13));
                 assertThat(table1.getDescription(), is("text 14"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 17"));
                 assertThat(table1.getPatternPos(), is((long)18));
                 assertThat(table1.getTime().getSQLTime().toString(), is("2012-01-19 00:00:00.0"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractAnnotation");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testKeyword(){
     
             try{
     
                 search.Keyword table1 = new search.Keyword("text 1", dummyKey, dummyKey, dummyKey);

                 assertThat(table1.getKeyword(), is("text 1"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));

                 table1.store();
                 search.Keyword table2 = new search.Keyword();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getKeyword(), is("text 1"));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getDocumentId(), is(dummyKey));
                 assertThat(table2.getDocument().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));

                 table1.setKeyword("text 11");
                 table1.setVersion( dummyKey);
                 table1.setDocument( dummyKey);
                 table1.setProject( dummyKey);
                 assertThat(table1.getKeyword(), is("text 11"));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Keyword");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testDefinition(){
     
             try{
     
                 crossReference.Definition table1 = new crossReference.Definition("text 1", dummyKey, dummyKey, dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDefinedInId(), is(dummyKey));
                 assertThat(table1.getDefinedIn().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));

                 table1.store();
                 crossReference.Definition table2 = new crossReference.Definition();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDefinedInId(), is(dummyKey));
                 assertThat(table2.getDefinedIn().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));

                 table1.setName("text 11");
                 table1.setDefinedIn( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDefinedInId(), is(dummyKey));
                 assertThat(table1.getDefinedIn().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Definition");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testReference(){
     
             try{
     
                 crossReference.Reference table1 = new crossReference.Reference("text 1", dummyKey, dummyKey, dummyKey, dummyKey, new crossReference.ReferenceTypeTable().getDummyConstantValue( ), "text 7", 8, dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getFromId(), is(dummyKey));
                 assertThat(table1.getFrom().exists(), is(false));
                 assertThat(table1.getToId(), is(dummyKey));
                 assertThat(table1.getTo().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getType(), is(new crossReference.ReferenceTypeTable().getDummyConstantValue( )));
                 assertThat(table1.getPattern(), is("text 7"));
                 assertThat(table1.getPatternPos(), is((long)8));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));

                 table1.store();
                 crossReference.Reference table2 = new crossReference.Reference();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getFromId(), is(dummyKey));
                 assertThat(table2.getFrom().exists(), is(false));
                 assertThat(table2.getToId(), is(dummyKey));
                 assertThat(table2.getTo().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getType(), is(new crossReference.ReferenceTypeTable().getDummyConstantValue( )));
                 assertThat(table2.getPattern(), is("text 7"));
                 assertThat(table2.getPatternPos(), is((long)8));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));

                 table1.setName("text 11");
                 table1.setFrom( dummyKey);
                 table1.setTo( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setType(new crossReference.ReferenceTypeTable().getDummyConstantValue( ));
                 table1.setPattern("text 17");
                 table1.setPatternPos(18);
                 table1.setCreator( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getFromId(), is(dummyKey));
                 assertThat(table1.getFrom().exists(), is(false));
                 assertThat(table1.getToId(), is(dummyKey));
                 assertThat(table1.getTo().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getType(), is(new crossReference.ReferenceTypeTable().getDummyConstantValue( )));
                 assertThat(table1.getPattern(), is("text 17"));
                 assertThat(table1.getPatternPos(), is((long)18));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Reference");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testFragmentClassification(){
     
             try{
     
                 classification.FragmentClassification table1 = new classification.FragmentClassification(dummyKey, "text 2", 3, 4, "text 5", "text 6", dummyKey, dummyKey, dummyKey, "text 10", 11, 12, 13, "text 14", "2012-01-15 00:00:00.0");

                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getClassTag(), is("text 2"));
                 assertThat(table1.getRequirementLevel(), is((long)3));
                 assertThat(table1.getApplicablePhase(), is((long)4));
                 assertThat(table1.getComment(), is("text 5"));
                 assertThat(table1.getKeywords(), is("text 6"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 10"));
                 assertThat(table1.getPos(), is((long)11));
                 assertThat(table1.getLength(), is((long)12));
                 assertThat(table1.getSignificance(), is((long)13));
                 assertThat(table1.getRuleId(), is("text 14"));
                 assertThat(table1.getTime().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));

                 table1.store();
                 classification.FragmentClassification table2 = new classification.FragmentClassification();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getFragmentId(), is(dummyKey));
                 assertThat(table2.getFragment().exists(), is(false));
                 assertThat(table2.getClassTag(), is("text 2"));
                 assertThat(table2.getRequirementLevel(), is((long)3));
                 assertThat(table2.getApplicablePhase(), is((long)4));
                 assertThat(table2.getComment(), is("text 5"));
                 assertThat(table2.getKeywords(), is("text 6"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getPattern(), is("text 10"));
                 assertThat(table2.getPos(), is((long)11));
                 assertThat(table2.getLength(), is((long)12));
                 assertThat(table2.getSignificance(), is((long)13));
                 assertThat(table2.getRuleId(), is("text 14"));
                 assertThat(table2.getTime().getSQLTime().toString(), is("2012-01-15 00:00:00.0"));

                 table1.setFragment( dummyKey);
                 table1.setClassTag("text 12");
                 table1.setRequirementLevel(13);
                 table1.setApplicablePhase(14);
                 table1.setComment("text 15");
                 table1.setKeywords("text 16");
                 table1.setCreator( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setPattern("text 20");
                 table1.setPos(21);
                 table1.setLength(22);
                 table1.setSignificance(23);
                 table1.setRuleId("text 24");
                 table1.setTime(new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, "2012-01-25 00:00:00.0"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getClassTag(), is("text 12"));
                 assertThat(table1.getRequirementLevel(), is((long)13));
                 assertThat(table1.getApplicablePhase(), is((long)14));
                 assertThat(table1.getComment(), is("text 15"));
                 assertThat(table1.getKeywords(), is("text 16"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 20"));
                 assertThat(table1.getPos(), is((long)21));
                 assertThat(table1.getLength(), is((long)22));
                 assertThat(table1.getSignificance(), is((long)23));
                 assertThat(table1.getRuleId(), is("text 24"));
                 assertThat(table1.getTime().getSQLTime().toString(), is("2012-01-25 00:00:00.0"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table FragmentClassification");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testRiskClassification(){
     
             try{
     
                 risk.RiskClassification table1 = new risk.RiskClassification(dummyKey, new risk.ContractRiskTable().getDummyConstantValue( ), "text 3", "text 4", dummyKey, dummyKey, dummyKey, "text 8", 9, "2012-01-10");

                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getComment(), is("text 3"));
                 assertThat(table1.getKeywords(), is("text 4"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 8"));
                 assertThat(table1.getPatternPos(), is((long)9));
                 assertThat(table1.getTime().getISODate(), is("2012-01-10"));

                 table1.store();
                 risk.RiskClassification table2 = new risk.RiskClassification();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getFragmentId(), is(dummyKey));
                 assertThat(table2.getFragment().exists(), is(false));
                 assertThat(table2.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table2.getComment(), is("text 3"));
                 assertThat(table2.getKeywords(), is("text 4"));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getVersionId(), is(dummyKey));
                 assertThat(table2.getVersion().exists(), is(false));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getPattern(), is("text 8"));
                 assertThat(table2.getPatternPos(), is((long)9));
                 assertThat(table2.getTime().getISODate(), is("2012-01-10"));

                 table1.setFragment( dummyKey);
                 table1.setRisk(new risk.ContractRiskTable().getDummyConstantValue( ));
                 table1.setComment("text 13");
                 table1.setKeywords("text 14");
                 table1.setCreator( dummyKey);
                 table1.setVersion( dummyKey);
                 table1.setProject( dummyKey);
                 table1.setPattern("text 18");
                 table1.setPatternPos(19);
                 table1.setTime(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-20"));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));
                 assertThat(table1.getRisk(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getComment(), is("text 13"));
                 assertThat(table1.getKeywords(), is("text 14"));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVersionId(), is(dummyKey));
                 assertThat(table1.getVersion().exists(), is(false));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getPattern(), is("text 18"));
                 assertThat(table1.getPatternPos(), is((long)19));
                 assertThat(table1.getTime().getISODate(), is("2012-01-20"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table RiskClassification");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testReclassification(){
     
             try{
     
                 classification.Reclassification table1 = new classification.Reclassification("text 1", false, dummyKey, "text 4", "text 5", 6, "text 7", "text 8", "text 9", 10, 11, new risk.ContractRiskTable().getDummyConstantValue( ), dummyKey, "2012-01-14", true);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getisPositive(), is(false));
                 assertThat(table1.getUserId(), is(dummyKey));
                 assertThat(table1.getUser().exists(), is(false));
                 assertThat(table1.getFragment(), is("text 4"));
                 assertThat(table1.getHeadline(), is("text 5"));
                 assertThat(table1.getFragmentNo(), is((long)6));
                 assertThat(table1.getPattern(), is("text 7"));
                 assertThat(table1.getComment(), is("text 8"));
                 assertThat(table1.getClassTag(), is("text 9"));
                 assertThat(table1.getRequirementLevel(), is((long)10));
                 assertThat(table1.getApplicablePhase(), is((long)11));
                 assertThat(table1.getRiskLevel(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getDate().getISODate(), is("2012-01-14"));
                 assertThat(table1.getClosed(), is(true));

                 table1.store();
                 classification.Reclassification table2 = new classification.Reclassification();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getisPositive(), is(false));
                 assertThat(table2.getUserId(), is(dummyKey));
                 assertThat(table2.getUser().exists(), is(false));
                 assertThat(table2.getFragment(), is("text 4"));
                 assertThat(table2.getHeadline(), is("text 5"));
                 assertThat(table2.getFragmentNo(), is((long)6));
                 assertThat(table2.getPattern(), is("text 7"));
                 assertThat(table2.getComment(), is("text 8"));
                 assertThat(table2.getClassTag(), is("text 9"));
                 assertThat(table2.getRequirementLevel(), is((long)10));
                 assertThat(table2.getApplicablePhase(), is((long)11));
                 assertThat(table2.getRiskLevel(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table2.getDocumentId(), is(dummyKey));
                 assertThat(table2.getDocument().exists(), is(false));
                 assertThat(table2.getDate().getISODate(), is("2012-01-14"));
                 assertThat(table2.getClosed(), is(true));

                 table1.setName("text 11");
                 table1.setisPositive(false);
                 table1.setUser( dummyKey);
                 table1.setFragment("text 14");
                 table1.setHeadline("text 15");
                 table1.setFragmentNo(16);
                 table1.setPattern("text 17");
                 table1.setComment("text 18");
                 table1.setClassTag("text 19");
                 table1.setRequirementLevel(20);
                 table1.setApplicablePhase(21);
                 table1.setRiskLevel(new risk.ContractRiskTable().getDummyConstantValue( ));
                 table1.setDocument( dummyKey);
                 table1.setDate(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-24"));
                 table1.setClosed(true);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getisPositive(), is(false));
                 assertThat(table1.getUserId(), is(dummyKey));
                 assertThat(table1.getUser().exists(), is(false));
                 assertThat(table1.getFragment(), is("text 14"));
                 assertThat(table1.getHeadline(), is("text 15"));
                 assertThat(table1.getFragmentNo(), is((long)16));
                 assertThat(table1.getPattern(), is("text 17"));
                 assertThat(table1.getComment(), is("text 18"));
                 assertThat(table1.getClassTag(), is("text 19"));
                 assertThat(table1.getRequirementLevel(), is((long)20));
                 assertThat(table1.getApplicablePhase(), is((long)21));
                 assertThat(table1.getRiskLevel(), is(new risk.ContractRiskTable().getDummyConstantValue( )));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getDate().getISODate(), is("2012-01-24"));
                 assertThat(table1.getClosed(), is(true));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Reclassification");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractSelection(){
     
             try{
     
                 contractManagement.ContractSelection table1 = new contractManagement.ContractSelection("text 1", dummyKey, dummyKey);

                 assertThat(table1.getname(), is("text 1"));
                 assertThat(table1.getSelectionViewId(), is(dummyKey));
                 assertThat(table1.getSelectionView().exists(), is(false));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));

                 table1.store();
                 contractManagement.ContractSelection table2 = new contractManagement.ContractSelection();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getname(), is("text 1"));
                 assertThat(table2.getSelectionViewId(), is(dummyKey));
                 assertThat(table2.getSelectionView().exists(), is(false));
                 assertThat(table2.getFragmentId(), is(dummyKey));
                 assertThat(table2.getFragment().exists(), is(false));

                 table1.setname("text 11");
                 table1.setSelectionView( dummyKey);
                 table1.setFragment( dummyKey);
                 assertThat(table1.getname(), is("text 11"));
                 assertThat(table1.getSelectionViewId(), is(dummyKey));
                 assertThat(table1.getSelectionView().exists(), is(false));
                 assertThat(table1.getFragmentId(), is(dummyKey));
                 assertThat(table1.getFragment().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractSelection");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testContractSelectionView(){
     
             try{
     
                 contractManagement.ContractSelectionView table1 = new contractManagement.ContractSelectionView("text 1", dummyKey, dummyKey, dummyKey);

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVisibilityId(), is(dummyKey));
                 assertThat(table1.getVisibility().exists(), is(false));

                 table1.store();
                 contractManagement.ContractSelectionView table2 = new contractManagement.ContractSelectionView();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getProjectId(), is(dummyKey));
                 assertThat(table2.getProject().exists(), is(false));
                 assertThat(table2.getCreatorId(), is(dummyKey));
                 assertThat(table2.getCreator().exists(), is(false));
                 assertThat(table2.getVisibilityId(), is(dummyKey));
                 assertThat(table2.getVisibility().exists(), is(false));

                 table1.setName("text 11");
                 table1.setProject( dummyKey);
                 table1.setCreator( dummyKey);
                 table1.setVisibility( dummyKey);
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getProjectId(), is(dummyKey));
                 assertThat(table1.getProject().exists(), is(false));
                 assertThat(table1.getCreatorId(), is(dummyKey));
                 assertThat(table1.getCreator().exists(), is(false));
                 assertThat(table1.getVisibilityId(), is(dummyKey));
                 assertThat(table1.getVisibility().exists(), is(false));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table ContractSelectionView");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testVisibility(){
     
             try{
     
                 userManagement.Visibility table1 = new userManagement.Visibility("text 1", "text 2");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDescription(), is("text 2"));

                 table1.store();
                 userManagement.Visibility table2 = new userManagement.Visibility();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDescription(), is("text 2"));

                 table1.setName("text 11");
                 table1.setDescription("text 12");
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDescription(), is("text 12"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Visibility");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
             try{
                assertThat(userManagement.Visibility.getPrivate(), is(userManagement.Visibility.getPrivate()));
                assertThat(userManagement.Visibility.getOrg(), is(userManagement.Visibility.getOrg()));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table Visibility");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }

         @Test
         public void testAccessGrant(){
     
             try{
     
                 userManagement.AccessGrant table1 = new userManagement.AccessGrant("text 1", dummyKey, new userManagement.AccessRightTable().getDummyConstantValue( ), dummyKey, dummyKey, "2012-01-06");

                 assertThat(table1.getName(), is("text 1"));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getAccessRight(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));
                 assertThat(table1.getVisibilityId(), is(dummyKey));
                 assertThat(table1.getVisibility().exists(), is(false));
                 assertThat(table1.getIssuerId(), is(dummyKey));
                 assertThat(table1.getIssuer().exists(), is(false));
                 assertThat(table1.getTime().getISODate(), is("2012-01-06"));

                 table1.store();
                 userManagement.AccessGrant table2 = new userManagement.AccessGrant();
                 table2.load(new LookupByKey(table1.getKey()));

                 assertThat(table2.getName(), is("text 1"));
                 assertThat(table2.getDocumentId(), is(dummyKey));
                 assertThat(table2.getDocument().exists(), is(false));
                 assertThat(table2.getAccessRight(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));
                 assertThat(table2.getVisibilityId(), is(dummyKey));
                 assertThat(table2.getVisibility().exists(), is(false));
                 assertThat(table2.getIssuerId(), is(dummyKey));
                 assertThat(table2.getIssuer().exists(), is(false));
                 assertThat(table2.getTime().getISODate(), is("2012-01-06"));

                 table1.setName("text 11");
                 table1.setDocument( dummyKey);
                 table1.setAccessRight(new userManagement.AccessRightTable().getDummyConstantValue( ));
                 table1.setVisibility( dummyKey);
                 table1.setIssuer( dummyKey);
                 table1.setTime(new DBTimeStamp(DBTimeStamp.ISO_DATE, "2012-01-16"));
                 assertThat(table1.getName(), is("text 11"));
                 assertThat(table1.getDocumentId(), is(dummyKey));
                 assertThat(table1.getDocument().exists(), is(false));
                 assertThat(table1.getAccessRight(), is(new userManagement.AccessRightTable().getDummyConstantValue( )));
                 assertThat(table1.getVisibilityId(), is(dummyKey));
                 assertThat(table1.getVisibility().exists(), is(false));
                 assertThat(table1.getIssuerId(), is(dummyKey));
                 assertThat(table1.getIssuer().exists(), is(false));
                 assertThat(table1.getTime().getISODate(), is("2012-01-16"));

             }catch(BackOfficeException e){
     
                 e.logError("Error creating table AccessGrant");
                 assertTrue(false);
                 
             }catch(Exception e){

                 e.printStackTrace();
                 assertTrue(false);
             }
          }


}
