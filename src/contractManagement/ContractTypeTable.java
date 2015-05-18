package contractManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
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
 *    ContractType - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractTypeTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Contract Type";
    public static final String TABLE = "ContractType";
    private static final String DESCRIPTION = "Contract classification";

    public enum Columns {Id, Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.noFormatting),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
    };

    private static final ContractType associatedObject = new ContractType();
    public ContractTypeTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new ContractType(1, "Unclassified", "Unclassified"));
          add(new ContractType(2, "Unknown", "Unknown classification"));



    }};

    public ContractType getValue(int id){
        
        return (ContractType)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/





}
