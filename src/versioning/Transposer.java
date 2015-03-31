package versioning;

import actions.Action;
import cache.ServiceCache;
import classification.FragmentClassification;
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
import userManagement.PortalUser;

import java.util.Comparator;
import java.util.List;

/*******************************************************************************************************''
 *
 *      Clone document is used when a new document is uploaded and we want to transpose information to it.
 *
 *      It will try to map all the fragments in the new document to the old document and move over:
 *
 *       - annotations
 *       - risk
 *       - classifications
 *       - actions
 *
 *      //TODO: This has to produce a feedback log that is propagated to the user
 *
 */



public class Transposer {


    /**********************************************************************************************
     *
     *          Clone will move all attributes from the old version of the document to the new.
     *
     *
     * @param fromVersion             - source
     * @param toVersion               - target
     * @throws BackOfficeException
     */

    public void clone(ContractVersionInstance fromVersion, ContractVersionInstance toVersion) {


        List<ContractFragment> activeFragments = toVersion.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));
        List<ContractFragment> initialFragments = fromVersion.getFragmentsForVersion(new LookupList(new Sorting(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST)));


        FragmentComparator comparator = new FragmentComparator();
        DiffStructure diffStructure = comparator.diff(asTextArray(activeFragments), asTextArray(initialFragments));

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Got diff structure: " + diffStructure.toString());
        int fragmentNo = 0;

        for(ContractFragment fragment : initialFragments){

            try{

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

                    PortalUser owner = annotation.getCreator();

                    if(owner.getType().equals(PortalUser.getExternalUser().getType())){

                        // The annotation is made by the "external user, i.e. imported comment. This should NOT
                        // be transposed as it is either removed or imported again in the upload

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring imported annotation " + annotation.getDescription() + "(" + owner.getType() + ") in " + fromVersion.getVersion());

                    }else if(alreadyExists(annotation, targetFragment)){

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Ignoring already generated annotation "+ annotation.getDescription() + "(" + owner.getType()+") from " + fromVersion.getVersion());

                    }
                    else if(targetFragmentNumber != Match.ORPHAN){

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring an annotation by "+ owner.getName() + "(" + owner.getType()+") from " + fromVersion.getVersion() + " to " + toVersion.getVersion());
                        annotation.setFragment(targetFragment.getKey());
                        annotation.update();
                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Dropping an annotation "+ annotation.getDescription() + "(" + owner.getType()+") in " + fromVersion.getVersion() + " as there is no corresponding target fragment (" + fragment.getText() + ")");
                        // TODO: Add this as some kind of output report

                    }
                    annotationCount++;
                }

                List<Action> actionsForFragment = fragment.getActionsForFragment();

                for(Action action : actionsForFragment){

                    if(targetFragmentNumber != Match.ORPHAN){

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring an action " + action.getName());
                        action.setFragment(targetFragment.getKey());
                        action.update();
                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Dropping an action "+ action.getDescription() + "(" + action.getIssuer().getName()+") in " + fromVersion.getVersion() + " as there is no corresponding target fragment (" + fragment.getText() + ")");
                        // TODO: Add this as some kind of output report

                    }
                    actionCount++;
                }

                List<FragmentClassification> fragmentClassifications = fragment.getClassificationsForFragment();

                for(FragmentClassification classification : fragmentClassifications){

                    if(targetFragmentNumber != Match.ORPHAN){

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring a classification " + classification.getClassTag());
                        classification.setFragment(targetFragment.getKey());
                        classification.update();
                    }
                    else{

                        PukkaLogger.log(PukkaLogger.Level.WARNING, "Dropping a classification "+ classification.getClassTag() + "(" + classification.getCreator().getName()+") in " + fromVersion.getVersion() + " as there is no corresponding target fragment (" + fragment.getText() + ")");
                        // TODO: Add this as some kind of output report

                    }
                    classificationCount++;
                }

                List<RiskClassification> fragmentRisks = fragment.getRiskClassificationsForFragment();

                for(RiskClassification risk : fragmentRisks){

                    if(targetFragmentNumber != Match.ORPHAN){

                        PukkaLogger.log(PukkaLogger.Level.INFO, "Transferring a risk " + risk.getRisk().getName());
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

            }catch(BackOfficeException e){

                e.logError("Failed transposing attributes from fragment " + fragment.getText());
            }


        }

        // Finally clear the cache for the Fragment for Document call

        try{

            ServiceCache cache = new ServiceCache(FragmentServlet.DataServletName);
            cache.remove(fromVersion.getDocumentId().toString());

        }catch(BackOfficeException e){

            e.logError("Failed Updating cache ");
        }

    }

    /**************************************************************************
     *
     *          Can we find the annotation in the fragment
     *
     *          (avoiding duplicates in the cloning)
     *
     *
     * @param annotation               - the annotation
     * @param targetFragment           - the actual target fragment
     * @return
     */


    private boolean alreadyExists(ContractAnnotation annotation, ContractFragment targetFragment) {

        FragmentComparator comparator = new FragmentComparator();

        List<ContractAnnotation> annotationsForFragment = targetFragment.getAnnotationsForFragment();

        System.out.println(" --- Checking if annotation " + annotation.getDescription()+ " already exists");

        for (ContractAnnotation existingAnnotation : annotationsForFragment) {

            if(     existingAnnotation.getCreatorId().equals(annotation.getCreatorId()) &&
                    comparator.isSame(existingAnnotation.getDescription(), annotation.getDescription())){

                return true;
            }
        }

        System.out.println("       --- Not found!");

        return false;

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
