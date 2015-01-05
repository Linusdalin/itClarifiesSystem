package backend;

import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import userManagement.OrganizationTable;
import userManagement.PortalUser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *      Form for creating a new user
 *
 *      It is located in the new User list
 */


public class EditUserForm extends Form implements FormInterface {

    public EditUserForm(BackOfficeInterface bo, String section, String list, PortalUser user){

        this.name = "updateUser";
        title = "Update User";

        try {

            new OrganizationTable().getDropDown(null);

            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            elements.add(new HiddenField("Key")
                    .setSize(0, 100, 20)
                    .withValue(user.getKey().toString())
            );


            elements.add(new TextField("Name")
                    .setSize(0, 30, 20)
                    .withTooltip("User name for display")
                    .withValue(user.getName())
            );

            elements.add(new TextField("Email")
                    .setSize(0, 50, 20)
                    .withPlaceholder("user name")
                    .withTooltip("User name for display")
                    .withValue(user.getEmail())
            );

            System.out.println("**** Checkbox admin " + user.getWSAdmin());

            elements.add(new Checkbox("wsAdmin", user.getWSAdmin())
                    .withTooltip("User name for display")


            );
            System.out.println("**** Checkbox active " + user.getActive());

            elements.add(new Checkbox("active", user.getActive())
                    .withTooltip("User name for display")

            );


            elements.add(new PwdField("Password")
                   .setSize(0, 30, 20)
                   .withPlaceholder("change password")
                   .withFieldName("Password"));

            elements.add(new PwdField("Password")
                    .setSize(0, 30, 20)
                    .withPlaceholder("confirm new password")
                    .withFieldName("Confirm"));

            elements.add(new Button("Submit", FormPlacement.NEW_LINE, false));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "User", ""));


            setActionURL("?list="+list, "Create", true);

        } catch (BackOfficeException e) {

            e.logError("Error creating form");
        }
    }
}
