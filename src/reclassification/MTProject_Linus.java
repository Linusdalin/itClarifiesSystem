package reclassification;

import dataRepresentation.DBTimeStamp;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-03-27
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 *
 */

public class MTProject_Linus extends NewMTProject {

    public static void main(String[] par){

        MTProject_Linus project = new MTProject_Linus();
        project.init();
        project.inject();
    }


    MTProject_Linus(){

        //super("Linus", "https://itclarifiesapistage.appspot.com");
        super("Demo", "http://localhost:8080");
    }


    public void init(){



        /***********************************************************
          *
          *      Generate the correct name and target server
          */
              DBTimeStamp now = new DBTimeStamp();
              setProjectName("Linus");
              setTargetServer("https://itclarifiesapistage.appspot.com");


         /***********************************************************
          *
          *      Regeneration of classification, action, risk, and annotation
          *      Project:  Linus
          *      Document: Request for ProposalNew TMSMain Document
          *
          */
                 // No manual classifications, risks or annotations for the document RFP Swedbank New TMS Main Document 150304.docx



         /***********************************************************
          *
          *      Regeneration of classification, action, risk, and annotation
          *      Project:  Linus
          *      Document: Request for ProposalNew TMSAppendix B, Functional and Non-functional Requirements
          *
          *         public Reclassification(String classification, boolean add, String date, String project, String document, long fragmentno,
          *                     String fragment,
          *                     String pattern, long patternpos, String user, boolean closed){

          *
          */
                 addClassification(new Reclassification("#BACKGROUND", true, "2015-04-20", "Linus", "RFP Swedbank New TMS, Appendix B - Functional and Technical requirements.docx", 3,
                                 "1  INSTRUCTIONS TO THE READER",
                                 "", -1, "linus", false));

                 addRisk(new Rerisk("Medium", "2015-04-20", "Linus", "RFP Swedbank New TMS, Appendix B - Functional and Technical requirements.docx", 4,
                                "Please read the requirements in this document and answer them in the attached document \"Appendix F - Response Sheet.xls\"."+
                                "Vendors are requested to provide a solution that addresses all the requirements outlined in this document."+
                                "If a requirement can be fulfilled please put 'OK' and include a brief explanation of how the requirement will be met."+
                                "If a requirement cannot be fulfilled please put 'Unable to Meet Requirement' together with a brief explanation why."+
                                "If a requi",
                                 "", 0, "linus", false));

                 addAnnotation(new Reannotation("No blanks!", true, "2015-04-20", "Linus", "RFP Swedbank New TMS, Appendix B - Functional and Technical requirements.docx", 4,
                         "Please read the requirements in this document and answer them in the attached document \"Appendix F - Response Sheet.xls\"."+
                         "Vendors are requested to provide a solution that addresses all the requirements outlined in this document."+
                         "If a requirement can be fulfilled please put 'OK' and include a brief explanation of how the requirement will be met."+
                         "If a requirement cannot be fulfilled please put 'Unable to Meet Requirement' together with a brief explanation why."+
                         "If a requi",
                                 "", 0, "linus", false));



         /***********************************************************
          *
          *      Regeneration of classification, action, risk, and annotation
          *      Project:  Linus
          *      Document: RFP Swedbank New TMS, Appendix F - Response sheet 150304.xlsx
          *
          */
                 // No manual classifications, risks or annotations for the document RFP Swedbank New TMS, Appendix F - Response sheet 150304.xlsx



         /***********************************************************
          *
          *      Regeneration of classification, action, risk, and annotation
          *      Project:  Linus
          *      Document: New TMS Concepts and glossary
          *
          */
                 // No manual classifications, risks or annotations for the document New TMS Concepts and Glossary 1.0.docx

    }
}
