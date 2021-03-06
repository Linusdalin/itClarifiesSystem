package contractManagement;

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
 *    StructureItem - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class StructureItemTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Strurcure Item";
    public static final String TABLE = "StructureItem";
    private static final String DESCRIPTION = "A structure item in the document to which the fragments are connected";

    public enum Columns {Name, TopElement, Version, Project, Ordinal, Type, Indentation, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new IntColumn("TopElement", DataColumn.noFormatting),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new IntColumn("Ordinal", DataColumn.noFormatting),
            new StringColumn("Type", DataColumn.noFormatting),
            new IntColumn("Indentation", DataColumn.noFormatting),
    };

    private static final StructureItem associatedObject = new StructureItem();
    public StructureItemTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public StructureItemTable(ConditionInterface condition){

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

          {"Introduction", "1", "Cannon v1.0", "Demo", "1", "Heading", "1", "system"},
          {"Definition", "2", "Cannon v1.0", "Demo", "2", "Heading", "1", "system"},
          {"INTRODUCTION", "1", "Google v1.0", "Demo", "1", "Heading", "1", "system"},
          {"Definitions", "5", "Google v1.0", "Demo", "2", "Heading", "1", "system"},
          {"Fees and Service", "31", "Google v1.0", "Demo", "3", "Heading", "1", "system"},
          {"Member Account, Password, and Security", "36", "Google v1.0", "Demo", "4", "Heading", "1", "system"},
          {"Nonexclusive License", "41", "Google v1.0", "Demo", "5", "Heading", "1", "system"},
          {"Confidentiality", "52", "Google v1.0", "Demo", "6", "Heading", "1", "system"},
          {"Information Rights and Publicity", "54", "Google v1.0", "Demo", "7", "Heading", "1", "system"},
          {"Privacy", "61", "Google v1.0", "Demo", "8", "Heading", "1", "system"},
          {"Indemnification", "67", "Google v1.0", "Demo", "9", "Heading", "1", "system"},



    };

    @Override
    public void clearConstantCache(){

        StructureItem.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
