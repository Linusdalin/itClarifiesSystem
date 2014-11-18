package contractManagement;

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
 *    Reclassification - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ReclassificationTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Reclassification Log";
    public static final String TABLE = "Reclassification";
    private static final String DESCRIPTION = "Classifications that are manually corrected  for review and automatic analysis";

    public enum Columns {Name, isPositive, User, Fragment, Headline, Pattern, Tag, Classification, Document, Date, Closed, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new BoolColumn("isPositive", DataColumn.noFormatting),
            new ReferenceColumn("User", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new BlobColumn("Fragment", DataColumn.noFormatting),
            new BlobColumn("Headline", DataColumn.noFormatting),
            new StringColumn("Pattern", DataColumn.noFormatting),
            new StringColumn("Tag", DataColumn.noFormatting),
            new ReferenceColumn("Classification", DataColumn.noFormatting, new TableReference("FragmentClass", "Name")),
            new ReferenceColumn("Document", DataColumn.noFormatting, new TableReference("Contract", "Name")),
            new DateColumn("Date", DataColumn.noFormatting),
            new BoolColumn("Closed", DataColumn.noFormatting),
    };

    private static final Reclassification associatedObject = new Reclassification();
    public ReclassificationTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ReclassificationTable(ConditionInterface condition){

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

        Reclassification.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
