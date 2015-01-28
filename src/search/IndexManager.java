package search;

import com.google.appengine.api.search.*;
import contractManagement.ContractFragment;
import contractManagement.ContractFragmentTable;
import contractManagement.ContractVersionInstance;
import dataRepresentation.DataObjectInterface;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;

import java.util.ArrayList;
import java.util.List;


/****************************************************************************************
 *
 *              Search Index wrapper
 *
 *
 *              This is built with the app engine search API and is used in the SearchManager2
 *
 *              The wrapper leaks the search result types, but these could quite easily be changed in the surrounding class.
 *
 *              // TODO: Batch index up to 200 documents   (with sublist)
 *
 */


public class IndexManager {

    private static SearchService searchService = SearchServiceFactory.getSearchService();
    private Index index;

    // The spsecific Field names

    public static final String DOCUMENT_FIELD = "Document";
    public static final String VERSION_FIELD = "Version";
    public static final String CONTENT_FIELD = "Content";
    public static final String PARENT_FIELD = "Parent";
    public static final String KEYWORD_FIELD = "Keyword";
    public static final String VISIBILITY_FIELD = "Visibility";
    public static final String OWNER_FIELD = "Owner";
    public static final String ORDINAL_FIELD = "Ordinal";


    //Visibility keys for the visibility field in the index document


    public static final int PRIVATE = 1;
    public static final int TEAM = 2;           // Not implemented in v 1.0
    public static final int PUBLIC = 3;

    /*************************************************************************'
     *
     *          Creating an index manager with a name
     *
     *          The name is the scope of future searches. Creating to IndexManagers with the
     *          same name will index and search in the same scope.
     *
     *
     * @param name - name of the index.
     */


    public IndexManager(String name){

        // Wash name

        String indexName = name.replaceAll(" ", "_")
                .replaceAll("å", "aa")
                .replaceAll("ä", "ae")
                .replaceAll("ö", "oe");

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Creating index manager " + indexName);
        IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
        index = searchService.getIndex(indexSpec);

    }

    /**************************************************************
     *
     *          Index a document.
     *
     *          NOTE: If a document with the same id exists, it will be replaced in the search index
     *
     * @param document - the index document, created by the create document method
     *
     *          The indexing will be retried if it fails
     */

    public void indexDocument(Document document) {


        int retries = 3;

        while(retries > 0){

            try {

                index.put(document);
                return;

            } catch (PutException e) {

                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode()))
                    retries--;
                else retries = 0;
            }
        }
    }

    /*****************************************************************************
     *
     *              Create a document (storable fragment in the indexing)
     *
     *
     * @param text          - the actual fragment text
     * @param id            - ContractFragment key, for retrieval
     * @param version       - the document version (contract)
     * @param document      - the document (contract)
     * @param owner         - PortalUser key
     * @param structureItem - structure Item (headline) for widened search
     * @param keywords      - keywords (classification tags etc.
     * @param visibility    - visibility of the document
     * @param ordinal       - fragment numbering (for display in the frontend)

     * @return              - the created index document
     *
     */

    public Document createDocument(String text, String id, String version, String document, String owner, int structureItem, String keywords, int visibility, int ordinal){

        Document doc = Document.newBuilder()
                .setId(id)
                .addField(Field.newBuilder().setName(CONTENT_FIELD).setText(text))
                .addField(Field.newBuilder().setName(VERSION_FIELD).setText(version))
                .addField(Field.newBuilder().setName(DOCUMENT_FIELD).setText(document))
                .addField(Field.newBuilder().setName(KEYWORD_FIELD).setText(keywords))
                .addField(Field.newBuilder().setName(VISIBILITY_FIELD).setNumber(visibility))
                .addField(Field.newBuilder().setName(OWNER_FIELD).setText(owner))
                .addField(Field.newBuilder().setName(ORDINAL_FIELD).setNumber(ordinal))
                .addField(Field.newBuilder().setName(PARENT_FIELD).setNumber(structureItem))
            .build();


        return doc;
    }

    /**********************************************************************
     *
     *      Get document per name
     *
     * @param id - the fragment key
     * @return
     */

    public Document getDocument(String id){

        return index.get( id );
    }

    /*************************************************************
     *
     *              Perform the search
     *
     * @param searchString - the search string as derived from the user
     * @return             - The matched document (with scoring information)
     *
     * @throws BackOfficeException
     */

    public Results<ScoredDocument> search(String searchString) throws BackOfficeException{

        int retries = 3;

        while(retries > 0){

            try {

                Results<ScoredDocument> results = index.search(searchString);
                return results;

            } catch (SearchException e) {

                if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                    retries--;
                }
                else retries = 0;
            }

        }


        throw new BackOfficeException(BackOfficeException.General, "Could not search");
    }


    /****************************************************************
     *
     *          Clear all elements in the index
     *
     */


    public void clear() {

        // looping because getRange by default returns up to 100 documents at a time

        while (true) {

            List<String> docIds = new ArrayList<String>();

            // Return a set of doc_ids.
            GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
            GetResponse<Document> response = index.getRange(request);

            if (response.getResults().isEmpty())
                break;

            for (Document doc : response) {

                docIds.add(doc.getId());
            }

            index.delete(docIds);
        }


    }




    public int clear(ContractFragmentTable fragments) {

        List<String> keyList = new ArrayList<String>();

        for (DataObjectInterface fragment : fragments.getValues()) {

            keyList.add(fragment.getKey().toString());

            if(keyList.size() >=200){
                index.delete(keyList);
                keyList.clear();
            }

        }
        index.delete(keyList);
        return keyList.size();
    }
}
