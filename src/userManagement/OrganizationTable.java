package userManagement;

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
 *    Organization - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class OrganizationTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Organization";
    public static final String TABLE = "Organization";
    private static final String DESCRIPTION = "All organizations";

    public enum Columns {Name, Date, Description, Token, Config, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new DateColumn("Date", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new StringColumn("Token", DataColumn.noFormatting),
            new ReferenceColumn("Config", DataColumn.noFormatting, new TableReference("OrganizationConf", "Name")),
    };

    private static final Organization associatedObject = new Organization();
    public OrganizationTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public OrganizationTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"no org", "2014-01-01", "Generic", "-", "Config for no org", "system"},
          {"itClarifies", "2014-01-01", "Our own group", "ItClarifiesSessionToken", "Config for itClarifies", "system"},



    };
    private static final String[][] TestValues = {

          {"demo.org", "2014-01-02", "Test organization in the system", "SystemSessionToken", "Config for demo", "system"},
          {"evil.org", "2014-01-03", "Another organization that should not see the documents", "EvilSessionToken", "Config for another org", "system"},



    };

    @Override
    public void clearConstantCache(){

        Organization.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
