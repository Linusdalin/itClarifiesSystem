package backend;

import contractManagement.ContractFragment;
import log.PukkaLogger;
import pukkaBO.GenericPage.Lightbox;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import pukkaBO.style.Html;
import userManagement.OrganizationTable;

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


public class CreateReferenceLightbox extends Lightbox {

    public CreateReferenceLightbox(){

        // Create the lightbox

        super(  "testLightbox",
                Lightbox.BIG,
                "This is an example lightbox page");


    }

    @Override
    public String render(int activeTab, String callbackMessage, BackOfficeInterface backOffice, HttpServletRequest request) {
        StringBuffer html = new StringBuffer();

        String fromKey = request.getParameter("fragment");

        html.append(new ReferenceForm(backOffice, fromKey).renderForm());

        return html.toString();
    }


    public String getLink(ContractFragment fragment){

        return getLink(getLink() + "&fragment=" + fragment.getKey().toString(), "Ref", Lightbox.NORMAL);
    }


}


/**
 *
 *
 *      Form for creating a new user
 *
 *      It is located in the new User list
 */


class ReferenceForm extends Form implements FormInterface {

    public ReferenceForm(BackOfficeInterface bo, String from){

        this.name = "referenceForm";
        title = "Create Reference";

        try {


            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();


            elements.add(new TextField("From")
                    .setSize(0, 30, 20)
                    .withPlaceholder("user name")
                    .withValue(from)
                    .withTooltip("Fragment to reference from")
            );


            elements.add(new TextField("To")
                    .setSize(0, 50, 20)
                    .withPlaceholder("fragment key...")
                    .withTooltip("Copy paste fragment key to here")
            );

            elements.add(new Button("Submit", FormPlacement.NEW_LINE, false));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "Documents", ""));


            setActionURL("?page=", "Create", true);

        } catch (Exception e) {

            PukkaLogger.log( e );
        }
    }
}
