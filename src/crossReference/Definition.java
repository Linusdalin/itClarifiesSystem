package crossReference;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
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
 *    Definition - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Definition extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new DefinitionTable();

    public Definition(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Definition(String name, DataObjectInterface definedin, long fragmentno, DataObjectInterface version, DataObjectInterface project, String definition) throws BackOfficeException{

        this(name, definedin.getKey(), fragmentno, version.getKey(), project.getKey(), definition);
    }


    public Definition(String name, DBKeyInterface definedin, long fragmentno, DBKeyInterface version, DBKeyInterface project, String definition){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new ReferenceData(definedin, columns[1].getTableReference());
           data[2] = new IntData(fragmentno);
           data[3] = new ReferenceData(version, columns[3].getTableReference());
           data[4] = new ReferenceData(project, columns[4].getTableReference());
           data[5] = new TextData(definition);

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

    public Definition(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Definition o = new Definition();
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



    public DBKeyInterface getDefinedInId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public ContractFragment getDefinedIn(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new ContractFragment(new LookupByKey(data.value));
    }

    public void setDefinedIn(DBKeyInterface definedin){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = definedin;
    }



    public long getFragmentNo(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setFragmentNo(long fragmentno){

        IntData data = (IntData) this.data[2];
        data.value = fragmentno;
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



    public String getDefinition(){

        TextData data = (TextData) this.data[5];
        return data.getStringValue();
    }

    public void setDefinition(String definition){

        TextData data = (TextData) this.data[5];
        data.setStringValue(definition);
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    public int deleteReferencesForDefinition() {

        ContractVersionInstance v = this.getVersion();
        Contract c = v.getDocument();

        Project currentProject = this.getVersion().getDocument().getProject();
        DBKeyInterface currentFragment = this.getDefinedInId();
        int count = 0;

        for (Reference reference : currentProject.getReferencesForProject()) {

            if(reference.getToId().equals(currentFragment) &&
                    reference.getType().equals(ReferenceType.getDefinitionUsage()) &&
                    reference.getPattern().equalsIgnoreCase(this.getName())){


                reference.delete();
                count++;


            }


        }

        for (FragmentClassification classification : currentProject.getFragmentClassificationsForProject()){

            if(classification.getClassTag().equals(featureTypes.FeatureTypeTree.DefinitionUsage.getName()) &&
                    classification.getPattern().equalsIgnoreCase(this.getName())){

                classification.delete();
                count++;
            }

            if(classification.getClassTag().equals(featureTypes.FeatureTypeTree.DefinitionDef.getName()) &&
                    classification.getPattern().equalsIgnoreCase(this.getName()) &&
                    classification.getFragmentId().equals(currentFragment)){

                classification.delete();
                count++;
            }


        }


        return count;


    }
}
