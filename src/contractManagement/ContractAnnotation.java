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
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    ContractAnnotation - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractAnnotation extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractAnnotationTable();

    public ContractAnnotation(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractAnnotation(String name, DataObjectInterface fragment, long ordinal, String description, DataObjectInterface creator, DataObjectInterface version, String pattern, String time) throws BackOfficeException{

        this(name, fragment.getKey(), ordinal, description, creator.getKey(), version.getKey(), pattern, time);
    }


    public ContractAnnotation(String name, DBKeyInterface fragment, long ordinal, String description, DBKeyInterface creator, DBKeyInterface version, String pattern, String time){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(fragment, columns[1].getTableReference());
           data[2] = new IntData(ordinal);
           data[3] = new TextData(description);
           data[4] = new ReferenceData(creator, columns[4].getTableReference());
           data[5] = new ReferenceData(version, columns[5].getTableReference());
           data[6] = new TextData(pattern);
           data[7] = new TimeStampData(time);

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

    public ContractAnnotation(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractAnnotation o = new ContractAnnotation();
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



    public DBKeyInterface getFragmentId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractFragment getFragment(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setFragment(DBKeyInterface fragment){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = fragment;
    }



    public long getOrdinal(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[2];
        data.value = ordinal;
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = creator;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[5];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[5];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[5];
        data.value = version;
    }



    public String getPattern(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[6];
        data.setStringValue(pattern);
    }



    public DBTimeStamp getTime()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[7];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setTime(DBTimeStamp time){

        TimeStampData data = (TimeStampData) this.data[7];
        data.value = time.getSQLTime().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
