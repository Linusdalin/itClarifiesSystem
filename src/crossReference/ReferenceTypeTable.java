package crossReference;

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
 *    ReferenceType - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ReferenceTypeTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Reference Type";
    public static final String TABLE = "ReferenceType";
    private static final String DESCRIPTION = "Type of reference";

    public enum Columns {Id, Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.noFormatting),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
    };

    private static final ReferenceType associatedObject = new ReferenceType();
    public ReferenceTypeTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new ReferenceType(1, "Unknown", "Unknown or unclassified reference"));
          add(new ReferenceType(2, "Explicit", "An explicit reference to another clause or document"));
          add(new ReferenceType(3, "Implicit", "An implicit or derived reference"));
          add(new ReferenceType(4, "Definition Usage", "A reference to a definition"));
          add(new ReferenceType(5, "Open", "A link to external or unknown destination"));



    }};

    public ReferenceType getValue(int id){
        
        return (ReferenceType)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
