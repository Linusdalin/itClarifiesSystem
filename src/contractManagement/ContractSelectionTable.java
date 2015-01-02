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
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    ContractSelection - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractSelectionTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Contract Selection";
    public static final String TABLE = "ContractSelection";
    private static final String DESCRIPTION = "Link between views and fragments";

    public enum Columns {name, SelectionView, Fragment, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("name", DataColumn.noFormatting),
            new ReferenceColumn("SelectionView", DataColumn.noFormatting, new TableReference("ContractSelectionView", "Name")),
            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Text")),
    };

    private static final ContractSelection associatedObject = new ContractSelection();
    public ContractSelectionTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ContractSelectionTable(ConditionInterface condition){

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

        ContractSelection.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
