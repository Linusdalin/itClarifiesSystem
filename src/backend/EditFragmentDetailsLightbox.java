package backend;

import classification.FragmentClassification;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import project.Project;
import crossReference.Definition;
import crossReference.Reference;
import crossReference.ReferenceType;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DatabaseAbstractionFactory;
import document.DefinitionType;
import featureTypes.FeatureTypeTree;
import log.PukkaLogger;
import pukkaBO.GenericPage.Lightbox;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import pukkaBO.style.Html;
import reclassification.Reclassification;
import reclassification.Redefinition;
import reclassification.Rereference;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *      This is a generic lightbox page body component
 *      As opposed to other pages, there is no need to set the section
 *      as there is no menu.
 *
 */


public class EditFragmentDetailsLightbox extends Lightbox {

    public EditFragmentDetailsLightbox(){

        // Create the lightbox

        super(  "editFragmentLightbox",
                Lightbox.BIG,
                "Edit fragment");


    }

    @Override
    public String render(String callbackMessage, BackOfficeInterface backOffice, HttpServletRequest request) {

        StringBuffer html = new StringBuffer();

        String fromKey = request.getParameter("fragment");
        String formAction = request.getParameter("formAction");
        String add = request.getParameter("add");

        if(add != null && add.equals("definition")){

            html.append("<h1> New Definition </h1>");
            html.append(new AddDefinitionForm(backOffice, fromKey).renderForm());

        }
        if(add != null && add.equals("reference")){

            html.append("<h1> New Reference </h1>");
            html.append(new AddReferenceForm(backOffice, fromKey).renderForm());

        }
        else if(formAction == null){

            html.append("<a href=\"?add=definition&page="+getName()+ "&fragment="+ fromKey +"\">new Definition</a>");
            html.append(" <a href=\"?add=reference&page="+getName()+ "&fragment="+ fromKey +"\">new Reference</a>");
            html.append(new EditFragmentDetailsForm(backOffice, fromKey).renderForm());
        }
        else{

            html.append("<h1> Updated! </h1>");
            html.append(callbackMessage);

            html.append(Html.button("Close", "parent.window.location.reload(true)"));

        }

        return html.toString();
    }


    public String getLink(ContractFragment fragment){

        if(fragment == null)
            return "#";

        return getLink(getLink() + "&fragment=" + fragment.getKey().toString(), "Edit", Lightbox.NORMAL);
    }


}


/*****************************************************************'
 *
 *
 *      Form for editing the details
 *
 */


class EditFragmentDetailsForm extends Form implements FormInterface {

    private ContractFragment fragment = null;


    public EditFragmentDetailsForm(BackOfficeInterface bo, String fragmentKey){

        this.name = "editFragmentForm";
        title = "Reference and Definitions";

        try {

            List<Reference> referencesFromFragment;
            List<Definition> definitionsForFragment;

            if(fragmentKey != null && !fragmentKey.equals("")){

                fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));
                referencesFromFragment = fragment.getReferencesForFragment();
                definitionsForFragment = fragment.getDefinitionsForFragment();

            }
            else{

                referencesFromFragment = new ArrayList<Reference>();
                definitionsForFragment = new ArrayList<Definition>();

            }

            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();
            int count = 0;

            for (Reference reference : referencesFromFragment) {

                FormFieldInterface field =
                        new TextField(reference.getPattern())
                                        .setSize(0, 100, 30)
                                        .withPlaceholder("to fragment")
                                        .withValue(reference.getTo().getKey().toString())
                                        .withTooltip("Fragment reference TO");

                // Potentially add a "to fragment"-key if it exists

                if(reference.getTo().exists())
                    field.withValue(reference.getTo().getKey().toString());


                elements.add(field);
                count++;

            }

            for (Definition definition : definitionsForFragment) {

                elements.add(new Checkbox(definition.getName(), true).withFieldName("_" + definition.getName()));
                count++;
            }

            if(count == 0){

                elements.add(new DescriptionText("No elements to update."));
                // Add a message here when it is empty
            }

            //TODO: Improvement Usability (bo): make this hidden

            elements.add(new TextField("fragment")
                    .setSize(0, 100, 30)
                    .withValue(fragmentKey)
            );


