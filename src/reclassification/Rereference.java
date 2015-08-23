package reclassification;

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
 *    Rereference - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Rereference extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new RereferenceTable();

    public Rereference(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Rereference(String name, boolean add, String project, String document, long fragmentno, String fragment, String tofragment, String type, boolean closed, String date){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new BoolData(add);
           data[2] = new StringData(project);
           data[3] = new StringData(document);
           data[4] = new IntData(fragmentno);
           data[5] = new BlobData(fragment);
           data[6] = new BlobData(tofragment);
           data[7] = new StringData(type);
           data[8] = new BoolData(closed);
           data[9] = new DateData(date);

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

    public Rereference(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Rereference o = new Rereference();
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



    public boolean getAdd(){

        BoolData data = (BoolData) this.data[1];
        return data.value;
    }

    public void setAdd(boolean add){

        BoolData data = (BoolData) this.data[1];
        data.value = add;
    }



    public String getProject(){

        StringData data = (StringData) this.data[2];
        return data.getStringValue();
    }

    public void setProject(String project){

        StringData data = (StringData) this.data[2];
        data.setStringValue(project);
    }



    public String getDocument(){

        StringData data = (StringData) this.data[3];
        return data.getStringValue();
    }

    public void setDocument(String document){

        StringData data = (StringData) this.data[3];
        data.setStringValue(document);
    }



    public long getFragmentNo(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setFragmentNo(long fragmentno){

        IntData data = (IntData) this.data[4];
        data.value = fragmentno;
    }



    public String getFragment(){

        BlobData data = (BlobData) this.data[5];
        return data.getStringValue();
    }

    public void setFragment(String fragment){

        BlobData data = (BlobData) this.data[5];
        data.setStringValue(fragment);
    }



    public String getToFragment(){

        BlobData data = (BlobData) this.data[6];
        return data.getStringValue();
    }

    public void setToFragment(String tofragment){

        BlobData data = (BlobData) this.data[6];
        data.setStringValue(tofragment);
    }



    public String getType(){

        StringData data = (StringData) this.data[7];
        return data.getStringValue();
    }

    public void setType(String type){

        StringData data = (StringData) this.data[7];
        data.setStringValue(type);
    }



    public boolean getClosed(){

        BoolData data = (BoolData) this.data[8];
        return data.value;
    }

    public void setClosed(boolean closed){

        BoolData data = (BoolData) this.data[8];
        data.value = closed;
    }



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[9];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[9];
        data.value = date.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
