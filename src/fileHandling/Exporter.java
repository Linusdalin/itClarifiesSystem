package fileHandling;

import classification.FragmentClassification;
import contractManagement.*;
import document.AbstractComment;
import featureTypes.FeatureType;
import featureTypes.FeatureTypeTree;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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




    public DocXExport enhanceFile(ContractVersionInstance documentVersion) throws BackOfficeException{

        String fileName = documentVersion.getDocument().getFile();
        DocXExport docXDocument = new DocXExport(new RepositoryFileHandler(fileName));

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
     * @param document              - the DocX file to generate to
     * @param documentVersion       - current version of the internal document
     */

    private void addComments(DocXExport document, ContractVersionInstance documentVersion) {

        try {

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding comments for all attributes");
            List<FragmentClassification> classificationsForDocument = documentVersion.getFragmentClassificationsForVersion();

            List<RiskClassification> risksForDocument = documentVersion.getRiskClassificationsForVersion();
            List<ContractAnnotation> annotationsForDocument = documentVersion.getContractAnnotationsForVersion();

            List<AbstractComment> commentList = new ArrayList<AbstractComment>();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding "+ classificationsForDocument.size()+" classification comments");

            commentList.addAll(createCommentsFromClassifications(classificationsForDocument));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all annotation comments");
            commentList.addAll(createCommentsFromAnnotations(annotationsForDocument));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all risk comments");
            commentList.addAll(createCommentsFromRisks(risksForDocument));


            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding "+ commentList.size()+" comments to docX file");
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
     * @param classificationsForDocument          - all classifications
     * @return - list of abstract comments of the type "Classification"
     */

    private List<AbstractComment> createCommentsFromClassifications(List<FragmentClassification> classificationsForDocument) {

        List<AbstractComment> list = new ArrayList<AbstractComment>();

        for(FragmentClassification classification : classificationsForDocument){

            if(isRelevantForExport(classification)){

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "  -- Found a classification " + classification.getClassTag() + " for " + classification.getPattern());

                list.add(new AbstractComment("Classification", classification.getPattern(), classification.getClassTag(),
                        (int)classification.getFragment().getOrdinal(), (int)classification.getPos(), (int)classification.getLength()));
            }
            else{

                PukkaLogger.log(PukkaLogger.Level.DEBUG, "  -- Ignoring classification " + classification.getClassTag() + " as irrelevant for export." );

            }

        }


        System.out.println("  -- Extracted " + list.size() + " comments for classifications ");

        return list;

    }

    /***************************************************************************************'
     *
     *              Filter classifications
     *
     *
     * @param classification     - the potential classification
     * @return                   - is it relevant to export or not?
     */

    private boolean isRelevantForExport(FragmentClassification classification) {

        if(     classification.getClassTag().equals(FeatureTypeTree.DefinitionRepetition.getName()) ||
                classification.getClassTag().equals(FeatureTypeTree.DefinitionDef.getName()) ||
                classification.getClassTag().equals(FeatureTypeTree.DefinitionUsage.getName())
        ){

            return false;
        }

        return true;

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

            ContractFragment fragment = risk.getFragment();

            int anchorStart = (int)risk.getPatternPos();
            int anchorLength = risk.getPattern().length();

            list.add(new AbstractComment("Risk", risk.getPattern(), "Risk(" + risk.getRisk().getName() + ")", (int)risk.getFragment().getOrdinal(), anchorStart, anchorLength));
            System.out.println("**** Risk " + risk.getDescription() + "(" + risk.getPattern() + ") for fragment " + fragment.getName() + " with id " + fragment.getOrdinal());
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

                    int anchorStart = (int)annotation.getPatternPos();
                    int anchorLength = annotation.getPattern().length();

                    ContractFragment fragment = annotation.getFragment();
                    if(annotation.getPatternPos() < 0){

                        // The pattern is empty. Set the entire fragment as an anchor
                        anchorStart = 0;
                        anchorLength = fragment.getText().length();

                    }

                    list.add(new AbstractComment("Annotation", annotation.getPattern(), annotation.getDescription(), (int)fragment.getOrdinal(), anchorStart, anchorLength));
                    System.out.println("**** Annotation " + annotation.getDescription() + "("+ annotation.getPattern()+") for fragment " + fragment.getName() + " with id " + fragment.getOrdinal());
                }

            } catch (BackOfficeException e) {

                PukkaLogger.log( e );

            }
        }

        return list;

    }


}
