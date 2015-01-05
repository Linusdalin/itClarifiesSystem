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
 *    ContractStatus - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractStatusTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Contract Status";
    public static final String TABLE = "ContractStatus";
    private static final String DESCRIPTION = "Status of a document";

    public enum Columns {Id, Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.noFormatting),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
    };

    private static final ContractStatus associatedObject = new ContractStatus();
    public ContractStatusTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new ContractStatus(1, "Uploaded", "Uploaded"));
          add(new ContractStatus(2, "Analysing", "Analysis in progress"));
          add(new ContractStatus(3, "Analysed", "Document analysed"));
          add(new ContractStatus(4, "Failed", "Analysis Failed"));



    }};

    public ContractStatus getValue(int id){
        
        return (ContractStatus)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
