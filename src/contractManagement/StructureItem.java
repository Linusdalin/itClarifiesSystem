package contractManagement;

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
 *    StructureItem - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class StructureItem extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new StructureItemTable();

    public StructureItem(){

        super();

        if(table == null)
            table = TABLE;
    }

    public StructureItem(String name, long topelement, DataObjectInterface version, DataObjectInterface project, long ordinal, String type, long indentation) throws BackOfficeException{

        this(name, topelement, version.getKey(), project.getKey(), ordinal, type, indentation);
    }


    public StructureItem(String name, long topelement, DBKeyInterface version, DBKeyInterface project, long ordinal, String type, long indentation){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new IntData(topelement);
           data[2] = new ReferenceData(version, columns[2].getTableReference());
           data[3] = new ReferenceData(project, columns[3].getTableReference());
           data[4] = new IntData(ordinal);
           data[5] = new StringData(type);
           data[6] = new IntData(indentation);

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

    public StructureItem(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        StructureItem o = new StructureItem();
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



    public long getTopElement(){

        IntData data = (IntData) this.data[1];
        return data.value;
    }

    public void setTopElement(long topelement){

        IntData data = (IntData) this.data[1];
        data.value = topelement;
    }



    public DBKeyInterface getVersionId(){

        ReferenceData data = (ReferenceData)this.data[2];
        return data.value;
    }

    public ContractVersionInstance getVersion(){

        ReferenceData data = (ReferenceData)this.data[2];
        return new ContractVersionInstance(new LookupByKey(data.value));
    }

    public void setVersion(DBKeyInterface version){

        ReferenceData data = (ReferenceData)this.data[2];
        data.value = version;
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



    public long getOrdinal(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[4];
        data.value = ordinal;
    }



    public String getType(){

        StringData data = (StringData) this.data[5];
        return data.getStringValue();
    }

    public void setType(String type){

        StringData data = (StringData) this.data[5];
        data.setStringValue(type);
    }



    public long getIndentation(){

        IntData data = (IntData) this.data[6];
        return data.value;
    }

    public void setIndentation(long indentation){

        IntData data = (IntData) this.data[6];
        data.value = indentation;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/






    /***************************************************************************''
     *
     *          The clause has an associated fragment. It is the first fragment
     *          pointing to this clause
     *
     *          // TODO: This can be optimized by adding a reference directly in the data structure
     *
     * @return
     * @throws BackOfficeException
     */

    public ContractFragment getFragmentForStructureItem() throws BackOfficeException{

        ContractFragment fragment = new ContractFragment(new LookupItem()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Version.name(), this.getVersionId()))
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), getTopElement())));

        if(!fragment.exists()){

            PukkaLogger.log(PukkaLogger.Level.WARNING, " There is no top element for structure item " + this.getName() + " in document " + getVersion().getDocument().getName());

        }

        return fragment;

    }

    public ContractFragment getFragmentForStructureItem(List<ContractFragment> fragmentsForVersion) throws BackOfficeException {

        for(ContractFragment fragment : fragmentsForVersion){

            if(fragment.getOrdinal() == this.getTopElement())
                return fragment;
        }

        PukkaLogger.log(PukkaLogger.Level.WARNING, " There is no top element for structure item " + this.getName() + " in document " + getVersion().getDocument().getName());

        return new ContractFragment();

    }

    public ContractFragmentTable getChildrenUnderStructureItem() {

        System.out.print(" *** Getting children for structure item " + getOrdinal());

        ContractFragmentTable children = new ContractFragmentTable(new LookupList()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Version.name(), this.getVersionId()))
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.StructureNo.name(), getOrdinal())));

        System.out.println("... found " + children.values.size() + children );


        return children;

    }
}
