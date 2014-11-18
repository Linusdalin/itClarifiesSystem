package backend;

import javafx.scene.control.PasswordField;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import userManagement.OrganizationTable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *      Form for creating a new user
 *
 *      It is located in the new User list
 */


public class NewUserForm extends Form implements FormInterface, FormInternalInterface {

    public NewUserForm(BackOfficeInterface bo, String section, String list){

        this.name = "newUser";
        title = "Create New User";

        try {

            new OrganizationTable().getDropDown();


            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            elements.add(new TextField("Name")
                    .setSize(0, 20, 20)
                    .withPlaceholder("user name")
                    .withTooltip("User name for display")
            );


            //elements.add(new TextField("Name", "Name",
            //        "", "user name", "The name of the organization", 0, 20, 20));
            elements.add(new PwdField("Password", "Password",
                    "", "password", 0, 20, 20));
            elements.add(new PwdField("Confirm Password", "Confirm",
                    "", "confirm password", 0, 20, 20));

            elements.add(new TableField("Organization", "Organization", new OrganizationTable(), null));


            elements.add(new Button("Submit", FormPlacement.NEW_LINE, false));
            super.init(elements, FormInterface.STARLIGHT, null, bo, 0);
            setActionURL("?list="+list+"&section="+section, "Create", true);

        } catch (BackOfficeException e) {

            e.logError("Error creating form");
        }
    }
}
