package classification;

import contractManagement.*;
import project.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;

/********************************************************
 *
 *    FragmentClassification - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class FragmentClassification extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new FragmentClassificationTable();

    public FragmentClassification(){

        super();

        if(table == null)
            table = TABLE;
    }

    public FragmentClassification(DataObjectInterface fragment, String classtag, long requirementlevel, long applicablephase, long blockingstate, String comment, String keywords, DataObjectInterface creator, DataObjectInterface version, DataObjectInterface project, String pattern, long pos, long length, long significance, String ruleid, String time) throws BackOfficeException{

        this(fragment.getKey(), classtag, requirementlevel, applicablephase, blockingstate, comment, keywords, creator.getKey(), version.getKey(), project.getKey(), pattern, pos, length, significance, ruleid, time);
    }


    public FragmentClassification(DBKeyInterface fragment, String classtag, long requirementlevel, long applicablephase, long blockingstate, String comment, String keywords, DBKeyInterface creator, DBKeyInterface version, DBKeyInterface project, String pattern, long pos, long length, long significance, String ruleid, String time){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new ReferenceData(fragment, columns[0].getTableReference());
           data[1] = new StringData(classtag);
           data[2] = new IntData(requirementlevel);
           data[3] = new IntData(applicablephase);
           data[4] = new IntData(blockingstate);
           data[5] = new TextData(comment);
           data[6] = new TextData(keywords);
           data[7] = new ReferenceData(creator, columns[7].getTableReference());
           data[8] = new ReferenceData(version, columns[8].getTableReference());
           data[9] = new ReferenceData(project, columns[9].getTableReference());
           data[10] = new TextData(pattern);
           data[11] = new IntData(pos);
           data[12] = new IntData(length);
           data[13] = new IntData(significance);
           data[14] = new TextData(ruleid);
           data[15] = new TimeStampData(time);

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

    public FragmentClassification(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        FragmentClassification o = new FragmentClassification();
        o.data = data;
        o.exists = true;
        return o;
    }

    public DBKeyInterface getFragmentId(){

        ReferenceData data = (ReferenceData)this.data[0];
        return data.value;
    }

    public ContractFragment getFragment(){

        ReferenceData data = (ReferenceData)this.data[0];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setFragment(DBKeyInterface fragment){

        ReferenceData data = (ReferenceData)this.data[0];
        data.value = fragment;
    }



    public String getClassTag(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setClassTag(String classtag){

        StringData data = (StringData) this.data[1];
        data.setStringValue(classtag);
    }



    public long getRequirementLevel(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setRequirementLevel(long requirementlevel){

        IntData data = (IntData) this.data[2];
        data.value = requirementlevel;
    }



    public long getApplicablePhase(){

        IntData data = (IntData) this.data[3];
        return data.value;
    }

    public void setApplicablePhase(long applicablephase){

        IntData data = (IntData) this.data[3];
        data.value = applicablephase;
    }



    public long getblockingState(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setblockingState(long blockingstate){

        IntData data = (IntData) this.data[4];
        data.value = blockingstate;
    }



    public String getComment(){

        TextData data = (TextData) this.data[5];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[5];
        data.setStringValue(comment);
    }



    public String getKeywords(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setKeywords(String keywords){

        TextData data = (TextData) this.data[6];
        data.setStringValue(keywords);
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[7];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[7];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[7];
        data.value = creator;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = version;
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[9];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[9];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[9];
        data.value = project;
    }



    public String getPattern(){

        TextData data = (TextData) this.data[10];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[10];
        data.setStringValue(pattern);
    }



    public long getPos(){

        IntData data = (IntData) this.data[11];
        return data.value;
    }

    public void setPos(long pos){

        IntData data = (IntData) this.data[11];
        data.value = pos;
    }



    public long getLength(){

        IntData data = (IntData) this.data[12];
        return data.value;
    }

    public void setLength(long length){

        IntData data = (IntData) this.data[12];
        data.value = length;
    }



    public long getSignificance(){

        IntData data = (IntData) this.data[13];
        return data.value;
    }

    public void setSignificance(long significance){

        IntData data = (IntData) this.data[13];
        data.value = significance;
    }



    public String getRuleId(){

        TextData data = (TextData) this.data[14];
        return data.getStringValue();
    }

    public void setRuleId(String ruleid){

        TextData data = (TextData) this.data[14];
        data.setStringValue(ruleid);
    }



    public DBTimeStamp getTime()throws BackOfficeException{

        TimeStampData data = (TimeStampData) this.data[15];
        return new DBTimeStamp(DBTimeStamp.SQL_TIMESTAMP, data.value);
    }

    public void setTime(DBTimeStamp time){

        TimeStampData data = (TimeStampData) this.data[15];
        data.value = time.getSQLTime().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/

    public static final int GENERATED = 0;
    public static final int BLOCKED     = 1;
    public static final int BLOCKING    = 2;
    public static final int IMPORTED    = 3;
    public static final int OVERRIDDEN  = 4;
    public static final int MANUAL      = 5;
    public static final int DUPLICATE_EXTERNAL      = 6;



    public String getType(){

        String type;

        switch ((int) this.getblockingState()){

            case GENERATED:
                type = "GENERATED";
                break;

            case BLOCKED:
                type = "BLOCKED";
                break;

            case BLOCKING:
                type = "BLOCKING";
                break;

            case IMPORTED:
                type = "IMPORTED";
                break;

            case OVERRIDDEN:
                type = "OVERRIDDEN";
                break;

            case MANUAL:
                type = "MANUAL";
                break;

            case DUPLICATE_EXTERNAL:
                type = "DUPLICATE";
                break;

            default:
                type = "unknown("+ this.getblockingState()+")";
        }

        return type;
    }



    public String toString(){

        String type = getType();
        return this.getClassTag() + "(" + type  + ") for \""+ this.getPattern() + "\"";
    }





}
