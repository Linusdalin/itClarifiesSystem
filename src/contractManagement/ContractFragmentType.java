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
 *    ContractFragmentType - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractFragmentType extends DataObject implements DataObjectInterface{

    private static ContractFragmentType Text = null;  
    private static ContractFragmentType Headline = null;  
    private static ContractFragmentType Sub = null;  
    private static ContractFragmentType BList = null;  
    private static ContractFragmentType IList = null;  
    private static ContractFragmentType NList = null;  
    private static ContractFragmentType DocTitle = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ContractFragmentTypeTable();

    public ContractFragmentType(){

        super();         if(table == null)
            table = TABLE;
    }

    public ContractFragmentType(String name, String description) throws BackOfficeException{

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

    public ContractFragmentType(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractFragmentType o = new ContractFragmentType();
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



    public static ContractFragmentType getText( ) throws BackOfficeException{

       if(ContractFragmentType.Text == null)
          ContractFragmentType.Text = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "Text")));
       if(!ContractFragmentType.Text.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Text is missing (db update required?)");

       return ContractFragmentType.Text;
     }

    public static ContractFragmentType getHeadline( ) throws BackOfficeException{

       if(ContractFragmentType.Headline == null)
          ContractFragmentType.Headline = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "Headline")));
       if(!ContractFragmentType.Headline.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Headline is missing (db update required?)");

       return ContractFragmentType.Headline;
     }

    public static ContractFragmentType getSub( ) throws BackOfficeException{

       if(ContractFragmentType.Sub == null)
          ContractFragmentType.Sub = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "Subsection")));
       if(!ContractFragmentType.Sub.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Sub is missing (db update required?)");

       return ContractFragmentType.Sub;
     }

    public static ContractFragmentType getBList( ) throws BackOfficeException{

       if(ContractFragmentType.BList == null)
          ContractFragmentType.BList = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "List Item")));
       if(!ContractFragmentType.BList.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant BList is missing (db update required?)");

       return ContractFragmentType.BList;
     }

    public static ContractFragmentType getIList( ) throws BackOfficeException{

       if(ContractFragmentType.IList == null)
          ContractFragmentType.IList = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "Count Item")));
       if(!ContractFragmentType.IList.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant IList is missing (db update required?)");

       return ContractFragmentType.IList;
     }

    public static ContractFragmentType getNList( ) throws BackOfficeException{

       if(ContractFragmentType.NList == null)
          ContractFragmentType.NList = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "Number List")));
       if(!ContractFragmentType.NList.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant NList is missing (db update required?)");

       return ContractFragmentType.NList;
     }

    public static ContractFragmentType getDocTitle( ) throws BackOfficeException{

       if(ContractFragmentType.DocTitle == null)
          ContractFragmentType.DocTitle = new ContractFragmentType(new LookupItem().addFilter(new ColumnFilter("Name", "DocTitle")));
       if(!ContractFragmentType.DocTitle.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant DocTitle is missing (db update required?)");

       return ContractFragmentType.DocTitle;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ContractFragmentType.Text = null;
        ContractFragmentType.Headline = null;
        ContractFragmentType.Sub = null;
        ContractFragmentType.BList = null;
        ContractFragmentType.IList = null;
        ContractFragmentType.NList = null;
        ContractFragmentType.DocTitle = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




}
