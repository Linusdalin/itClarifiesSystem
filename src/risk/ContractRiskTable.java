package risk;

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
 *    ContractRisk - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractRiskTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Risk Assessment";
    public static final String TABLE = "ContractRisk";
    private static final String DESCRIPTION = "Risk Classification of a contract fragment";

    public enum Columns {Id, Name, Severity, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.narrowColumn),
            new StringColumn("Name", DataColumn.noFormatting),
            new StringColumn("Severity", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.wideColumn),
    };

    private static final ContractRisk associatedObject = new ContractRisk();
    public ContractRiskTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new ContractRisk(10, "Blocker", "100", "Showstopper/blocker"));
          add(new ContractRisk(20, "High", "75", "Potentially expensive"));
          add(new ContractRisk(30, "Medium", "25", "An elevated risk"));
          add(new ContractRisk(40, "Potential", "25", "unknown impact"));
          add(new ContractRisk(50, "No Risk", "0", "no risk"));
          add(new ContractRisk(60, "Not set", "0", "not set"));
          add(new ContractRisk(70, "Advantage", "-10", "A potential competitive advantage"));



    }};

    public ContractRisk getValue(int id){
        
        return (ContractRisk)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/

    public List<ContractRisk> getAll() throws BackOfficeException{

        List<ContractRisk> list = (List<ContractRisk>)(List<?>) this.Values;

        return list;
    }

    public ContractRisk getValueForName(String riskLevel) {

        for (DataObjectInterface value : this.values) {
            ContractRisk risk = (ContractRisk)value;
            if(risk.getName().equals(riskLevel))
                return risk;
        }

        return null;

    }



}
