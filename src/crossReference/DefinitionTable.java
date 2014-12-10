package crossReference;

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
 *    Definition - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class DefinitionTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Definition";
    public static final String TABLE = "Definition";
    private static final String DESCRIPTION = "A definition of a concept in the document (or project)";

    public enum Columns {Name, DefinedIn, Version, Project, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("DefinedIn", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
    };

    private static final Definition associatedObject = new Definition();
    public DefinitionTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public DefinitionTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {




    };
    private static final String[][] TestValues = {




    };

    @Override
    public void clearConstantCache(){

        Definition.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
