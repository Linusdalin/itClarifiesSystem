package fileHandling;

import contractManagement.*;
import document.AbstractComment;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import userManagement.PortalUser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *              The exporter is responsible for taking a document (version) and exporting it my matching it to the
 *              original document.
 *
 *
 */
public class Exporter {

    private static final String suffix = "_(clarified)";  // For output file


    public Exporter(){


    }

    /***************************************************************************************'''
     *
     *          Export the document.
     *
     *           - Adding comments to the original file and storing it as (clarified)
     *
     *
     * @param documentVersion - the version to export
     * @throws BackOfficeException
     */




    public DocXFile getFileToExport(ContractVersionInstance documentVersion) throws BackOfficeException{

        String fileName = documentVersion.getDocument().getFile();
        DocXFile docXDocument = new DocXFile(new RepositoryFileHandler(fileName));

        addComments(docXDocument, documentVersion);

        return docXDocument;


    }


    /***************************************************************''
     *
     *      Create an output name from the file name given.
     *
     *      Example:
     *
     *      myFile.docx -> myFile_(clarified).docx
     *
     *
     * @param fileName
     * @return
     */


    public String getOutputName(String fileName) {

        int dot = fileName.lastIndexOf(".");

        String name = fileName.substring(0, dot) + suffix + fileName.substring(dot);

        return name;

    }


    /**************************************************************************''
     *
     *          main modification. Adding comments from:
     *
     *           - classifications
     *           - risks
     *           - annotations
     *
     *
     *
     * @param document - the DocX file to generate to
     * @param documentVersion - current version of the internal document
     */

    private void addComments(DocXFile document, ContractVersionInstance documentVersion) {

        try {

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding comments");
            List<FragmentClassification> classificationsForDocument = documentVersion.getFragmentClassificationsForVersion();
            List<RiskClassification> risksForDocument = documentVersion.getRiskClassificationsForVersion();
            List<ContractAnnotation> annotationsForDocument = documentVersion.getContractAnnotationsForVersion();

            List<AbstractComment> commentList = new ArrayList<AbstractComment>();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all classification comments");
            commentList.addAll(createCommentsFromClassifications(classificationsForDocument));

            /*
                Removing risk from the export. The risk has an associated risk description annotation.
                //TODO: When the risk annotation is integrated into the risk, this should be changed back

                PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all risk comments");
                commentList.addAll(createCommentsFromRisks(risksForDocument));

            */

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all annotation comments");
            commentList.addAll(createCommentsFromAnnotations(annotationsForDocument));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding comments to docX file");
            document.annotateFile(commentList, documentVersion);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Done");

        } catch (BackOfficeException e) {

            PukkaLogger.log(e);
            return;
        }


    }

    /*************************************************************************'
     *
     *          Convert the classifications to a list of AbstractComments
     *
     *
     *
     * @param classificationsForDocument
     * @return - list of abstract comments of the type "Classification"
     */

    private List<AbstractComment> createCommentsFromClassifications(List<FragmentClassification> classificationsForDocument) {

        List<AbstractComment> list = new ArrayList<AbstractComment>();

        for(FragmentClassification classification : classificationsForDocument){

            list.add(new AbstractComment("Classification", classification.getPattern(), classification.getName(),
                    (int)classification.getFragment().getOrdinal(), (int)classification.getPos(), (int)classification.getLength()));
        }

        return list;

    }

    /*****************************************************************************
     *
     *          Convert the risks to a list of AbstractComments
     *
     *
     * @param risksForDocument
     * @return - list of abstract comments of the type "Risk"
     */

    private List<AbstractComment> createCommentsFromRisks(List<RiskClassification> risksForDocument) {

        List<AbstractComment> list = new ArrayList<AbstractComment>();

        for(RiskClassification risk : risksForDocument){

            list.add(new AbstractComment("Risk", risk.getPattern(), "Risk(" + risk.getRisk().getName() + ")", (int)risk.getFragment().getOrdinal(), -1, -1));
        }

        return list;

    }


    /**********************************************************************************************************
     *
     *          Convert the annotations to a list of AbstractComments
     *
     *
     *
     * @param annotationsForDocument
     * @return - list of abstract comments of the type "Annotation"
     */

    private List<AbstractComment> createCommentsFromAnnotations(List<ContractAnnotation> annotationsForDocument) {

        List<AbstractComment> list = new ArrayList<AbstractComment>();

        for(ContractAnnotation annotation : annotationsForDocument){


            try {

                // Annotations made by external users are already in the document, so we don't need to add them back

                if(!annotation.getCreatorId().equals(PortalUser.getExternalUser().getKey())){

                    list.add(new AbstractComment("Annotation", annotation.getPattern(), annotation.getDescription(), (int)annotation.getFragment().getOrdinal(), -1, -1));
                    System.out.println("**** Annotation " + annotation.getDescription() + " for fragment " + annotation.getFragment().getName() + " with id " + annotation.getFragment().getOrdinal());
                }

            } catch (BackOfficeException e) {

                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return list;

    }

}
