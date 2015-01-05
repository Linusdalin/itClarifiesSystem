package versioning;

import actions.Action;
import cache.ServiceCache;
import contractManagement.*;
import diff.DiffStructure;
import diff.FragmentComparator;
import diff.Match;
import log.PukkaLogger;
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
import pukkaBO.condition.Sorting;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import services.FragmentServlet;

import java.util.List;

/*******************************************************************************************************''
 *
 *      Clone document is used when a new document is uploaded and we want to transpose information to it.
 *
 *      It will try to map all the fragments in the new document to the old document and move over:
 *
 *       - annotations
 *       - risk classifications
 *       - references
 *
 *
 */


public class Transposer {

    //TODO: This class actually moves an annotation/classification to a new fragment. Should be creating new and copy


    public void clone(ContractVersionInstance fromVersion, ContractVersionInstance toVersion) throws BackOfficeException {


        List<ContractFragment> activeFragments = toVersion.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));
        List<ContractFragment> initialFragments = fromVersion.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));


        FragmentComparator comparator = new FragmentComparator();
        DiffStructure diffStructure = comparator.diff(asTextArray(activeFragments), asTextArray(initialFragments));

        PukkaLogger.log(PukkaLogger.Level.INFO, "Got diff structure: " + diffStructure.toString());
        int fragmentNo = 0;

        for(ContractFragment fragment : initialFragments){

            int annotationCount = 0;
            int classificationCount = 0;
            int actionCount = 0;

            // Get the corresponding fragment in the new version
            ContractFragment targetFragment = null;
            int targetFragmentNumber = diffStructure.getFragmentInActive(fragmentNo);

            if(targetFragmentNumber != Match.ORPHAN){

                 targetFragment = activeFragments.get(targetFragmentNumber);
            }

            List<ContractAnnotation> annotationsForFragment = fragment.getAnnotationsForFragment();

            for(ContractAnnotation annotation : annotationsForFragment){

                if(targetFragmentNumber != Match.ORPHAN){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring an annotation ");
                    annotation.setFragment(targetFragment.getKey());
                    annotation.update();
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.WARNING, "Dropping an annotation of an orphaned fragment ");
                    // TODO: Add this as some kind of output report

                }
                annotationCount++;
            }

            List<Action> actionsForFragment = fragment.getActionsForFragment();

            for(Action action : actionsForFragment){

                if(targetFragmentNumber != Match.ORPHAN){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring an action ");
                    action.setFragment(targetFragment.getKey());
                    action.update();
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.WARNING, "Dropping an action of an orphaned fragment ");
                    // TODO: Add this as some kind of output report

                }
                actionCount++;
            }

            List<FragmentClassification> fragmentClassifications = fragment.getClassificationsForFragment();

            for(FragmentClassification classification : fragmentClassifications){

                if(targetFragmentNumber != Match.ORPHAN){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring a classification ");
                    classification.setFragment(targetFragment.getKey());
                    classification.update();
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Dropping a classification of an orphaned fragment ");
                    // TODO: Add this as some kind of output report

                }
                classificationCount++;
            }

            List<RiskClassification> fragmentRisks = fragment.getRiskClassificationsForFragment();

            for(RiskClassification risk : fragmentRisks){

                if(targetFragmentNumber != Match.ORPHAN){

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring a risk ");
                    risk.setFragment(targetFragment.getKey());
                    risk.update();
                }
                else{

                    PukkaLogger.log(PukkaLogger.Level.INFO, "Dropping a risk classification of an orphaned fragment ");
                    // TODO: Add this as some kind of output report

                }
            }

            if(targetFragment != null){

                targetFragment.setRisk(fragment.getRisk());
                targetFragment.setAnnotationCount(annotationCount);
                targetFragment.setActionCount(actionCount);
                targetFragment.setClassificatonCount(classificationCount);
                targetFragment.update();
            }
            fragmentNo++;

        }

        // Finally clear the cache for the Fragment for Document call

        ServiceCache cache = new ServiceCache(FragmentServlet.DataServletName);
        cache.remove(fromVersion.getDocumentId().toString());




    }

    /*********************************************************************************************
     *
     *      Getting the fragments as a textArray
     *
     *
     * @param fragmentsForVersion
     * @return
     */


    private String[] asTextArray(List<ContractFragment> fragmentsForVersion) {

        String[] fragmentBodyArray = new String[fragmentsForVersion.size()];

        int i = 0;
        for(ContractFragment fragment : fragmentsForVersion){

            fragmentBodyArray[i++] = fragment.getText();
        }

        return fragmentBodyArray;
    }


}
