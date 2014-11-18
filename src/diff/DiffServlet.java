package diff;

import contractManagement.*;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;
import services.ItClarifiesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Diff servlet will return a diff between two versions
 *          of the same document
 *
 *          It works on two already uploaded versions
 *
 */

public class DiffServlet extends ItClarifiesService {

    public static final String DataServletName = "Diff";


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

     }


    /*************************************************************************
     *
     *          Get diff
     *
     *          Diff will return a structure describing the differences between the individual fragments.
     *          There are two different classes of the differences:
     *
     *           - A fragment that only appears in one of the documents
     *           - A fragment that is changed
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


            DBKeyInterface _active = getMandatoryKey("active", req);
            ContractVersionInstance active = new ContractVersionInstance(new LookupByKey(_active));

            if(!mandatoryObjectExists(active, resp))
                return;

            DBKeyInterface _reference = getMandatoryKey("reference", req);
            ContractVersionInstance reference = new ContractVersionInstance(new LookupByKey(_reference));

            if(!mandatoryObjectExists(active, resp))
                return;

            // We also have to check the actual document. Both versions have to point to the same document and
            // the user has to have access to it.

            Contract contract1 = active.getDocument();
            Contract contract2 = reference.getDocument();

            if(!mandatoryObjectExists(contract1, resp))
                return;

            if(!contract1.isSame(contract2))
                returnError("Diff can only be performed between two versions of the same document", HttpServletResponse.SC_BAD_REQUEST, resp);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Performing diff between the versions " + active.getVersion() + " and " + reference.getVersion() + " of the document " + contract1.getName());

            JSONArray diffJSON = getDiffJSON(active, reference);

            JSONObject json = new JSONObject().put(DataServletName, diffJSON);

            resp.getWriter().print(json);
            setRespHeaders(resp, 0);
            resp.flushBuffer();


        } catch (BackOfficeException e) {

            e.logError("Error (Get) in Search Servlet");
            returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();

        }


    }

    /************************************************************************************'
     *
     *          Get the diff Array of objects
     *
     * @param active - the active version we are comparing with
     * @param reference - reference version
     * @return  - array with JSON representation according to the spec
     * @throws BackOfficeException
     */

    private JSONArray getDiffJSON(ContractVersionInstance active, ContractVersionInstance reference) throws BackOfficeException {

        JSONArray diffList = new JSONArray();

        List<ContractFragment> activeFragments = active.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));
        List<ContractFragment> initialFragments = reference.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));


        FragmentComparator comparator = new FragmentComparator();
        DiffStructure diffStructure = comparator.diff(asTextArray(activeFragments), asTextArray(initialFragments));

        PukkaLogger.log(PukkaLogger.Level.INFO, "Got diff structure: " + diffStructure.toString());

        for(Match match : diffStructure.getMatches()){

            JSONObject matchJSON = new JSONObject();

            matchJSON.put("active",     (match.active == Match.ORPHAN ? "Orphaned" : activeFragments.get(match.active).getKey().toString()));
            matchJSON.put("reference",  (match.referenced == Match.ORPHAN ? "Orphaned" : initialFragments.get(match.referenced).getKey().toString()));
            matchJSON.put("distance",   match.distance);

            diffList.put(matchJSON);

        }


        return diffList;
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


}
