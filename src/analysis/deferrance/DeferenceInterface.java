package analysis.deferrance;

import classifiers.Classification;
import contractManagement.ContractFragment;

/**
 *          Common interface for all deference
 *
 */
public interface DeferenceInterface {

    Classification getClassification();
    DeferenceType getType();
    ContractFragment getFragment();
    boolean isActive();
    void consume();
    boolean triggers(ContractFragment activeFragment);
}
