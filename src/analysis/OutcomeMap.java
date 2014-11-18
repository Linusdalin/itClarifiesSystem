package analysis;

import analysis.AnalysisOutcome;
import contractManagement.ContractFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-08-07
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class OutcomeMap {

    public final AnalysisOutcome outcome;
    public final ContractFragment fragment;

    public OutcomeMap(AnalysisOutcome outcome, ContractFragment fragment){

        this.outcome = outcome;
        this.fragment = fragment;
    }
}
