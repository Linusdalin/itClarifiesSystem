package backend;

import contractManagement.ContractFragment;
import crossReference.Definition;
import crossReference.Reference;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import pukkaBO.GenericPage.Lightbox;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import pukkaBO.style.Html;

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
    public String render(int activeTab, String callbackMessage, BackOfficeInterface backOffice, HttpServletRequest request) {

        StringBuffer html = new StringBuffer();

        String fromKey = request.getParameter("fragment");
        String formAction = request.getParameter("formAction");

        if(formAction == null)

            html.append(new EditFragmentDetailsForm(backOffice, fromKey).renderForm());

        else{

            html.append("<h1> Updated! </h1>");
            html.append(callbackMessage);

            html.append(Html.button("Close", "parent.window.location.reload(true)"));

        }

        return html.toString();
    }


    public String getLink(ContractFragment fragment){

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
