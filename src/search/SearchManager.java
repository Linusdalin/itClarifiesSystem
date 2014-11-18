package search;

import actions.Action;
import analysis.DocumentAnalysisException;
import analysis.Significance;
import contractManagement.*;
import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import language.English;
import language.LanguageAnalyser;
import language.LanguageInterface;
import language.Swedish;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import services.ItClarifiesService;
import system.TextMatcher;
import userManagement.Organization;
import userManagement.PortalUser;
import userManagement.SessionManagement;

import java.util.*;

/******************************************************************************
 *
 *          SearchFilter provides the service of filtering text for a search.
 *
 *          TODO: Search in freeze version
 *
 *
 */

public class SearchManager {


    public SearchManager(){


    }



    /*************************************************************************************************************
     *
     *          Get the JSON for all the matching fragments
     *
     *          The search is done in the following areas:
     *
     *           - Fragment text
     *           - Classifications  (with tags)
     *           - Annotations
     *
     *
     * @param searchText
     * @param project
     * @param session
     * @return
     * @throws BackOfficeException
     *
     *
     *
     *
     */

    public JSONObject getMatchJson(String searchText, Project project, SessionManagement session) throws BackOfficeException {


        List<SearchHit> matches = getAllMatches(searchText, project, session);

        JSONArray jsonMatches = new JSONArray();

        for(SearchHit match : matches){

            jsonMatches.put(match.toJSON());
        }


        JSONObject result = new JSONObject()
                .put("fragments", jsonMatches);

        //PukkaLogger.log(PukkaLogger.Level.INFO, "Created Search json" + result);

        return result;


    }



    /*********************************************************************************************************
     *
     *          Get matches against all data in the project
     *
     *
     *
     * @param searchText - the search string from the client
     * @param project    - project to search in
     * @param session    - session (for access rights lookup)
     *
     * @throws BackOfficeException
     */

