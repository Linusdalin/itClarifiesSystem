package module;

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
 *    ModuleTag - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ModuleTagTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Module Tag";
    public static final String TABLE = "ModuleTag";
    private static final String DESCRIPTION = "Which are used in which modules";

    public enum Columns {Name, Module, Tag, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("Module", DataColumn.noFormatting, new TableReference("Module", "Name")),
            new StringColumn("Tag", DataColumn.noFormatting),
    };

    private static final ModuleTag associatedObject = new ModuleTag();
    public ModuleTagTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ModuleTagTable(ConditionInterface condition){

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

          {"#DateTest", "Test", "#Date", "system"},
          {"#BackgroundTest", "Test", "#Background", "system"},



    };

    @Override
    public void clearConstantCache(){

        ModuleTag.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}