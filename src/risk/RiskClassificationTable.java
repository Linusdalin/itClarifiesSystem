package risk;

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
 *    RiskClassification - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class RiskClassificationTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Risk Classification";
    public static final String TABLE = "RiskClassification";
    private static final String DESCRIPTION = "Risk Classifications";

    public enum Columns {Fragment, Risk, Comment, Keywords, Creator, Version, Project, Pattern, PatternPos, Time, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new ConstantColumn("Risk", DataColumn.noFormatting, new TableReference("ContractRisk", "Name")),
            new TextColumn("Comment", DataColumn.noFormatting),
            new TextColumn("Keywords", DataColumn.noFormatting),
            new ReferenceColumn("Creator", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new TextColumn("Pattern", DataColumn.noFormatting),
            new IntColumn("PatternPos", DataColumn.noFormatting),
            new DateColumn("Time", DataColumn.noFormatting),
    };

    private static final RiskClassification associatedObject = new RiskClassification();
    public RiskClassificationTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
        nameColumn = 3;
        // Not set as external
        // Not a constant table
    }

    public RiskClassificationTable(ConditionInterface condition){

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

          {"first fragment", "Blocker", "No particular reason...", "#RISK", "demo", "Cannon v1.0", "Demo", "2014-07-01", "0", "2014-07-10", "system"},



    };

    @Override
    public void clearConstantCache(){

        RiskClassification.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/






}