            elements.add(new Button("Update", FormPlacement.NEW_LINE, false));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "Documents", ""));


            setActionURL("?page=editFragmentLightbox", "Create", true);

        } catch (Exception e) {

            PukkaLogger.log( e );
        }
    }

    /***********************************************************************
     *
     *          Callback for the edit details form
     *
     *
     * @param request
     * @param backOffice
     * @return
     * @throws BackOfficeException
     */

    public String submitCallBack(HttpServletRequest request, BackOfficeInterface backOffice) throws BackOfficeException {

        StringBuffer message = new StringBuffer();

        System.out.println(" *** Edit fragment callback!");

        try {

            String fragmentKey = request.getParameter("fragment");
            if(fragmentKey == null){

                System.out.println("No fragment");
                message.append(Html.paragraph("No fragment given. No updates."));

            }

            fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));

            if(!fragment.exists()){

                System.out.println("No fragment found");
                message.append(Html.paragraph("No fragment found for key " + fragmentKey));

            }
            else{

                List<Reference> referencesFromFragment = fragment.getReferencesForFragment();
                List<Definition> definitionsForFragment = fragment.getDefinitionsForFragment();

                int count = 0;

                System.out.println("Looking for: " +  referencesFromFragment.size() + " references and " + definitionsForFragment.size() + " definitions");

                for (Reference reference : referencesFromFragment) {

                    // TODO: Not Implemented: (bo) set toFragment for reference here

                    message.append(Html.paragraph("Update of reference " + reference.getName() + " NOT implemented!"));

                }

                for (Definition definition : definitionsForFragment) {

                    String checkbox = request.getParameter("_"+definition.getName());
                    System.out.println("Found " + checkbox + " for definition " + definition.getName());

                    if(checkbox == null){

                        int removed  = definition.deleteReferencesForDefinition();
                        definition.delete();

                        message.append(Html.paragraph("Removed definition with " + removed + " references."));
                        count++;

                    }

                }

                if(count == 0){

                    System.out.println("No updates performed");
                    message.append(Html.paragraph("No updates performed"));

                }

            }

        } catch (Exception e) {

            PukkaLogger.log( e );
            return "Internal error when setting the fragment details";
        }


        return message.toString();

     }

}



/*****************************************************************'
 *
 *
 *      Form for editing the details
 *
 */


class AddDefinitionForm extends Form implements FormInterface {

    private ContractFragment fragment = null;


    public AddDefinitionForm(BackOfficeInterface bo, String fragmentKey){

        this.name = "addDefinitionForm";
        title = "Add Definition for Fragment";

        try{

            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            if(fragmentKey != null && !fragmentKey.equals("")){

                fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));

                elements.add(new DescriptionText(fragment.getText() + "\n\n"));
            }


            //TODO: Improvment Usability: (bo) make this hidden

            elements.add(new TextField("fragment")
                    .setSize(0, 30, 20)
                    .withValue(fragmentKey)
            );

            elements.add(new TextField("pattern")
                    .setSize(0, 30, 20)
                    .withPlaceholder("Definition...")
            );


