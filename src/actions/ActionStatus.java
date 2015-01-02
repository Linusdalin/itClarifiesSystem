package actions;

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
 *    ActionStatus - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ActionStatus extends DataObject implements DataObjectInterface{

    private static ActionStatus Open = null;  
    private static ActionStatus InProgress = null;  
    private static ActionStatus Completed = null;  
    private static ActionStatus Blocked = null;  
    private static ActionStatus Cancelled = null;  
    private static ActionStatus Done = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ActionStatusTable();

    public ActionStatus(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ActionStatus(String name, long ordinal, String comment){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new IntData(ordinal);
           data[2] = new TextData(comment);

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

    public ActionStatus(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ActionStatus o = new ActionStatus();
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



    public long getOrdinal(){

        IntData data = (IntData) this.data[1];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[1];
        data.value = ordinal;
    }



    public String getComment(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[2];
        data.setStringValue(comment);
    }



    public static ActionStatus getOpen( ) throws BackOfficeException{

       if(ActionStatus.Open == null)
          ActionStatus.Open = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Open")));
       if(!ActionStatus.Open.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Open is missing (db update required?)");

       return ActionStatus.Open;
    }

    public static ActionStatus getInProgress( ) throws BackOfficeException{

       if(ActionStatus.InProgress == null)
          ActionStatus.InProgress = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "In Progress")));
       if(!ActionStatus.InProgress.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant InProgress is missing (db update required?)");

       return ActionStatus.InProgress;
    }

    public static ActionStatus getCompleted( ) throws BackOfficeException{

       if(ActionStatus.Completed == null)
          ActionStatus.Completed = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Completed")));
       if(!ActionStatus.Completed.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Completed is missing (db update required?)");

       return ActionStatus.Completed;
    }

    public static ActionStatus getBlocked( ) throws BackOfficeException{

       if(ActionStatus.Blocked == null)
          ActionStatus.Blocked = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Blocked")));
       if(!ActionStatus.Blocked.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Blocked is missing (db update required?)");

       return ActionStatus.Blocked;
    }

    public static ActionStatus getCancelled( ) throws BackOfficeException{

       if(ActionStatus.Cancelled == null)
          ActionStatus.Cancelled = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Cancelled")));
       if(!ActionStatus.Cancelled.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Cancelled is missing (db update required?)");

       return ActionStatus.Cancelled;
    }

    public static ActionStatus getDone( ) throws BackOfficeException{

       if(ActionStatus.Done == null)
          ActionStatus.Done = new ActionStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Done")));
       if(!ActionStatus.Done.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Done is missing (db update required?)");

       return ActionStatus.Done;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ActionStatus.Open = null;
        ActionStatus.InProgress = null;
        ActionStatus.Completed = null;
        ActionStatus.Blocked = null;
        ActionStatus.Cancelled = null;
        ActionStatus.Done = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
