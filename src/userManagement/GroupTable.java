package userManagement;

import risk.*;
import contractManagement.*;
import classification.*;
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
 *    Group - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class GroupTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Group";
    public static final String TABLE = "Group";
    private static final String DESCRIPTION = "Access group within an organization";

    public enum Columns {Name, Description, Organization, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new ReferenceColumn("Organization", DataColumn.noFormatting, new TableReference("Organization", "Name")),
    };

    private static final Group associatedObject = new Group();
    public GroupTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public GroupTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"User", "Default Group", "itClarifies", "system"},



    };
    private static final String[][] TestValues = {




    };

    @Override
    public void clearConstantCache(){

        Group.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




}
