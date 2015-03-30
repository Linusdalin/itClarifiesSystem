package actions;

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
 *    Action - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ActionTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Contract Action";
    public static final String TABLE = "Action";
    private static final String DESCRIPTION = "Actions and work items";

    public enum Columns {Id, Name, Description, Pattern, Fragment, Version, Comply, Project, Issuer, Assignee, Priority, Status, Created, Due, Completed, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.numberColumn),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.wideColumn),
            new TextColumn("Pattern", DataColumn.noFormatting),
            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new ReferenceColumn("Version", DataColumn.narrowColumn, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Comply", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new ReferenceColumn("Project", DataColumn.narrowColumn, new TableReference("Project", "Name")),
            new ReferenceColumn("Issuer", DataColumn.narrowColumn, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Assignee", DataColumn.narrowColumn, new TableReference("PortalUser", "Name")),
            new IntColumn("Priority", DataColumn.numberColumn),
            new ConstantColumn("Status", DataColumn.narrowColumn, new TableReference("ActionStatus", "Name")),
            new DateColumn("Created", DataColumn.noFormatting),
            new DateColumn("Due", DataColumn.noFormatting),
            new DateColumn("Completed", DataColumn.noFormatting),
    };

    private static final Action associatedObject = new Action();
    public ActionTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ActionTable(ConditionInterface condition){

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

          {"0", "TestAction", "This is a demo action", "", "first fragment", "Cannon v1.0", "Definition fragment", "Demo", "admin", "demo", "1", "In Progress", "2014-10-10", "2014-10-20", "2014-10-30", "system"},



    };

    @Override
    public void clearConstantCache(){

        Action.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
