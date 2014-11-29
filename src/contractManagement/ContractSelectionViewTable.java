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
 *    ContractSelectionView - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractSelectionViewTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Selection View";
    public static final String TABLE = "ContractSelectionView";
    private static final String DESCRIPTION = "A selection of fragments for a project";

    public enum Columns {Name, Project, Creator, Visibility, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new TextColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new ReferenceColumn("Creator", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Visibility", DataColumn.noFormatting, new TableReference("Visibility", "Name")),
    };

    private static final ContractSelectionView associatedObject = new ContractSelectionView();
    public ContractSelectionViewTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ContractSelectionViewTable(ConditionInterface condition){

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

        ContractSelectionView.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
