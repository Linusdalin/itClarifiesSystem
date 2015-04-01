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
               setProjectName("Demo");
               setTargetServer("http://localhost:8080");


          /***********************************************************
           *
           *      Regeneration of classification, action, risk, and annotation
           *      Project:  Demo
           *      Document: Cannon
           *
           */
                  // No manual classifications, risks or annotations for the document Cannon.docx



          /***********************************************************
           *
           *      Regeneration of classification, action, risk, and annotation
           *      Project:  Demo
           *      Document: Google Analytics
           *
           */
                  // No manual classifications, risks or annotations for the document GA.docx



          /***********************************************************
           *
           *      Regeneration of classification, action, risk, and annotation
           *      Project:  Demo
           *      Document: Test document
           *
           */
                  addClassification(new Reclassification("#DEFINITION_SOURCE", true, "2015-04-01", "Demo", "Test document.docx", 1,
                                  "Introduction is normal, but this should be detected 2014-06-01 in analysis. This contains an internal link to a later chapter. Date is not a tag",
                                  "Introduction", 0, "itClarifies", false));

                  addDefinition(new Redefinition("Introduction", true, "Demo", "Test document.docx", 1,
                                  "Introduction is normal, but this should be detected 2014-06-01 in analysis. This contains an internal link to a later chapter. Date is not a tag", false));

                  addDefinition(new Redefinition("Introduction", true, "Demo", "Test document.docx", 1,
                                  "Introduction is normal, but this should be detected 2014-06-01 in analysis. This contains an internal link to a later chapter. Date is not a tag", false));

    }
}
