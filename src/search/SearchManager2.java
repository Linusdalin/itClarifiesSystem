package search;

import com.google.appengine.api.search.*;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.FragmentClassification;
import contractManagement.Project;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import userManagement.PortalUser;

import java.util.Arrays;
import java.util.List;

/**
 *              This is the new Search manager for index search.
 *
 *              It uses the IndexManager that wraps most of the search engine functionality
 *              and maps the data representation to the index functionality
 *
 *              The project is used as index group for all searches, so it is defining the boundaries for the search
 *
 */

public class SearchManager2 {

    private IndexManager indexManager;
    private PortalUser activeUser;


    public SearchManager2(Project project, PortalUser user){

        indexManager = new IndexManager(project.getName());
        activeUser = user;
    }





    public void indexFragment(ContractFragment fragment, Contract document) {

        Document indexDocument = createDocument(fragment, document);
        indexManager.indexDocument(indexDocument);

    }

    //TODO: Use batch index with 200 items at a time

    public void indexFragments(List<DataObjectInterface> values, Contract document) {

        for (DataObjectInterface value : values) {

            ContractFragment fragment = (ContractFragment)value;
            Document indexDocument = createDocument(fragment, document);
            indexManager.indexDocument(indexDocument);

        }
    }


    private Document createDocument(ContractFragment fragment, Contract document) {

        int visibility = getVisibilityForDocument(document);

        return indexManager.createDocument(
                fragment.getText(),
                fragment.getKey().toString(),
                document.getKey().toString(),
                document.getOwnerId().toString(),
                (int)fragment.getStructureNo(),
                "",
                visibility,
                (int)fragment.getOrdinal());
    }

    /*****************************************************************************************
     *
     *          Update a indexed fragment with the classification
     *
     *
     * @param fragment                  - the fragment
     * @param fragmentClassification    - the classification to put on the fragment
     *
     *          //TODO: a.k.a. classifications not implemented
     */

    public void updateIndexWithClassification(ContractFragment fragment, FragmentClassification fragmentClassification) {

        tagFragment(fragment, fragmentClassification.getClassTag(), fragmentClassification.getKeywords(), fragmentClassification.getPattern());

    }

    /*****************************************************************************
     *
     *          Update the indexed fragment with a risk tagging
     *
     *
     * @param fragment
     * @param risk
     *
     *
     */


    public void updateIndexWithRisk(ContractFragment fragment, RiskClassification risk) {

        tagFragment(fragment, risk.getRisk().getName(), risk.getComment(), risk.getPattern());

    }


    private void tagFragment(ContractFragment fragment, String tag, String keywords, String pattern){

        // Get the index document from the index

        Document existingDocument = indexManager.getDocument(fragment.getKey().toString());

        // Add both the tag and the keywords (including the parent and aka tags) to the keyword field

        KeywordFieldHandler keywordField = new KeywordFieldHandler(existingDocument.getOnlyField(IndexManager.KEYWORD_FIELD).getText());
        keywordField.addTag(tag + keywords, pattern);

        // Clone the existing document, update the keyword field with the classifications and finally resubmit it for indexing.
        // This will replace the existing index document (as it has the same key)

        Document newDocument = indexManager.createDocument(
                existingDocument.getOnlyField(IndexManager.CONTENT_FIELD).getText(),
                existingDocument.getId(),
                existingDocument.getOnlyField(IndexManager.DOCUMENT_FIELD).getText(),
                existingDocument.getOnlyField(IndexManager.OWNER_FIELD).getText(),
                existingDocument.getOnlyField(IndexManager.PARENT_FIELD).getNumber().intValue(),
                keywordField.encode(),
                existingDocument.getOnlyField(IndexManager.VISIBILITY_FIELD).getNumber().intValue(),
                existingDocument.getOnlyField(IndexManager.ORDINAL_FIELD).getNumber().intValue());

        indexManager.indexDocument(newDocument);



    }



    public void clear(){

        indexManager.clear();
    }


    /******************************************************'
     *
     *      //TODO: This should be handled with an enum in Contract.
     *
     * @param document
     * @return
     */

    private int getVisibilityForDocument(Contract document) {


        if(document.getAccess().equals("no")){

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got access level " + document.getAccess() + " for document " + document.getName() + " setting PRIVATE access for fragment");
            return IndexManager.PRIVATE;
        }
        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got access level " + document.getAccess() + " for document " + document.getName() + " setting PUBLIC access for fragment");
        return IndexManager.PUBLIC;

    }

    /***********************************************************************'
     *
     *      Lookup fragments matching the search string and filter on visibility
     *      It searches in the project given in the constructor
     *
     *      //TODO: It would be possible to return keys as JSON for optimization.
     *
     * @param searchString  - search string
     * @return
     *
     *          NOTE: For efficiency, all analysis has to be done with indexed
     *          parameters. The lookup cant access the individual fragments
     */


    public JSONArray search(String searchString) {

        JSONArray fragmentList = new JSONArray();
        DatabaseAbstractionFactory keyFactory = new DatabaseAbstractionFactory();  // For key creation
        String[] searchWords = searchString.split(" ");     // TODO: More advanced split

        try {

            Results<ScoredDocument> results = indexManager.search(searchString);

            for (ScoredDocument result : results) {

                int visibility = result.getOnlyField(IndexManager.VISIBILITY_FIELD).getNumber().intValue();
                DBKeyInterface _owner = keyFactory.createKey(result.getOnlyField(IndexManager.OWNER_FIELD).getText());

                if(visibility == IndexManager.PUBLIC || _owner.toString().equals(activeUser.getKey().toString())){

                    JSONObject hit = createHit(result, searchWords);
                    fragmentList.put(hit);
                }else{

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got access " + visibility + " for fragment and owner("+ _owner.toString()+") != activeUser("+ activeUser.getKey().toString()+"). IGNORING");

                }
            }


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
        }

        return fragmentList;

    }

    //TODO: Add search patterns to list

    private JSONObject createHit(ScoredDocument result, String[] searchWords) {

        JSONArray patternList = getPatternsForHit(result, searchWords);

        JSONObject hit = new JSONObject()
                .put("fragment", result.getId())
                .put("document", result.getOnlyField(IndexManager.DOCUMENT_FIELD).getText())
                .put("ordinal", result.getOnlyField(IndexManager.ORDINAL_FIELD).getNumber())
                .put("patternlist", patternList);


        return hit;
    }

    private JSONArray getPatternsForHit(ScoredDocument result, String[] searchWords) {

        JSONArray patternList = new JSONArray();

        KeywordFieldHandler keywordFieldHandler = new KeywordFieldHandler(result.getOnlyField(IndexManager.KEYWORD_FIELD).getText());
        PukkaLogger.log(PukkaLogger.Level.DEBUG, "search words:" + Arrays.asList(searchWords).toString());

        for (String word : searchWords) {

            if(word.startsWith("#")){

                String pattern = keywordFieldHandler.getPatternForTag( word );
                patternList.put(new JSONObject().put("pattern", pattern));

            }
            else{

                patternList.put(new JSONObject().put("pattern", word));
            }

        }

        return patternList;

    }


}
