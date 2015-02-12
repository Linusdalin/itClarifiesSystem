package actions;


import classification.ClassificationStatistics;
import classification.FragmentClassification;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*********************************************************************************''
 *
 *          handle compilation of the the classification overview
 *
 */


public class ChecklistManager {

    // The found observations while parsing the
    private Map<String, ClassificationStatistics> statisticsMap = new HashMap<String, ClassificationStatistics>();
    private Checklist checklist;

    public ChecklistManager( Checklist checklist, Map<String, ClassificationStatistics> classificationStatistics  ){

        this.checklist = checklist;
        this.statisticsMap = classificationStatistics;
    }



    /*************************************************************************'
     *
     *              Traverse and create the JSON representation of the checklist.
     *
     *              The traverse is reverse order to aggregate all hits up
     *
     *
     * @return  - nested json object representing the tree with aggregated stats
     */


    public JSONObject getChecklist(){

        List<ChecklistItem> items = checklist.getChecklistItemsForChecklist();
        JSONObject checklistJSON = getChecklistOverview();
        JSONArray checklistItemsJSON = new JSONArray();

        for(ChecklistItem item : items){

            JSONObject checklistItemJSON = createItemJSON(item);
            checklistItemsJSON.put(checklistItemJSON);

        }

        checklistJSON.put("items", checklistItemsJSON);
        return checklistJSON;

    }

    private JSONObject createItemJSON(ChecklistItem item) {

        int classificationCount = 0;

        if(item.getTagReference() != null && !item.getTagReference().equals("")){

            ClassificationStatistics statisticsForTag = statisticsMap.get(item.getTagReference());
            if(statisticsForTag != null){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Getting statistics for tag \"#" + item.getTagReference()+"\"");
                classificationCount = statisticsForTag.direct;
            }else{

                PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring unknown tag \"#" + item.getTagReference()+"\" when retrieving statistics");
            }
        }else{

            PukkaLogger.log(PukkaLogger.Level.INFO, "Tag missing for Checklist Item. No statistics retrieved");
        }

        String sourceKey = (item.getSourceId() == null ? "" : item.getSourceId().toString());
        String complianceKey = (item.getCompletionId() == null ? "" : item.getCompletionId().toString());


        return createJSON(
                item.getId(),
                item.getKey().toString(),
                sourceKey,
                complianceKey,
                item.getStatus().getId(),
                item.getName(),
                item.getDescription(),
                item.getComment(),
                item.getTagReference(),
                classificationCount);
    }


    private JSONObject createJSON(long id, String key, String source, String completion, long status, String name, String description, String comment, String tagReference, int classificationCount) {

        return new JSONObject().put("checklistItem",
                new JSONObject()
                .put("key", key)
                .put("id", id)
                .put("status", status)
                .put("name",            name)
                .put("description",     description)
                .put("comment", comment)
                .put("tag",             tagReference)
                .put("source",          source)
                .put("completion",      completion)
                .put("classifications", classificationCount)
                .put("children",        new JSONArray())                   // Subitems in a checklist not implemented
        )
        ;

    }



    private void updateStatistics(FragmentClassification classification) {

        ClassificationStatistics statForClassification = statisticsMap.get(classification.getClassTag());

        if(statForClassification == null)
            statForClassification = new ClassificationStatistics();

        statForClassification.updateHit();
        statisticsMap.put(classification.getClassTag(), statForClassification);


    }

    public JSONObject getChecklistOverview() {

        JSONObject checklistJSON = new JSONObject()

                .put("id", checklist.getKey().toString())
                .put("name", checklist.getName())
                .put("description", checklist.getDescription());

        return checklistJSON;
    }
}
