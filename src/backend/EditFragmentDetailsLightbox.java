package backend;

import classification.FragmentClassification;
import contractManagement.Contract;
import contractManagement.ContractFragment;
import contractManagement.ContractVersionInstance;
import contractManagement.Project;
import crossReference.Definition;
import crossReference.Reference;
import dataRepresentation.DBTimeStamp;
import databaseLayer.DatabaseAbstractionFactory;
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
        else if(formAction == null){

            html.append("<a href=\"?add=definition&page="+getName()+ "&fragment="+ fromKey +"\">new Definition</a>");
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

                elements.add(new TextField(reference.getPattern())
                        .setSize(0, 30, 20)
                        .withPlaceholder("user name")
                        .withValue(reference.getTo().getKey().toString())
                        .withTooltip("Fragment reference TO")
                );
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

            //TODO: make this hidden

            elements.add(new TextField("fragment")
                    .setSize(0, 30, 20)
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


            //TODO: make this hidden

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
     * @param request
     * @param backOffice
     * @return
     * @throws BackOfficeException
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
            if(fragmentKey == null){

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

                Definition definition = new Definition(pattern, fragment, fragment.getOrdinal(), version, project, definitionText);
                definition.store();

                Redefinition redefinition = new Redefinition(pattern, true, project.getName(), document.getName(),fragment.getOrdinal(), fragment.getText(), false);
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
