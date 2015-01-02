package contractManagement;

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
 *    ContractStatus - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractStatus extends DataObject implements DataObjectInterface{

    private static ContractStatus Uploaded = null;  
    private static ContractStatus Analysing = null;  
    private static ContractStatus Analysed = null;  
    private static ContractStatus Failed = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ContractStatusTable();

    public ContractStatus(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractStatus(String name, String description){

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

    public ContractStatus(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractStatus o = new ContractStatus();
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



    public static ContractStatus getUploaded( ) throws BackOfficeException{

       if(ContractStatus.Uploaded == null)
          ContractStatus.Uploaded = new ContractStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Uploaded")));
       if(!ContractStatus.Uploaded.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Uploaded is missing (db update required?)");

       return ContractStatus.Uploaded;
    }

    public static ContractStatus getAnalysing( ) throws BackOfficeException{

       if(ContractStatus.Analysing == null)
          ContractStatus.Analysing = new ContractStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Analysing")));
       if(!ContractStatus.Analysing.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Analysing is missing (db update required?)");

       return ContractStatus.Analysing;
    }

    public static ContractStatus getAnalysed( ) throws BackOfficeException{

       if(ContractStatus.Analysed == null)
          ContractStatus.Analysed = new ContractStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Analysed")));
       if(!ContractStatus.Analysed.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Analysed is missing (db update required?)");

       return ContractStatus.Analysed;
    }

    public static ContractStatus getFailed( ) throws BackOfficeException{

       if(ContractStatus.Failed == null)
          ContractStatus.Failed = new ContractStatus(new LookupItem().addFilter(new ColumnFilter("Name", "Failed")));
       if(!ContractStatus.Failed.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Failed is missing (db update required?)");

       return ContractStatus.Failed;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ContractStatus.Uploaded = null;
        ContractStatus.Analysing = null;
        ContractStatus.Analysed = null;
        ContractStatus.Failed = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
