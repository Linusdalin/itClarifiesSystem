package userManagement;

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
 *    Visibility - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Visibility extends DataObject implements DataObjectInterface{

    private static Visibility Private = null;  
    private static Visibility Org = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new VisibilityTable();

    public Visibility(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Visibility(String name, String description){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new TextData(description);

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

    public Visibility(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Visibility o = new Visibility();
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



    public static Visibility getPrivate( )  throws BackOfficeException{

       if(Visibility.Private == null)
          Visibility.Private = new Visibility(new LookupItem().addFilter(new ColumnFilter("Name", "Private")));
       if(!Visibility.Private.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Private is missing (db update required?)");

       return Visibility.Private;
    }

    public static Visibility getOrg( )  throws BackOfficeException{

       if(Visibility.Org == null)
          Visibility.Org = new Visibility(new LookupItem().addFilter(new ColumnFilter("Name", "Organization")));
       if(!Visibility.Org.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Org is missing (db update required?)");

       return Visibility.Org;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        Visibility.Private = null;
        Visibility.Org = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
