package contractManagement;

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
 *    DocumentSection - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class DocumentSection extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new DocumentSectionTable();

    public DocumentSection(){

        super();

        if(table == null)
            table = TABLE;
    }

    public DocumentSection(String name, long ordinal, String description, DataObjectInterface project, DataObjectInterface owner, DataObjectInterface access, DataObjectInterface parent, String creation) throws BackOfficeException{

        this(name, ordinal, description, project.getKey(), owner.getKey(), access, parent.getKey(), creation);
    }


    public DocumentSection(String name, long ordinal, String description, DBKeyInterface project, DBKeyInterface owner, DataObjectInterface access, DBKeyInterface parent, String creation){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new IntData(ordinal);
           data[2] = new TextData(description);
           data[3] = new ReferenceData(project, columns[3].getTableReference());
           data[4] = new ReferenceData(owner, columns[4].getTableReference());
           data[5] = new ConstantData(access.get__Id(), columns[5].getTableReference());
           data[6] = new ReferenceData(parent, columns[6].getTableReference());
           data[7] = new DateData(creation);

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

    public DocumentSection(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        DocumentSection o = new DocumentSection();
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



    public long getOrdinal(){

        IntData data = (IntData) this.data[1];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[1];
        data.value = ordinal;
    }



    public String getDescription(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[2];
        data.setStringValue(description);
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[3];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[3];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[3];
        data.value = project;
    }



    public DBKeyInterface getOwnerId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public PortalUser getOwner(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setOwner(DBKeyInterface owner){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = owner;
    }



    public userManagement.AccessRight getAccess(){

        ConstantData data = (ConstantData)this.data[5];
        return (userManagement.AccessRight)(new userManagement.AccessRightTable().getConstantValue(data.value));

    }

    public void setAccess(DataObjectInterface access){

        ConstantData data = (ConstantData)this.data[5];
        data.value = access.get__Id();
    }



    public DBKeyInterface getParentId(){

        ReferenceData data = (ReferenceData)this.data[6];
        return data.value;
    }

    public DocumentSection getParent(){

        ReferenceData data = (ReferenceData)this.data[6];
        return new DocumentSection(new LookupByKey(data.value));
    }

    public void setParent(DBKeyInterface parent){

        ReferenceData data = (ReferenceData)this.data[6];
        data.value = parent;
    }



    public DBTimeStamp getCreation()throws BackOfficeException{

        DateData data = (DateData) this.data[7];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCreation(DBTimeStamp creation){

        DateData data = (DateData) this.data[7];
        data.value = creation.getISODate().toString();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
