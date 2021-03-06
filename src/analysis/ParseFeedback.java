package analysis;

import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************************************
 *
 *              Complete feedback for a parsing
 *
 *              The parse feedback contains a list of feedback items with notes on what has happened
 *
 */


public class ParseFeedback {

    private List<ParseFeedbackItem> list;



    public ParseFeedback(){

        list = new ArrayList<ParseFeedbackItem>();
    }

    public void add(ParseFeedbackItem item){

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Creating feedback (" + item.severity.name() + "@" + item.row  + ": \"" + item.message + "\")");
        list.add(item);
    }

    /*********************************************************************
     *
     *              Merge two lists of feedback
     *
     *
     * @param f    - the other feedback list
     */

    public void add(ParseFeedback f){


        list.addAll(f.list);

    }

    /******************************************************************
     *
     *          Convert the feedback to a JSON object to be able to send to the frontend/user
     *
     *
     * @return
     */

    public JSONObject toJSON() {

        JSONArray feedbackArray = new JSONArray();

        for (ParseFeedbackItem analysisFeedbackItem : list) {

            if(analysisFeedbackItem.severity != ParseFeedbackItem.Severity.HIDE)
                feedbackArray.put(analysisFeedbackItem.toJSON());
        }


        JSONObject json = new JSONObject().put("feedback", feedbackArray);

        return json;
    }

    public int getNo() {
        return list.size();
    }
}
