package contractManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
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
 *    StructureItemType - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class StructureItemType extends DataObject implements DataObjectInterface{

    private static StructureItemType Headline = null;  
    private static StructureItemType List = null;  
    private static StructureItemType ListItem = null;  
    private static StructureItemType FragmentGroup = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new StructureItemTypeTable();

    public StructureItemType(){

        super();

        if(table == null)
            table = TABLE;
    }

    public StructureItemType(long id, String name, String description){

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

        StructureItemType o = new StructureItemType();
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



    public static StructureItemType getHeadline( )  {

       return (StructureItemType)StructureItemTypeTable.Values.get(0);
    }

    public static StructureItemType getList( )  {

       return (StructureItemType)StructureItemTypeTable.Values.get(1);
    }

    public static StructureItemType getListItem( )  {

       return (StructureItemType)StructureItemTypeTable.Values.get(2);
    }

    public static StructureItemType getFragmentGroup( )  {

       return (StructureItemType)StructureItemTypeTable.Values.get(3);
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
