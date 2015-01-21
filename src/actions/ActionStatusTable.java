package actions;

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
 *    ActionStatus - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ActionStatusTable extends ConstantTable implements DataTableInterface{

    private static final String TITLE = "Action Status";
    public static final String TABLE = "ActionStatus";
    private static final String DESCRIPTION = "Status updates and changes for a document";

    public enum Columns {Id, Name, Comment, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new IntColumn("Id", DataColumn.narrowColumn),
            new StringColumn("Name", DataColumn.noFormatting),
            new TextColumn("Comment", DataColumn.wideColumn),
    };

    private static final ActionStatus associatedObject = new ActionStatus();
    public ActionStatusTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, Values);
        nameColumn = 2;
    }

    public static final List<DataObjectInterface> Values = new ArrayList<DataObjectInterface>() {{

          add(new ActionStatus(10, "Open", "Action created but not assigned"));
          add(new ActionStatus(20, "In Progress", "Action started"));
          add(new ActionStatus(30, "Completed", "Work completed"));
          add(new ActionStatus(40, "Blocked", "Waiting for external factors"));
          add(new ActionStatus(60, "Cancelled", "No action needed"));
          add(new ActionStatus(70, "Accepted", "Completed and approved"));



    }};

    public ActionStatus getValue(int id){
        
        return (ActionStatus)super.getConstantValue( id );
    }
    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
