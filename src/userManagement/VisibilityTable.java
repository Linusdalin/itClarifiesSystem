package userManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
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
 *    Visibility - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class VisibilityTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Visibility";
    public static final String TABLE = "Visibility";
    private static final String DESCRIPTION = "Visibility for stored views";

    public enum Columns {Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
    };

    private static final Visibility associatedObject = new Visibility();
    public VisibilityTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        setIsConstant();
    }

    public VisibilityTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"Private", "Private View", "system"},
          {"Organization", "Organization Wide share", "system"},



    };
    private static final String[][] TestValues = {




    };

    @Override
    public void clearConstantCache(){

        Visibility.clearConstantCache();
    }

    private static Map<String, String> constantMap = null;


    @Override
    public Map<String, String> getConstantMap(){

        if(constantMap == null)
            constantMap = asNameMap();
        return constantMap;

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
