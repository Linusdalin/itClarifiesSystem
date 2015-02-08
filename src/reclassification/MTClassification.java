package reclassification;

/**
* Created with IntelliJ IDEA.
* User: Linus
* Date: 2015-02-03
* Time: 07:29
* To change this template use File | Settings | File Templates.
*/
public class MTClassification {

    public final String classification;
    public final int requirementLevel;
    public final int applicablePhase;
    public final String document;
    public final int ordinal;
    public String fragment;
    public final String riskLevel;
    public final String pattern;
    public final String comment;
    public final String user;




    public MTClassification(String classification, int requirementLevel, int applicablePhase, String document, int ordinal, String fragment, String riskLevel, String pattern, String comment, String user) {

        this.classification = classification;
        this.requirementLevel = requirementLevel;
        this.applicablePhase = applicablePhase;
        this.document = document;
        this.ordinal = ordinal;
        this.fragment = fragment;
        this.riskLevel = riskLevel;
        this.pattern = pattern;
        this.comment = comment;
        this.user = user;

    }
}
