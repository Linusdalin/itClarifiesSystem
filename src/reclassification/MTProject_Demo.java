package reclassification;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-03-27
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 *
 */

public class MTProject_Demo extends NewMTProject {

    public static void main(String[] par){

        MTProject_Demo project = new MTProject_Demo();
        project.init();
        project.inject();
    }


    MTProject_Demo(){

        //super("Linus", "https://itclarifiesapistage.appspot.com");
        super("Demo", "http://localhost:8080");
    }


    public void init(){


        /***********************************************************
            *
            *      Generate the correct name and target server
            */
                setProjectName("Linus3");
                setTargetServer("https://itclarifiesapistage.appspot.com");


           /***********************************************************
            *
            *      Regeneration of classification, action, risk, and annotation
            *      Project:  Linus3
            *      Document: Test document
            *
            */
                   addClassification(new Reclassification("#BACKGROUND", true, "2015-03-30", "Linus3", "Test document.docx", 1,
                                   "Introduction is normal, but this should be detected 2014-06-01 in analysis. This contains an internal link to a later chapter. Date is not a tag",
                                   "", -1, "linus", false));

                   addClassification(new Reclassification("#DATE", false, "2015-03-30", "Linus3", "Test document.docx", 1,
                                   "Introduction is normal, but this should be detected 2014-06-01 in analysis. This contains an internal link to a later chapter. Date is not a tag",
                                   "2014-06-01", -1, "linus", false));

                   addRisk(new Rerisk("High", "2015-03-30", "Linus3", "Test document.docx", 10,
                                   "And an unnumbered list:",
                                   "", 0, "linus", false));

    }


}
