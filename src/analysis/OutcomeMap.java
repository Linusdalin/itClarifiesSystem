package analysis;

import analysis2.NewAnalysisOutcome;
import contractManagement.ContractFragment;

/**
 *
 *          Outcome Map is the value pair that connects an analysis outcome with the fragment.
 *
 *          It is used to be able to do a second pass over all the
 *          fragments without redoing the initial analysis
 *
 */
public class OutcomeMap {

    public final NewAnalysisOutcome outcome;
    public final ContractFragment fragment;

    public OutcomeMap(NewAnalysisOutcome outcome, ContractFragment fragment){

        this.outcome = outcome;
        this.fragment = fragment;
    }
}
