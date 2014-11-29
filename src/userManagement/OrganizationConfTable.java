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
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    OrganizationConf - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class OrganizationConfTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Organization Configuration";
    public static final String TABLE = "OrganizationConf";
    private static final String DESCRIPTION = "Configuration data for organizations";

    public enum Columns {Name, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
    };

    private static final OrganizationConf associatedObject = new OrganizationConf();
    public OrganizationConfTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public OrganizationConfTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"Config for no org", "system"},
          {"Config for itClarifies", "system"},



    };
    private static final String[][] TestValues = {

          {"Config for demo", "system"},
          {"Config for another org", "system"},



    };

    @Override
    public void clearConstantCache(){

        OrganizationConf.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
