package contractManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
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
 *    ContractSelectionView - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractSelectionView extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractSelectionViewTable();

    public ContractSelectionView(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractSelectionView(String name, DataObjectInterface project, DataObjectInterface creator, DataObjectInterface visibility) throws BackOfficeException{

        this(name, project.getKey(), creator.getKey(), visibility.getKey());
    }


    public ContractSelectionView(String name, DBKeyInterface project, DBKeyInterface creator, DBKeyInterface visibility){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new TextData(name);
           data[1] = new ReferenceData(project, columns[1].getTableReference());
           data[2] = new ReferenceData(creator, columns[2].getTableReference());
           data[3] = new ReferenceData(visibility, columns[3].getTableReference());

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

    public ContractSelectionView(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractSelectionView o = new ContractSelectionView();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getName(){

        TextData data = (TextData) this.data[0];
        return data.getStringValue();
    }

    public void setName(String name){

        TextData data = (TextData) this.data[0];
        data.setStringValue(name);
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = project;
    }



    public DBKeyInterface getCreatorId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public userManagement.PortalUser getCreator(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new userManagement.PortalUser(new LookupByKey(data.value));
    }

    public void setCreator(DBKeyInterface creator){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = creator;
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




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
