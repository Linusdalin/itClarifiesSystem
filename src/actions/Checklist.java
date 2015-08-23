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
 *    Checklist - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Checklist extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ChecklistTable();

    public Checklist(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Checklist(String name, String description, String id, DataObjectInterface project, DataObjectInterface owner, String created) throws BackOfficeException{

        this(name, description, id, project.getKey(), owner.getKey(), created);
    }


    public Checklist(String name, String description, String id, DBKeyInterface project, DBKeyInterface owner, String created){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new TextData(description);
           data[2] = new TextData(id);
           data[3] = new ReferenceData(project, columns[3].getTableReference());
           data[4] = new ReferenceData(owner, columns[4].getTableReference());
           data[5] = new DateData(created);

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

    public Checklist(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Checklist o = new Checklist();
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



    public String getId(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setId(String id){

        TextData data = (TextData) this.data[2];
        data.setStringValue(id);
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = project;
    }



    public DBKeyInterface getOwnerId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public PortalUser getOwner(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setOwner(DBKeyInterface owner){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = owner;
    }



    public DBTimeStamp getCreated()throws BackOfficeException{

        DateData data = (DateData) this.data[5];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCreated(DBTimeStamp created){

        DateData data = (DateData) this.data[5];
        data.value = created.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



    public List<ChecklistItem> getChecklistItemsForChecklist(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(ChecklistItemTable.Columns.Checklist.name(), getKey()));

        List<DataObjectInterface> objects = new ChecklistItemTable(condition).getValues();

        List<ChecklistItem> list = (List<ChecklistItem>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<ChecklistItem> getChecklistItemsForChecklist(){

        return getChecklistItemsForChecklist(new LookupList());
    }


    /************************************************************************
     *
     *          Recursively delete all
     *
     * @return
     * @throws BackOfficeException
     */

    public DocumentDeleteOutcome recursivelyDelete() throws BackOfficeException {

        DocumentDeleteOutcome outcome = new DocumentDeleteOutcome();

        ChecklistItemTable itemsInChecklist = new ChecklistItemTable(new LookupList()
                    .addFilter(new ReferenceFilter(ChecklistItemTable.Columns.Checklist.name(), getKey())));

        outcome.checklistItems = itemsInChecklist.getCount();
        itemsInChecklist.delete();

        new ChecklistTable().deleteItem(this);

        outcome.checklists = 1;

        return new DocumentDeleteOutcome();

    }
}
