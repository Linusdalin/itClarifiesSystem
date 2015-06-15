package userManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
import module.*;
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
 *    Organization - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Organization extends DataObject implements DataObjectInterface{

    private static Organization none = null;  
    private static Organization itClarifies = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new OrganizationTable();

    public Organization(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Organization(String name, String date, String description, String token, DataObjectInterface config) throws BackOfficeException{

        this(name, date, description, token, config.getKey());
    }


    public Organization(String name, String date, String description, String token, DBKeyInterface config){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new DateData(date);
           data[2] = new TextData(description);
           data[3] = new StringData(token);
           data[4] = new ReferenceData(config, columns[4].getTableReference());

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

    public Organization(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Organization o = new Organization();
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



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[1];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[1];
        data.value = date.getISODate().toString();
    }



    public String getDescription(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[2];
        data.setStringValue(description);
    }



    public String getToken(){

        StringData data = (StringData) this.data[3];
        return data.getStringValue();
    }

    public void setToken(String token){

        StringData data = (StringData) this.data[3];
        data.setStringValue(token);
    }



    public DBKeyInterface getConfigId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public OrganizationConf getConfig(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new OrganizationConf(new LookupByKey(data.value));
    }

    public void setConfig(DBKeyInterface config){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = config;
    }



    public static Organization getnone( )  throws BackOfficeException{

       if(Organization.none == null)
          Organization.none = new Organization(new LookupItem().addFilter(new ColumnFilter("Name", "no org")));
       if(!Organization.none.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant none is missing (db update required?)");

       return Organization.none;
    }

    public static Organization getitClarifies( )  throws BackOfficeException{

       if(Organization.itClarifies == null)
          Organization.itClarifies = new Organization(new LookupItem().addFilter(new ColumnFilter("Name", "itClarifies")));
       if(!Organization.itClarifies.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant itClarifies is missing (db update required?)");

       return Organization.itClarifies;
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        Organization.none = null;
        Organization.itClarifies = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



    public List<FragmentClass> getCustomTagsForOrganization(ConditionInterface condition){

        condition.addFilter(new ReferenceFilter(FragmentClassTable.Columns.Organization.name(), getKey()));

        List<DataObjectInterface> objects = new FragmentClassTable(condition).getValues();

        List<FragmentClass> list = (List<FragmentClass>)(List<?>) objects;

        return list;
    }

    // No condition retrieves all items

    public List<FragmentClass> getCustomTagsForOrganization(){

        return getCustomTagsForOrganization(new LookupList());
    }

    /*************************************************************************
     *
     *          Get modules for organization will use the ModuleOrganization table to
     *          find access
     *
     *          It is implemented by getting all objects at once to avoid multiple db accesses.
     *
     *
     * @return   - list of modules
     */


    public List<Module> getModulesForOrganization(){

        List<Module> modulesForOrganization = new ArrayList<Module>();

        List<DataObjectInterface> modules = new ModuleTable(new LookupList()).getValues();
        List<DataObjectInterface> accessObjects = new ModuleOrganizationTable(new LookupList()).getValues();


        for (DataObjectInterface _accessObject : accessObjects) {

            ModuleOrganization accessObject = (ModuleOrganization)_accessObject;

            if(accessObject.getOrganizationId().equals(getKey())){

                // The access object is applicable to this organization

                for (DataObjectInterface _module : modules) {

                    Module module = (Module)_module;

                    if(accessObject.getModuleId().equals(module.getKey())){

                        modulesForOrganization.add(module);
                    }

                }

            }


        }
        return modulesForOrganization;
    }

}
