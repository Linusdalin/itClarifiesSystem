package module;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import project.*;
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
 *    ModuleOrganization - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ModuleOrganization extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ModuleOrganizationTable();

    public ModuleOrganization(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ModuleOrganization(String name, String timestamp, DataObjectInterface organization, DataObjectInterface module, DataObjectInterface owner) throws BackOfficeException{

        this(name, timestamp, organization.getKey(), module.getKey(), owner.getKey());
    }


    public ModuleOrganization(String name, String timestamp, DBKeyInterface organization, DBKeyInterface module, DBKeyInterface owner){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new TimeStampData(timestamp);
           data[2] = new ReferenceData(organization, columns[2].getTableReference());
           data[3] = new ReferenceData(module, columns[3].getTableReference());
           data[4] = new ReferenceData(owner, columns[4].getTableReference());

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

    public ModuleOrganization(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ModuleOrganization o = new ModuleOrganization();
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



    public DBTimeStamp getTimestamp()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[1];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setTimestamp(DBTimeStamp timestamp){

        TimeStampData data = (TimeStampData) this.data[1];
        data.value = timestamp.getSQLTime().toString();
    }



    public DBKeyInterface getOrganizationId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public Organization getOrganization(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new Organization(new LookupByKey(data.value));
    }

    public void setOrganization(DBKeyInterface organization){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = organization;
    }



    public DBKeyInterface getModuleId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public Module getModule(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new Module(new LookupByKey(data.value));
    }

    public void setModule(DBKeyInterface module){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = module;
    }



    public DBKeyInterface getOwnerId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public userManagement.PortalUser getOwner(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setOwner(DBKeyInterface owner){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = owner;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
