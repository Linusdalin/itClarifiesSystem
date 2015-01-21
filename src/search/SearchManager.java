package search;

import actions.Action;
import analysis.Significance;
import analysis2.AnalysisException;
import classification.FragmentClassification;
import contractManagement.*;
import databaseLayer.DBKeyInterface;
import language.LanguageAnalyser;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import services.ItClarifiesService;
import system.TextMatcher;
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
     *
     *
     *
     * @throws BackOfficeException
     */

    public List<SearchHit> getAllMatches(String searchText, Project project, SessionManagement session) throws BackOfficeException{


        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 1");

        List<SearchHit> results = new ArrayList<SearchHit>();

        TextMatcher textmatcher = new TextMatcher();
        String strippedSearch = searchText.replaceAll("[@]", "");

        List<String> emptyMatch = new ArrayList<String>();
        emptyMatch.add("");

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 2");

       // Get all risks in a map

        PortalUser user = session.getUser();
        DBKeyInterface organization = user.getOrganizationId();

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Get Matches 3");

        List<Contract> allDocuments = project.getContractsForProject();

        for(Contract document : allDocuments){

            //List to hold all fragments for header lookup
            List<ContractFragment> fragmentList = new ArrayList<ContractFragment>();


            // Set the language from the document

            try {
                textmatcher.setLanguage(new LanguageAnalyser().getLanguageByName(document.getLanguage()), ItClarifiesService.MODEL_DIRECTORY);
                textmatcher.prepareSearchString(searchText).useSynonyms().useStemMatch().caseInsensitive();

            } catch (AnalysisException e) {

                System.out.println("Error in search");
                e.printStackTrace();

                throw new BackOfficeException(BackOfficeException.General, "Error matching text: " + e.narration + " in document " + e.document);
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

                textmatcher.prepareSearchString(searchText)
                        .caseInsensitive().useHashTags();

                for(FragmentClassification classification : classifications){

                    // Only add this if the significance is enough

                    if(classification.getSignificance() > Significance.MATCH_SIGNIFICANCE){

                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Classification : " + classification.getDescription());

                        // Searching in name and comment. (That contains the tags and parent classes)

                        String matchPatternForClassification = classification.getClassTag() + " " + classification.getComment() + " " + classification.getKeywords();
                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "*** Trying to match classification: \"" + matchPatternForClassification + "\"");

                        if(textmatcher.getMatch(matchPatternForClassification) != null){

                            PukkaLogger.log(PukkaLogger.Level.INFO, "*** Matched!");
                            ContractFragment fragment = classification.getFragment();

                            List<String> match = new ArrayList<String>();
                            match.add(classification.getPattern());

                            results.add(new SearchHit(fragment, document, match)
                                    .withClassification(classification));


                            // If the classification is a header, we want to add all the children too

                            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding: " + fragment.getChildren().size() + " children");


                            for(ContractFragment child : fragment.getChildren()){

                                results.add(new SearchHit(child, document, match)
                                        .withClassification(classification));

                            }

                        }

                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring classification "+ classification.getClassTag()+" with low significance. (" + classification.getSignificance() + " < " + Significance.MATCH_SIGNIFICANCE + ")");
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


                textmatcher.prepareSearchString(strippedSearch).caseInsensitive().useHashTags();

                PukkaLogger.log(PukkaLogger.Level.INFO , "***************** Found " + classifications.size() + " risks");

                for(RiskClassification classification : risks){

                    String riskName = classification.getRisk().getName();

                    String matchPatternForClassification = "#" + riskName + " " + classification.getComment() + " " + classification.getKeywords();
                    PukkaLogger.log(PukkaLogger.Level.INFO, "*** Trying to match classification: \"" + matchPatternForClassification + "\"");

                    List<String> res = textmatcher.getMatch(matchPatternForClassification);
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

            if(headline != null){

                ContractFragment headlineFragment = headline.getFragmentForStructureItem(fragmentsForVersion);

                // Avoid adding one self. This is already done while parsing the fragments

                if(fragment.equals(headlineFragment))
                    return;

                List<String> emptyMatches = new ArrayList<String>();

                results.add(new SearchHit(headlineFragment, document, emptyMatches));

            }


        } catch (BackOfficeException e) {
            PukkaLogger.log(e, "Error getting fragment for clause");
        }

    }



}