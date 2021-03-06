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
 *    Module - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ModuleTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Module";
    public static final String TABLE = "Module";
    private static final String DESCRIPTION = "A group of rules and tags";

    public enum Columns {Name, Description, isPublic, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new BoolColumn("isPublic", DataColumn.noFormatting),
    };

    private static final Module associatedObject = new Module();
    public ModuleTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ModuleTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"Contracting", "ItClarifies Contracting Module", "true", "system"},
          {"Risk", "Risk Analysis Module", "true", "system"},
          {"Definitions", "Definition Module", "true", "system"},



    };
    private static final String[][] TestValues = {

          {"Test", "Stage Test Module", "true", "system"},



    };

    @Override
    public void clearConstantCache(){

        Module.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
