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
 *    ContractSelection - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractSelection extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractSelectionTable();

    public ContractSelection(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractSelection(String name, DataObjectInterface selectionview, DataObjectInterface fragment) throws BackOfficeException{

        this(name, selectionview.getKey(), fragment.getKey());
    }


    public ContractSelection(String name, DBKeyInterface selectionview, DBKeyInterface fragment){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(selectionview, columns[1].getTableReference());
           data[2] = new ReferenceData(fragment, columns[2].getTableReference());

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

    public ContractSelection(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractSelection o = new ContractSelection();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getname(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setname(String name){

        StringData data = (StringData) this.data[0];
        data.setStringValue(name);
    }



    public DBKeyInterface getSelectionViewId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractSelectionView getSelectionView(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractSelectionView(new LookupByKey(data.value));
    }

    public void setSelectionView(DBKeyInterface selectionview){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = selectionview;
    }



    public DBKeyInterface getFragmentId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public ContractFragment getFragment(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setFragment(DBKeyInterface fragment){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = fragment;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
