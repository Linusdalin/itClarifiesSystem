package crossReference;

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
 *    ReferenceType - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ReferenceType extends DataObject implements DataObjectInterface{

    private static ReferenceType Unknown = null;  
    private static ReferenceType Explicit = null;  
    private static ReferenceType Implicit = null;  
    private static ReferenceType DefinitionUsage = null;  
    private static ReferenceType Open = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ReferenceTypeTable();

    public ReferenceType(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ReferenceType(long id, String name, String description){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new IntData(id);
           data[1] = new StringData(name);
           data[2] = new TextData(description);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ReferenceType o = new ReferenceType();
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



    public String getDescription(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[2];
        data.setStringValue(description);
    }



    public static ReferenceType getUnknown( )  {

       return (ReferenceType)ReferenceTypeTable.Values.get(0);
    }

    public static ReferenceType getExplicit( )  {

       return (ReferenceType)ReferenceTypeTable.Values.get(1);
    }

    public static ReferenceType getImplicit( )  {

       return (ReferenceType)ReferenceTypeTable.Values.get(2);
    }

    public static ReferenceType getDefinitionUsage( )  {

       return (ReferenceType)ReferenceTypeTable.Values.get(3);
    }

    public static ReferenceType getOpen( )  {

       return (ReferenceType)ReferenceTypeTable.Values.get(4);
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
