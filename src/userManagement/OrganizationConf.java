package userManagement;

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
 *    OrganizationConf - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class OrganizationConf extends DataObject implements DataObjectInterface{

    private static OrganizationConf noOrg = null;  
    private static OrganizationConf itClarifies = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new OrganizationConfTable();

    public OrganizationConf(){

        super();         if(table == null)
            table = TABLE;
    }

    public OrganizationConf(String name, DataObjectInterface defaultrisk) throws BackOfficeException{

        this(name, defaultrisk.getKey());
    }


    public OrganizationConf(String name, DBKeyInterface defaultrisk) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new ReferenceData(defaultrisk, columns[1].getTableReference());

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public OrganizationConf(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        OrganizationConf o = new OrganizationConf();
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



    public DBKeyInterface getDefaultRiskId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractRisk getDefaultRisk(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractRisk(new LookupByKey(data.value));
    }

    public void setDefaultRisk(DBKeyInterface defaultrisk){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = defaultrisk;
    }



    public static OrganizationConf getnoOrg( ) throws BackOfficeException{

       if(OrganizationConf.noOrg == null)
          OrganizationConf.noOrg = new OrganizationConf(new LookupItem().addFilter(new ColumnFilter("Name", "Config for no org")));
       if(!OrganizationConf.noOrg.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant noOrg is missing (db update required?)");

       return OrganizationConf.noOrg;
     }

    public static OrganizationConf getitClarifies( ) throws BackOfficeException{

       if(OrganizationConf.itClarifies == null)
          OrganizationConf.itClarifies = new OrganizationConf(new LookupItem().addFilter(new ColumnFilter("Name", "Config for itClarifies")));
       if(!OrganizationConf.itClarifies.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant itClarifies is missing (db update required?)");

       return OrganizationConf.itClarifies;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        OrganizationConf.noOrg = null;
        OrganizationConf.itClarifies = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
