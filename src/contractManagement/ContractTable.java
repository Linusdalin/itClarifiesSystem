package contractManagement;

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
 *    Contract - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Contract";
    public static final String TABLE = "Contract";
    private static final String DESCRIPTION = "All contract base data.";

    public enum Columns {Name, File, Ordinal, Type, Status, Message, AnalysisDetails, Description, Project, Owner, Creation, Language, Section, Access, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new StringColumn("File", DataColumn.noFormatting),
            new IntColumn("Ordinal", DataColumn.noFormatting),
            new ConstantColumn("Type", DataColumn.noFormatting, new TableReference("ContractType", "Name")),
            new ConstantColumn("Status", DataColumn.noFormatting, new TableReference("ContractStatus", "Name")),
            new TextColumn("Message", DataColumn.noFormatting),
            new BlobColumn("AnalysisDetails", DataColumn.noFormatting),
            new TextColumn("Description", DataColumn.noFormatting),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new ReferenceColumn("Owner", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new DateColumn("Creation", DataColumn.noFormatting),
            new StringColumn("Language", DataColumn.noFormatting),
            new ReferenceColumn("Section", DataColumn.noFormatting, new TableReference("DocumentSection", "Name")),
            new ConstantColumn("Access", DataColumn.noFormatting, new TableReference("AccessRight", "Name")),
    };

    private static final Contract associatedObject = new Contract();
    public ContractTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
         /* No name column set for table. Using default ( 1 ) */
         // Not set as external
        // Not a constant table
    }

    public ContractTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {




    };
    private static final String[][] TestValues = {

          {"Cannon", "Cannon.docx", "1", "Unknown", "Analysed", "Successfully analysed", "[]", "Printer support Contract", "Demo", "admin", "2014-06-01", "EN", "All Documents", "no", "system"},
          {"Google Analytics", "GA.docx", "2", "Unknown", "Analysed", "Successfully uploaded", "[]", "EULA", "Demo", "admin", "2014-06-01", "EN", "All Documents", "rwd", "system"},



    };

    @Override
    public void clearConstantCache(){

        Contract.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/





    /**************************************************************************************
     *
     *      Adding a new document
     *
     *
     *
     * @param document              - the name of the document
     * @param fileHandler
     * @param creator               - the user creating the instance of the document
     * @param fingerprint           - unique hash to detect if the document is updated
     * @return - return information for the client
     *
     */


    public ContractVersionInstance createNewVersion(Contract document, fileHandling.RepositoryFileHandler fileHandler, PortalUser creator, String fingerprint) throws BackOfficeException{

            String versionName = createVersionName(document);
            DBTimeStamp creationTime = new DBTimeStamp();

            // Create a new version instance

            ContractVersionInstance newInstance = new ContractVersionInstance(versionName, document, fileHandler.toString(), creator, creationTime.getSQLTime().toString(), fingerprint);
            newInstance.store();

            return newInstance;

    }

    /*******************************************************************************************************
     *
     *          Add a new document and grant access right to it
     *
     *
     *
     * @param filename          - document name
     * @param fileHandler
     * @param creator           - owner
     * @param accessRight       - access to grant
     * @param section           - where to put it (directory)
     * @param fingerprint       - unique hash to detect if the document is changed
     * @return - The new version
     *
     */



    public ContractVersionInstance addNewDocument(Project project,
                                                  String filename,
                                                  fileHandling.RepositoryFileHandler fileHandler,
                                                  language.LanguageCode languageCode,
                                                  PortalUser creator,
                                                  AccessRight accessRight,
                                                  DocumentSection section,
                                                  String fingerprint) {

        try{

            ContractStatus status = ContractStatus.getUploaded();

            // Default values for a new document

            ContractType type = ContractType.getUnclassified();
            DBTimeStamp creationTime = new DBTimeStamp();               // Now
            String desc = "Unclassified document";
            String defaultMessage = "Successfully uploaded ";
            String feedback = "[]";

            //PukkaLogger.log(PukkaLogger.Level.INFO, "Adding new document " + name);

            // Get a ordinal number which is last

            int number = getCount(new LookupList()) + 1;

            //    public Contract(String name, long ordinal, DBKeyInterface type, String description, DBKeyInterface project, DBKeyInterface owner, String creation) throws BackOfficeException{


            Contract newDoc = new Contract(filename, fileHandler.getFileName(), number, type, status, defaultMessage, feedback, desc, project, creator, creationTime.getISODate(), languageCode.code, section, accessRight);
            newDoc.store();

            // Grant access

            //userManagement.AccessGrant grant = new userManagement.AccessGrant("new doc Access", newDoc, accessRight, visibility, creator, creationTime.getSQLTime().toString());
            //grant.store();

            PukkaLogger.log(PukkaLogger.Level.MAJOR_EVENT, "User "+ creator.getName()+" creating a new document " + newDoc.getName() + " in project " + project.getName());

            return createNewVersion(newDoc, fileHandler, creator, fingerprint);


        } catch (BackOfficeException e) {
            e.logError("Error adding document");
            return null;
        }

    }

    /************************************************************************************
     *
     *          This is a simple naming convention for versions
     *
     * @param document
     * @return
     */

    private String createVersionName(Contract document) throws BackOfficeException {

        int version = new ContractVersionInstanceTable().getNextVersionForDocument(document);

        return document.getName() + "_v" + version;
    }


}
