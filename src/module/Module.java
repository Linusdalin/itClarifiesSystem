package module;

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
 *    Module - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Module extends DataObject implements DataObjectInterface{

    private static Module Contracting = null;  
    private static Module Risk = null;  
    private static Module Definitions = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ModuleTable();

    public Module(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Module(String name, String description, boolean ispublic){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new TextData(description);
           data[2] = new BoolData(ispublic);

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

    public Module(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Module o = new Module();
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



    public boolean getisPublic(){

        BoolData data = (BoolData) this.data[2];
        return data.value;
    }

    public void setisPublic(boolean ispublic){

        BoolData data = (BoolData) this.data[2];
        data.value = ispublic;
    }



    public static Module getContracting( )  throws BackOfficeException{

       if(Module.Contracting == null)
          Module.Contracting = new Module(new LookupItem().addFilter(new ColumnFilter("Name", "Contracting")));
       if(!Module.Contracting.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Contracting is missing (db update required?)");

       return Module.Contracting;
    }

    public static Module getRisk( )  throws BackOfficeException{

       if(Module.Risk == null)
          Module.Risk = new Module(new LookupItem().addFilter(new ColumnFilter("Name", "Risk")));
       if(!Module.Risk.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Risk is missing (db update required?)");

       return Module.Risk;
    }

    public static Module getDefinitions( )  throws BackOfficeException{

       if(Module.Definitions == null)
          Module.Definitions = new Module(new LookupItem().addFilter(new ColumnFilter("Name", "Definitions")));
       if(!Module.Definitions.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Definitions is missing (db update required?)");

       return Module.Definitions;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        Module.Contracting = null;
        Module.Risk = null;
        Module.Definitions = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
