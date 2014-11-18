package actions;

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
 *    Action - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Action extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ActionTable();

    public Action(){

        super();         if(table == null)
            table = TABLE;
    }

    public Action(long id, String name, String description, String pattern, DataObjectInterface fragment, DataObjectInterface version, DataObjectInterface project, DataObjectInterface issuer, DataObjectInterface assignee, long priority, DataObjectInterface status, String created, String due, String completed) throws BackOfficeException{

        this(id, name, description, pattern, fragment.getKey(), version.getKey(), project.getKey(), issuer.getKey(), assignee.getKey(), priority, status.getKey(), created, due, completed);
    }


    public Action(long id, String name, String description, String pattern, DBKeyInterface fragment, DBKeyInterface version, DBKeyInterface project, DBKeyInterface issuer, DBKeyInterface assignee, long priority, DBKeyInterface status, String created, String due, String completed) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new IntData(id);
        data[1] = new StringData(name);
        data[2] = new TextData(description);
        data[3] = new TextData(pattern);
        data[4] = new ReferenceData(fragment, columns[4].getTableReference());
        data[5] = new ReferenceData(version, columns[5].getTableReference());
        data[6] = new ReferenceData(project, columns[6].getTableReference());
        data[7] = new ReferenceData(issuer, columns[7].getTableReference());
        data[8] = new ReferenceData(assignee, columns[8].getTableReference());
        data[9] = new IntData(priority);
        data[10] = new ReferenceData(status, columns[10].getTableReference());
        data[11] = new DateData(created);
        data[12] = new DateData(due);
        data[13] = new DateData(completed);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Action(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Action o = new Action();
        o.data = data;
        o.exists = true;
        return o;
    }

    public long getId(){

        IntData data = (IntData) this.data[0];
        return data.value;
    }

    public void setId(long id){

        IntData data = (IntData) this.data[0];
        data.value = id;
    }



    public String getName(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[1];
        data.setStringValue(name);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[2];
        data.setStringValue(description);
    }



    public String getPattern(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[3];
        data.setStringValue(pattern);
    }



    public DBKeyInterface getFragmentId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public ContractFragment getFragment(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setFragment(DBKeyInterface fragment){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = fragment;
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



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[6];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[6];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[6];
        data.value = project;
    }



    public DBKeyInterface getIssuerId(){

        ReferenceData data = (ReferenceData)this.data[7];
        return data.value;
    }

    public PortalUser getIssuer(){

        ReferenceData data = (ReferenceData)this.data[7];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setIssuer(DBKeyInterface issuer){

        ReferenceData data = (ReferenceData)this.data[7];
        data.value = issuer;
    }



    public DBKeyInterface getAssigneeId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public PortalUser getAssignee(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setAssignee(DBKeyInterface assignee){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = assignee;
    }



    public long getPriority(){

        IntData data = (IntData) this.data[9];
        return data.value;
    }

    public void setPriority(long priority){

        IntData data = (IntData) this.data[9];
        data.value = priority;
    }



    public DBKeyInterface getStatusId(){

        ReferenceData data = (ReferenceData)this.data[10];
        return data.value;
    }

    public ActionStatus getStatus(){

        ReferenceData data = (ReferenceData)this.data[10];
        return new ActionStatus(new LookupByKey(data.value));
    }

    public void setStatus(DBKeyInterface status){

        ReferenceData data = (ReferenceData)this.data[10];
        data.value = status;
    }



    public DBTimeStamp getCreated()throws BackOfficeException{

        DateData data = (DateData) this.data[11];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCreated(DBTimeStamp created){

        DateData data = (DateData) this.data[11];
        data.value = created.getISODate().toString();
    }



    public DBTimeStamp getDue()throws BackOfficeException{

        DateData data = (DateData) this.data[12];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDue(DBTimeStamp due){

        DateData data = (DateData) this.data[12];
        data.value = due.getISODate().toString();
    }



    public DBTimeStamp getCompleted()throws BackOfficeException{

        DateData data = (DateData) this.data[13];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCompleted(DBTimeStamp completed){

        DateData data = (DateData) this.data[13];
        data.value = completed.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
