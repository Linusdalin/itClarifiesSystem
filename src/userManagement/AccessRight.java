package userManagement;

import risk.*;
import contractManagement.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
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
 *    AccessRight - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class AccessRight extends DataObject implements DataObjectInterface{

    private static AccessRight no = null;  
    private static AccessRight ro = null;  
    private static AccessRight rc = null;  
    private static AccessRight rwd = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new AccessRightTable();

    public AccessRight(){

        super();

        if(table == null)
            table = TABLE;
    }

    public AccessRight(String name, String description){

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

    public AccessRight(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        AccessRight o = new AccessRight();
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



    public static AccessRight getno( ) throws BackOfficeException{

       if(AccessRight.no == null)
          AccessRight.no = new AccessRight(new LookupItem().addFilter(new ColumnFilter("Name", "no")));
       if(!AccessRight.no.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant no is missing (db update required?)");

       return AccessRight.no;
    }

    public static AccessRight getro( ) throws BackOfficeException{

       if(AccessRight.ro == null)
          AccessRight.ro = new AccessRight(new LookupItem().addFilter(new ColumnFilter("Name", "ro")));
       if(!AccessRight.ro.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant ro is missing (db update required?)");

       return AccessRight.ro;
    }

    public static AccessRight getrc( ) throws BackOfficeException{

       if(AccessRight.rc == null)
          AccessRight.rc = new AccessRight(new LookupItem().addFilter(new ColumnFilter("Name", "rc")));
       if(!AccessRight.rc.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant rc is missing (db update required?)");

       return AccessRight.rc;
    }

    public static AccessRight getrwd( ) throws BackOfficeException{

       if(AccessRight.rwd == null)
          AccessRight.rwd = new AccessRight(new LookupItem().addFilter(new ColumnFilter("Name", "rwd")));
       if(!AccessRight.rwd.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant rwd is missing (db update required?)");

       return AccessRight.rwd;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        AccessRight.no = null;
        AccessRight.ro = null;
        AccessRight.rc = null;
        AccessRight.rwd = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
