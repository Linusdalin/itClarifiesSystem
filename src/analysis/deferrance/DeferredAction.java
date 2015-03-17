package analysis.deferrance;

import classifiers.Classification;
import contractManagement.ContractFragment;
import log.PukkaLogger;

/**********************************************************
 *
 *          Remember an action that shall be performed on another fragment
 *
 *
 */


abstract public class DeferredAction implements DeferenceInterface {

    protected DeferenceType type;               // The type defining WHEN it shall be activated
    protected Classification classification;    // The classification (i.e. action) to be deferred
    protected ContractFragment fragment;        // Initial fragment

    private boolean isActive = true;            // Will be set to false when the deferredAction is activated

    public Classification getClassification() {

        return classification;
    }


    public ContractFragment getFragment() {
        return fragment;
    }

    public boolean isActive() {
        return isActive;
    }

    public void consume() {
        isActive = false;
    }

    public boolean triggers(ContractFragment fragment) {

        PukkaLogger.log(PukkaLogger.Level.ERROR, "Calling the default implementation of triggers! Not implemented");
        return false;
    }

    public DeferenceType getType() {
        return type;
    }

}
