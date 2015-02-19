package contractManagement;

import classifiers.Classification;
import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    Project - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Project extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ProjectTable();

    public Project(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Project(String name, String description, DataObjectInterface creator, DataObjectInterface organization, String creationtime, String access) throws BackOfficeException{

        this(name, description, creator.getKey(), organization.getKey(), creationtime, access);
    }


    public Project(String name, String description, DBKeyInterface creator, DBKeyInterface organization, String creationtime, String access){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new TextData(description);
           data[2] = new ReferenceData(creator, columns[2].getTableReference());
           data[3] = new ReferenceData(organization, columns[3].getTableReference());
           data[4] = new TimeStampData(creationtime);
           data[5] = new StringData(access);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Project(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Project o = new Project();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getName(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[0];
        data.setStringValue(name);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[1];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[1];
        data.setStringValue(description);
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = creator;
    }



    public DBKeyInterface getOrganizationId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public userManagement.Organization getOrganization(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new userManagement.Organization(new LookupByKey(data.value));
    }

    public void setOrganization(DBKeyInterface organization){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = organization;
    }



    public DBTimeStamp getCreationTime()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[4];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setCreationTime(DBTimeStamp creationtime){

        TimeStampData data = (TimeStampData) this.data[4];
        data.value = creationtime.getSQLTime().toString();
    }



    public String getAccess(){

        StringData data = (StringData) this.data[5];
        return data.getStringValue();
    }

    public void setAccess(String access){

        StringData data = (StringData) this.data[5];
        data.setStringValue(access);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    /***********************************************************************
     *
     *      Delete an entire project by recursively delete all documents.
     *
     *
     * @return - a compilation of all the deleted items.
     *
     * @throws BackOfficeException
     */


    public DocumentDeleteOutcome recursivelyDelete() throws BackOfficeException {

        DocumentDeleteOutcome outcome = new DocumentDeleteOutcome();

        // Recursively delete the documents in the project

        List<Contract> allContracts = getContractsForProject();

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found " + allContracts.size() + " documents for project " + getName());

        for(Contract contract : allContracts){

            // Compile the outcome
            outcome.add(contract.recursivelyDeleteDocument());

        }

        new ProjectTable().deleteItem(this);

        return outcome;

    }

    public List<Contract> getContractsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ContractTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new ContractTable(condition).getValues();

        List<Contract> documents = (List<Contract>)(List<?>) objects;

        return documents;

    }

    public List<Contract> getContractsForProject(){

        return getContractsForProject(new LookupList());
    }



    public List<FragmentClassification> getFragmentClassificationsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new FragmentClassificationTable(condition).getValues();

        List<FragmentClassification> documents = (List<FragmentClassification>)(List<?>) objects;

        return documents;

    }

    public List<FragmentClassification> getFragmentClassificationsForProject(){

        return getFragmentClassificationsForProject(new LookupList());
    }





    public List<Action> getActionsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ActionTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new ActionTable(condition).getValues();

        List<Action> actions = (List<Action>)(List<?>) objects;

        return actions;

    }

    public List<Action> getActionsForProject(){

        return getActionsForProject(new LookupList());
    }



    //TODO: Should be automatic

    public List<Snapshot> getSnapshotsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(SnapshotTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new SnapshotTable(condition).getValues();

        List<Snapshot> versions = (List<Snapshot>)(List<?>) objects;

        return versions;
    }

    // No condition retrieves all items

    public List<Snapshot> getSnapshotsForProject(){

        return getSnapshotsForProject(new LookupList());
    }

    public List<Keyword> getKeywordsForProject(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(KeywordTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new KeywordTable(condition).getValues();

        List<Keyword> versions = (List<Keyword>)(List<?>) objects;

        return versions;
    }

    // No condition retrieves all items

    public List<Keyword> getKeywordsForProject() throws BackOfficeException{

        return getKeywordsForProject(new LookupList());
    }





    public List<Reference> getReferencesForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ReferenceTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new ReferenceTable(condition).getValues();

        List<Reference> list = (List<Reference>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<Reference> getReferencesForProject(){

        return getReferencesForProject(new LookupList());
    }



    public List<Definition> getDefinitionsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(DefinitionTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new DefinitionTable(condition).getValues();

        List<Definition> list = (List<Definition>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<Definition> getDefinitionsForProject(){

        return getDefinitionsForProject(new LookupList());
    }


    public List<Checklist> getChecklistsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ChecklistTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new ChecklistTable(condition).getValues();

        List<Checklist> list = (List<Checklist>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<Checklist> getChecklistsForProject(){

        return getChecklistsForProject(new LookupList());
    }



    public List<ChecklistItem> getChecklistItemsForProject(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ChecklistItemTable.Columns.Project.name(), getKey()));

        List<DataObjectInterface> objects = new ChecklistItemTable(condition).getValues();

        List<ChecklistItem> list = (List<ChecklistItem>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<ChecklistItem> getChecklistItemsForProject(){

        return getChecklistItemsForProject(new LookupList());
    }




    public document.AbstractProject createAbstractProject() throws BackOfficeException{

        document.AbstractProject aProject = new document.AbstractProject();
        List<Contract> documentsForProject = getContractsForProject();

        for(Contract document : documentsForProject){


            aProject.addDocument(document.createAbstractDocument(aProject));

        }

        return aProject;
    }

    public List<Reference> getOpenReferences() throws BackOfficeException{

        List<Contract> documentsForProject = getContractsForProject();
        List<Reference> openReferences = new java.util.ArrayList<Reference>();

        ConditionInterface onlyOpen = new LookupList().addFilter(new ColumnFilter(ReferenceTable.Columns.Type.name(), ReferenceType.getOpen().get__Id()));
        //ConditionInterface all = new LookupList();

        for(Contract document : documentsForProject){

            ContractVersionInstance head = document.getHeadVersion();

            List<Reference> references = document.getHeadVersion().getReferencesForVersion(onlyOpen);
            openReferences.addAll(references);
        }
        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found " + openReferences.size() + "open references in project " + getName());

        return openReferences;
    }
}
