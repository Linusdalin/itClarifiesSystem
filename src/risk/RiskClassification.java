package risk;

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
 *    RiskClassification - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class RiskClassification extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new RiskClassificationTable();

    public RiskClassification(){

        super();         if(table == null)
            table = TABLE;
    }

    public RiskClassification(DataObjectInterface fragment, DataObjectInterface risk, String comment, DataObjectInterface creator, DataObjectInterface version, DataObjectInterface project, String pattern, String time) throws BackOfficeException{

        this(fragment.getKey(), risk.getKey(), comment, creator.getKey(), version.getKey(), project.getKey(), pattern, time);
    }


    public RiskClassification(DBKeyInterface fragment, DBKeyInterface risk, String comment, DBKeyInterface creator, DBKeyInterface version, DBKeyInterface project, String pattern, String time) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new ReferenceData(fragment, columns[0].getTableReference());
        data[1] = new ReferenceData(risk, columns[1].getTableReference());
        data[2] = new TextData(comment);
        data[3] = new ReferenceData(creator, columns[3].getTableReference());
        data[4] = new ReferenceData(version, columns[4].getTableReference());
        data[5] = new ReferenceData(project, columns[5].getTableReference());
        data[6] = new TextData(pattern);
        data[7] = new DateData(time);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public RiskClassification(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        RiskClassification o = new RiskClassification();
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



    public DBKeyInterface getRiskId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractRisk getRisk(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractRisk(new LookupByKey(data.value));
    }

    public void setRisk(DBKeyInterface risk){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = risk;
    }



    public String getComment(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setComment(String comment){

        TextData data = (TextData) this.data[2];
        data.setStringValue(comment);
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = creator;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = version;
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[5];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[5];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[5];
        data.value = project;
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

        DateData data = (DateData) this.data[7];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setTime(DBTimeStamp time){

        DateData data = (DateData) this.data[7];
        data.value = time.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
