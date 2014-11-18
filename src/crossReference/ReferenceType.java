package crossReference;

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

        super();         if(table == null)
            table = TABLE;
    }

    public ReferenceType(String name, String description) throws BackOfficeException{

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

    public ReferenceType(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ReferenceType o = new ReferenceType();
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



    public static ReferenceType getUnknown( ) throws BackOfficeException{

       if(ReferenceType.Unknown == null)
          ReferenceType.Unknown = new ReferenceType(new LookupItem().addFilter(new ColumnFilter("Name", "Unknown")));
       if(!ReferenceType.Unknown.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Unknown is missing (db update required?)");

       return ReferenceType.Unknown;
     }

    public static ReferenceType getExplicit( ) throws BackOfficeException{

       if(ReferenceType.Explicit == null)
          ReferenceType.Explicit = new ReferenceType(new LookupItem().addFilter(new ColumnFilter("Name", "Explicit")));
       if(!ReferenceType.Explicit.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Explicit is missing (db update required?)");

       return ReferenceType.Explicit;
     }

    public static ReferenceType getImplicit( ) throws BackOfficeException{

       if(ReferenceType.Implicit == null)
          ReferenceType.Implicit = new ReferenceType(new LookupItem().addFilter(new ColumnFilter("Name", "Implicit")));
       if(!ReferenceType.Implicit.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Implicit is missing (db update required?)");

       return ReferenceType.Implicit;
     }

    public static ReferenceType getDefinitionUsage( ) throws BackOfficeException{

       if(ReferenceType.DefinitionUsage == null)
          ReferenceType.DefinitionUsage = new ReferenceType(new LookupItem().addFilter(new ColumnFilter("Name", "Definition Usage")));
       if(!ReferenceType.DefinitionUsage.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant DefinitionUsage is missing (db update required?)");

       return ReferenceType.DefinitionUsage;
     }

    public static ReferenceType getOpen( ) throws BackOfficeException{

       if(ReferenceType.Open == null)
          ReferenceType.Open = new ReferenceType(new LookupItem().addFilter(new ColumnFilter("Name", "Open")));
       if(!ReferenceType.Open.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Open is missing (db update required?)");

       return ReferenceType.Open;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ReferenceType.Unknown = null;
        ReferenceType.Explicit = null;
        ReferenceType.Implicit = null;
        ReferenceType.DefinitionUsage = null;
        ReferenceType.Open = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
