package search;

import com.google.gson.JsonObject;
import contractManagement.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.exceptions.BackOfficeException;

import java.util.List;

/**
 *      Generic type for handling a search hit. This will then be converted to a JSON object
 *      for the communication with the front end or a csv file for the backend
 *
 */

public class SearchHit {

    private final ContractFragment  fragment;
    private final Contract          document;
    private final List<String>      matchPatterns;
    private ContractAnnotation      annotation = null;
    private String                  risk = null;
    private FragmentClassification  classification = null;

    public SearchHit(ContractFragment fragment, Contract document, List<String> matchPatterns){


        this.fragment = fragment;
        this.document = document;
        this.matchPatterns = matchPatterns;
    }

    public SearchHit withAnnotation(ContractAnnotation annotation){

        this.annotation = annotation;
        return this;
    }

    public SearchHit withRisk(String risk){

        this.risk = risk;
        return this;
    }

    public SearchHit withClassification(FragmentClassification classification){

        this.classification = classification;
        return this;
    }


    /*************************************************************************************
     *
     *          Convert the search hit to JSON according to the specification with the frontend
     *
     *
     * @return
     */

    public JSONObject toJSON() {

        JSONArray patternList = new JSONArray();

        if(matchPatterns != null){

            for(String pattern : matchPatterns){

                patternList
                        .put(new JSONObject()
                                    .put("pattern", pattern));
            }

        }

        JSONObject json = new JSONObject()
                .put("fragment", fragment.getKey().toString())
                .put("document", document.getKey().toString())
                .put("ordinal", fragment.getOrdinal())
                .put("patternlist", patternList);


        //System.out.println("Adding fragment: " + fragment.getText());

        return json;

    }


    /**************************************************************************************'
     *
     *          Convert one search hit to a line in the csv file
     *          //TODO: Add actions, annotations and risks
     *          // TODO: Escape the text for ","
     *
     *          //TODO: Refactor this into a CSVFile class
     *
     *
     *
     * @return - csv file as String
     */

    public String toCSV() {

        StringBuffer line = new StringBuffer();
        line.append(escapeToCSV(document.getName()) + ", \"" +escapeToCSV(fragment.getText())+"\"");

        try {

            List<FragmentClassification> classifications = fragment.getClassificationsForFragment();
            List<ContractAnnotation> annotations = fragment.getAnnotationsForFragment();

            for(FragmentClassification classification : classifications){

                line.append(classification.getClassTag() + " ");
            }
            line.append(", ");

            for(ContractAnnotation annotation : annotations){

                line.append("\"" + escapeToCSV(annotation.getDescription()) + "\" ");
            }


        } catch (BackOfficeException e) {

            e.logError("Converting to csv");
        }

        line.append("\n");

        return line.toString();
    }

    public static String getHeader() {
        return("Document, Fragment, Classifications, Comments\n");
    }


    private String escapeToCSV(String s){

        return s.replace("\n", "\\n")
                .replace("\"", "\\\"");
    }
}
