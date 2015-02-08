package reclassification;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-03
 * Time: 07:36
 * To change this template use File | Settings | File Templates.
 */
public class MT_Test extends MTDocument implements MechanicalTurkInterface {

    private static final String document = "Test Document";

    private static final MTClassification[] classifications = {

            new MTClassification("#ACCEPTANCE_CRITERIA", 0, 0, "Test document.docx", 2, "", "Medium", "later chapter", "var är detta???", "itClarifies"),

    };


    MT_Test(){

        super(document, classifications);
    }
}
