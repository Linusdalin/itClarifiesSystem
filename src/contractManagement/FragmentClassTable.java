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
 *    FragmentClass - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class FragmentClassTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Class of Fragments";
    public static final String TABLE = "FragmentClass";
    private static final String DESCRIPTION = "Manual classification of a contract fragment";

    public enum Columns {Name, Type, Keywords, Description, Organization, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new StringColumn("Type", DataColumn.noFormatting),
            new TextColumn("Keywords", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new ReferenceColumn("Organization", DataColumn.noFormatting, new TableReference("Organization", "Name")),
    };

    private static final FragmentClass associatedObject = new FragmentClass();
    public FragmentClassTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        setIsConstant();
    }

    public FragmentClassTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"unknown", "#UNKNOWN", "", "Unknown class", "no org", "top", "system"},
          {"Url", "#URL", "", "A link", "no org", "top", "system"},
          {"Entitet", "#LEGALENTITY", "", "A legal entity in the contract (physical or organization)", "no org", "top", "system"},
          {"Datum", "#DATE", "", "A defined date", "no org", "top", "system"},
          {"Siffra", "#AMOUNT", "", "A figure", "no org", "top", "system"},
          {"Procent", "#PERCENTAGE", "", "A defined percentage", "no org", "top", "system"},
          {"Nummer", "#NUMEX", "", "A number", "no org", "top", "system"},
          {"Email", "#EMAIL", "", "A mail address", "no org", "top", "system"},
          {"Definition", "#DEFINITION", "", "Definition of a specific term", "no org", "top", "system"},
          {"Definition Source", "#DEFINITIONSOURCE", "", "Definition of a specific term", "no org", "top", "system"},
          {"Definition Referens", "#DEFINITIONUSAGE", "", "Usage of a specific term", "no org", "top", "system"},
          {"Avtalsperiod", "#TERM", "", "Definition of a specific term", "no org", "top", "system"},
          {"Betalning", "#PAYMENT", "", "Related to payments and delivery", "no org", "top", "system"},
          {"Finansiering", "#CAPITALIZATION", "", "Related to financial status", "no org", "top", "system"},
          {"Lagar", "#REGULATION", "", "Legal aspects", "no org", "top", "system"},
          {"Tvist", "#ARBITRATION", "", "Governing law and arbitration rules", "no org", "top", "system"},
          {"Organisation", "#ORGANIZATION", "", "Organizational aspects", "no org", "top", "system"},
          {"Tvetydighet", "#AMBIGUITY", "", "A potential problem in the iterpretation", "no org", "top", "system"},
          {"Kompensation", "#COMPENSATION", "", "compensation", "no org", "top", "system"},
          {"Tilldelningsgrund", "#AWARDCRITERIA", "", "How the contract proposals will be evaluated", "no org", "top", "system"},
          {"IT-drift", "#ITOPERATION", "", "IT Operations", "no org", "top", "system"},
          {"Granska Risk", "#RISK", "", "A potential risk that should be reviewd", "no org", "top", "system"},
          {"Unspecific", "#UNSPECIFIC", "", "Unspecified or uncertain", "no org", "system"},
          {"Restriction", "#RESTRICTION", "", "A restriction on rights", "no org", "system"},
          {"Right", "#RIGHT", "", "A right", "no org", "system"},
          {"Restriction", "#RIGTHSANDOBLIGATIONS", "", "Restrictions and obligations", "no org", "system"},



    };
    private static final String[][] TestValues = {




    };

    @Override
    public void clearConstantCache(){

        FragmentClass.clearConstantCache();
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
