package versioning;

import classification.FragmentClassification;
import contractManagement.*;
import crossReference.Definition;
import crossReference.Reference;
import dataRepresentation.DBTimeStamp;
import fileHandling.RepositoryFileHandler;
import project.Project;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import userManagement.PortalUser;

import java.util.List;

/**
 *          Freeze Snapshot is the module class for creating snapshots
 *
 *          A freeze snapshot is conducted in the following steps:
 *
 *           - A snapshot is created defining the timestamp
 *           - A new head version is created directly after that time for all documents
 *           - All fragments and attributes are cloned to the new head
 *           - The old head version is renamed to reflect the freeze snapshot
 *
 *
 */


public class FreezeSnapshot {


    private final String name;
    private final Project project;
    private final String description;
    private final PortalUser user;

    public FreezeSnapshot(String name, Project project, String description, PortalUser user){


        this.name = name;
        this.project = project;
        this.description = description;
        this.user = user;
    }

    /****************************************************************************
     *
     *      Freeze creates a snapshot and a new head versioin of all the
     *
     *
     *       - fragments
     *       - Annotations
     *       - Risk
     *       - Classifications
     *       - Reference
     *
     *       The actual snapshot object is created before the cloning to ensure that the new objects belong
     *       to the new head
     *
     *
     * @return - the snapshot object in the database
     * @throws BackOfficeException
     *
     *
     *          //TODO: This should really be a transaction
     */

    public Snapshot freeze() throws BackOfficeException{

        DBTimeStamp freezeTime = new DBTimeStamp();

        List<Contract> documents = project.getContractsForProject();

        Snapshot snapshot = new Snapshot(name, project.getKey(), description, user.getKey(), freezeTime.getSQLTime().toString());
        snapshot.store();

        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {

            throw new BackOfficeException(BackOfficeException.General, "Fail to wait for snapshot to be saved");
        }


        for(Contract document : documents){

            ContractVersionInstance oldHeadVersion =  document.getHeadVersion();

            // Create a new version for the document that is used for the
            // TODO: This should be a minor version update. Implement minor versions

            ContractVersionInstance newVersion = document.addNewVersion(user, new RepositoryFileHandler(document.getFile()), oldHeadVersion.getFingerprint());

            // Clone all data for the document

            cloneFragments(oldHeadVersion, newVersion);

            // Now go through all References and repoint the "to"-clauses

            repointReferences( newVersion );

            // Rename the old head

            oldHeadVersion.setVersion(oldHeadVersion.getVersion() + " (frozen at " + freezeTime.getISODate() + ")");
            oldHeadVersion.update();

        }




        return snapshot;

    }

    /*****************************************************************************************
     *
     *
     *              At this point the reference in the new version still points to clauses
     *              in the old version. We look them up by using the ordinal number in the
     *              referenced clauses in the old document version
     *
     *              //TODO: Batch save here
     *
     * @param newVersion
     * @throws BackOfficeException
     */

    private void repointReferences(ContractVersionInstance newVersion) throws BackOfficeException {


        List<Reference> referenceList = newVersion.getReferencesForVersion();

        for(Reference reference : referenceList){

            ContractFragment oldAndWrong = reference.getTo();

            //Lookup the clause in the new document version that has the same ordinal number

            ContractFragment newAndCorrect = new ContractFragment(new LookupItem()
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), newVersion.getKey()))
                    .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), oldAndWrong.getOrdinal())));


            //Update in the database
            reference.setTo(newAndCorrect.getKey());
            reference.update();
        }
    }

    /*******************************************************************************'
     *
     *
     * @param from
     * @param to
     * @throws BackOfficeException
     *
     *          NOTE: This uses store to create new objects. If update and store are merged, this will have to change
     *
     *          NOTE: This does not handle the TO pointers in the reference. These can only be repointed
     *                AFTER the entire project is cloned
     *
     *          TODO: Use batch store here
     *          TODO: Create a result data type here with the number of cloned objects and pass back
     */

    private void cloneFragments(ContractVersionInstance from, ContractVersionInstance to) throws BackOfficeException {


        List<ContractFragment> fragments = from.getFragmentsForVersion();

        // Loop over the fragments to set a new version

        for(ContractFragment fragment : fragments){

            List<ContractAnnotation> annotationList = fragment.getAnnotationsForFragment();
            List<FragmentClassification> classificationList = fragment.getClassificationsForFragment();
            List<RiskClassification> riskList = fragment.getRiskClassificationsForFragment();
            List<Definition> definitionList = fragment.getDefinitionsForFragment();
            List<Reference> referenceList = fragment.getReferencesForFragment();

            fragment.setVersion(to.getKey());
            fragment.store();  // This creates a new object and the key point to the new clone

            // Also clone all attributes

            for(ContractAnnotation annotation : annotationList){

                annotation.setFragment(fragment.getKey());
                annotation.store();
            }

            for(FragmentClassification classification : classificationList){

                classification.setFragment(fragment.getKey());
                classification.store();
            }

            for(RiskClassification risk : riskList){

                risk.setFragment(fragment.getKey());
                risk.store();
            }

            for(Reference reference : referenceList){

                reference.setFrom(fragment.getKey());
                reference.store();
            }


            for(Definition definition : definitionList){

                definition.setDefinedIn(fragment.getKey());
                definition.store();
            }


        }


    }
}
