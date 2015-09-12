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
 *    ExtractionState - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ExtractionState extends DataObject implements DataObjectInterface{

    private static ExtractionState Empty = null;  
    private static ExtractionState Generating = null;  
    private static ExtractionState Dirty = null;  
    private static ExtractionState Ready = null;  
    private static ExtractionState Failed = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ExtractionStateTable();

    public ExtractionState(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ExtractionState(long id, String name, String comment){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new IntData(id);
           data[1] = new StringData(name);
           data[2] = new TextData(comment);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ExtractionState o = new ExtractionState();
        o.data = data;
        o.exists = true;
        return o;
    }

    public long getId(){

        IntData data = (IntData) this.data[0];
        return data.value;
    }

    public void setId(long id){

        IntData data = (IntData) this.data[0];
        data.value = id;
    }



    public String getName(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[1];
        data.setStringValue(name);
    }



    public String getComment(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[2];
        data.setStringValue(comment);
    }



    public static ExtractionState getEmpty( )  {

       return (ExtractionState)ExtractionStateTable.Values.get(0);
    }

    public static ExtractionState getGenerating( )  {

       return (ExtractionState)ExtractionStateTable.Values.get(1);
    }

    public static ExtractionState getDirty( )  {

       return (ExtractionState)ExtractionStateTable.Values.get(2);
    }

    public static ExtractionState getReady( )  {

       return (ExtractionState)ExtractionStateTable.Values.get(3);
    }

    public static ExtractionState getFailed( )  {

       return (ExtractionState)ExtractionStateTable.Values.get(4);
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}