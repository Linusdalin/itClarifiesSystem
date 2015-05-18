package overviewExport;

/********************************************************************
 *
 *             This is the extraction for the internal extraction search
 *             into the excel export
 *
 *
 *             //TODO: add different styles for different headlines
 *             //TODO: add annotations, classifications and risk
 *             //TODO: add washing of html tags in the text
 *
 */


public class ExtractionFragment {


    enum Style {Title, Heading, Text}

    private String text;
    private String key;
    private int ordinal;
    private Style style;

    private String name = "";
    private String risk = "";
    private String riskDescription = "";
    private String comment = "";
    private String sheet = "";


    ExtractionFragment(String text, String key, int ordinal){

        this("", text, key, ordinal, "", "", "", "");
    }


    ExtractionFragment(String name, String text, String key, int ordinal, String risk, String riskDescription, String comment, String sheet){

        this.text = text;
        this.ordinal = ordinal;
        this.key = key;

        this.name = name;
        this.risk = risk;
        this.riskDescription = riskDescription;
        this.comment = comment;
        this.sheet = sheet;


        this.style = Style.Text;  //Default



    }

    public String toString(){

        return "(" +sheet + ": " + name + " " + text + ")";
    }

    public ExtractionFragment asHeadline(int level){

        //TODO: level not supported

        style = Style.Heading;
        return this;
    }

    public ExtractionFragment asTitle(){

        style = Style.Title;
        return this;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public Style getStyle() {
        return style;
    }


    public int getOrdinal() {
        return ordinal;
    }
}
