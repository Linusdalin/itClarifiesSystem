package userManagement;

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
 *    AccessRight - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class AccessRightTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Access Right";
    public static final String TABLE = "AccessRight";
    private static final String DESCRIPTION = "Access writes";

    public enum Columns {Id, Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.narrowColumn),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.wideColumn),
    };

    private static final AccessRight associatedObject = new AccessRight();
    public AccessRightTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new AccessRight(10, "no", "No access"));
          add(new AccessRight(20, "ro", "Read only access"));
          add(new AccessRight(30, "rc", "Read and comment access"));
          add(new AccessRight(40, "rwd", "Read write and delete access"));



    }};

    public AccessRight getValue(int id){
        
        return (AccessRight)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    public AccessRight getValueByName(String name) {

        for (DataObjectInterface value : this.values) {
            AccessRight item = (AccessRight)value;
            if(item.getName().equals(name))
                return item;
        }

        return new AccessRight();

    }




}
