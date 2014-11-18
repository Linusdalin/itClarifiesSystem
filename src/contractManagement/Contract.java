package contractManagement;

import contractManagement.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    Contract - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Contract extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractTable();

    public Contract(){

        super();         if(table == null)
            table = TABLE;
    }

    public Contract(String name, String file, long ordinal, DataObjectInterface type, DataObjectInterface status, String message, String description, DataObjectInterface project, DataObjectInterface owner, String creation, String language) throws BackOfficeException{

        this(name, file, ordinal, type.getKey(), status.getKey(), message, description, project.getKey(), owner.getKey(), creation, language);
    }


    public Contract(String name, String file, long ordinal, DBKeyInterface type, DBKeyInterface status, String message, String description, DBKeyInterface project, DBKeyInterface owner, String creation, String language) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new StringData(file);
        data[2] = new IntData(ordinal);
        data[3] = new ReferenceData(type, columns[3].getTableReference());
        data[4] = new ReferenceData(status, columns[4].getTableReference());
        data[5] = new TextData(message);
        data[6] = new TextData(description);
        data[7] = new ReferenceData(project, columns[7].getTableReference());
        data[8] = new ReferenceData(owner, columns[8].getTableReference());
        data[9] = new DateData(creation);
        data[10] = new StringData(language);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Contract(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Contract o = new Contract();
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



    public String getFile(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setFile(String file){

        StringData data = (StringData) this.data[1];
        data.setStringValue(file);
    }



    public long getOrdinal(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[2];
        data.value = ordinal;
    }



    public DBKeyInterface getTypeId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public ContractType getType(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new ContractType(new LookupByKey(data.value));
    }

    public void setType(DBKeyInterface type){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = type;
    }



    public DBKeyInterface getStatusId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public ContractStatus getStatus(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new ContractStatus(new LookupByKey(data.value));
    }

    public void setStatus(DBKeyInterface status){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = status;
    }



    public String getMessage(){

        TextData data = (TextData) this.data[5];
        return data.getStringValue();
    }

    public void setMessage(String message){

        TextData data = (TextData) this.data[5];
        data.setStringValue(message);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[6];
        data.setStringValue(description);
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[7];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[7];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[7];
        data.value = project;
    }



    public DBKeyInterface getOwnerId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public PortalUser getOwner(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setOwner(DBKeyInterface owner){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = owner;
    }



    public DBTimeStamp getCreation()throws BackOfficeException{

        DateData data = (DateData) this.data[9];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCreation(DBTimeStamp creation){

        DateData data = (DateData) this.data[9];
        data.value = creation.getISODate().toString();
    }



    public String getLanguage(){

        StringData data = (StringData) this.data[10];
        return data.getStringValue();
    }

    public void setLanguage(String language){

        StringData data = (StringData) this.data[10];
        data.setStringValue(language);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




    /****************************************************************************************
     *
     *          Recursively delete a document.
     *          This should remove all instances, clauses and fragments
     *
     * @return - Outcome object listing the number of items deleted
     * @throws BackOfficeException
     *
     *
     *          TODO: Use batch delete (important for fragment)
     *          TODO: Remove risk
     *
     */

    public DocumentDeleteOutcome recursivelyDeleteDocument() throws BackOfficeException{

        int noFragments = 0;
        int noClauses = 0;
        int noInstances = 0;
        int noAnnotations = 0;
        int noReferences = 0;
        int noClassifications = 0;
        int noFlags = 0;

       // Get all instances

       ContractVersionInstanceTable versions = new ContractVersionInstanceTable(new LookupItem()
               .addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey())));

       for(DataObjectInterface v : versions.getValues()){

           ContractVersionInstance version = (ContractVersionInstance)v;

           // Get all fragments for this version and delete them


           ContractFragmentTable allFragments = new ContractFragmentTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey())));

           noFragments += allFragments.getCount();
           allFragments.delete();


           // Get all annotations for this version and delete them


           ContractAnnotationTable allAnnotations = new ContractAnnotationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), version.getKey())));

           noAnnotations += allAnnotations.getCount();
           allAnnotations.delete();


           // Get all references for this version and delete them

           ReferenceTable allReferences = new ReferenceTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), version.getKey())));

           noReferences += allReferences.getCount();
           allReferences.delete();

           // Get all classifications for this version and delete them

           FragmentClassificationTable allClassifications = new FragmentClassificationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Version.name(), version.getKey())));

           noClassifications += allClassifications.getCount();
           allClassifications.delete();

            // Get all clauses for this version and delete them


           ContractClauseTable allClauses = new ContractClauseTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractClauseTable.Columns.Version.name(), version.getKey())));

           noClauses += allClauses.getCount();
           allClauses.delete();

            // Get all risk for this version and delete them


           RiskClassificationTable allRiskClassifications = new RiskClassificationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Version.name(), version.getKey())));


           noFlags += allRiskClassifications.getCount();
           System.out.println("Found " + noFlags + " risks to delete for document" + getName() + "(version " + version.getVersion() + ")");

           allRiskClassifications.delete();

           // Delete the version

           services.DocumentService.invalidateFragmentCache(version);
           versions.deleteItem(version);
           noInstances++;
       }

        // Now delete the actual document

       new ContractTable().deleteItem(this);

        // Clear the caches

        services.DocumentService.invalidateDocumentCache(this, this.getProject());

        return new DocumentDeleteOutcome(1, noInstances, noClauses, noFragments, noAnnotations, noClassifications, noFlags, noReferences);

    }


    //TODO: Should be automatic

    public List<ContractVersionInstance> getVersionsForDocument(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey()));

        List<DataObjectInterface> objects = new ContractVersionInstanceTable(condition).getValues();

        List<ContractVersionInstance> versions = (List<ContractVersionInstance>)(List<?>) objects;

        return versions;
    }




    // No condition retrieves all items

    public List<ContractVersionInstance> getVersionsForDocument() throws BackOfficeException{

        return getVersionsForDocument(new LookupList());
    }

    public List<Keyword> getKeywordsForDocument(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(KeywordTable.Columns.Document.name(), getKey()));

        List<DataObjectInterface> objects = new KeywordTable(condition).getValues();

        List<Keyword> versions = (List<Keyword>)(List<?>) objects;

        return versions;
    }

    // No condition retrieves all items

    public List<Keyword> getKeywordsForDocument() throws BackOfficeException{

        return getKeywordsForDocument(new LookupList());
    }



    /******************************************************************************'
     *
     *          Lookup the corresponding version for a snapshot.
     *
     *          This is done by checking the time
     *
     * @param freezeSnapshot
     * @return
     * @throws BackOfficeException
     */

    public ContractVersionInstance getVersionForSnapshot(Snapshot freezeSnapshot) throws BackOfficeException {



        DBTimeStamp snapShotTime = freezeSnapshot.getTimestamp();
        List<ContractVersionInstance> versionList = getVersionsForDocument(new LookupList(new Sorting(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.FIRST)));
        ContractVersionInstance last = null;

        for(ContractVersionInstance version : versionList){

            if(version.getCreation().isBefore(snapShotTime))
                last = version;
            else{

                if(last == null)
                    throw new BackOfficeException(BackOfficeException.TableError,
                            "No version found for document " + getName() + " and snapshot " + freezeSnapshot.getName() + "(@ " + snapShotTime.getSQLTime().toString() + ")");
                return last;
            }


        }

        return last;

    }

    public ContractVersionInstance getHeadVersion() throws BackOfficeException {

        ContractVersionInstance last = new ContractVersionInstance(new LookupItem()
                .addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey()))
                .addSorting(new Sorting(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.LAST)));

        if(!last.exists())
            throw new BackOfficeException(BackOfficeException.TableError, "Could not find last version for document " + this.getName());

        return last;
    }

    public ContractVersionInstance addNewVersion(PortalUser creator, fileHandling.RepositoryFileHandler fileHandler) throws BackOfficeException{


                return new ContractTable().createNewVersion(this, fileHandler, creator);

    }

    public document.AbstractDocument createAbstractDocument(document.AbstractProject aProject) throws BackOfficeException {

        ContractVersionInstance latestVersion = getHeadVersion();
        return latestVersion.createAbstractDocumentVersion(aProject);


    }
}
