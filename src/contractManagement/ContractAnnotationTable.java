package contractManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
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
 *    ContractAnnotation - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractAnnotationTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Annotation";
    public static final String TABLE = "ContractAnnotation";
    private static final String DESCRIPTION = "Manual comment";

    public enum Columns {Name, Fragment, Ordinal, Description, Creator, Version, Project, Pattern, PatternPos, Time, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new IntColumn("Ordinal", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new ReferenceColumn("Creator", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new TextColumn("Pattern", DataColumn.noFormatting),
            new IntColumn("PatternPos", DataColumn.noFormatting),
            new TimeStampColumn("Time", DataColumn.noFormatting),
    };

    private static final ContractAnnotation associatedObject = new ContractAnnotation();
    public ContractAnnotationTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
        nameColumn = 1;
        // Not set as external
        // Not a constant table
    }

    public ContractAnnotationTable(ConditionInterface condition){

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

          {"test annotation", "first fragment", "1", "This is an annotation", "admin", "Cannon v1.0", "Demo", "", "0", "2014-07-10 00:00:00", "system"},



    };

    @Override
    public void clearConstantCache(){

        ContractAnnotation.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
