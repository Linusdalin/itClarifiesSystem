package classification;

import analysis2.Pattern;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import project.Project;
import crossReference.Definition;
import dataRepresentation.DBTimeStamp;
import document.DefinitionType;
import featureTypes.FeatureTypeTree;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.RiskClassification;
import userManagement.PortalUser;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************'
 *
 *              Handle setting a classification on a fragment with
 *              all potential side effects
 *
 *
 */

public class Classifier {

    private final ContractVersionInstance version;
    private final Project project;
    private DBTimeStamp analysisTime;
    PortalUser classifier;

    private List<FragmentClassification>  classificationsToStore = new ArrayList<FragmentClassification>();
    private List<Definition>              definitionsToStore = new ArrayList<Definition>();
    private List<ContractFragment>        fragmentsToUpdate = new ArrayList<ContractFragment>();
    private List<RiskClassification>      risksToStore = new ArrayList<RiskClassification>();

    private List<FragmentClassification> existingClassifications;


    // These are classifications that also should be classified as risk

    private static final ClassificationPattern[] riskPattern = {

            new ClassificationPattern(FeatureTypeTree.Obligation,            "Vague"),
            new ClassificationPattern(FeatureTypeTree.Termination,           "Convenience"),
            new ClassificationPattern(FeatureTypeTree.Acceptance,            "Subjective"),
            new ClassificationPattern(FeatureTypeTree.LimitationOfLiability, "Uncapped"),
            new ClassificationPattern(FeatureTypeTree.LimitationOfLiability, "Indirect"),
            new ClassificationPattern(FeatureTypeTree.Compensation,          "Risk"),
            new ClassificationPattern(FeatureTypeTree.IPR,                   "Abstract"),

    };


    /*****************************************************************************'
     *
     *      Create a classifier for a document in a project
     *
     *
     * @param project
     * @param version
     * @param analysisTime
     */



    public Classifier(Project project, ContractVersionInstance version, DBTimeStamp analysisTime){

        this.version = version;
        this.project = project;
        this.analysisTime = analysisTime;


        try {

            this.classifier = PortalUser.getSystemUser();

        } catch (BackOfficeException e) {
            PukkaLogger.log( e );
        }

        existingClassifications =  version.getFragmentClassificationsForVersion();  // Get all existing classifications from previous runs.
    }



    /**************************************************************************
     *
     *          Add a classification (and handle side effects) to a fragment
     *
     *          This method stores the classifications (and other attributes) in
     *          the lists pending the store command.
     *
     *
     * @param classification
     * @param fragment
     *
     *          //TODO: Pass pattern w position
     */


    public void addClassification(FragmentClassification classification, ContractFragment fragment) {

        Pattern pattern = new Pattern(classification.getPattern(), fragment.getText().indexOf(classification.getPattern()));

        if(manuallyOverridden(classification, existingClassifications)){

            classification.setblockingState(FragmentClassification.OVERRIDDEN);

        }

        classificationsToStore.add(classification);
        fragmentsToUpdate.add(fragment);
        handleSideEffects(classification, fragment, pattern, analysisTime);
        extractRiskForClassification(fragment, classification, pattern);                // -> new Risk

    }


    /*******************************************************************
     *
     *          A classification is manually overridden if the classification
     *          already exists for the same fragment with the same pattern
     *
     *          (I.e. it was a comment in the document
     *          either by the user or re-imported)
     *
     *          NOTE: This will also avoid duplicates
     *
     *          //TODO: Improvement Functionality: Note that the manual classification is covered by the generated classification to avoid generating comments
     *
     *
     * @param classification          - the classification
     * @param existingClassifications  - all previous classifications
     * @return                        - true if the classification already exists
     */

    private boolean manuallyOverridden(FragmentClassification classification, List<FragmentClassification> existingClassifications) {

        System.out.println("Checking for duplicate classifications among " + existingClassifications.size() + " existing classifications.");

        //Only debug. TODO: Remove

        /*
        int i = 1;
        for (FragmentClassification fragmentClassification : existingClassifications) {

            System.out.println(" --- "+ i++ + fragmentClassification.getClassTag() + "(" + classification.getblockingState() +  ") for " + classification.getPattern());
        }

            */

        for (FragmentClassification existing : existingClassifications) {

            //System.out.println("  -- Comparing classification " + classification.getClassTag() + "("+ classification.getPattern() + ") with " +
            //        existing.getClassTag() + "("+ existing.getPattern() + ")");

            if(     existing.getClassTag().equals(classification.getClassTag()) &&
                    existing.getblockingState() != FragmentClassification.BLOCKING &&
                    existing.getFragmentId().equals(classification.getFragmentId()) &&
                    isSamePattern(existing.getPattern(), classification.getPattern())){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring Classification " + classification.getClassTag() + " for " + classification.getPattern() + " already exists.");
                return true;
            }
        }

        return false;

    }

    /**********************************************************************
     *
     *          Comparing patterns.
     *
     *
     *          //TODO: Improvement Functionality: We should really compare positions here to see overlap, but that requires implementation of pattern pos in export
     *
     * @param pattern1              -
     * @param pattern2              -
     * @return                      - are they actually the same
     */

