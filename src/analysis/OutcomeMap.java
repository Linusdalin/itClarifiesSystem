package analysis;

import analysis.AnalysisOutcome;
import analysis2.NewAnalysisOutcome;
import classifiers.Classification;
import contractManagement.ContractFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-08-07
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class OutcomeMap {

    public final NewAnalysisOutcome outcome;
    public final ContractFragment fragment;

    public OutcomeMap(NewAnalysisOutcome outcome, ContractFragment fragment){

        this.outcome = outcome;
        this.fragment = fragment;
    }
}
