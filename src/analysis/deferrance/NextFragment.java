package analysis.deferrance;

import classifiers.Classification;
import contractManagement.ContractFragment;

/**************************************************************''
 *
 *          Deferring the action to the NEXT fragment in order
 */

public class NextFragment extends DeferredAction implements DeferenceInterface {



    public NextFragment(Classification classification, ContractFragment fragment){

        type = DeferenceType.NEXT_FRAGMENT;
        this.classification = classification;
        this.fragment = fragment;
    }

    /*******************************************************************
     *
     *      The trigger rule for "NextFragment" is that the active fragment
     *      is directly after the fragment from the deference
     *
     *
     * @param activeFragment      - current fragment
     * @return                    - true if the deferred actions should be reactivated
     */


    public boolean triggers( ContractFragment activeFragment ) {

        System.out.println("*** Deference NextFragment: Testing trigger for fragment " + activeFragment.getOrdinal() + " against " + fragment.getOrdinal());
        return (activeFragment.getOrdinal() == fragment.getOrdinal() + 1);
    }
}
