package fileHandling;

import classification.FragmentClassification;
import contractManagement.*;
import document.AbstractComment;
import featureTypes.FeatureTypeTree;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import project.Project;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import services.DocumentService;
import userManagement.Organization;
import userManagement.PortalUser;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************************************************
 *
 *              The exporter is responsible for taking a document (version) and exporting it
 *              by matching it to the original stored document.
 *
 *              //TODO: Improvement: It would be nice to be able to have a version number or date in the file name in the export
 *
 */

public class Exporter {


    //The suffix for the exported file
    private static final String suffix = "_(clarified)";


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
     * @param filter - JSON tag list
     *
     * @throws BackOfficeException
     */




    public DocXExport enhanceFile(ContractVersionInstance documentVersion, String filter) throws BackOfficeException{

        Contract document = documentVersion.getDocument();
        String fileName = document.getFile();
        Project project = document.getProject();
        DocXExport docXDocument = new DocXExport(new RepositoryFileHandler(fileName));

        addComments(docXDocument, documentVersion, project, filter);

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
     * @param fileName     - the name of the uploaded document
     * @return             - the name of the exported document
     */


    public String getOutputName(String fileName) {

        int dot = fileName.lastIndexOf(".");

        return fileName.substring(0, dot) + suffix + fileName.substring(dot);


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
     * @param project               - the project of the document (for project/organization settings)
     * @param filter                - JSON list of tags
     */

    private void addComments(DocXExport document, ContractVersionInstance documentVersion, Project project, String filter) {

        try {

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding comments for all attributes");
            List<FragmentClassification> classificationsForDocument = documentVersion.getFragmentClassificationsForVersion();

            List<RiskClassification> risksForDocument = documentVersion.getRiskClassificationsForVersion();
            List<ContractAnnotation> annotationsForDocument = documentVersion.getContractAnnotationsForVersion();

            List<AbstractComment> commentList = new ArrayList<AbstractComment>();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding "+ classificationsForDocument.size()+" classification comments");

            commentList.addAll(createCommentsFromClassifications(classificationsForDocument, project, filter));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all annotation comments");
            commentList.addAll(createCommentsFromAnnotations(annotationsForDocument));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding all risk comments");
            commentList.addAll(createCommentsFromRisks(risksForDocument));


            PukkaLogger.log(PukkaLogger.Level.INFO, "Adding "+ commentList.size()+" comments to docX file");
            document.annotateFile(commentList, documentVersion);
            PukkaLogger.log(PukkaLogger.Level.INFO, "Done");

        } catch (BackOfficeException e) {

            PukkaLogger.log(e);

        }


    }

    /*************************************************************************'
     *
     *          Convert the classifications to a list of AbstractComments
     *
     *
     *
     *
     *
     * @param classificationsForDocument          - all classifications
     * @param project                             - the project
     *@param filter                              - JSON list of #tags  @return - list of abstract comments of the type "Classification"
     */

    private List<AbstractComment> createCommentsFromClassifications(List<FragmentClassification> classificationsForDocument, Project project, String filter) {

        List<AbstractComment> list = new ArrayList<AbstractComment>();
        LanguageInterface language = new English();
        Organization organization = project.getOrganization();

        for(FragmentClassification classification : classificationsForDocument){

            if(isRelevantForExport(classification, filter)){

                String localizedTag = "#" + DocumentService.getLocalizedTag(classification.getClassTag(), organization, language);

                PukkaLogger.log(PukkaLogger.Level.INFO, "  -- Found a classification " + localizedTag + "(" + classification.getClassTag() + ") for " + classification.getPattern());

                list.add(new AbstractComment("Classification", classification.getPattern(), localizedTag,
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
     *
     * @param classification     - the potential classification
     * @param filter             - JSON list of tags
     * @return                   - is it relevant to export or not?
     *
     *              //TODO. Not implemented TAG filter
     *
     */

    private boolean isRelevantForExport(FragmentClassification classification, String filter) {

        if(     classification.getClassTag().equals(FeatureTypeTree.DefinitionRepetition.getName()) ||
                classification.getClassTag().equals(FeatureTypeTree.DefinitionDef.getName()) ||
                classification.getClassTag().equals(FeatureTypeTree.DefinitionUsage.getName())
        )
        {  return false; }

        // A classification generated from a comment in the system is labelled with the external user.
        // It already exists so we will ignore it when exporting.

        if(classification.getCreator().getName().equals("External")){

            return false;

        }

        System.out.println(" Classification to export: " + classification.toStringLong());

        return true;

    }

    /*****************************************************************************
     *
     *          Convert the risks to a list of AbstractComments
     *
     *
     * @param risksForDocument         - all risks in the document
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
     * @param annotationsForDocument   - all annotations pre-fetched from database
     *
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
