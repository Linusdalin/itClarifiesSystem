package crossReference;

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
 *    Definition - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Definition extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new DefinitionTable();

    public Definition(){

        super();         if(table == null)
            table = TABLE;
    }

    public Definition(String name, DataObjectInterface definedin, DataObjectInterface version) throws BackOfficeException{

        this(name, definedin.getKey(), version.getKey());
    }


    public Definition(String name, DBKeyInterface definedin, DBKeyInterface version) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new ReferenceData(definedin, columns[1].getTableReference());
        data[2] = new ReferenceData(version, columns[2].getTableReference());

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Definition(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Definition o = new Definition();
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



    public DBKeyInterface getDefinedInId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractFragment getDefinedIn(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setDefinedIn(DBKeyInterface definedin){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = definedin;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = version;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
