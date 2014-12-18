package userManagement;

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
 *    PortalSession - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class PortalSessionTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Session";
    public static final String TABLE = "PortalSession";
    private static final String DESCRIPTION = "Active and closed sessions for all users";

    public enum Columns {User, Token, IP, Start, Latest, Status, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new ReferenceColumn("User", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new StringColumn("Token", DataColumn.noFormatting),
            new StringColumn("IP", DataColumn.noFormatting),
            new TimeStampColumn("Start", DataColumn.noFormatting),
            new TimeStampColumn("Latest", DataColumn.noFormatting),
            new ReferenceColumn("Status", DataColumn.noFormatting, new TableReference("SessionStatus", "Name")),
    };

    private static final PortalSession associatedObject = new PortalSession();
    public PortalSessionTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
        nameColumn = 2;
        // Not set as external
        // Not a constant table
    }

    public PortalSessionTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {

          {"itClarifies", "SystemSessionToken", "127.0.0.1", "2020-05-01 00:00:00", "2014-05-01 00:00:00", "open", "system"},



    };
    private static final String[][] TestValues = {

          {"demo", "DummySessionToken", "127.0.0.1", "2014-05-01 00:00:00", "2015-05-01 00:00:00", "open", "system"},
          {"admin", "DummyAdminToken", "127.0.0.1", "2014-05-01 00:00:00", "2015-05-01 00:00:00", "open", "system"},
          {"eve", "DummyEveToken", "127.0.0.1", "2014-05-01 00:00:00", "2015-05-01 00:00:00", "open", "system"},



    };

    @Override
    public void clearConstantCache(){

        PortalSession.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




    /*************************************************************************************
     *
     *          Create a new session given username and password
     *
     * @param user - the user as entered in the login
     * @return - a new session
     * @throws BackOfficeException
     */



    public PortalSession createNewSession(PortalUser user, String token, String ipAddress) throws BackOfficeException {


        DBTimeStamp startTime = new DBTimeStamp();  // Now
        SessionStatus status;

        status = SessionStatus.getopen();
        PortalSession session = new PortalSession(user, token, ipAddress, startTime.getSQLTime().toString(), startTime.getSQLTime().toString(), status);
        session.store();
        return session;


    }


}
