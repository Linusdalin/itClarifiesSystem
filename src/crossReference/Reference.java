package crossReference;

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
 *    Reference - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Reference extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReferenceTable();

    public Reference(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Reference(String name, DataObjectInterface from, DataObjectInterface to, DataObjectInterface version, DataObjectInterface project, DataObjectInterface type, String pattern, long patternpos, DataObjectInterface creator) throws BackOfficeException{

        this(name, from.getKey(), to.getKey(), version.getKey(), project.getKey(), type, pattern, patternpos, creator.getKey());
    }


    public Reference(String name, DBKeyInterface from, DBKeyInterface to, DBKeyInterface version, DBKeyInterface project, DataObjectInterface type, String pattern, long patternpos, DBKeyInterface creator){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(from, columns[1].getTableReference());
           data[2] = new ReferenceData(to, columns[2].getTableReference());
           data[3] = new ReferenceData(version, columns[3].getTableReference());
           data[4] = new ReferenceData(project, columns[4].getTableReference());
           data[5] = new ConstantData(type.get__Id(), columns[5].getTableReference());
           data[6] = new TextData(pattern);
           data[7] = new IntData(patternpos);
           data[8] = new ReferenceData(creator, columns[8].getTableReference());

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

    public Reference(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Reference o = new Reference();
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



    public DBKeyInterface getFromId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractFragment getFrom(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setFrom(DBKeyInterface from){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = from;
    }



    public DBKeyInterface getToId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public ContractFragment getTo(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setTo(DBKeyInterface to){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = to;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = version;
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = project;
    }



    public ReferenceType getType(){

        ConstantData data = (ConstantData)this.data[5];
        return (ReferenceType)(new ReferenceTypeTable().getConstantValue(data.value));

    }

    public void setType(DataObjectInterface type){

        ConstantData data = (ConstantData)this.data[5];
        data.value = type.get__Id();
    }



    public String getPattern(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[6];
        data.setStringValue(pattern);
    }



    public long getPatternPos(){

        IntData data = (IntData) this.data[7];
        return data.value;
    }

    public void setPatternPos(long patternpos){

        IntData data = (IntData) this.data[7];
        data.value = patternpos;
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = creator;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    /*

        public ReferenceType getType(){

        ConstantData data = (ConstantData)this.data[5];
        return ((ReferenceTypeTable)table).getValue(data.value) ;
    }

    public void setType(DataObjectInterface type){

        ConstantData data = (ConstantData)this.data[5];
        data.value = type.get__Id();
    }


     */

}
