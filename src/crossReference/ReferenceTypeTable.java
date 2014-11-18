package crossReference;

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
 *    ReferenceType - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ReferenceTypeTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Reference Type";
    public static final String TABLE = "ReferenceType";
    private static final String DESCRIPTION = "Type of reference";

    public enum Columns {Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
    };

    private static final ReferenceType associatedObject = new ReferenceType();
    public ReferenceTypeTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        setIsConstant();
    }

    public ReferenceTypeTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"Unknown", "Unknown or unclassified reference", "system"},
          {"Explicit", "An explicit reference to another clause or document", "system"},
          {"Implicit", "An implicit or derived reference", "system"},
          {"Definition Usage", "A reference to a definition", "system"},
          {"Open", "A link to external or unknown destination", "system"},



    };
    private static final String[][] TestValues = {




    };

    @Override
    public void clearConstantCache(){

        ReferenceType.clearConstantCache();
    }

    private static Map<String, String> constantMap = null;


    @Override
    public Map<String, String> getConstantMap(){

        if(constantMap == null)
            constantMap = asNameMap();
        return constantMap;

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
