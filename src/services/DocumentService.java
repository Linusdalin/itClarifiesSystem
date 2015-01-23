package services;

import analysis.*;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DBKeyInterface;
import document.*;
import document.SimpleStyle;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import search.Keyword;
import search.KeywordTable;
import search.SearchManager2;
import userManagement.PortalUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***************************************************************************''
 *
 *      Base functionality for a document service
 *
 */

public class DocumentService extends ItClarifiesService{

    public static String MODEL_DIRECTORY = "models";


    /*********************************************************************************
     *
     *          Fragment a document
     *
     * @param fileName - the name of the file. Temporary name of the document
     * @param versionInstance - the instance of the document
     * @param fragmenter - The fragmenter to use
     * @throws pukkaBO.exceptions.BackOfficeException
     *
     * //TODO: Create a special class for document/file name to pass around here allowing for more dynamic display (e.g. truncate extensions)
     * //TODO: Accumulate outcome on the analysis and return all the way to frontend
     */


    protected void fragmentDocument(String fileName, ContractVersionInstance versionInstance, FragmentSplitterInterface fragmenter) throws BackOfficeException {

        Contract document = versionInstance.getDocument();
        Project project = document.getProject();

        AutoNumberer autoNumberer = new AutoNumberer();
        ContractRisk defaultRisk = ContractRisk.getNotSet();
        DBTimeStamp analysisTime = new DBTimeStamp();

        List<AbstractFragment> fragments = fragmenter.getFragments();

       // Create empty list to store the fragments in the list for batch storing in the database

        ContractFragmentTable fragmentsToStore = new ContractFragmentTable();
        fragmentsToStore.createEmpty();

        int fragmentNo = 0;
        ContractFragment topFragment = new ContractFragment();

        Set<String> newKeywords = new HashSet<String>();  // To store all new keywords

        PukkaLogger.log(PukkaLogger.Level.ACTION, "*******************Phase II: Fragmenting Document");
        PukkaLogger.log(PukkaLogger.Level.INFO, "Found " + fragmenter.getFragments().size() + " abstract fragments from the parsing");

        SearchManager2 searchManager = new SearchManager2(project, document.getOwner());


        for(AbstractFragment aFragment : fragments){

            try{

                AbstractStructureItem aStructureItem = aFragment.getStructureItem();
                DBKeyInterface structureItemKey = null;
                String fragmentName = createNameFromBody(aFragment);
                //int indentation = aStructureItem.getIndentation();
                int indentation = (int)aFragment.getIndentation();
                int structureNo = aStructureItem.getID();
                boolean newStructureItemCreated = false;

                PukkaLogger.log(PukkaLogger.Level.INFO, "fragment " + fragmentNo + ": ("+ aFragment.getStyle().type.name()+")" + aFragment.getBody() +"     (" +
                        indentation + ": " + aStructureItem.getStructureType().name() + ":" +
                        (aStructureItem.getTopElement() != null ? aStructureItem.getTopElement().getBody() : "--") +")" );


                CellInfo cellinfo = aFragment.getCellInfo();
                if(cellinfo == null)
                    cellinfo = new CellInfo();


                int row = cellinfo.row;
                int column = cellinfo.col;

                // First check if this is an implicit top level fragment that we have not created yet

                if(aFragment.getStyle().type == StructureType.IMPLICIT){


                    PukkaLogger.log(PukkaLogger.Level.INFO, "Creating a new implicit structure item");

                    StructureItem item = new StructureItem(
                            "Implicit Structure Item " + structureNo,
                            fragmentNo,
                            versionInstance,
                            project,
                            structureNo,
                            aStructureItem.getStructureType().name(),
                            indentation);

                    item.store();

                    PukkaLogger.log(PukkaLogger.Level.INFO, "  - Stored");

                    /*
                    structureItemKey = item.getKey();
                    aStructureItem.setKey(structureItemKey.toString());  // Store the key here as all fragments under this structure element will need the key


                    ContractFragment fragment = new ContractFragment(
                            "Implicit",                                     //TODO: This should really be an attribute
                            versionInstance.getKey(),
                            structureNo - 1,                        // Using id as key here for quicker lookup
                            fragmentNo++,
                            "implicit",
                            aFragment.getIndentation(),                //TODO: Remove - Deprecated...
                            StructureType.IMPLICIT.name(),
                            defaultRisk.getKey(),
                            0,     // annotation
                            0,     // reference
                            0,     // classificaton
                            column,
                            row

                    );


                    System.out.println("  -- Adding a fragment " + fragment.getName());
                    fragmentsToStore.add(fragment);

                    */

                    indentation++;     // When creating an implicit element, the indentation automatically increases

                }

                String bodyText = testAddingHeadlineNumber(aFragment, autoNumberer);


                // Now see if this is a new top level item. If that is the case, we create a new structure item for the fragment

                StructureItem item = null;
                ContractFragment fragment;

                if(aStructureItem.getTopElement() == aFragment){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Fragment " + aFragment.getBody() + " is a top item on indentation level " + aFragment.getIndentation());

                    item = new StructureItem(
                            bodyText,
                            fragmentNo,
                            versionInstance,
                            project,
                            structureNo,
                            aStructureItem.getStructureType().name(),
                            aStructureItem.getIndentation());

                    item.store();


                }

                // We want to ignore empty fragments. They are not really needed in the presentation

                if(aFragment.getStyle().type == StructureType.TEXT && aFragment.getBody().equals("")){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring empty fragment");

                }
                else{

                    // Finally we create the actual fragment

                    fragment = new ContractFragment(
                            fragmentName,
                            versionInstance.getKey(),
                            project.getKey(),
                            aStructureItem.getID(),
                            fragmentNo++,
                            bodyText,
                            aFragment.getIndentation(),
                            aFragment.getStyle().type.name(),
                            defaultRisk,
                            0,     // annotation
                            0,     // reference
                            0,     // classificaton
                            0,     // actions
                            column,
                            row,
                            toJSON(cellinfo)

                    );

                    fragmentsToStore.add(fragment);
                    System.out.println("  ***** Adding a fragment " + fragment.getName() + " with ordinal " + (fragmentNo - 1));


                }

            }catch(Exception e){

                e.printStackTrace(System.out);
                PukkaLogger.log(PukkaLogger.Level.FATAL, "Internal Error analysing fragment \"" + aFragment.getBody() + "\" in document "+ document.getName()+ "Error:" + e.getMessage());
            }




            // Store all new keywords from the fragment
            if(aFragment.getKeywords() != null)
                newKeywords.addAll(aFragment.getKeywords());

        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Storing " + fragmentsToStore.getCount() + " fragments for the analysis of the document " + document);
        fragmentsToStore.store(); // Save all

        // Index all fragments in the search engine

        searchManager.indexFragments(fragmentsToStore.getValues(), versionInstance, document);

        // Store the keywords

        storeKeyWords(newKeywords, versionInstance, document, project);

        // Handle comments. The comments are extracted by the parsing and stored with a fragment id,
        // so left to do is to find the corresponding fragment.

        PukkaLogger.log(PukkaLogger.Level.INFO, "** Found " + fragmenter.getComments().size() + " comments in the document" );

        for(AbstractComment aComment : fragmenter.getComments()){

            ContractFragment fragment = new ContractFragment(new LookupItem()
                    .addFilter(new ColumnFilter   (ContractFragmentTable.Columns.Ordinal.name(), aComment.getFragmentId() - 1))
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), versionInstance.getKey())));

