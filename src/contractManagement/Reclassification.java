package contractManagement;

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
 *    Reclassification - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Reclassification extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReclassificationTable();

    public Reclassification(){

        super();         if(table == null)
            table = TABLE;
    }

    public Reclassification(String name, boolean ispositive, DataObjectInterface user, String fragment, String headline, String pattern, String tag, DataObjectInterface classification, DataObjectInterface document, String date, boolean closed) throws BackOfficeException{

        this(name, ispositive, user.getKey(), fragment, headline, pattern, tag, classification.getKey(), document.getKey(), date, closed);
    }


    public Reclassification(String name, boolean ispositive, DBKeyInterface user, String fragment, String headline, String pattern, String tag, DBKeyInterface classification, DBKeyInterface document, String date, boolean closed) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new BoolData(ispositive);
        data[2] = new ReferenceData(user, columns[2].getTableReference());
        data[3] = new BlobData(fragment);
        data[4] = new BlobData(headline);
        data[5] = new StringData(pattern);
        data[6] = new StringData(tag);
        data[7] = new ReferenceData(classification, columns[7].getTableReference());
        data[8] = new ReferenceData(document, columns[8].getTableReference());
        data[9] = new DateData(date);
        data[10] = new BoolData(closed);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Reclassification(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Reclassification o = new Reclassification();
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



    public boolean getisPositive(){

        BoolData data = (BoolData) this.data[1];
        return data.value;
    }

    public void setisPositive(boolean ispositive){

        BoolData data = (BoolData) this.data[1];
        data.value = ispositive;
    }



    public DBKeyInterface getUserId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public PortalUser getUser(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setUser(DBKeyInterface user){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = user;
    }



    public String getFragment(){

        BlobData data = (BlobData) this.data[3];
        return data.getStringValue();
    }

    public void setFragment(String fragment){

        BlobData data = (BlobData) this.data[3];
        data.setStringValue(fragment);
    }



    public String getHeadline(){

        BlobData data = (BlobData) this.data[4];
        return data.getStringValue();
    }

    public void setHeadline(String headline){

        BlobData data = (BlobData) this.data[4];
        data.setStringValue(headline);
    }



    public String getPattern(){

        StringData data = (StringData) this.data[5];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        StringData data = (StringData) this.data[5];
        data.setStringValue(pattern);
    }



    public String getTag(){

        StringData data = (StringData) this.data[6];
        return data.getStringValue();
    }

    public void setTag(String tag){

        StringData data = (StringData) this.data[6];
        data.setStringValue(tag);
    }



    public DBKeyInterface getClassificationId(){

        ReferenceData data = (ReferenceData)this.data[7];
        return data.value;
    }

    public FragmentClass getClassification(){

        ReferenceData data = (ReferenceData)this.data[7];
        return new FragmentClass(new LookupByKey(data.value));
    }

    public void setClassification(DBKeyInterface classification){

        ReferenceData data = (ReferenceData)this.data[7];
        data.value = classification;
    }



    public DBKeyInterface getDocumentId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public Contract getDocument(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new Contract(new LookupByKey(data.value));
    }

    public void setDocument(DBKeyInterface document){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = document;
    }



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[9];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[9];
        data.value = date.getISODate().toString();
    }



    public boolean getClosed(){

        BoolData data = (BoolData) this.data[10];
        return data.value;
    }

    public void setClosed(boolean closed){

        BoolData data = (BoolData) this.data[10];
        data.value = closed;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
