package analysis;


import log.PukkaLogger;
import net.sf.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-10
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisFeedbackItem {

    public final Severity severity;
    public final String message;
    public final int row;


    public enum Severity {
        ABORT,
        ERROR,
        WARNING,
        INFO,
        HIDE
    }

    public AnalysisFeedbackItem(Severity severity, String message, int row){

        this.severity = severity;
        this.message = message;
        this.row = row;
    }


    public JSONObject toJSON() {
        return new JSONObject()
                .put("severity", severity.name())
                .put("message", message)
                .put("row", row)
        ;
    }

}
