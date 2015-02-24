package contractManagement;

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
 *    ContractFragment - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractFragment extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractFragmentTable();

    public ContractFragment(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractFragment(String name, DataObjectInterface version, DataObjectInterface project, long structureno, long ordinal, String text, long indentation, String type, DataObjectInterface risk, long annotationcount, long referencecount, long classificatoncount, long actioncount, long xpos, long ypos, long width, String display) throws BackOfficeException{

        this(name, version.getKey(), project.getKey(), structureno, ordinal, text, indentation, type, risk, annotationcount, referencecount, classificatoncount, actioncount, xpos, ypos, width, display);
    }


    public ContractFragment(String name, DBKeyInterface version, DBKeyInterface project, long structureno, long ordinal, String text, long indentation, String type, DataObjectInterface risk, long annotationcount, long referencecount, long classificatoncount, long actioncount, long xpos, long ypos, long width, String display){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(version, columns[1].getTableReference());
           data[2] = new ReferenceData(project, columns[2].getTableReference());
           data[3] = new IntData(structureno);
           data[4] = new IntData(ordinal);
           data[5] = new BlobData(text);
           data[6] = new IntData(indentation);
           data[7] = new StringData(type);
           data[8] = new ConstantData(risk.get__Id(), columns[8].getTableReference());
           data[9] = new IntData(annotationcount);
           data[10] = new IntData(referencecount);
           data[11] = new IntData(classificatoncount);
           data[12] = new IntData(actioncount);
           data[13] = new IntData(xpos);
           data[14] = new IntData(ypos);
           data[15] = new IntData(width);
           data[16] = new StringData(display);

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

    public ContractFragment(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractFragment o = new ContractFragment();
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



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = version;
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = project;
    }



    public long getStructureNo(){

        IntData data = (IntData) this.data[3];
        return data.value;
    }

    public void setStructureNo(long structureno){

        IntData data = (IntData) this.data[3];
        data.value = structureno;
    }



    public long getOrdinal(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[4];
        data.value = ordinal;
    }



    public String getText(){

        BlobData data = (BlobData) this.data[5];
        return data.getStringValue();
    }

    public void setText(String text){

        BlobData data = (BlobData) this.data[5];
        data.setStringValue(text);
    }



    public long getIndentation(){

        IntData data = (IntData) this.data[6];
        return data.value;
    }

    public void setIndentation(long indentation){

        IntData data = (IntData) this.data[6];
        data.value = indentation;
    }



    public String getType(){

        StringData data = (StringData) this.data[7];
        return data.getStringValue();
    }

    public void setType(String type){

        StringData data = (StringData) this.data[7];
        data.setStringValue(type);
    }



    public risk.ContractRisk getRisk(){

        ConstantData data = (ConstantData)this.data[8];
        return (risk.ContractRisk)(new risk.ContractRiskTable().getConstantValue(data.value));

    }

    public void setRisk(DataObjectInterface risk){

        ConstantData data = (ConstantData)this.data[8];
        data.value = risk.get__Id();
    }



    public long getAnnotationCount(){

        IntData data = (IntData) this.data[9];
        return data.value;
    }

    public void setAnnotationCount(long annotationcount){

        IntData data = (IntData) this.data[9];
        data.value = annotationcount;
    }



    public long getReferenceCount(){

        IntData data = (IntData) this.data[10];
        return data.value;
    }

    public void setReferenceCount(long referencecount){

        IntData data = (IntData) this.data[10];
        data.value = referencecount;
    }



    public long getClassificatonCount(){

        IntData data = (IntData) this.data[11];
        return data.value;
    }

    public void setClassificatonCount(long classificatoncount){

        IntData data = (IntData) this.data[11];
        data.value = classificatoncount;
    }



    public long getActionCount(){

        IntData data = (IntData) this.data[12];
        return data.value;
    }

    public void setActionCount(long actioncount){

        IntData data = (IntData) this.data[12];
        data.value = actioncount;
    }



    public long getxPos(){

        IntData data = (IntData) this.data[13];
        return data.value;
    }

    public void setxPos(long xpos){

        IntData data = (IntData) this.data[13];
        data.value = xpos;
    }



    public long getyPos(){

        IntData data = (IntData) this.data[14];
        return data.value;
    }

    public void setyPos(long ypos){

        IntData data = (IntData) this.data[14];
        data.value = ypos;
    }



    public long getwidth(){

        IntData data = (IntData) this.data[15];
        return data.value;
    }

    public void setwidth(long width){

        IntData data = (IntData) this.data[15];
        data.value = width;
    }



    public String getdisplay(){

        StringData data = (StringData) this.data[16];
        return data.getStringValue();
    }

    public void setdisplay(String display){

        StringData data = (StringData) this.data[16];
        data.setStringValue(display);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    // Store the keywords for the classification in memory.
    // These will be used fo the indexing only.

    public String keywordString = "";


    /*********************************************************************************'
     *
     *              Reverse lookup of incoming reference from table FragmentClassification
     *
     *              //TODO: This should be automatically generated
     *
     * @param condition
     * @return
     * @throws BackOfficeException
     */

    public List<FragmentClassification> getClassificationsForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Fragment.name(), getKey()));

        List<DataObjectInterface> objects = new FragmentClassificationTable(condition).getValues();

        List<FragmentClassification> classifications = (List<FragmentClassification>)(List<?>) objects;

        return classifications;
    }


    // No condition retrieves all items

    public List<RiskClassification> getRiskClassificationsForFragment() throws BackOfficeException{

        return getRiskClassificationsForFragment(new LookupList());
    }


    public List<RiskClassification> getRiskClassificationsForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Fragment.name(), getKey()));

        List<DataObjectInterface> objects = new RiskClassificationTable(condition).getValues();

        List<RiskClassification> classifications = (List<RiskClassification>)(List<?>) objects;

        return classifications;
    }

    public RiskClassification getFirstRiskClassificationForFragment() throws BackOfficeException{

        List<RiskClassification> classifications = getRiskClassificationsForFragment(new LookupList(new Sorting(RiskClassificationTable.Columns.Time.name(), Ordering.FIRST)));
        if(classifications.size() == 0)
            throw new BackOfficeException(BackOfficeException.AccessError, "No RiskClassifications available for Fragment " + this.getKey());

        return classifications.get(0);
    }

    public RiskClassification getLastRiskClassificationForFragment(String orderColumn) throws BackOfficeException{

        List<RiskClassification> classifications = getRiskClassificationsForFragment(new LookupList(new Sorting(orderColumn, Ordering.LAST)));
        if(classifications.size() == 0)
            throw new BackOfficeException(BackOfficeException.AccessError, "No RiskClassifications available for Fragment " + this.getKey());

        return classifications.get(0);
    }

    // No condition retrieves all items

    public List<FragmentClassification> getClassificationsForFragment() throws BackOfficeException{

        return getClassificationsForFragment(new LookupList());
    }


    //TODO: Should be automatic

    public List<ContractAnnotation> getAnnotationsForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), getKey()));

        List<DataObjectInterface> objects = new ContractAnnotationTable(condition).getValues();

        List<ContractAnnotation> annotations = (List<ContractAnnotation>)(List<?>) objects;

        return annotations;
    }




    // No condition retrieves all items

    public List<ContractAnnotation> getAnnotationsForFragment() throws BackOfficeException{

        return getAnnotationsForFragment(new LookupList());
    }



    //TODO: Should be automatic

    public List<Action> getActionsForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ActionTable.Columns.Fragment.name(), getKey()));

        List<DataObjectInterface> objects = new ActionTable(condition).getValues();

        List<Action> actions = (List<Action>)(List<?>) objects;

        return actions;
    }




    // No condition retrieves all items

    public List<Action> getActionsForFragment() throws BackOfficeException{

        return getActionsForFragment(new LookupList());
    }




    public List<Definition> getDefinitionsForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(DefinitionTable.Columns.DefinedIn.name(), getKey()));

        List<DataObjectInterface> objects = new DefinitionTable(condition).getValues();

        List<Definition> definitions = (List<Definition>)(List<?>) objects;

        return definitions;
    }




    // No condition retrieves all items

    public List<Definition> getDefinitionsForFragment() throws BackOfficeException{

        return getDefinitionsForFragment(new LookupList());
    }

    public List<Reference> getReferencesForFragment(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ReferenceTable.Columns.From.name(), getKey()));

        List<DataObjectInterface> objects = new ReferenceTable(condition).getValues();

        List<Reference> references = (List<Reference>)(List<?>) objects;

        return references;
    }




    // No condition retrieves all items

    public List<Reference> getReferencesForFragment() throws BackOfficeException{

        return getReferencesForFragment(new LookupList());
    }

    public StructureItem getStructureItem() throws BackOfficeException{

        StructureItem item = new StructureItem(new LookupItem()
                .addFilter(new ReferenceFilter(StructureItemTable.Columns.Version.name(), this.getVersionId() ))
                .addFilter(new ColumnFilter(StructureItemTable.Columns.Ordinal.name(), this.getStructureNo() )));

        return item;
    }

    public StructureItem getStructureItem(List<StructureItem> headlines) throws BackOfficeException {

        for(StructureItem item : headlines){
            if(item.getOrdinal() == this.getStructureNo())
                return item;
        }

        return null;

    }

    public List<ContractFragment> getChildren() {

        List<ContractFragment> children = new java.util.ArrayList<ContractFragment>();

        try {
            StructureItem headline = getStructureItem();

            if(!headline.exists()){

                // There is no headline, which it should

                PukkaLogger.log(PukkaLogger.Level.WARNING, "There is no structure item parent for fragment " + this.getName() + " in document " + this.getVersion().getDocument().getName());


            }
            else{

                if(headline.getTopElement() == getOrdinal() ){


                    ContractFragmentTable table = headline.getChildrenUnderStructureItem();
                    for(DataObjectInterface o : table.getValues()){

                        children.add((ContractFragment)o);
                    }

                }

            }
        } catch (BackOfficeException e) {

            PukkaLogger.log(e, "Error getting structure item for fragment \"" + getDescription() + "\" in document " + this.getVersion().getDocument().getName());
        }
        return children;


    }

    /*******************************************************************************
     *
     *          This is used to pass to the analysis. Not all elements are used
     *
     *
     * @return
     */


    public document.CellInfo getCellInfo() {

        document.TableSpan tableSpan = new document.TableSpan( 1, 1 );  //TODO: Table span not implemented

        document.CellInfo cellInfo = new document.CellInfo((int)this.getxPos(), (int)this.getyPos(), "#FFFFFF", 0, 0, tableSpan , 1, true, (int)this.getwidth());
        return cellInfo;
    }
}
