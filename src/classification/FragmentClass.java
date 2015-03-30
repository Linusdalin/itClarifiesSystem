package classification;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
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
 *    FragmentClass - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class FragmentClass extends DataObject implements DataObjectInterface{

    private static FragmentClass Req1 = null;  
    private static FragmentClass Req2 = null;  
    private static FragmentClass Req3 = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new FragmentClassTable();

    public FragmentClass(){

        super();

        if(table == null)
            table = TABLE;
    }

    public FragmentClass(String name, String type, String keywords, String description, DataObjectInterface organization) throws BackOfficeException{

        this(name, type, keywords, description, organization.getKey());
    }


    public FragmentClass(String name, String type, String keywords, String description, DBKeyInterface organization){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new StringData(type);
           data[2] = new TextData(keywords);
           data[3] = new TextData(description);
           data[4] = new ReferenceData(organization, columns[4].getTableReference());

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

    public FragmentClass(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        FragmentClass o = new FragmentClass();
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



    public String getType(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setType(String type){

        StringData data = (StringData) this.data[1];
        data.setStringValue(type);
    }



    public String getKeywords(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setKeywords(String keywords){

        TextData data = (TextData) this.data[2];
        data.setStringValue(keywords);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public DBKeyInterface getOrganizationId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public Organization getOrganization(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new Organization(new LookupByKey(data.value));
    }

    public void setOrganization(DBKeyInterface organization){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = organization;
    }



    public static FragmentClass getReq1( )  throws BackOfficeException{

       if(FragmentClass.Req1 == null)
          FragmentClass.Req1 = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Must")));
       if(!FragmentClass.Req1.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Req1 is missing (db update required?)");

       return FragmentClass.Req1;
    }

    public static FragmentClass getReq2( )  throws BackOfficeException{

       if(FragmentClass.Req2 == null)
          FragmentClass.Req2 = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Should")));
       if(!FragmentClass.Req2.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Req2 is missing (db update required?)");

       return FragmentClass.Req2;
    }

    public static FragmentClass getReq3( )  throws BackOfficeException{

       if(FragmentClass.Req3 == null)
          FragmentClass.Req3 = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Optional")));
       if(!FragmentClass.Req3.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Req3 is missing (db update required?)");

       return FragmentClass.Req3;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        FragmentClass.Req1 = null;
        FragmentClass.Req2 = null;
        FragmentClass.Req3 = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
