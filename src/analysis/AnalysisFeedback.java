package analysis;

import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-10
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 *
 */


public class AnalysisFeedback {

    private List<AnalysisFeedbackItem> list;



    public AnalysisFeedback(){

        list = new ArrayList<AnalysisFeedbackItem>();
    }

    public void add(AnalysisFeedbackItem item){

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Creating feedback (" + item.severity.name() + "@" + item.row  + ": \"" + item.message + "\")");
        list.add(item);
    }

    public JSONObject toJSON() {

        JSONArray feedbackArray = new JSONArray();

        for (AnalysisFeedbackItem analysisFeedbackItem : list) {

            feedbackArray.put(analysisFeedbackItem.toJSON());
        }


        JSONObject json = new JSONObject().put("feedback", feedbackArray);

        return json;
    }
}
