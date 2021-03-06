package backend;

import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import databaseLayer.DatabaseAbstractionFactory;
import httpRequest.ServerFactory;
import httpRequest.RequestHandler;
import log.PukkaLogger;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.LookupByKey;

import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.FormInterface;
import pukkaBO.list.*;
import pukkaBO.renderer.GroupListRenderer;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.style.Html;
import userManagement.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/***************************************************************
 *
 *
 *          User list
 *
 *
 */


public class UserList extends GroupByList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "UserList";
    public static final String Title = "All Users";
    public static final String Description = "All Users in the system grouped by organization.";
    public static final int GroupColumn = 6; // Group by Organization

    // ids for the callback actions

    public static final int Callback_Action_Do      = 1;
    public static final int Callback_Action_Delete  = 2;
    public static final int Callback_Action_View    = 3;
    public static final int Callback_Action_Add     = 4;

    public static final ListRendererInterface Renderer = new GroupListRenderer();

    private static final DataTableInterface table = new PortalUserTable();


    public UserList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn());
            add(new ListTableColumn( 2, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 3, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.WIDE)));
            add(new ListTableColumn( 4, table ).withNameFromTableColumn());
            add(new ListTableColumn( 5, table ).withNameFromTableColumn());
            add(new ListTableColumn( 6, table ).withNameFromTableColumn());
            add(new ListTableColumn( 7, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));
            add(new ListTableColumn( 8, table ).withNameFromTableColumn().withFormat(new DisplayFormat(DisplayFormat.SMALL)));

        }};



        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable(table, columnStructure);

        setGroupColumn(GroupColumn);


        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        actions.add(new ListAction(Callback_Action_Delete,    ActionType.List, "Delete").setIcon(Icon.Trash));
        actions.add(new ListAction(Callback_Action_View,      ActionType.Item, "Details").setIcon(Icon.Pencil));

        setAddAction(new ListAction(Callback_Action_Add, ActionType.Add, "Create User"));

        // Set the number of elements to display
        setDisplaySize(20);
    }



    /*************************************************************************'
     *
     *
     *          getHighlight - this is the logic for the list defining how the
     *          item (row) shall be displayed.
     *
     *          The logic is normally defined by the content in the table.
     *
     * @param object - The object on which the selection is done
     * @return - the enum defining the class
     *
     */

    @Override
    public DisplayHighlight getHighlight(DataObjectInterface object){


        return DisplayHighlight.FYI;
    }



    /********************************************************************
     *
     *          Callback implementation.
     *
     *
     *
     *
     * @param action - the callback action as defined by the actions
     * @param key - the object on the selected row
     *
     * @return - text to be displayed. The interpretation will be different depending on the action type.
     */

    @Override
    public String callBack(int action, DBKeyInterface key, String section, HttpServletRequest request){

        PortalUser user = new PortalUser(new LookupByKey(key));

        try{

            switch(action){


                case Callback_Action_Do:


                    return "Error: Not implemented DO";

                case Callback_Action_Delete:

                    user.delete();

                    return "Success: Deleted user " + user.getName();

                case Callback_Action_View:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return detailedView(user);
            }

        }catch(Exception e){

            // Handle exception here
        }

        return "Error: No action performed...";

    }

    private String detailedView(PortalUser user) {

        StringBuffer page = new StringBuffer();

        try {

            page.append(Html.heading(1, "Details for User " + user.getName()));
            FormInterface userDetailForm = new EditUserForm(backOffice, "", Name, user );

            page.append(userDetailForm.renderForm());

        } catch (BackOfficeException e) {
            page.append(Html.paragraph("Could not render user detail form..."));
        }

        return page.toString();
    }


    @Override
    public boolean hasAction(int action, DataObjectInterface object){


        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{

        DataTableInterface table = new PortalUserTable();
        //String html = new TableEditForm(table, null, TableEditForm.FormType.ADD, backOffice, section, "&list=" + Name).renderForm("", 0);

        String html = new NewUserForm(backOffice, section, Name).renderForm();


        return html;
    }

    /*************************************************************************************************
     *
     *          Submit can receive values from either the new user or the edit user
     *
     * @param request
     * @return
     */

    @Override
    public String submit(HttpServletRequest request){

        try{

            // Get the parameters

            String userKey              = request.getParameter("Key");
            String name                 = request.getParameter("Name");
            String email                = request.getParameter("Email");
            boolean wsAdmin             = request.getParameter("wsAdmin") != null;
            boolean active              = request.getParameter("active") != null;
            String password             = request.getParameter("Password");
            String confirmPassword      = request.getParameter("Confirm");
            String organizationKey      = request.getParameter("Organization");
            DBTimeStamp timeStamp       = new DBTimeStamp();

            UserManager usermanager = new UserManager();

            if(userKey == null){

                // If there is no key passed, we will create a new user


                // First check mandatory data

                DBKeyInterface _organization = new DatabaseAbstractionFactory().createKey(organizationKey);


                Organization organization = new Organization(new LookupByKey(_organization));
                if(!organization.exists()){

                    return("Error: No Organization with key " + organizationKey + " exists. Cannot create/update user");

                }


                if(!password.equals(confirmPassword)){

                    return("Error: Passwords does not match \"" + password + "\" \"" + confirmPassword + "\"");

                }

                boolean isActive = true; //For users created in the back office we do not need an activation

                // Create a user in the Login service
                PortalUser user = usermanager.createUser(name, password, email, organization, wsAdmin, isActive);
                return("Success: Created user " + user.getName());

            }
            else{

                // This is an update of an existing user.

                DBKeyInterface _user         = new DatabaseAbstractionFactory().createKey(userKey);
                PortalUser user = new PortalUser(new LookupByKey(_user));

                if(!user.exists()){

                    return("Error: No User with key " + userKey + " exists. Cannot update user");

                }


                if(password != null && !password.equals("")){

                    // If there is a password given in the update. It has to match the verify password field

                    if(!password.equals(confirmPassword)){

                        return("Error: Passwords does not match \"" + password + "\" \"" + confirmPassword + "\"");

                    }

                    usermanager.updateUser(user, name, email, password, wsAdmin, active);

                }
                else{

                    if(!user.getName().equals(name))
                        return("Error: cant update name without a password.");
                }


                return ("Success:Updated User \""+user.getName()+"\"");

            }


        }
        catch(BackOfficeException e){

            PukkaLogger.log( e );
            return "Could not add user (" + e.narration + ")";

        }
        catch(JSONException e){

            PukkaLogger.log( e );
        }

        return "Error: Could not create Item...";
    }








}