            elements.add(new Button("Create", FormPlacement.NEW_LINE, false));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "Documents", ""));


            setActionURL("?page=editFragmentLightbox", "Create", true);

        } catch (Exception e) {

            PukkaLogger.log( e );
        }
    }

    /**************************************************************************'
     *
     *          Callback for creating a definition
     *
     *
     * @param request           -
     * @param backOffice        -
     * @return                  -
     * @throws BackOfficeException
     *
     *          //TODO: Improvement Functionality: Definition Type not implemented
     *
     */

    public String submitCallBack(HttpServletRequest request, BackOfficeInterface backOffice) throws BackOfficeException {

        StringBuffer message = new StringBuffer();

        System.out.println(" *** New Definition callback!");

        try {

            String fragmentKey = request.getParameter("fragment");
            if(fragmentKey == null){

                System.out.println("No fragment");
                message.append(Html.paragraph("No fragment given. Can't create definition."));

            }

            String pattern = request.getParameter("pattern");
            if(pattern == null){

                System.out.println("No pattern given");
                message.append(Html.paragraph("No pattern given. Can't create definition."));

            }
            String definitionText = "";   // Creating a separate definition text is not implemented


            fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));
            int patternPos = fragment.getText().indexOf(pattern);

            if(!fragment.exists()){

                System.out.println("No fragment found");
                message.append(Html.paragraph("No fragment found for key " + fragmentKey));

            }
            else if(patternPos < 0){

                System.out.println("Pattern not found");
                message.append(Html.paragraph("Could not find the pattern" + pattern + " in the fragment text \""+ fragment.getText()+"\""));
            }
            else{

                // Create the new definition

                DBTimeStamp now = new DBTimeStamp();

                ContractVersionInstance version = fragment.getVersion();
                Contract document = version.getDocument();
                Project project = document.getProject();
                PortalUser user = PortalUser.getSystemUser();
                String tag = FeatureTypeTree.DefinitionDef.getName();
                String keyWords = FeatureTypeTree.DefinitionDef.getHierarchy();

                Definition definition = new Definition(pattern,
                        DefinitionType.REGULAR.name(),
                        fragment, fragment.getOrdinal(), version, project, definitionText);
                definition.store();

                Redefinition redefinition = new Redefinition(pattern, true, project.getName(), document.getName(),fragment.getOrdinal(), fragment.getText(), false, now.getISODate());
                redefinition.store();

                FragmentClassification definitionSource = new FragmentClassification(fragment, tag, 80, 0, "", "", user, version, project, pattern, patternPos, pattern.length(), 100, keyWords, now.getISODate());
                definitionSource.store();

                Reclassification reclassification = new Reclassification(tag, true, now.getISODate(), project.getName(), document.getName(),fragment.getOrdinal(), fragment.getText(), pattern, patternPos, user.getName(), false);
                reclassification.store();


                message.append(Html.paragraph("Stored a new definition" + pattern));

            }

        } catch (Exception e) {

            PukkaLogger.log( e );
            return "Internal error when setting the fragment details";
        }


        return message.toString();

     }

}

    /*****************************************************************'
     *
     *
     *      Form for editing the details
     *
     */


    class AddReferenceForm extends Form implements FormInterface {

        private ContractFragment fragment = null;


        public AddReferenceForm(BackOfficeInterface bo, String fragmentKey){

            this.name = "addReferenceForm";
            title = "Add Reference for Fragment";

            try{

                List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

                if(fragmentKey != null && !fragmentKey.equals("")){

                    fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));

                    elements.add(new DescriptionText(fragment.getText() + "\n\n"));
                }


                //TODO: Improvement Usability: (bo) make this hidden

                elements.add(new TextField("fragment")
                        .setSize(0, 100, 20)
                        .withValue(fragmentKey)
                );

                elements.add(new TextField("toFragment")
                        .setSize(0, 100, 20)
                        .withPlaceholder("frgment key...")
                );


                elements.add(new TextField("pattern")
                        .setSize(0, 50, 20)
                        .withPlaceholder("Definition...")
                );


                elements.add(new Button("Create", FormPlacement.NEW_LINE, false));

                setElements(elements);
                setRenderer(new StarlightFormRenderer());
                setBackOfficeLocation(new BackOfficeLocation(bo, "Documents", ""));


                setActionURL("?page=editFragmentLightbox", "Create", true);

            } catch (Exception e) {

                PukkaLogger.log( e );
            }
        }

        /**************************************************************************'
         *
         *          Callback for creating a definition
         *
         *
         * @param request
         * @param backOffice
         * @return
         * @throws BackOfficeException
         */

        public String submitCallBack(HttpServletRequest request, BackOfficeInterface backOffice) throws BackOfficeException {

            StringBuffer message = new StringBuffer();

            System.out.println(" *** New Reference callback!");
            ContractFragment destinationFragment = null;
            ReferenceType referenceType = ReferenceType.getExplicit();
            try {

                String fragmentKey = request.getParameter("fragment");
                if(fragmentKey == null){

                    System.out.println("No fragment");
                    message.append(Html.paragraph("No fragment given. Can't create definition."));
                    return message.toString();
                }

                String pattern = request.getParameter("pattern");
                if(pattern == null){

                    System.out.println("No pattern given");
                    message.append(Html.paragraph("No pattern given. Can't create definition."));
                    return message.toString();
                }

                fragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(fragmentKey)));
                int patternPos = fragment.getText().indexOf(pattern);

                String destinationFragmentKey = request.getParameter("toFragment");
                if(destinationFragmentKey == null){

                    System.out.println("No destination fragment");
                    message.append(Html.paragraph("No destination fragment given. Creating an open reference"));
                    destinationFragment = fragment; // Use same key when the type is external
                    referenceType = ReferenceType.getOpen();

                }else{

                    destinationFragment = new ContractFragment(new LookupByKey(new DatabaseAbstractionFactory().createKey(destinationFragmentKey)));

                }

                if(!fragment.exists()){

                    System.out.println("No fragment found");
                    message.append(Html.paragraph("No fragment found for key " + fragmentKey));
                    return message.toString();
                }

                if(patternPos < 0){

                    System.out.println("Pattern not found");
                    message.append(Html.paragraph("Could not find the pattern" + pattern + " in the fragment text \""+ fragment.getText()+"\""));
                    return message.toString();
                }


                // Create the new reference

                DBTimeStamp now = new DBTimeStamp();

                ContractVersionInstance version = fragment.getVersion();
                Contract document = version.getDocument();
                Project project = document.getProject();
                PortalUser user = PortalUser.getSystemUser();
                String tag = FeatureTypeTree.DefinitionDef.getName();
                String keyWords = FeatureTypeTree.DefinitionDef.getHierarchy();

                Reference reference = new Reference(pattern, fragment.getKey(), destinationFragment.getKey(), version.getKey(), project.getKey(), referenceType, pattern, patternPos,  user.getKey());
                reference.store();

                Rereference reReference = new Rereference(pattern, true, project.getName(), document.getName(),fragment.getOrdinal(), fragment.getText(), fragment.getText(), referenceType.getName(), false, now.getISODate());
                reReference.store();

                message.append(Html.paragraph("Stored a new reference for" + pattern + ". (Using default type " + referenceType.getName() + ")"));



            } catch (Exception e) {

                PukkaLogger.log( e );
                return "Internal error when trying to add a reference";
            }


            return message.toString();

         }



}
