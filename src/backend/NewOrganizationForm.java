package backend;

import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *      Form for creating a new organization
 *
 *      It is located in the new Organization list
 */


public class NewOrganizationForm extends Form implements FormInterface, FormInternalInterface {

    public NewOrganizationForm(BackOfficeInterface bo, String section, String list){

        this.name = "newOrganization";
        title = "Create New Organization";

        try {


            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            elements.add(new TextField("Name", "Name",
                    "", "org name", "The name of the organization", 0, 20, 20));
            elements.add(new TextField("Description", "Description",
                    "", "description", "Longer description", 0, 20, 20));

            elements.add(new Button("Submit", FormPlacement.NEW_LINE, false));
            super.init(elements, FormInterface.STARLIGHT, null, bo, 0);
            setActionURL("?list="+list+"&section="+section, "Create", true);

        } catch (BackOfficeException e) {

            e.logError("Error creating form");
        }
    }
}
