package contractManagement;

import risk.*;
import classification.*;
import userManagement.*;
import project.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;

import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;

/********************************************************
 *
 *    Contract - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Contract extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ContractTable();

    public Contract(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Contract(String name, String file, long ordinal, DataObjectInterface type, DataObjectInterface status, String message, String analysisdetails, String description, DataObjectInterface project, DataObjectInterface owner, String creation, String language, DataObjectInterface section, DataObjectInterface access) throws BackOfficeException{

        this(name, file, ordinal, type, status, message, analysisdetails, description, project.getKey(), owner.getKey(), creation, language, section.getKey(), access);
    }


    public Contract(String name, String file, long ordinal, DataObjectInterface type, DataObjectInterface status, String message, String analysisdetails, String description, DBKeyInterface project, DBKeyInterface owner, String creation, String language, DBKeyInterface section, DataObjectInterface access){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(name);
           data[1] = new StringData(file);
           data[2] = new IntData(ordinal);
           data[3] = new ConstantData(type.get__Id(), columns[3].getTableReference());
           data[4] = new ConstantData(status.get__Id(), columns[4].getTableReference());
           data[5] = new TextData(message);
           data[6] = new BlobData(analysisdetails);
           data[7] = new TextData(description);
           data[8] = new ReferenceData(project, columns[8].getTableReference());
           data[9] = new ReferenceData(owner, columns[9].getTableReference());
           data[10] = new DateData(creation);
           data[11] = new StringData(language);
           data[12] = new ReferenceData(section, columns[12].getTableReference());
           data[13] = new ConstantData(access.get__Id(), columns[13].getTableReference());

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Contract(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Contract o = new Contract();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getName(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[0];
        data.setStringValue(name);
    }



    public String getFile(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setFile(String file){

        StringData data = (StringData) this.data[1];
        data.setStringValue(file);
    }



    public long getOrdinal(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setOrdinal(long ordinal){

        IntData data = (IntData) this.data[2];
        data.value = ordinal;
    }



    public ContractType getType(){

        ConstantData data = (ConstantData)this.data[3];
        return (ContractType)(new ContractTypeTable().getConstantValue(data.value));

    }

    public void setType(DataObjectInterface type){

        ConstantData data = (ConstantData)this.data[3];
        data.value = type.get__Id();
    }



    public ContractStatus getStatus(){

        ConstantData data = (ConstantData)this.data[4];
        return (ContractStatus)(new ContractStatusTable().getConstantValue(data.value));

    }

    public void setStatus(DataObjectInterface status){

        ConstantData data = (ConstantData)this.data[4];
        data.value = status.get__Id();
    }



    public String getMessage(){

        TextData data = (TextData) this.data[5];
        return data.getStringValue();
    }

    public void setMessage(String message){

        TextData data = (TextData) this.data[5];
        data.setStringValue(message);
    }



    public String getAnalysisDetails(){

        BlobData data = (BlobData) this.data[6];
        return data.getStringValue();
    }

    public void setAnalysisDetails(String analysisdetails){

        BlobData data = (BlobData) this.data[6];
        data.setStringValue(analysisdetails);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[7];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[7];
        data.setStringValue(description);
    }



    public DBKeyInterface getProjectId(){

        ReferenceData data = (ReferenceData)this.data[8];
        return data.value;
    }

    public Project getProject(){

        ReferenceData data = (ReferenceData)this.data[8];
        return new Project(new LookupByKey(data.value));
    }

    public void setProject(DBKeyInterface project){

        ReferenceData data = (ReferenceData)this.data[8];
        data.value = project;
    }



    public DBKeyInterface getOwnerId(){

        ReferenceData data = (ReferenceData)this.data[9];
        return data.value;
    }

    public PortalUser getOwner(){

        ReferenceData data = (ReferenceData)this.data[9];
        return new PortalUser(new LookupByKey(data.value));
    }

    public void setOwner(DBKeyInterface owner){

        ReferenceData data = (ReferenceData)this.data[9];
        data.value = owner;
    }



    public DBTimeStamp getCreation()throws BackOfficeException{

        DateData data = (DateData) this.data[10];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setCreation(DBTimeStamp creation){

        DateData data = (DateData) this.data[10];
        data.value = creation.getISODate().toString();
    }



    public String getLanguage(){

        StringData data = (StringData) this.data[11];
        return data.getStringValue();
    }

    public void setLanguage(String language){

        StringData data = (StringData) this.data[11];
        data.setStringValue(language);
    }



    public DBKeyInterface getSectionId(){

        ReferenceData data = (ReferenceData)this.data[12];
        return data.value;
    }

    public DocumentSection getSection(){

        ReferenceData data = (ReferenceData)this.data[12];
        return new DocumentSection(new LookupByKey(data.value));
    }

    public void setSection(DBKeyInterface section){

        ReferenceData data = (ReferenceData)this.data[12];
        data.value = section;
    }



    public userManagement.AccessRight getAccess(){

        ConstantData data = (ConstantData)this.data[13];
        return (userManagement.AccessRight)(new userManagement.AccessRightTable().getConstantValue(data.value));

    }

    public void setAccess(DataObjectInterface access){

        ConstantData data = (ConstantData)this.data[13];
        data.value = access.get__Id();
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/




    /****************************************************************************************
     *
     *          Recursively delete a document.
     *          This should remove all instances, clauses and fragments
     *
     * @return - Outcome object listing the number of items deleted
     * @throws BackOfficeException
     *
     *
     *          TODO: Use batch delete (important for fragment)
     *          TODO: Remove risk
     *
     */

    public DocumentDeleteOutcome recursivelyDeleteDocument(boolean deleteIndex) throws BackOfficeException{

        int noFragments = 0;
        int noClauses = 0;
        int noInstances = 0;
        int noAnnotations = 0;
        int noReferences = 0;
        int noClassifications = 0;
        int noKeywords = 0;
        int noFlags = 0;
        int noIndices = 0;
        int noDefinitions = 0;

        SearchManager2 searchManager = new SearchManager2(this.getProject(), this.getOwner());

       // Get all instances

       ContractVersionInstanceTable versions = new ContractVersionInstanceTable(new LookupItem()
               .addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey())));

       for(DataObjectInterface v : versions.getValues()){

           ContractVersionInstance version = (ContractVersionInstance)v;

           // Get all fragments for this version and delete them

           ContractFragmentTable allFragments = new ContractFragmentTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey())));

           noFragments += allFragments.getCount();
           allFragments.delete();

           // Clear the search index

           if(deleteIndex)
                noIndices += searchManager.remove(allFragments);


           // Get all annotations for this version and delete them


           ContractAnnotationTable allAnnotations = new ContractAnnotationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), version.getKey())));

           noAnnotations += allAnnotations.getCount();
           allAnnotations.delete();


           // Get all references for this version and delete them

           ReferenceTable allReferences = new ReferenceTable(new LookupItem()
                   .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Version.name(), version.getKey())));

           noReferences += allReferences.getCount();
           allReferences.delete();

           // Get all classifications for this version and delete them

           FragmentClassificationTable allClassifications = new FragmentClassificationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Version.name(), version.getKey())));

           noClassifications += allClassifications.getCount();
           allClassifications.delete();

           // Get all keywords for this version and delete them

           KeywordTable allKeywords = new KeywordTable(new LookupItem()
                   .addFilter(new ReferenceFilter(KeywordTable.Columns.Version.name(), version.getKey())));

           noKeywords += allKeywords.getCount();
           allKeywords.delete();


            // Get all structure items for this version and delete them

           StructureItemTable allStructureItems = new StructureItemTable(new LookupItem()
                   .addFilter(new ReferenceFilter(StructureItemTable.Columns.Version.name(), version.getKey())));

           noClauses += allStructureItems.getCount();
           allStructureItems.delete();

            // Get all risk for this version and delete them


           RiskClassificationTable allRiskClassifications = new RiskClassificationTable(new LookupItem()
                   .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Version.name(), version.getKey())));


           noFlags += allRiskClassifications.getCount();
           System.out.println("Found " + noFlags + " risks to delete for document" + getName() + "(version " + version.getVersion() + ")");

           allRiskClassifications.delete();

           // Definitions

           DefinitionTable allDefinitions = new DefinitionTable(new LookupItem()
                   .addFilter(new ReferenceFilter(DefinitionTable.Columns.Version.name(), version.getKey())));


           noDefinitions += allDefinitions.getCount();
           System.out.println("Found " + noDefinitions + " definitions to delete for document" + getName() + "(version " + version.getVersion() + ")");

           allDefinitions.delete();


           // Delete the version

           services.DocumentService.invalidateFragmentCache(version);
           versions.deleteItem(version);
           noInstances++;
       }

        // Now delete the actual document

       new ContractTable().deleteItem(this);

        // Clear the caches

        services.DocumentService.invalidateDocumentCache(this, this.getProject());

        return new DocumentDeleteOutcome(1, noInstances, noClauses, noFragments, noAnnotations, noClassifications, noFlags, noReferences, noKeywords, noDefinitions, noIndices, 0, 0);

    }


    //TODO: Should be automatic

    public List<ContractVersionInstance> getVersionsForDocument(ConditionInterface condition) throws BackOfficeException{

        condition.addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey()));

        List<DataObjectInterface> objects = new ContractVersionInstanceTable(condition).getValues();

        List<ContractVersionInstance> versions = (List<ContractVersionInstance>)(List<?>) objects;

        return versions;
    }




    // No condition retrieves all items

    public List<ContractVersionInstance> getVersionsForDocument() throws BackOfficeException{

        return getVersionsForDocument(new LookupList());
    }



    /******************************************************************************'
     *
     *          Lookup the corresponding version for a snapshot.
     *
     *          This is done by checking the time
     *
     * @param freezeSnapshot
     * @return
     * @throws BackOfficeException
     */

    public ContractVersionInstance getVersionForSnapshot(Snapshot freezeSnapshot) throws BackOfficeException {



        DBTimeStamp snapShotTime = freezeSnapshot.getTimestamp();
        List<ContractVersionInstance> versionList = getVersionsForDocument(new LookupList(new Sorting(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.FIRST)));
        ContractVersionInstance last = null;

        for(ContractVersionInstance version : versionList){

            if(version.getCreation().isBefore(snapShotTime))
                last = version;
            else{

                if(last == null)
                    throw new BackOfficeException(BackOfficeException.TableError,
                            "No version found for document " + getName() + " and snapshot " + freezeSnapshot.getName() + "(@ " + snapShotTime.getSQLTime().toString() + ")");
                return last;
            }


        }

        return last;

    }

    public ContractVersionInstance getHeadVersion() throws BackOfficeException {

        ContractVersionInstance last = new ContractVersionInstance(new LookupItem()
                .addFilter(new ReferenceFilter(ContractVersionInstanceTable.Columns.Document.name(), getKey()))
                .addSorting(new Sorting(ContractVersionInstanceTable.Columns.Creation.name(), Ordering.LAST)));

        if(!last.exists())
            throw new BackOfficeException(BackOfficeException.TableError, "Could not find last version for document " + this.getName());

        return last;
    }

    /*****************************************************************************************
     *
     * @param creator
     * @param fileHandler
     * @param fingerprint
     * @return
     * @throws BackOfficeException
     */

    public ContractVersionInstance addNewVersion(PortalUser creator, fileHandling.RepositoryFileHandler fileHandler, String fingerprint) throws BackOfficeException{

        return new ContractTable().createNewVersion(this, fileHandler, creator, fingerprint);

    }

    public document.AbstractDocument createAbstractDocument(document.AbstractProject aProject) throws BackOfficeException {

        ContractVersionInstance latestVersion = getHeadVersion();
        return latestVersion.createAbstractDocumentVersion(aProject, new language.LanguageCode(getLanguage()));


    }


    private static final String[] types = { "HEADING", "TEXT", "LISTITEM", "TABLE", "IMPLICIT", "IMAGE"};


    /**********************************************************************************************'
     *
     *
     * @param editable - is it possible to edit (adding form for changing style)
     * @return - html version of the document for backoffice view
     * @throws BackOfficeException
     *
     */

    public String getInternalView(boolean editable) throws BackOfficeException {

        StringBuffer html = new StringBuffer();

        boolean tableMode = false;      // Are we in a table or not? Tables will compile all cells into one display
        long tableRow = 0;

        ContractVersionInstance version = this.getHeadVersion();

        String versionName = version.getVersion();

        //versionName = new String(version.getVersion().getBytes(), "ISO-8859-1");
        try {
            versionName = new String(version.getVersion().getBytes(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            //Swallow
        }


        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Getting fragments for version " + version.getVersion());
        html.append("<p>Version: "+ versionName+"</p>");

        // Load all the fragments for the document

        List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));
        StructureItem[] structureItemsForDocument = version.getStructureItemsForVersionAsArray(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));

        html.append("<table width=\"100%\">");


        /*
          This is only for printing.Remove for optimization

        int j=0;
        for(StructureItem structureItem : structureItemsForDocument){

            System.out.println("SI:" + j + " no:" + structureItem.getOrdinal() + " (" + structureItem.getType() + ") top->" + structureItem.getFragmentForStructureItem().getName() );

            j++;
        }



        int i =0;
        for(ContractFragment fragment : fragmentsForDocument){

            System.out.println("structureItem for fragment " + fragment.getOrdinal() + " = " + fragment.getStructureNo());

            StructureItem structureItem = structureItemsForDocument[(int)fragment.getStructureNo()];

            System.out.println(" Fragment " + i++ + ": (i:" + structureItem.getIndentation() + " style: " + fragment.getType() + " struct: " + structureItem.getType() + "/" +
                    " name: " + structureItem.getName() + ") " + fragment.getName());

        }
        */


        if(fragmentsForDocument == null){

            html.append("Table is empty...");

        }
        else{

            StringBuffer style = null;
            StringBuffer body = null;
            StringBuffer comments = null;


            // Loop over all table fragments

            for(ContractFragment fragment : fragmentsForDocument){

                System.out.println(" *** Displaying fragment (" + fragment.getType() + ") "+ fragment.getText());

                if(!tableMode){

                    // Clear all from last fragment.

                    style = new StringBuffer();
                    body = new StringBuffer();
                    comments = new StringBuffer();

                }

                if(fragment.getType().equals("TABLE") && !tableMode){

                    // Entering table mode
                    tableMode = true;
                    tableRow = 0;
                    System.out.println(" *** Entering table mode!");

                }else if(!fragment.getType().equals("TABLE") && tableMode){

                    // Leaving table mode. Create the (multi) line

                    System.out.println(" *** Found fragment type: " + fragment.getType()+ "Leaving table mode!");

                    if(editable)
                        html.append(createLine(this, fragment, style, body , comments));
                    else
                        html.append(createLine(null, null, style, body , comments));

                    tableMode = false;

                    // And Clear all from last fragment.

                    style = new StringBuffer();
                    body = new StringBuffer("");
                    comments = new StringBuffer();

                }else if(fragment.getxPos() == 0 && !body.toString().equals("")){

                    // Leaving table mode. Create the (multi) line
                    System.out.println(" *** Found new line. Writing table content: " + body);

                    if(editable)
                        html.append(createLine(this, fragment, style, body , comments));
                    else
                        html.append(createLine(null, null, style, body , comments));

                    // And Clear all from last fragment.

                    style = new StringBuffer();
                    body = new StringBuffer("");
                    comments = new StringBuffer();

                }


                // Display Annotations

                List<ContractAnnotation> annotations = fragment.getAnnotationsForFragment();

                for(ContractAnnotation annotation : annotations){

                    comments.append(" Annotation: \"" + annotation.getDescription() + "\"( for " + annotation.getPattern() + ") by " + annotation.getCreator().getName() + "@" + annotation.getTime().getISODate() +
                            "<br/>");
                }



               //comments.append("Annotations: " + fragment.getAnnotationCount() + " (fragment ac:"+ fragment.getAnnotationCount()+")</br>");


                // Add classifications
                List<FragmentClassification> classifications = fragment.getClassificationsForFragment();

                for(FragmentClassification classification : classifications){

                    switch((int)classification.getblockingState()){

                        case FragmentClassification.GENERATED:
                            comments.append(" Classification: \"" + classification.getClassTag() + "\"(" + classification.getSignificance() + ")(\""+  classification.getPattern() + "\" " +
                                    classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() + "@" + classification.getTime().getISODate() +
                                    " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() +"\"<br/>");
                            break;

                        case FragmentClassification.BLOCKED:
                            comments.append(" Blocked(invisible) Classification: \"" + classification.getClassTag() + "\"(" + classification.getSignificance() + ")(\""+  classification.getPattern() + "\" " +
                                    classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() + "@" + classification.getTime().getISODate() +
                                    " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() +"\"<br/>");
                            break;

                        case FragmentClassification.BLOCKING:
                            comments.append(" Blocking: \"" + classification.getClassTag() + "\" for)\""+  classification.getPattern() + "\" " +
                                    classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() +
                                    "@" + classification.getTime().getISODate() +
                                    "\"<br/>");
                            break;

                        case FragmentClassification.IMPORTED:

                            comments.append(" Imported Classification: \"" + classification.getClassTag() + "\"(" + classification.getSignificance() + ")(\""+  classification.getPattern() + "\" " +
                                    classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() + "@" + classification.getTime().getISODate() +
                                    " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() +"\"<br/>");
                            break;
                        case FragmentClassification.OVERRIDDEN:
                            comments.append(" Overridden Classification: \"" + classification.getClassTag() + "\"(" + classification.getSignificance() + ")(\""+  classification.getPattern() + "\" " +
                                    classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() + "@" + classification.getTime().getISODate() +
                                    " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() +"\"<br/>");
                            break;

                        default:


                    }







                }

                // Add Definitions
                List<Definition> definitions = fragment.getDefinitionsForFragment();

                for(Definition definition : definitions){

                    comments.append(" Definition of: \"" + definition.getName());
                    if(!definition.getType().equals(document.DefinitionType.REGULAR.name()))
                        comments.append(" - " + definition.getType());
                    comments.append("\"<br/>");
                }

               // Add references
                List<Reference> references = fragment.getReferencesForFragment();

                for(Reference reference : references){

                    if(reference.getType().equals(ReferenceType.getOpen()))
                        comments.append(" Open Reference " + reference.getName() +" <br/>");
                    else{

                        // Reference to. We are assuming that the "to"-fragment exists

                        if(reference.getTo() == null){

                            PukkaLogger.log(PukkaLogger.Level.FATAL, "No To-reference in the closed reference " + reference.getName());
                            comments.append("Invalid to-reference... " + reference.getName());

                        }
                        else{

                            comments.append(" Reference To: " + reference.getTo().getName() + " for \""+ reference.getName()+"\"( type "+ reference.getType().getName() + "@"+ reference.getPatternPos()+") <br/>");
                        }
                    }
                }


                // Display Actions

                List<Action> actions = fragment.getActionsForFragment();

                for(Action action : actions){

                    comments.append(" Action: \"" + action.getDescription() + "\"( for " + action.getPattern() + ") " + action.getIssuer().getName() + " -> " + action.getAssignee().getName() + "@" + action.getCreated().getISODate() +
                            "<br/>");
                }




                // Add risk
                try{

                    if(!fragment.getRisk().equals(ContractRisk.getNotSet())){

                        RiskClassification classification = fragment.getLastRiskClassificationForFragment(RiskClassificationTable.Columns.Time.name());

                        comments.append(" Risk: Lvl \""+ classification.getRisk().getName()+"\" (\"" + classification.getPattern() + "\") by "+
                                classification.getCreator().getName() + "@" + classification.getTime().getISODate() + " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() + "\"<br/>");
                    }

                }catch(BackOfficeException e){


                    PukkaLogger.log(PukkaLogger.Level.WARNING, "No classification found for fragment " + fragment.getName());
                }

                comments.append( fragment.getKey().toString() + pukkaBO.style.Html.newLine());


                if(tableMode){

                    if(fragment.getyPos() > tableRow){

                        // New row

                        tableRow = fragment.getyPos();
                        body.append("<div style=\"clear:both\">&nbsp;</div>\n");

                    }

                    // Create a table cell

                    body.append("<div style=\"float:left\">"+fragment.getText()+"</div>\n");

                }else if(fragment.getType().equals(document.StructureType.HEADING.name())){

                    body.append("<b>"+fragment.getText()+"</b>");

                }else if(fragment.getType().equals(document.StructureType.TEXT.name())){

                    body.append(fragment.getText());

                }else if(fragment.getType().equals(document.StructureType.IMPLICIT.name())){

                    body.append("");

                }else if(fragment.getType().equals(document.StructureType.LISTITEM.name())){

                    // This is a list item. However, only items with indentation are actually new list items. The rest is
                    // continuation of existing list bullets.

                    if(fragment.getIndentation() >= 0){

                        // Calculate an appropriate indentation for the css. This is a bit arbitrary for the display in the backoffice
                        long indentationInEm = 2 * (fragment.getIndentation() + 1);

                        body.append("<li style=\"margin-left: "+indentationInEm+"em;\">" + fragment.getText() + "</li>");

                        //body.append(fragment.getText());

                    }
                    else{

                        body.append(fragment.getText());

                    }

                }else{

                    //style = "Type: " + fragment.getType();
                    body.append(fragment.getText());

                }


                //System.out.println("Style = " + fragment.getType());

                if(!tableMode){

                    style.append(" ( i: " + fragment.getIndentation());
                    style.append(" S: " + fragment.getStructureNo());
                    style.append(" o: " + fragment.getOrdinal());
                    style.append(")");


                    // Only generate a line if it is not a table. The table is withheld and generated upon leaving the table

                    if(editable)
                        html.append(createLine(this, fragment, style, body , comments));
                    else
                        html.append(createLine(null, null, style, body , comments));

                }

            }

            if(tableMode){

                System.out.println(" *** Ending in table mode. Closing up!");

                if(editable)
                    html.append(createLine(this, null, style, body , comments));
                else
                    html.append(createLine(null, null, style, body , comments));

            }
        }

        html.append("</table>");

        return html.toString();
    }


    /*************************************************************************'
     *
     *          Create one line in the preview
     *
     *              NOTE: The style form is a back-office function
     *
     *
     * @param contract
     * @param fragment
     * @param textTags
     * @param fragmentText
     * @param analysisTags   @return    */

    private String createLine(Contract contract, ContractFragment fragment, StringBuffer textTags, StringBuffer fragmentText, StringBuffer analysisTags){

        String styleForm = "";
        String fragmentBody = "";

        try{

            if(fragment != null){

                ContractFragmentTypeTable allTypes = new ContractFragmentTypeTable();
                styleForm = "<FORM METHOD=POST action=\"?id="+contract.getKey().toString()+
                        "&list=DocumentList" +
                        "&action=Item" +
                        "&callbackAction="+ backend.DocumentList.Callback_Action_ChangeStyle+
                        "&section=Documents" +
                        "&fragment="+fragment.getKey().toString()+"\" name=\"styleForm\">\n";
                //styleForm += allTypes.getDropDown().generate("styleType", fragment.getType(), null, null, true);

                styleForm += pukkaBO.style.Html.dropDown("styleType", types, fragment.getType(), " onchange='this.form.submit()'", "stylaType");
                //styleForm += fragment.getType();
                styleForm += "</FORM>";

                fragmentBody = fragmentText.toString();
                fragmentBody = fragmentBody.replace("$(_SESSION)", "DummyAdminToken");

            }

        }catch(Exception e){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not get style for fragment " + fragmentText);
        }




        return "<tr>" +
                  "<td width=\"10%\">"+ textTags.toString()+"</td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"10%\">"+ styleForm + "</td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"40%\">"+ fragmentBody +"</td>" +
                  "<td width=\"30px\">"+ new backend.EditFragmentDetailsLightbox().getLink(fragment) +"</td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"40%\">"+ analysisTags.toString() +"</td>" +
                "</tr>";
    }


    public static String htmlDecode(String text){

        return text
                .replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ")

                ;

    }



    public String htmlDecode(){

        return htmlDecode(getName());

    }




}