    private boolean isSamePattern(String pattern1, String pattern2) {

        return pattern1.contains(pattern2) || pattern2.contains(pattern1);

    }

    // TODO: Handle classification count for multiple classifications added to the same fragment

    public void store() {

        try {

            FragmentClassificationTable classificationTable = new FragmentClassificationTable();
            classificationTable.createEmpty();

            // Merge the existing classifications in the database with the new classifications added here
            List<FragmentClassification> allClassifications = new ArrayList<FragmentClassification>();
            allClassifications.addAll(existingClassifications);
            allClassifications.addAll(classificationsToStore);

            for (FragmentClassification classification : classificationsToStore) {

                updateBlockStatus(classification, allClassifications);  // Se if it is blocked by another block tag
                classificationTable.add(classification);

            }

            classificationTable.store();

            for (Definition definition : definitionsToStore) {

                System.out.println(" --- Storing definition " + definition.getName());
                definition.store();
            }

            for (RiskClassification risk : risksToStore) {

                System.out.println(" --- Storing risk " + risk.getComment());
                risk.store();
            }

            for (ContractFragment fragment : fragmentsToUpdate) {

                fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
                fragment.update();

            }



        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
        }


    }

    /****************************************************************************************'
     *
     *
     *
     *
     * @param classification                 - this classification
     * @param classificationsToStore         - all other classificaiton
     */

    private void updateBlockStatus(FragmentClassification classification, List<FragmentClassification> classificationsToStore) {

        System.out.println("Checking for blocking classifications among " + classificationsToStore.size() + " existing classifications.");



        for (FragmentClassification otherClassification : classificationsToStore) {

            System.out.println(" --- Comparing Tag " + classification.getClassTag() + ": " + classification.getblockingState() + " for " + classification.getPattern() + " with " +
                    otherClassification.getClassTag() + ": " + otherClassification.getblockingState() + " for " + classification.getPattern());


            if(     classification.getblockingState() == FragmentClassification.NOT_BLOCKED &&
                    otherClassification.getblockingState() == FragmentClassification.BLOCKING &&
                    otherClassification.getFragmentId().equals(classification.getFragmentId()) &&
                    otherClassification.getClassTag().equals(classification.getClassTag())){

                classification.setblockingState( FragmentClassification.BLOCKED );       // Set the state to blocked here
                PukkaLogger.log(PukkaLogger.Level.INFO, "    --- Blocking the classification " + classification.getClassTag() + " for " + classification.getPattern());
            }else
                System.out.println("    --- Ok to leave.");

        }


    }

    /***************************************************************************
     *
     *          Some classifications has side effects. Handle these and store the
     *          attributes in the appropriate lists pending store
     *
     *
     * @param classification
     * @param fragment
     * @param analysisTime
     */


    private void handleSideEffects(FragmentClassification classification, ContractFragment fragment, Pattern pattern, DBTimeStamp analysisTime) {

        // If the classification is a #Party, we shall also create a definition
        // for this with the type party

        if(classification.getClassTag().equals(FeatureTypeTree.Parts.getName())){

            Definition party = new Definition(
                    classification.getPattern(),
                    DefinitionType.ACTOR.name(),
                    fragment.getKey(),
                    fragment.getOrdinal(),
                    version.getKey(),
                    project.getKey(),
                    classification.getPattern());

            definitionsToStore.add(party);

        }


    }

    /******************************************************************************************************'
     *
     *          A classification may also be a risk
     *
     *
     * @param fragment                 - the current fragment
     * @param classification           - the classification
     * @param pattern                  - pattern from the classification
     */


    public void extractRiskForClassification(ContractFragment fragment, FragmentClassification classification, Pattern pattern) {


        PukkaLogger.log(PukkaLogger.Level.INFO, "*** Extract risk for classification " + classification.getClassTag() + " " + classification.getComment());

        for (ClassificationPattern classificationPattern : riskPattern) {

            if(classification.getClassTag().equals(classificationPattern.getClassification().getName()) &&
               classification.getComment().equals(classificationPattern.getTag())){

                PukkaLogger.log(PukkaLogger.Level.ACTION, "*** Creating risk from classification fragment " + fragment.getName() + "(" + pattern.getText() + ")");

                String riskDescription = "The phrasing " + pattern.getText() + "( " + classification.getComment()+ " )";

                RiskClassification risk = createRisk(fragment, riskDescription, pattern);

                fragmentsToUpdate.add(fragment);
                risksToStore.add(risk);

            }

        }

    }

    private RiskClassification createRisk(ContractFragment fragment, String riskDescription, Pattern pattern){

        ContractRisk defaultRisk = ContractRisk.getUnknown();

        fragment.setRisk(defaultRisk);

        return new RiskClassification(
                            fragment.getKey(),
                            defaultRisk,
                            riskDescription,
                            "#RISK",
                            classifier.getKey(),
                            version.getKey(),
                            project.getKey(),
                            pattern.getText(),
                            pattern.getPos(),
                            analysisTime.getSQLTime().toString(),
                            FragmentClassification.NOT_BLOCKED
                    );

    }

}
