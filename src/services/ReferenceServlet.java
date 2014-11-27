package services;

import contractManagement.Contract;
import contractManagement.ContractTable;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.Reference;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;

import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.AccessGrant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Search Servlet returning a list of fragment id:s that should be listed
 *
 */

public class ReferenceServlet extends ItClarifiesService{

    public static final String DataServletName = "Reference";


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in Reference", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }


    /*************************************************************************
     *
     *          Get document reference
     *
     *          Parameters:
     *
     *          &project=<key>      mandatory project
     *
     *
     *          {"references":
     *              [
     *                      {document: <key>,
     *                          inLinks: [ document: { id: <document key>}, document: { id: <document key> } ],
     *                          outlinks [ document: { id: <document key>}, document: { id: <document key> } ]
     *                      }
     *              ]
     *          }
     *
     *          //TODO Allow references for a freeze version
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            DBKeyInterface _project = getMandatoryKey("project", req);
            Project project = new Project(new LookupByKey(_project));


            if(!mandatoryObjectExists(project, resp))
                return;

            JSONObject json = new JSONObject();
            JSONArray referenceList = new JSONArray();

           // Only show documents for the project

            List<Contract> documents = project.getContractsForProject(new LookupList()
                    .addOrdering(ContractTable.Columns.Ordinal.name(), Ordering.FIRST)
            );

            List<Reference> references =  project.getReferencesForProject();


            for(Contract document : documents){

                if(sessionManagement.getReadAccess(document)){

                    ContractVersionInstance version = document.getHeadVersion();

                    JSONArray inReferences = getInReferencesForDocument(version, references);
                    JSONArray outReferences = getOutReferencesForDocument(version, references);


                    JSONObject documentReference = new JSONObject()
                            .put("document", document.getKey().toString())
                            .put("inLinks", inReferences )
                            .put("outLinks", outReferences );

                    referenceList.put(documentReference);

                }
            }

            json.put("references", referenceList);

            sendJSONResponse(json, formatter, resp);


        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);

        } catch (Exception e) {

            PukkaLogger.log( e );
            returnError("Error in " + DataServletName, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
        }



    }


    /*********************************************************************************
     *
     *          In references are all references pointing to a fragment in the document
     *
     *
     * @param version    - version to display references for
     * @param references - all references for the document
     * @return
     *
     *              //TODO:     The destination is looked up multiple times for each reference. We could store it in the database
     */

    private JSONArray getInReferencesForDocument(ContractVersionInstance version, List<Reference> references) {

        JSONArray referenceList = new JSONArray();
        for(Reference reference : references){

            ContractVersionInstance toDocument = reference.getTo().getVersion();

            if(toDocument.isSame(version)){

                referenceList.put(new JSONObject()
                    .put("id", toDocument.getDocumentId().toString()));

            }
        }

        return referenceList;
    }

    private JSONArray getOutReferencesForDocument(ContractVersionInstance version, List<Reference> references) {

        JSONArray referenceList = new JSONArray();
        for(Reference reference : references){

            if(reference.getVersionId().equals(version.getKey())){

                referenceList.put(new JSONObject()
                    .put("id", reference.getVersion().getDocumentId().toString()));

            }
        }

        return referenceList;
    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Search", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}


/*

            String json = "[\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 1 Inledning\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 2 Tidplan för upphandlingen\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 3 Anbudsgivaren och anbudslämning\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 1 Inledning\", \"outgoing\" : 3},\n" +
                    "            { \"document\" : \"Förfrågan. 9 Ramavtal – Kommersiella villkor\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 4 Prövning och utvärdering\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 8 Priser\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"Förfrågan. 9 Ramavtal – Kommersiella villkor\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 8, Prövning och utvärdering\", \"outgoing\": 2}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 5 Upphandlingsvillkor \",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 6 Krav på leverantören \",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 4, Referensuppdragsmallar för Anbudsgivare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 5, Referensmall Projektledare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 6, Referensmall Prodiktionsledare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B7, Referensmall Manusförfattare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 8, Prövning och utvärdering\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 7 Krav på tjänsten\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 8 Priser\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 6 Krav på leverantören \", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 4}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 9 Ramavtal – Kommersiella villkor\",\n" +
                    "        \"links\": [\n" +
                    "            {\"document\" : \"B 3a, Ramavtal o Avropsavtal\", \"outgoing\" : 2},\n" +
                    "            {\"document\" : \"B 3b, Personuppgiftsbiträdesavtal\", \"outgoing\" : 3}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"Förfrågan. 10  Bilagor\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 4 Prövning och utvärdering\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 3a, Ramavtal o Avropsavtal\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 3b, Personuppgiftsbiträdesavtal\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 4, Referensuppdragsmallar för Anbudsgivare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 5, Referensmall Projektledare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 6, Referensmall Prodiktionsledare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B7, Referensmall Manusförfattare\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 8, Prövning och utvärdering\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 1, Kravspecikation på tjänsten\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 8, Prövning och utvärdering\", \"outgoing\" : 3}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 2, Pris- utvärderingsmall\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 8 Priser\", \"outgoing\" : 3},\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 3a, Ramavtal o Avropsavtal\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 6 Krav på leverantören \", \"outgoing\" : 5},\n" +
                    "            { \"document\" : \"Förfrågan. 8 Priser\", \"outgoing\" : 6},\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 4},\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 5},\n" +
                    "            { \"document\" : \"B 3b, Personuppgiftsbiträdesavtal\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 4, Referensuppdragsmallar för Anbudsgivare\", \"outgoing\" : 3},\n" +
                    "            { \"document\" : \"B 5, Referensmall Projektledare\", \"outgoing\" : 2}\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 3b, Personuppgiftsbiträdesavtal\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 4, Referensuppdragsmallar för Anbudsgivare\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 5, Referensmall Projektledare\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 6, Referensmall Prodiktionsledare\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B7, Referensmall Manusförfattare\",\n" +
                    "        \"links\": []\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"B 8, Prövning och utvärdering\",\n" +
                    "        \"links\": [\n" +
                    "            { \"document\" : \"Förfrågan. 1 Inledning\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"Förfrågan. 4 Prövning och utvärdering\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"Förfrågan. 7 Krav på tjänsten\", \"outgoing\" : 2},\n" +
                    "            { \"document\" : \"Förfrågan. 8 Priser\", \"outgoing\" : 3},\n" +
                    "            { \"document\" : \"Förfrågan. 9 Ramavtal – Kommersiella villkor\", \"outgoing\" : 1},\n" +
                    "            { \"document\" : \"B 1, Kravspecikation på tjänsten\", \"outgoing\" : 3},\n" +
                    "            { \"document\" : \"B 2, Pris- utvärderingsmall\", \"outgoing\" : 7},\n" +
                    "            { \"document\" : \"B 4, Referensuppdragsmallar för Anbudsgivare\", \"outgoing\" : 1}\n" +
                    "        ]\n" +
                    "    }\n" +
                    "]";

*/