            if(!fragment.exists()){

                // Debugging only
                for(ContractFragment f : versionInstance.getFragmentsForVersion()){
                    System.out.println("Fragment: " + f.getName() + "id" + f.getOrdinal());
                }

                PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not find fragment for comment. Fragment id: " + aComment.getFragmentId() + "("+ aComment.getComment()+")");

            }
            else{

                ContractAnnotation annotation = new ContractAnnotation(
                        PortalUser.getExternalUser() + "@" + analysisTime.getSQLTime().toString(),
                        fragment,
                        (long)1,
                        aComment.getComment(),
                        PortalUser.getExternalUser(),
                        versionInstance,
                        aComment.getAnchor(),
                        analysisTime.getSQLTime().toString());

                annotation.store();

                fragment.setAnnotationCount(fragment.getAnnotationCount() + 1);
                fragment.update();


            }

        }

    }

    /******************************************************************************''''
     *
     *
     *
     *
     * @param aFragment
     * @param autoNumberer
     * @return
     *
     *
     *          //TODO: handle numbering restart
     */

    private String testAddingHeadlineNumber(AbstractFragment aFragment, AutoNumberer autoNumberer) {

        String body = aFragment.getBody();

        if(aFragment.getStyle().type != StructureType.HEADING)
            return body;

        if(aFragment.getStyle().numbering == SimpleStyle.Numbering.NONE)
            return body;

        boolean restartCount = aFragment.getStyle().restartNumbering;

        if(restartCount){

            System.out.println(" *** Numbering is set to restart for fragment" + aFragment.getBody());
        }

        String numberPrefix = autoNumberer.getNewNumber((int)aFragment.getIndentation(), restartCount);


        if(numberPrefix.equals(""))
            return body;

        if(startsWithNumber(aFragment.getBody())){

            PukkaLogger.log(PukkaLogger.Level.INFO, "Fragment starts with numbers so we assume numbering has been done manually");
            return body;
        }


        return numberPrefix + " " + body;

    }

    private static boolean startsWithNumber(String body) {

        return body.matches("^(\\d+[\\s]*\\.)[\\s]*.*");
    }


    private String toJSON(CellInfo cellinfo) {

        JSONObject json = new JSONObject()
                .put("backgroundColour", "#" + cellinfo.colour)
                .put("colSpan", cellinfo.span.cols)
                .put("rowSpan", cellinfo.span.rows)
                .put("width", cellinfo.width)
                .put("wrap", cellinfo.wrap)

        ;
        return json.toString();

    }

    /****************************************************************************************
     *
     *              Store the keywords from the analysis
     *
     *
     * @param newKeywords
     * @param version
     * @param document
     * @param project
     * @throws BackOfficeException
     */

    private void storeKeyWords(Set<String> newKeywords, ContractVersionInstance version, Contract document, Project project) throws BackOfficeException {

        KeywordTable keywordTable = new KeywordTable();
        keywordTable.createEmpty();

        for(String k : newKeywords){

            Keyword keyword = new Keyword(k, version, document,  project);
            keywordTable.add(keyword);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Added a bold keyword " + keyword.getKeyword());

        }

        keywordTable.store();

    }

    private String createNameFromBody(AbstractFragment aFragment) {

        return (aFragment.getBody().length() < 30 ? aFragment.getBody() : aFragment.getBody().substring(0, 29));

    }


    /*

    protected void fragmentDocumentOld(String fileName, ContractVersionInstance versionInstance, FragmentSplitterInterface fragmenter) throws BackOfficeException {

        ContractFragmentType listType = ContractFragmentType.getBList();
        ContractFragmentType headlineType = ContractFragmentType.getHeadline();
        DBTimeStamp analysisTime = new DBTimeStamp();
        Contract document = versionInstance.getDocument();
        Project project = document.getProject();


        ContractRisk defaultRisk = ContractRisk.getNone();

        int clauseNumber = 1;

        // The clauses cant be batch stored as we need the DB Key to be able to
        // link the fragments to the clause

        System.out.println("In fragmentDocument: Generated " + fragmenter.getFragments().size() + " fragments");


        for(AbstractClause aClause : fragmenter.getClauses()){

            ContractClause clause = new ContractClause(aClause.getBody(), versionInstance.getKey(), clauseNumber);
            clause.store();

            // Save the key

            aClause.setKey(clause.getKey().toString());
            clauseNumber++;

        }


        int fragmentNo = 0;

        List<AbstractFragment> fragments = fragmenter.getFragments();
        AutoNumberer autoNumberer = new AutoNumberer();  // Use the auto numberer to create numbering in the document

        List<String> definitions = new ArrayList<String>();  // Not implemented - just an empty list

        //TODO: Get the document title from the document by looking for a title element. Before this, we use document title as name.

        String documentName = fileName;

        AbstractProject currentProject = new AbstractProject(); // Empty placeholder for now

        // Create an empty list to store the fragments in.

        ContractFragmentTable fragmentsToStore = new ContractFragmentTable();
        fragmentsToStore.createEmpty();

        Set<String> newKeywords = new HashSet<String>();  // To store all new keywords


        //To keep track of which

        // Make a pass over the fragments to create teh data. We store them in a structure to be able to bach store in the database
        // This means we cant perform the analysis yet as we don't know the fragment id's to store the outcome at.

        for(AbstractFragment aFragment : fragments){



            // Store all new keywords from the fragment

            if(aFragment.getKeywords() != null)
                newKeywords.addAll(aFragment.getKeywords());


            String fragmentName = (aFragment.getBody().length() < 30 ? aFragment.getBody() : aFragment.getBody().substring(0, 29));

            DBKeyInterface clauseId = new DatabaseAbstractionFactory().createKey(aFragment.getClause().getKey());

            ContractFragmentType type = ContractFragmentType.getText();
            String numbering = "";

            if(aFragment.isList())
                type = listType;
            else if(aFragment.isHeading()){

                type = headlineType;
                numbering = autoNumberer.getNewNumber((int) aFragment.getIndentation(), aFragment.isRestart());

                // Update the clause with the numbering

                ContractClause clause = new ContractClause(new LookupByKey(clauseId));
                clause.setName(addNumber( numbering, clause.getName()));
                clause.update();

            }

            PukkaLogger.log(PukkaLogger.Level.INFO, "Creating a fragment " + fragmentName);

            //TODO Add these to the error list, ignoring the fragment

            if(aFragment == null){
                PukkaLogger.log(PukkaLogger.Level.INFO, "aFragment is null!");
            }

            if(aFragment.getClause() == null){
                PukkaLogger.log(PukkaLogger.Level.INFO, "fragment clause is null!");
            }

            int row = 0;
            int column = 0;

            if(aFragment.getCellInfo() != null){

                row = aFragment.getCellInfo().row;
                column = aFragment.getCellInfo().col;
            }


            ContractFragment fragment = new ContractFragment(
                    fragmentName,
                    versionInstance.getKey(),
                    clauseId,
                    fragmentNo++,
                    addNumber(numbering, aFragment.getBody()),  //TODO: Add warning to this too
                    aFragment.getIndentation(),
                    type.getKey(),
                    defaultRisk.getKey(),
                    0,     // annotation
                    0,     // reference
                    0,     // classificaton
                    column,
                    row

            );

            fragmentsToStore.add(fragment);

        }

        System.out.println("Storing " + fragmentsToStore.getCount() + " fragments");
        fragmentsToStore.store(); // Save all


        // Store the keywords:
        KeywordTable keywordTable = new KeywordTable();
        keywordTable.createEmpty();

        for(String k : newKeywords){

            Keyword keyword = new Keyword(k, document,  project);
            keywordTable.add(keyword);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Added a bold keyword " + keyword.getKeyword());

        }

        keywordTable.store();

        // Add comments from the document

        int noComments = fragmenter.getComments().size();
        System.out.println("** Found " + noComments + " comments in the document" );

        for(AbstractComment aComment : fragmenter.getComments()){

            ContractFragment fragment = new ContractFragment(new LookupItem()
                    .addFilter(new ColumnFilter   (ContractFragmentTable.Columns.Ordinal.name(), aComment.getFragmentId() - 1))
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), versionInstance.getKey())));

            if(!fragment.exists()){

                for(ContractFragment f : versionInstance.getFragmentsForVersion()){
                    System.out.println("Fragment: " + f.getName() + "id" + f.getOrdinal());
                }

                PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not find fragment for comment. Fragment id: " + aComment.getFragmentId() + "("+ aComment.getComment()+")");

            }
            else{

                ContractAnnotation annotation = new ContractAnnotation(fragment, (long)1, aComment.getComment(),
                        PortalUser.getExternalUser(),
                        versionInstance, aComment.getAnchor(), analysisTime.toString());
                annotation.store();

                fragment.setAnnotationCount(fragment.getAnnotationCount() + 1);
                fragment.update();


            }


        }


    }

      */


    public static void main(String[] args){

        String test =  "1. test";
        System.out.println(test +  startsWithNumber(test));

        test =  "7 . \tInformation om uppha";
        System.out.println(test +  startsWithNumber(test));

        test =  "1.2 test";
        System.out.println(test +  startsWithNumber(test));

        test =  "test";
        System.out.println(test +  startsWithNumber(test));

    }

}


