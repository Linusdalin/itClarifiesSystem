package classification;

import classifiers.Classification;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.Definition;
import document.DefinitionType;
import featureTypes.FeatureTypeTree;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************'
 *
 *              Handle setting a classification on a fragment with
 *              all potential side effects
 */

public class Classifier {

    private final ContractVersionInstance version;
    private final Project project;

    private List<FragmentClassification>  classificationsToStore = new ArrayList<FragmentClassification>();
    private List<Definition>              definitionsToStore = new ArrayList<Definition>();
    private List<ContractFragment>        fragmentsToUpdate = new ArrayList<ContractFragment>();


    /*****************************************************************************'
     *
     *      Create a classifier for a document in a project
     *
     *
     * @param project
     * @param version
     */



    public Classifier(Project project, ContractVersionInstance version){

        this.version = version;
        this.project = project;
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
     */


    public void addClassification(FragmentClassification classification, ContractFragment fragment) {

        classificationsToStore.add(classification);
        fragmentsToUpdate.add(fragment);
        handleSideEffects(classification, fragment);

    }

    //TODO: Implement batch update here
    // TODO: Handle classification count for multiple classifications added to the same fragment

    public void store() {

        try {

            for (FragmentClassification classification : classificationsToStore) {

                classification.store();

            }

            for (Definition definition : definitionsToStore) {

                System.out.println(" --- Storing definition " + definition.getName());
                definition.store();
            }

            for (ContractFragment fragment : fragmentsToUpdate) {

                fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
                fragment.update();

            }



        } catch (BackOfficeException e) {

            PukkaLogger.log( e );
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
     */


    private void handleSideEffects(FragmentClassification classification, ContractFragment fragment) {

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


}
