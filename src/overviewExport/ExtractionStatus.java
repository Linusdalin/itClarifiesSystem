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
 *    ExtractionStatus - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ExtractionStatus extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ExtractionStatusTable();

    public ExtractionStatus(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ExtractionStatus(String name, String date, DataObjectInterface user, DataObjectInterface project, String comment, DataObjectInterface status, String description, String tags) throws BackOfficeException{

        this(name, date, user.getKey(), project.getKey(), comment, status, description, tags);
    }


    public ExtractionStatus(String name, String date, DBKeyInterface user, DBKeyInterface project, String comment, DataObjectInterface status, String description, String tags){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new DateData(date);
           data[2] = new ReferenceData(user, columns[2].getTableReference());
           data[3] = new ReferenceData(project, columns[3].getTableReference());
           data[4] = new TextData(comment);
           data[5] = new ConstantData(status.get__Id(), columns[5].getTableReference());
           data[6] = new TextData(description);
           data[7] = new TextData(tags);

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

    public ExtractionStatus(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ExtractionStatus o = new ExtractionStatus();
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



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[1];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[1];
        data.value = date.getISODate().toString();
    }



    public DBKeyInterface getUserId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public PortalUser getUser(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setUser(DBKeyInterface user){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = user;
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



    public String getComment(){

        TextData data = (TextData) this.data[4];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[4];
        data.setStringValue(comment);
    }



    public ExtractionState getStatus(){

        ConstantData data = (ConstantData)this.data[5];
        return (ExtractionState)(new ExtractionStateTable().getConstantValue(data.value));

    }

    public void setStatus(DataObjectInterface status){

        ConstantData data = (ConstantData)this.data[5];
        data.value = status.get__Id();
    }



    public String getDescription(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[6];
        data.setStringValue(description);
    }



    public String getTags(){

        TextData data = (TextData) this.data[7];
        return data.getStringValue();
    }

    public void setTags(String tags){

        TextData data = (TextData) this.data[7];
        data.setStringValue(tags);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
