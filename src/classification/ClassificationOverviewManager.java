package classification;


import contractManagement.Contract;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import featureTypes.FeatureTypeInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.exceptions.BackOfficeException;
import userManagement.SessionManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*********************************************************************************''
 *
 *          handle compilation of the the classification overview
 *
 */


public class ClassificationOverviewManager {

    // The found observations while parsing the
    private Map<String, ClassificationStatistics> statisticsMap = new HashMap<String, ClassificationStatistics>(512);

    public ClassificationOverviewManager(){

    }


    /************************************************************************'
     *
     *          Parse the project to find all classifications
     *
     *
     * @param project   - the parsed project
     * @param session   - the user session for access lookup
     *
     *                  //TODO: Security: Add a "read all documents" session instead of using null as below
     */

    public void compileClassificationsForProject(Project project,  SessionManagement session){

        List<Contract> documents = project.getContractsForProject();

        // Go through all documents in the project.
        // For the documents which the user can see, get all classifications

        for (Contract document : documents) {

            try {

                if(session == null || session.getReadAccess(document)){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Compiling classifications for document " + document.getName());


                    ContractVersionInstance latest = document.getHeadVersion();
                    List<FragmentClassification> classifications = latest.getFragmentClassificationsForVersion();

                    for (FragmentClassification classification : classifications) {

                        // For each classification add it to the list or increase the count
                        boolean track = false;

                        if(classification.getClassTag().contains("RESOURCE")){

                            track = true;
                            PukkaLogger.log(PukkaLogger.Level.INFO, "Found classification " + classification.getClassTag() + ". Updating statistics ");
                        }

                        updateStatistics(classification, track);


                    }
                    PukkaLogger.log(PukkaLogger.Level.INFO, "Document ready");

                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring document " + document.getName() + ". No access.");

                }

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );
            }
        }

    }

    /*************************************************************************'
     *
     *              Traverse and create the JSON representation of the statistics.
     *
     *              The traverse is reverse order to aggregate all hits up
     *
     *
     * @return  - nested json object representing the tree with aggregated stats
     */


    public JSONObject getStatistics(){

        return getStatistics(ClassificationOverviewTree.root);
    }

    public JSONObject getStatistics(ClassificationOverviewNode node){

        JSONObject levelN = new JSONObject();    // levelN represents this level in the resulting hierarchy tree
        JSONArray children = new JSONArray();    // The list of children below


        // Lookup the stats for the node in the tree.
        // If it doesn't exist, we create a new (to potentially store indirect classifications)

        ClassificationStatistics statistics = getStatisticsNode(statisticsMap, node.type);

        // Traverse all children

        for (ClassificationOverviewNode childNode : node.children) {

            JSONObject childLevel = getStatistics(childNode);

            if(childLevel != null){

                // If there are statistics on a level below we aggregate all the hits
                // as indirect hits and add the statistics to the JSON tree

                statistics.addIndirectHits(getDirectAndIndirectHits(childLevel));
                children.put(childLevel);
            }

        }

        if(statistics.isEmpty())
            return null;

        // Create the JSON object with the statistics and the already completed children

        levelN.put("classification", node.type.getName());
        levelN.put("subClassifications", children);
        levelN.put("comment", node.type.getDescription());
        levelN.put("statistics", statistics.toJSON());

        return levelN;

    }

    private ClassificationStatistics getStatisticsNode(Map<String, ClassificationStatistics> statisticsMap, FeatureTypeInterface classification) {

        ClassificationStatistics statistics = statisticsMap.get(classification.getName());

        if(statistics == null){

            statistics = new ClassificationStatistics();
        }

        return statistics;
    }

    private int getDirectAndIndirectHits(JSONObject childLevel) {

        return childLevel.getJSONObject("statistics").getInt("direct") + childLevel.getJSONObject("statistics").getInt("indirect");

    }


    private void updateStatistics(FragmentClassification classification, boolean track) {

        ClassificationStatistics statForClassification = statisticsMap.get(classification.getClassTag());

        if(statForClassification == null)
            statForClassification = new ClassificationStatistics();

        statForClassification.updateHit();
        statisticsMap.put(classification.getClassTag(), statForClassification);

        if(track)
            System.out.println(" --- Updating statistics: " + classification.getClassTag() + ":" + statForClassification.toString());

    }

    public Map<String, ClassificationStatistics> getStatisticsMap(){

        return statisticsMap;
    }

}
