package analysis.deferrance;

import analysis2.NewAnalysisOutcome;
import classifiers.Classification;
import contractManagement.ContractFragment;
import log.PukkaLogger;
import system.Analyser;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 *
 *              Handling deferred classifications
 *
 *              A deferred classification is when a classification is triggered by one fragment
 *              but activated for another.
 *
 *              Deference only works with forward references. The fragments are still handled in order.
 *
 *
 */
public class DeferenceHandler {

    // The list of actions that are deferred (stored)

    private List<DeferenceInterface> deferredActions = new ArrayList<DeferenceInterface>();


    /************************************************************
     *
     *          Store a deference
     *
     *
     * @param deference    - the deference containing type, fragment and classification
     */

    public void defer(DeferenceInterface deference ){

        deferredActions.add(deference);
        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Deferring " + deference.getClassification().display());

    }


    /*****************************************************************
     *
     *          Go through the list of deferred actions and see if there is anyone that should be activated
     *
     *          It sets the pass to a special value DEFERENCE to indicate this when handling outcome to understand
     *          that this time it should not be deferred again.
     *
     * @param analysisResult        - the analysis result to be updated.
     * @param fragment              - the current fragment (to be deferred TO)
     * @return                      - updated analysis result with any potential deferred actions added
     */


    public NewAnalysisOutcome activateDeferences(NewAnalysisOutcome analysisResult, ContractFragment fragment){

        if(deferredActions.size() == 0)
            return analysisResult;

        for (DeferenceInterface deferredAction : deferredActions) {

            if(deferredAction.isActive()){


                if(deferredAction.triggers(fragment)){

                    // Add the classification to the outcome to activate

                    PukkaLogger.log(PukkaLogger.Level.INFO, " *** Activation of deferred action " + deferredAction.getClassification().display()+ " to fragment " + fragment.getName());
                    Classification activatedClassification = deferredAction.getClassification();
                    activatedClassification.setPass(Analyser.DEFERENCE_PASS);
                    analysisResult.addClassification(activatedClassification);

                    deferredAction.consume();

                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.DEBUG, " *** Deferred Action NOT Activated " + deferredAction.getFragment().getOrdinal() +" != " + (fragment.getOrdinal() - 1));

                }

            }

        }

        return analysisResult;
    }

}
