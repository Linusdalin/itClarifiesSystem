package actions;

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
 *    Checklist - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ChecklistTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Project Checklist";
    public static final String TABLE = "Checklist";
    private static final String DESCRIPTION = "Project checklist, cross-referencing content with the framework";

    public enum Columns {Name, Description, Id, Project, Owner, Created, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.wideColumn),
            new TextColumn("Id", DataColumn.noFormatting),
            new ReferenceColumn("Project", DataColumn.narrowColumn, new TableReference("Project", "Name")),
            new ReferenceColumn("Owner", DataColumn.narrowColumn, new TableReference("PortalUser", "Name")),
            new DateColumn("Created", DataColumn.noFormatting),
    };

    private static final Checklist associatedObject = new Checklist();
    public ChecklistTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ChecklistTable(ConditionInterface condition){

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

          {"DemoChecklist", "This is a demo checklist", "DE", "Demo", "admin", "2014-10-10", "system"},



    };

    @Override
    public void clearConstantCache(){

        Checklist.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
