package overviewExport;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import project.*;
import versioning.*;
import actions.*;
import overviewExport.*;
import module.*;
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
 *    Extraction - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Extraction extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ExtractionTable();

    public Extraction(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Extraction(String name, String classification, String text, String fragmentkey, long fragmentordinal, long extractionnumber, String style, DataObjectInterface project, DataObjectInterface document, String risk, String description, String comment, String sheet, DataObjectInterface extractionrun) throws BackOfficeException{

        this(name, classification, text, fragmentkey, fragmentordinal, extractionnumber, style, project.getKey(), document.getKey(), risk, description, comment, sheet, extractionrun.getKey());
    }


    public Extraction(String name, String classification, String text, String fragmentkey, long fragmentordinal, long extractionnumber, String style, DBKeyInterface project, DBKeyInterface document, String risk, String description, String comment, String sheet, DBKeyInterface extractionrun){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new StringData(classification);
           data[2] = new BlobData(text);
           data[3] = new StringData(fragmentkey);
           data[4] = new IntData(fragmentordinal);
           data[5] = new IntData(extractionnumber);
           data[6] = new StringData(style);
           data[7] = new ReferenceData(project, columns[7].getTableReference());
           data[8] = new ReferenceData(document, columns[8].getTableReference());
           data[9] = new StringData(risk);
           data[10] = new TextData(description);
           data[11] = new TextData(comment);
           data[12] = new StringData(sheet);
           data[13] = new ReferenceData(extractionrun, columns[13].getTableReference());

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

    public Extraction(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Extraction o = new Extraction();
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



    public String getClassification(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setClassification(String classification){

        StringData data = (StringData) this.data[1];
        data.setStringValue(classification);
    }



    public String getText(){

        BlobData data = (BlobData) this.data[2];
        return data.getStringValue();
    }

    public void setText(String text){

        BlobData data = (BlobData) this.data[2];
        data.setStringValue(text);
    }



    public String getFragmentKey(){

        StringData data = (StringData) this.data[3];
        return data.getStringValue();
    }

    public void setFragmentKey(String fragmentkey){

        StringData data = (StringData) this.data[3];
        data.setStringValue(fragmentkey);
    }



    public long getFragmentOrdinal(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setFragmentOrdinal(long fragmentordinal){

        IntData data = (IntData) this.data[4];
        data.value = fragmentordinal;
    }



    public long getExtractionNumber(){

        IntData data = (IntData) this.data[5];
        return data.value;
    }

    public void setExtractionNumber(long extractionnumber){

        IntData data = (IntData) this.data[5];
        data.value = extractionnumber;
    }



    public String getStyle(){

        StringData data = (StringData) this.data[6];
        return data.getStringValue();
    }

    public void setStyle(String style){

        StringData data = (StringData) this.data[6];
        data.setStringValue(style);
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



    public DBKeyInterface getDocumentId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public Contract getDocument(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new Contract(new LookupByKey(data.value));
    }

    public void setDocument(DBKeyInterface document){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = document;
    }



    public String getRisk(){

        StringData data = (StringData) this.data[9];
        return data.getStringValue();
    }

    public void setRisk(String risk){

        StringData data = (StringData) this.data[9];
        data.setStringValue(risk);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[10];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[10];
        data.setStringValue(description);
    }



    public String getComment(){

        TextData data = (TextData) this.data[11];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[11];
        data.setStringValue(comment);
    }



    public String getSheet(){

        StringData data = (StringData) this.data[12];
        return data.getStringValue();
    }

    public void setSheet(String sheet){

        StringData data = (StringData) this.data[12];
        data.setStringValue(sheet);
    }



    public DBKeyInterface getExtractionRunId(){

        ReferenceData data = (ReferenceData)this.data[13];
        return data.value;
    }

    public ExtractionStatus getExtractionRun(){

        ReferenceData data = (ReferenceData)this.data[13];
        return new ExtractionStatus(new LookupByKey(data.value));
    }

    public void setExtractionRun(DBKeyInterface extractionrun){

        ReferenceData data = (ReferenceData)this.data[13];
        data.value = extractionrun;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    // TODO: Improvement: indentations for headlines not implemented

    public void asHeadline(int indentation) {

        setStyle("Heading");
    }

    public String toString(){

        return this.getSheet() + ": " + this.getText();
    }

    public boolean isDefinition() {

        return getClassification().equals("#Definition");
    }

    public void asTitle() {

        setStyle("Title");
    }
}
