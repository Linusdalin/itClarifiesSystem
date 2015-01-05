package backend;

import javafx.scene.control.PasswordField;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
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


public class NewUserForm extends Form implements FormInterface {

    public NewUserForm(BackOfficeInterface bo, String section, String list){

        this.name = "newUser";
        title = "Create New User";

        try {

            new OrganizationTable().getDropDown(null);


            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();


            elements.add(new TextField("Name")
                    .setSize(0, 30, 20)
                    .withPlaceholder("user name")
                    .withTooltip("User name for display")
            );


            elements.add(new TextField("Email")
                    .setSize(0, 50, 20)
                    .withPlaceholder("user name")
                    .withTooltip("User name for display")
            );

            elements.add(new Checkbox("wsAdmin", false)
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


            /*


            elements.add(new PwdField("Password", "Password",
                    "", "password", 0, 20, 20));
            elements.add(new PwdField("Confirm Password", "Confirm",
                    "", "confirm password", 0, 20, 20));

            */
            elements.add(new TableField("Organization", "Organization", new OrganizationTable(), null));

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
