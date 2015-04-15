package overviewExport;

/********************************************************************
 *
 *             This is the extraction for the internal extraction search
 *             into the excel export
 *
 *
 *             //TODO: add style for headlines
 *             //TODO: add annotations, classifications and risk
 *             //TODO: add washing of html tags in the text
 *
 */


public class ExtractionFragment {


    enum Style {Title, Heading, Text}

    private String text;
    private int ordinal;
    private Style style;

    ExtractionFragment(String text, int ordinal){

        this.text = stripHtml(text);
        this.ordinal = ordinal;
        this.style = Style.Text;  //Default
    }

    private String stripHtml(String text) {

        return text.replaceAll("\\<.*?>","");
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

    public Style getStyle() {
        return style;
    }


    public int getOrdinal() {
        return ordinal;
    }
}
