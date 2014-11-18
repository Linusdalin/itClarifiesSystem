package contractManagement;

import contractManagement.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    ContractType - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractType extends DataObject implements DataObjectInterface{

    private static ContractType Unclassified = null;  
    private static ContractType Unknown = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ContractTypeTable();

    public ContractType(){

        super();         if(table == null)
            table = TABLE;
    }

    public ContractType(String name, String description) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new TextData(description);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public ContractType(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractType o = new ContractType();
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



    public static ContractType getUnclassified( ) throws BackOfficeException{

       if(ContractType.Unclassified == null)
          ContractType.Unclassified = new ContractType(new LookupItem().addFilter(new ColumnFilter("Name", "Unclassified")));
       if(!ContractType.Unclassified.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Unclassified is missing (db update required?)");

       return ContractType.Unclassified;
     }

    public static ContractType getUnknown( ) throws BackOfficeException{

       if(ContractType.Unknown == null)
          ContractType.Unknown = new ContractType(new LookupItem().addFilter(new ColumnFilter("Name", "Unknown")));
       if(!ContractType.Unknown.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Unknown is missing (db update required?)");

       return ContractType.Unknown;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ContractType.Unclassified = null;
        ContractType.Unknown = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
