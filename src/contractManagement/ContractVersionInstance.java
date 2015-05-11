package contractManagement;

import document.DefinitionType;
import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import reclassification.*;
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
 *    ContractVersionInstance - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractVersionInstance extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractVersionInstanceTable();

    public ContractVersionInstance(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractVersionInstance(String version, DataObjectInterface document, String filehandler, DataObjectInterface creator, String creation, String fingerprint) throws BackOfficeException{

        this(version, document.getKey(), filehandler, creator.getKey(), creation, fingerprint);
    }


    public ContractVersionInstance(String version, DBKeyInterface document, String filehandler, DBKeyInterface creator, String creation, String fingerprint){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(version);
           data[1] = new ReferenceData(document, columns[1].getTableReference());
           data[2] = new StringData(filehandler);
           data[3] = new ReferenceData(creator, columns[3].getTableReference());
           data[4] = new TimeStampData(creation);
           data[5] = new StringData(fingerprint);

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

    public ContractVersionInstance(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractVersionInstance o = new ContractVersionInstance();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getVersion(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setVersion(String version){

        StringData data = (StringData) this.data[0];
        data.setStringValue(version);
    }



    public DBKeyInterface getDocumentId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public Contract getDocument(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new Contract(new LookupByKey(data.value));
    }

    public void setDocument(DBKeyInterface document){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = document;
    }



    public String getFileHandler(){

        StringData data = (StringData) this.data[2];
        return data.getStringValue();
    }

    public void setFileHandler(String filehandler){

        StringData data = (StringData) this.data[2];
        data.setStringValue(filehandler);
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = creator;
    }



    public DBTimeStamp getCreation()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[4];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setCreation(DBTimeStamp creation){

        TimeStampData data = (TimeStampData) this.data[4];
        data.value = creation.getSQLTime().toString();
    }



    public String getFingerprint(){

        StringData data = (StringData) this.data[5];
        return data.getStringValue();
    }

    public void setFingerprint(String fingerprint){

        StringData data = (StringData) this.data[5];
        data.setStringValue(fingerprint);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



    //TODO: Should be automatic

    public List<ContractFragment> getFragmentsForVersion(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), getKey()));

        //PukkaLogger.log(PukkaLogger.Level.INFO, "dbLookup 1");

        List<DataObjectInterface> objects = new ContractFragmentTable(condition).getValues();

        //PukkaLogger.log(PukkaLogger.Level.INFO, "dbLookup 2");

        List<ContractFragment> fragments = (List<ContractFragment>)(List<?>) objects;

        //PukkaLogger.log(PukkaLogger.Level.INFO, "dbLookup 3");

        return fragments;
    }


    // No condition retrieves all items

    public List<ContractFragment> getFragmentsForVersion(){

        return getFragmentsForVersion(new LookupList());
    }


    public List<Definition> getDefinitionsForVersion(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(DefinitionTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new DefinitionTable(condition).getValues();

        List<Definition> definitions = (List<Definition>)(List<?>) objects;

        return definitions;
    }


    // No condition retrieves all items

    public List<Definition> getDefinitionsForVersion() throws BackOfficeException{

        return getDefinitionsForVersion(new LookupList());
    }

    public List<StructureItem> getStructureItemsForVersion(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(StructureItemTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new StructureItemTable(condition).getValues();

        List<StructureItem> clauses = (List<StructureItem>)(List<?>) objects;

        return clauses;
    }

    public StructureItem[] getStructureItemsForVersionAsArray(ConditionInterface condition) throws BackOfficeException{

        List<StructureItem> structureItemList = getStructureItemsForVersion(condition);
        StructureItem[] structureItems = new StructureItem[structureItemList.size()];
        structureItemList.toArray(structureItems);

        return structureItems;
    }

    // No condition retrieves all items

    public List<StructureItem> getStructureItemsForVersion() throws BackOfficeException{

        return getStructureItemsForVersion(new LookupList());
    }

        //TODO: Should be automatic

    public List<Reference> getReferencesForVersion(ConditionInterface condition) throws BackOfficeException{

        ConditionInterface c = condition.getCopy();

        c.addFilter(new ReferenceFilter(ReferenceTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new ReferenceTable(c).getValues();

        List<Reference> references = (List<Reference>)(List<?>) objects;

        return references;
    }


    // No condition retrieves all items

    public List<Reference> getReferencesForVersion() throws BackOfficeException{

        return getReferencesForVersion(new LookupList());
    }


    /************************************************************
     *
     *          Converting a document in the data base to an abstract document for the analysis
     *
     *
     * @param project
     * @param language
     * @return
     * @throws BackOfficeException
     */


    public document.AbstractDocument createAbstractDocumentVersion(document.AbstractProject project, language.LanguageCode language) throws BackOfficeException{


        List<Definition> definitions = getDefinitionsForVersion();
        List<document.AbstractDefinition> abstractDefinitions = new java.util.ArrayList<document.AbstractDefinition>();
        for(Definition definition : definitions){

            DefinitionType type = DefinitionType.getTypeByName(definition.getType());

            document.AbstractDefinition aDefinition = new document.AbstractDefinition(definition.getName(), (int)definition.getFragmentNo(), type);

            // If there is an actual definition text extracted for this definition, add it

            if(definition.getDefinition() != null && !definition.getDefinition().equals(""))
                aDefinition.withDefinition(definition.getDefinition());

            abstractDefinitions.add(aDefinition);

        }

        List<StructureItem> clauses = getStructureItemsForVersion();
        List<document.AbstractStructureItem> abstractStructureItem = new java.util.ArrayList<document.AbstractStructureItem>();
        List<ContractFragment> fragmentsForVersion = getFragmentsForVersion();

        int i = 0;
        for(StructureItem item : clauses){

            ContractFragment topElement = item.getFragmentForStructureItem(fragmentsForVersion);


            document.AbstractStructureItem aStructureItem =
                    new document.AbstractStructureItem()
                            .setLanguage(language)
                            .setTopElement(new document.AbstractFragment(item.getName())
                                                .setStyle(document.StructureType.TEXT));


            if(topElement.exists())
                aStructureItem.setKey(item.getFragmentForStructureItem(fragmentsForVersion).getKey().toString());


            abstractStructureItem.add(aStructureItem);

        }

        document.AbstractDocument aDocument = new document.AbstractDocument(getDocument().getName(), abstractStructureItem, abstractDefinitions, project, language);
        return aDocument;

    }

    public List<FragmentClassification> getFragmentClassificationsForVersion(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new FragmentClassificationTable(condition).getValues();

        List<FragmentClassification> list = (List<FragmentClassification>)(List<?>) objects;

        return list;
    }


    // No condition retrieves all items

    public List<FragmentClassification> getFragmentClassificationsForVersion(){

        return getFragmentClassificationsForVersion(new LookupList());
    }


    public List<ContractAnnotation> getContractAnnotationsForVersion(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new ContractAnnotationTable(condition).getValues();

        List<ContractAnnotation> list = (List<ContractAnnotation>)(List<?>) objects;

        return list;
    }


    // No condition retrieves all items

    public List<ContractAnnotation> getContractAnnotationsForVersion() throws BackOfficeException{

        return getContractAnnotationsForVersion(new LookupList());
    }

    public List<Action> getActionsForVersion(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ActionTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new ActionTable(condition).getValues();

        List<Action> list = (List<Action>)(List<?>) objects;

        return list;
    }


    // No condition retrieves all items

    public List<Action> getActionsForVersion() throws BackOfficeException{

        return getActionsForVersion(new LookupList());
    }



    public List<RiskClassification> getRiskClassificationsForVersion(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Version.name(), getKey()));

        List<DataObjectInterface> objects = new RiskClassificationTable(condition).getValues();

        List<RiskClassification> list = (List<RiskClassification>)(List<?>) objects;

        return list;
    }


    // No condition retrieves all items

    public List<RiskClassification> getRiskClassificationsForVersion() throws BackOfficeException{

        return getRiskClassificationsForVersion(new LookupList());
    }

    // TODO: Optimize this

    public ContractFragment getFirstFragment() throws BackOfficeException{

        List<ContractFragment> fragmentsForDocument = getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));
        return fragmentsForDocument.get( 0 );

    }

    public String getVersionDescription() {
        String version = getVersion();
        int pos = version.lastIndexOf("_");
        if(pos > 0)
            version = version.substring(pos);
        return version;
    }
}
