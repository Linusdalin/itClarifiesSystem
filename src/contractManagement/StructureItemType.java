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

        super();         if(table == null)
            table = TABLE;
    }

    public StructureItemType(String name, String description) throws BackOfficeException{

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

    public StructureItemType(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        StructureItemType o = new StructureItemType();
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



    public static StructureItemType getHeadline( ) throws BackOfficeException{

       if(StructureItemType.Headline == null)
          StructureItemType.Headline = new StructureItemType(new LookupItem().addFilter(new ColumnFilter("Name", "Headline")));
       if(!StructureItemType.Headline.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Headline is missing (db update required?)");

       return StructureItemType.Headline;
     }

    public static StructureItemType getList( ) throws BackOfficeException{

       if(StructureItemType.List == null)
          StructureItemType.List = new StructureItemType(new LookupItem().addFilter(new ColumnFilter("Name", "List")));
       if(!StructureItemType.List.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant List is missing (db update required?)");

       return StructureItemType.List;
     }

    public static StructureItemType getListItem( ) throws BackOfficeException{

       if(StructureItemType.ListItem == null)
          StructureItemType.ListItem = new StructureItemType(new LookupItem().addFilter(new ColumnFilter("Name", "List Item")));
       if(!StructureItemType.ListItem.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant ListItem is missing (db update required?)");

       return StructureItemType.ListItem;
     }

    public static StructureItemType getFragmentGroup( ) throws BackOfficeException{

       if(StructureItemType.FragmentGroup == null)
          StructureItemType.FragmentGroup = new StructureItemType(new LookupItem().addFilter(new ColumnFilter("Name", "FragmentGroup")));
       if(!StructureItemType.FragmentGroup.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant FragmentGroup is missing (db update required?)");

       return StructureItemType.FragmentGroup;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        StructureItemType.Headline = null;
        StructureItemType.List = null;
        StructureItemType.ListItem = null;
        StructureItemType.FragmentGroup = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
