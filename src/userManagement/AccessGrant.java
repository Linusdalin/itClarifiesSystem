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
 *    AccessGrant - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class AccessGrant extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new AccessGrantTable();

    public AccessGrant(){

        super();

        if(table == null)
            table = TABLE;
    }

    public AccessGrant(String name, DataObjectInterface document, DataObjectInterface accessright, DataObjectInterface visibility, DataObjectInterface issuer, String time) throws BackOfficeException{

        this(name, document.getKey(), accessright, visibility.getKey(), issuer.getKey(), time);
    }


    public AccessGrant(String name, DBKeyInterface document, DataObjectInterface accessright, DBKeyInterface visibility, DBKeyInterface issuer, String time){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(document, columns[1].getTableReference());
           data[2] = new ConstantData(accessright.get__Id(), columns[2].getTableReference());
           data[3] = new ReferenceData(visibility, columns[3].getTableReference());
           data[4] = new ReferenceData(issuer, columns[4].getTableReference());
           data[5] = new DateData(time);

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

    public AccessGrant(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        AccessGrant o = new AccessGrant();
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



    public DBKeyInterface getDocumentId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public Contract getDocument(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new Contract(new LookupByKey(data.value));
    }

    public void setDocument(DBKeyInterface document){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = document;
    }



    public AccessRight getAccessRight(){

        ConstantData data = (ConstantData)this.data[2];
        return (AccessRight)(new AccessRightTable().getConstantValue(data.value));

    }

    public void setAccessRight(DataObjectInterface accessright){

        ConstantData data = (ConstantData)this.data[2];
        data.value = accessright.get__Id();
    }



    public DBKeyInterface getVisibilityId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public Visibility getVisibility(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new Visibility(new LookupByKey(data.value));
    }

    public void setVisibility(DBKeyInterface visibility){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = visibility;
    }



    public DBKeyInterface getIssuerId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public PortalUser getIssuer(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setIssuer(DBKeyInterface issuer){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = issuer;
    }



    public DBTimeStamp getTime()throws BackOfficeException{

        DateData data = (DateData) this.data[5];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setTime(DBTimeStamp time){

        DateData data = (DateData) this.data[5];
        data.value = time.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
