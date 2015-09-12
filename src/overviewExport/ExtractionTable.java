package overviewExport;

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
 *    Extraction - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ExtractionTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Extraction";
    public static final String TABLE = "Extraction";
    private static final String DESCRIPTION = "Cached extractions";

    public enum Columns {Name, Classification, Text, FragmentKey, FragmentOrdinal, ExtractionNumber, Style, Project, Document, Risk, Description, Comment, Sheet, ExtractionRun, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new StringColumn("Classification", DataColumn.noFormatting),
            new BlobColumn("Text", DataColumn.noFormatting),
            new StringColumn("FragmentKey", DataColumn.noFormatting),
            new IntColumn("FragmentOrdinal", DataColumn.noFormatting),
            new IntColumn("ExtractionNumber", DataColumn.noFormatting),
            new StringColumn("Style", DataColumn.noFormatting),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new ReferenceColumn("Document", DataColumn.noFormatting, new TableReference("Contract", "Name")),
            new StringColumn("Risk", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new TextColumn("Comment", DataColumn.noFormatting),
            new StringColumn("Sheet", DataColumn.noFormatting),
            new ReferenceColumn("ExtractionRun", DataColumn.noFormatting, new TableReference("ExtractionStatus", "Name")),
    };

    private static final Extraction associatedObject = new Extraction();
    public ExtractionTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ExtractionTable(ConditionInterface condition){

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

        Extraction.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}