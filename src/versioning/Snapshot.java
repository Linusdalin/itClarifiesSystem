package versioning;

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
 *    Snapshot - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Snapshot extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new SnapshotTable();

    public Snapshot(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Snapshot(String name, DataObjectInterface project, String description, DataObjectInterface creator, String timestamp) throws BackOfficeException{

        this(name, project.getKey(), description, creator.getKey(), timestamp);
    }


    public Snapshot(String name, DBKeyInterface project, String description, DBKeyInterface creator, String timestamp){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(project, columns[1].getTableReference());
           data[2] = new TextData(description);
           data[3] = new ReferenceData(creator, columns[3].getTableReference());
           data[4] = new TimeStampData(timestamp);

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

    public Snapshot(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Snapshot o = new Snapshot();
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



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public project.Project getProject(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new project.Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = project;
    }



    public String getDescription(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[2];
        data.setStringValue(description);
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



    public DBTimeStamp getTimestamp()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[4];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setTimestamp(DBTimeStamp timestamp){

        TimeStampData data = (TimeStampData) this.data[4];
        data.value = timestamp.getSQLTime().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
