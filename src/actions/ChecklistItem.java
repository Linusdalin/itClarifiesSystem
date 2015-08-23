package actions;

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
 *    ChecklistItem - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ChecklistItem extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ChecklistItemTable();

    public ChecklistItem(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ChecklistItem(long identifier, long parent, String name, String description, String comment, DataObjectInterface checklist, DataObjectInterface source, DataObjectInterface completion, DataObjectInterface project, String conformancetag, String contexttag, DataObjectInterface status, String completed) throws BackOfficeException{

        this(identifier, parent, name, description, comment, checklist.getKey(), source.getKey(), completion.getKey(), project.getKey(), conformancetag, contexttag, status, completed);
    }


    public ChecklistItem(long identifier, long parent, String name, String description, String comment, DBKeyInterface checklist, DBKeyInterface source, DBKeyInterface completion, DBKeyInterface project, String conformancetag, String contexttag, DataObjectInterface status, String completed){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new IntData(identifier);
           data[1] = new IntData(parent);
           data[2] = new TextData(name);
           data[3] = new TextData(description);
           data[4] = new BlobData(comment);
           data[5] = new ReferenceData(checklist, columns[5].getTableReference());
           data[6] = new ReferenceData(source, columns[6].getTableReference());
           data[7] = new ReferenceData(completion, columns[7].getTableReference());
           data[8] = new ReferenceData(project, columns[8].getTableReference());
           data[9] = new TextData(conformancetag);
           data[10] = new TextData(contexttag);
           data[11] = new ConstantData(status.get__Id(), columns[11].getTableReference());
           data[12] = new DateData(completed);

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

    public ChecklistItem(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ChecklistItem o = new ChecklistItem();
        o.data = data;
        o.exists = true;
        return o;
    }

    public long getIdentifier(){

        IntData data = (IntData) this.data[0];
        return data.value;
    }

    public void setIdentifier(long identifier){

        IntData data = (IntData) this.data[0];
        data.value = identifier;
    }



    public long getParent(){

        IntData data = (IntData) this.data[1];
        return data.value;
    }

    public void setParent(long parent){

        IntData data = (IntData) this.data[1];
        data.value = parent;
    }



    public String getName(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setName(String name){

        TextData data = (TextData) this.data[2];
        data.setStringValue(name);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public String getComment(){

        BlobData data = (BlobData) this.data[4];
        return data.getStringValue();
    }

    public void setComment(String comment){

        BlobData data = (BlobData) this.data[4];
        data.setStringValue(comment);
    }



    public DBKeyInterface getChecklistId(){

        ReferenceData data = (ReferenceData)this.data[5];
        return data.value;
    }

    public Checklist getChecklist(){

        ReferenceData data = (ReferenceData)this.data[5];
        return new Checklist(new LookupByKey(data.value));
    }

    public void setChecklist(DBKeyInterface checklist){

        ReferenceData data = (ReferenceData)this.data[5];
        data.value = checklist;
    }



    public DBKeyInterface getSourceId(){

        ReferenceData data = (ReferenceData)this.data[6];
        return data.value;
    }

    public ContractFragment getSource(){

        ReferenceData data = (ReferenceData)this.data[6];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setSource(DBKeyInterface source){

        ReferenceData data = (ReferenceData)this.data[6];
        data.value = source;
    }



    public DBKeyInterface getCompletionId(){

        ReferenceData data = (ReferenceData)this.data[7];
        return data.value;
    }

    public ContractFragment getCompletion(){

        ReferenceData data = (ReferenceData)this.data[7];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setCompletion(DBKeyInterface completion){

        ReferenceData data = (ReferenceData)this.data[7];
        data.value = completion;
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = project;
    }



    public String getConformanceTag(){

        TextData data = (TextData) this.data[9];
        return data.getStringValue();
    }

    public void setConformanceTag(String conformancetag){

        TextData data = (TextData) this.data[9];
        data.setStringValue(conformancetag);
    }



    public String getContextTag(){

        TextData data = (TextData) this.data[10];
        return data.getStringValue();
    }

    public void setContextTag(String contexttag){

        TextData data = (TextData) this.data[10];
        data.setStringValue(contexttag);
    }



    public ActionStatus getStatus(){

        ConstantData data = (ConstantData)this.data[11];
        return (ActionStatus)(new ActionStatusTable().getConstantValue(data.value));

    }

    public void setStatus(DataObjectInterface status){

        ConstantData data = (ConstantData)this.data[11];
        data.value = status.get__Id();
    }



    public DBTimeStamp getCompleted()throws BackOfficeException{

        DateData data = (DateData) this.data[12];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCompleted(DBTimeStamp completed){

        DateData data = (DateData) this.data[12];
        data.value = completed.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
