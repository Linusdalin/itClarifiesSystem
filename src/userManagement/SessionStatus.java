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
 *    SessionStatus - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class SessionStatus extends DataObject implements DataObjectInterface{

    private static SessionStatus open = null;  
    private static SessionStatus closed = null;  
    private static SessionStatus timeout = null;  
    private static SessionStatus failed = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new SessionStatusTable();

    public SessionStatus(){

        super();

        if(table == null)
            table = TABLE;
    }

    public SessionStatus(String name, String description){

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

    public SessionStatus(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        SessionStatus o = new SessionStatus();
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



    public static SessionStatus getopen( ) throws BackOfficeException{

       if(SessionStatus.open == null)
          SessionStatus.open = new SessionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "open")));
       if(!SessionStatus.open.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant open is missing (db update required?)");

       return SessionStatus.open;
    }

    public static SessionStatus getclosed( ) throws BackOfficeException{

       if(SessionStatus.closed == null)
          SessionStatus.closed = new SessionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "closed")));
       if(!SessionStatus.closed.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant closed is missing (db update required?)");

       return SessionStatus.closed;
    }

    public static SessionStatus gettimeout( ) throws BackOfficeException{

       if(SessionStatus.timeout == null)
          SessionStatus.timeout = new SessionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "timeout")));
       if(!SessionStatus.timeout.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant timeout is missing (db update required?)");

       return SessionStatus.timeout;
    }

    public static SessionStatus getfailed( ) throws BackOfficeException{

       if(SessionStatus.failed == null)
          SessionStatus.failed = new SessionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "failed")));
       if(!SessionStatus.failed.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant failed is missing (db update required?)");

       return SessionStatus.failed;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        SessionStatus.open = null;
        SessionStatus.closed = null;
        SessionStatus.timeout = null;
        SessionStatus.failed = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