    public List<SearchHit> getAllMatches(String searchText, Project project, SessionManagement session) throws BackOfficeException{


        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 1");

        List<SearchHit> results = new ArrayList<SearchHit>();

        TextMatcher textmatcher = new TextMatcher();
        String strippedSearch = searchText.replaceAll("[@#]", "");

        List<String> emptyMatch = new ArrayList<String>();
        emptyMatch.add("");

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 2");

       // Get all risks in a map

        PortalUser user = session.getUser();
        DBKeyInterface organization = user.getOrganizationId();

        Map<String, String> riskMap = new HashMap<String, String>();
        ContractRiskTable riskTable = new ContractRiskTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractRiskTable.Columns.Organization.name(), organization))
        );

        for(DataObjectInterface object : riskTable.getValues()){

            ContractRisk risk = (ContractRisk)object;
            riskMap.put(risk.getKey().toString(), risk.getName() + " Risk");
        }

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 3");

        List<Contract> allDocuments = project.getContractsForProject();

        for(Contract document : allDocuments){

            //List to hold all fragments for header lookup
            List<ContractFragment> fragmentList = new ArrayList<ContractFragment>();


            // Set the language from the document

            try {
                textmatcher.setLanguage(new LanguageAnalyser().getLanguageByName(document.getLanguage()), ItClarifiesService.MODEL_DIRECTORY);
                textmatcher.prepareSearchString(searchText).useSynonyms().useStemMatch().caseInsensitive();

            } catch (DocumentAnalysisException e) {

                throw new BackOfficeException(BackOfficeException.General, "Error matching text: " + e.getMessage());
            }

            // Only look in documents that the user have access too

            if(session.getReadAccess(document)){

                ContractVersionInstance head = document.getHeadVersion();

                // ********************** 1. Go through fragments

                List<ContractFragment> fragmentsForVersion = head.getFragmentsForVersion();
                List<StructureItem> clausesForVersion = head.getStructureItemsForVersion();

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + fragmentsForVersion.size() + " fragments");


                for(ContractFragment fragment : fragmentsForVersion){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Fragment : " + fragment.getName());
                    List<String> match = textmatcher.getMatch(fragment.getText());

                    if(match != null){

                        System.out.println("Got " + match.size() + " fragment matches");

                        //PukkaLogger.log(PukkaLogger.Level.INFO, " -> Matching fragment " + fragment.getOrdinal() + " in document " + document.getName() + "(\"" + fragment.getText() + "\")");

                        results.add(new SearchHit(fragment, document, match));

                        // Add the clause headline too

                        addHeadlinesForFragment(results, fragment, document, fragmentsForVersion, clausesForVersion);

                    }
                }

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 4");


                // ********************** 2. Go through classifications

                List<FragmentClassification> classifications = head.getFragmentClassificationsForVersion();    // Get all the classifications in the document

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + classifications.size() + " classifications");

                textmatcher.prepareSearchString(strippedSearch).caseInsensitive();

                for(FragmentClassification classification : classifications){

                    // Only add this if the significance is enough

                    if(classification.getSignificance() > Significance.MATCH_SIGNIFICANCE){

                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Classification : " + classification.getDescription());

                        // Searching in name and comment. (That contains the tags and parent classes)

                        String matchPatternForClassification = classification.getName() + " " + classification.getComment() + " " + classification.getKeywords();

                        System.out.println("*** Trying to match classification: \"" + matchPatternForClassification + "\"");

                        if(textmatcher.getMatch(matchPatternForClassification) != null){

                            List<String> match = new ArrayList<String>();
                            match.add(classification.getPattern());

                            results.add(new SearchHit(classification.getFragment(), document, match)
                                    .withClassification(classification));


                        }


                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring classification "+ classification.getName()+" with low significance. (" + classification.getSignificance() + " < " + Significance.MATCH_SIGNIFICANCE + ")");
                    }
                }


                // ********************** 3. Go through annotations
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 5");


                List<ContractAnnotation> annotations = head.getContractAnnotationsForVersion();
                textmatcher.prepareSearchString(strippedSearch).caseInsensitive();

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + annotations.size() + " annotations");

                for(ContractAnnotation annotation : annotations){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Annotation : " + annotation.getDescription());

                    if(textmatcher.getMatch(annotation.getDescription() + " Annotation") != null){

                        List<String> pattern = new ArrayList<String>();
                        pattern.add(annotation.getPattern());

                        results.add(new SearchHit(annotation.getFragment(), document, pattern)
                        .withAnnotation(annotation));

                    }

                }


                // ********************** 3. Go through actions
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 5.2");


                List<Action> actions = head.getActionsForVersion();
                textmatcher.prepareSearchString(strippedSearch).caseInsensitive();

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + actions.size() + " actions");

                for(Action action : actions){

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Action : " + action.getDescription());

                    if(textmatcher.getMatch(action.getDescription() + " " + action.getName() + " " + action.getAssignee().getName() + " " + action.getIssuer().getName()) != null){

                        List<String> pattern = new ArrayList<String>();
                        pattern.add(action.getPattern());

                        results.add(new SearchHit(action.getFragment(), document, pattern));


                    }

                }



                // ********************** 4. Go through risk
                PukkaLogger.log(PukkaLogger.Level.INFO, "Get Matches 6");

                List<RiskClassification> risks = head.getRiskClassificationsForVersion();
                textmatcher.prepareSearchString(strippedSearch).caseInsensitive();

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + classifications.size() + " risks");

                for(RiskClassification classification : risks){



                    DBKeyInterface risk = classification.getRiskId();
                    String riskName = riskMap.get(risk.toString());
                    PukkaLogger.log(PukkaLogger.Level.DEBUG, "Risk : " + riskName);

                    List<String> res = textmatcher.getMatch(riskName);
                    if(res != null){

                        List<String> pattern = new ArrayList<String>();
                        pattern.add(classification.getPattern());

                        results.add(new SearchHit(classification.getFragment(), document, pattern)
                                .withRisk(riskName));

                    }

                }

            }

        }

        PukkaLogger.log(PukkaLogger.Level.INFO, "Get Matches 8");

        return results;

    }

    /***********************************************************************************''
     *
     *          Simply adding the clause headline.
     *
     *          //  TODO: create a hierarchy of headlines to show all headlines up to H1
     *
     *
     * @param results
     * @param fragment
     * @param document
     * @param fragmentsForVersion
     * @param clausesForVersion
     */

    private void addHeadlinesForFragment(List<SearchHit> results, ContractFragment fragment, Contract document, List<ContractFragment> fragmentsForVersion, List<StructureItem> clausesForVersion) {

        try {

            StructureItem headline = fragment.getStructureItem(clausesForVersion);
            ContractFragment headlineFragment = headline.getFragmentForStructureItem(fragmentsForVersion);

            // Avoid adding one self. This is already done while parsing the fragments

            if(fragment.isSame(headlineFragment))
                return;

            List<String> emptyMatches = new ArrayList<String>();


            results.add(new SearchHit(headlineFragment, document, emptyMatches));

        } catch (BackOfficeException e) {
            e.logError("Error getting fragment for clause");
        }

    }



}