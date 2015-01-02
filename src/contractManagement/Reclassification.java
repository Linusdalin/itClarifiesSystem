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
 *    Reclassification - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Reclassification extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReclassificationTable();

    public Reclassification(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Reclassification(String name, boolean ispositive, DataObjectInterface user, String fragment, String headline, long fragmentno, String pattern, String comment, String classtag, long requirementlevel, long applicablephase, DataObjectInterface risklevel, DataObjectInterface document, String date, boolean closed) throws BackOfficeException{

        this(name, ispositive, user.getKey(), fragment, headline, fragmentno, pattern, comment, classtag, requirementlevel, applicablephase, risklevel.getKey(), document.getKey(), date, closed);
    }


    public Reclassification(String name, boolean ispositive, DBKeyInterface user, String fragment, String headline, long fragmentno, String pattern, String comment, String classtag, long requirementlevel, long applicablephase, DBKeyInterface risklevel, DBKeyInterface document, String date, boolean closed){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new BoolData(ispositive);
           data[2] = new ReferenceData(user, columns[2].getTableReference());
           data[3] = new BlobData(fragment);
           data[4] = new BlobData(headline);
           data[5] = new IntData(fragmentno);
           data[6] = new TextData(pattern);
           data[7] = new TextData(comment);
           data[8] = new StringData(classtag);
           data[9] = new IntData(requirementlevel);
           data[10] = new IntData(applicablephase);
           data[11] = new ReferenceData(risklevel, columns[11].getTableReference());
           data[12] = new ReferenceData(document, columns[12].getTableReference());
           data[13] = new DateData(date);
           data[14] = new BoolData(closed);

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



    public long getFragmentNo(){

        IntData data = (IntData) this.data[5];
        return data.value;
    }

    public void setFragmentNo(long fragmentno){

        IntData data = (IntData) this.data[5];
        data.value = fragmentno;
    }



    public String getPattern(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[6];
        data.setStringValue(pattern);
    }



    public String getComment(){

        TextData data = (TextData) this.data[7];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[7];
        data.setStringValue(comment);
    }



    public String getClassTag(){

        StringData data = (StringData) this.data[8];
        return data.getStringValue();
    }

    public void setClassTag(String classtag){

        StringData data = (StringData) this.data[8];
        data.setStringValue(classtag);
    }



    public long getRequirementLevel(){

        IntData data = (IntData) this.data[9];
        return data.value;
    }

    public void setRequirementLevel(long requirementlevel){

        IntData data = (IntData) this.data[9];
        data.value = requirementlevel;
    }



    public long getApplicablePhase(){

        IntData data = (IntData) this.data[10];
        return data.value;
    }

    public void setApplicablePhase(long applicablephase){

        IntData data = (IntData) this.data[10];
        data.value = applicablephase;
    }



    public DBKeyInterface getRiskLevelId(){

        ReferenceData data = (ReferenceData)this.data[11];
        return data.value;
    }

    public ContractRisk getRiskLevel(){

        ReferenceData data = (ReferenceData)this.data[11];
        return new ContractRisk(new LookupByKey(data.value));
    }

    public void setRiskLevel(DBKeyInterface risklevel){

        ReferenceData data = (ReferenceData)this.data[11];
        data.value = risklevel;
    }



    public DBKeyInterface getDocumentId(){

        ReferenceData data = (ReferenceData)this.data[12];
        return data.value;
    }

    public Contract getDocument(){

        ReferenceData data = (ReferenceData)this.data[12];
        return new Contract(new LookupByKey(data.value));
    }

    public void setDocument(DBKeyInterface document){

        ReferenceData data = (ReferenceData)this.data[12];
        data.value = document;
    }



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[13];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[13];
        data.value = date.getISODate().toString();
    }



    public boolean getClosed(){

        BoolData data = (BoolData) this.data[14];
        return data.value;
    }

    public void setClosed(boolean closed){

        BoolData data = (BoolData) this.data[14];
        data.value = closed;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
