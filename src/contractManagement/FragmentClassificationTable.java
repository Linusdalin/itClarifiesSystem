package contractManagement;

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
 *    FragmentClassification - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class FragmentClassificationTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Classification";
    public static final String TABLE = "FragmentClassification";
    private static final String DESCRIPTION = "Classifications";

    public enum Columns {Fragment, Classification, Name, Comment, Keywords, Creator, Version, Pattern, Pos, Length, Significance, RuleId, Time, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new ReferenceColumn("Classification", DataColumn.noFormatting, new TableReference("FragmentClass", "Name")),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Comment", DataColumn.noFormatting),
            new TextColumn("Keywords", DataColumn.noFormatting),
            new ReferenceColumn("Creator", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new TextColumn("Pattern", DataColumn.noFormatting),
            new IntColumn("Pos", DataColumn.noFormatting),
            new IntColumn("Length", DataColumn.noFormatting),
            new IntColumn("Significance", DataColumn.noFormatting),
            new TextColumn("RuleId", DataColumn.noFormatting),
            new TimeStampColumn("Time", DataColumn.noFormatting),
    };

    private static final FragmentClassification associatedObject = new FragmentClassification();
    public FragmentClassificationTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
        nameColumn = 3;
        // Not set as external
        // Not a constant table
    }

    public FragmentClassificationTable(ConditionInterface condition){

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

          {"first fragment", "Datum", "Datum", "keywords", "comment...", "demo", "Cannon v1.0", "2014-07-01", "10", "10", "70", "0", "2014-07-10 00:00:00", "system"},



    };

    @Override
    public void clearConstantCache(){

        FragmentClassification.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/







}
