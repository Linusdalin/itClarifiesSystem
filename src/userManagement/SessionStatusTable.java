package userManagement;

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
 *    SessionStatus - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class SessionStatusTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Session Status";
    public static final String TABLE = "SessionStatus";
    private static final String DESCRIPTION = "Session Status Constants";

    public enum Columns {Id, Name, Description, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.narrowColumn),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.wideColumn),
    };

    private static final SessionStatus associatedObject = new SessionStatus();
    public SessionStatusTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new SessionStatus(10, "open", "Active open sessions"));
          add(new SessionStatus(20, "closed", "Manually closed session!"));
          add(new SessionStatus(30, "timeout", "Implicitly closed sessions"));
          add(new SessionStatus(40, "failed", "Login Fail"));



    }};

    public SessionStatus getValue(int id){
        
        return (SessionStatus)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




}
