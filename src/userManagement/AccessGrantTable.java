package userManagement;

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
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    AccessGrant - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class AccessGrantTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Grant of Access";
    public static final String TABLE = "AccessGrant";
    private static final String DESCRIPTION = "Access to documents";

    public enum Columns {Name, Document, AccessRight, Visibility, Issuer, Time, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("Document", DataColumn.noFormatting, new TableReference("Contract", "Name")),
            new ReferenceColumn("AccessRight", DataColumn.noFormatting, new TableReference("AccessRight", "Name")),
            new ReferenceColumn("Visibility", DataColumn.noFormatting, new TableReference("Visibility", "Name")),
            new ReferenceColumn("Issuer", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new DateColumn("Time", DataColumn.noFormatting),
    };

    private static final AccessGrant associatedObject = new AccessGrant();
    public AccessGrantTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public AccessGrantTable(ConditionInterface condition){

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

          {"cannon doc access", "Cannon", "ro", "Organization", "admin", "2014-06-01", "system"},
          {"google doc access", "Google Analytics", "ro", "Organization", "admin", "2014-06-01", "system"},



    };

    @Override
    public void clearConstantCache(){

        AccessGrant.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




}
