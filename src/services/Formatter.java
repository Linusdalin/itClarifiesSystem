package services;

import net.sf.json.JSONObject;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.export.ValuePair;
import java.util.List;

/**
 *          Formatter is used to format data. It can handle different output formats
 *          and style differently depending on how the data is used
 *
 */

public class Formatter {


    public String formatJSON(JSONObject json) {

        StringBuffer output = new StringBuffer();

        if(html)
            output.append("<pre>");

        if(prettyPrint || html)
            output.append(json.toString(4));
        else
            output.append(json.toString());

        if(html)
            output.append("</pre>");

            return output.toString();
    }

    public String formatCSV(String csvFile) {

        StringBuffer output = new StringBuffer();

        if(html)
            output.append("<pre>");

        output.append(csvFile);

        if(html)
            output.append("</pre>");

            return output.toString();


    }

    // The format of the output
    public enum OutputFormat    {JSON, XML, CSV, HTML, DOCX}
    private OutputFormat format;

    private String newLine = "";        //How newline shall be handled by the different formats
    private boolean html = false;
    private boolean prettyPrint = false;



    public Formatter(){

        format = OutputFormat.JSON;  // Default output

    }

    /******************************************************
     *
     *      Add pretty print attribute. This will add newline for readability.
     *      Doesn't change the semantics, but makes the output slightly bigger
     *
     * @return - self
     *
     */


    public Formatter prettyPrint(boolean yes){


        if(yes){

            prettyPrint = true;
        }
        return this;
    }

    /*********************************************************
     *
     *      The html attribute will make the export produce navigatable html (instead of pure json)
     *      This is used mostly for demonstration and testing
     *
     * @return -self
     */

    public Formatter htmlEncode(boolean yes){

        if(yes){

            html = true;
            newLine += "<br/>";
        }
        return this;
    }


    /**********************************************************
     *
     *      Setting which format should be used
     *
     * @param format - desired output format
     *
     * @return -self
     */


    public Formatter setFormat(OutputFormat format){

        this.format = format;
        if(format == OutputFormat.HTML){
            html = true;
            newLine = "</br>";
        }

        return this;
    }


    /**********************************************************************'''
     *
     *          Return the appropriate mime type for the content
     *
     * @return - textual representation of the type
     * @throws BackOfficeException
     */


    public String getContentType() throws BackOfficeException {

        if(html)
            return "text/html";

        switch(format){
            case JSON:
                return "application/json";

            case XML:
                return "text/xml";

            case CSV:
                return "text/csv";

            case DOCX:
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            default:
                throw new BackOfficeException(BackOfficeException.Usage, "Unknown Export format " + format.name() + "!");

        }

    }

}
