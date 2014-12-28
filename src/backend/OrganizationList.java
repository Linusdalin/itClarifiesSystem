package backend;

import risk.ContractRisk;
import dataRepresentation.DBTimeStamp;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import dataRepresentation.DisplayFormat;
import databaseLayer.DBKeyInterface;
import httpRequest.ServerFactory;
import httpRequest.RequestHandler;
import log.PukkaLogger;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.Icon;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupByKey;
import pukkaBO.condition.LookupItem;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.formsPredefined.TableEditForm;
import pukkaBO.list.*;
import pukkaBO.renderer.ListRendererInterface;
import pukkaBO.renderer.ListRendererJSStatic;
import userManagement.Organization;
import userManagement.OrganizationConf;
import userManagement.OrganizationTable;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *      An example list. It shows objects of the type Example1
 *      and allows for actions on them.
 *
 *      There are four action callbacks for this list example:
 *
 *       - Do           - Typically used to perform a main action on the item
 *       - Undo         - Typically reversing the Execute()
 *       - View         - opening a separate view for this specific item
 *       - Add          - Allowing to create a new one
 *
 *
 *       The actions operate on the data in the associated table. In this
 *       example, it uses a field called state.
 *
 *       There list also uses DisplayHighlight to define the style of each item, The
 *       different styles are:
 *
 *          RequireAction,          // Strong highlight, requires action
 *          FYI,                    // Normal
 *          Done,                   // Grey, already taken care of
 *          PositiveAlert,          // Green bold
 *          Alert,                  // Red bold
 *
 */


public class OrganizationList extends SimpleList implements ListInterface{

    // General definitions for the example list

    public static final String Name = "OrganizationList";
    public static final String Title = "All Organizations";
    public static final String Description = "A list of all the active organizations";


    // Renderer. It is possible to inject another renderer implementation here

    public static final ListRendererInterface Renderer = new ListRendererJSStatic();

    private static final DataTableInterface table = new OrganizationTable();

    // ids for the callback actions

    public static final int Callback_Action_Check   = 1;
    public static final int Callback_Action_Delete  = 2;
    public static final int Callback_Action_View    = 3;
    public static final int Callback_Action_Add     = 4;

    public OrganizationList(BackOfficeInterface backOffice){

        List<ListColumnInterface> columnStructure = new ArrayList<ListColumnInterface>() {{

            add(new ListTableColumn( 1, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 2, table ).withNameFromTableColumn( ));
            add(new ListTableColumn( 3, table ).withNameFromTableColumn( ).withFormat(new DisplayFormat(DisplayFormat.EXTRA_WIDE)));

        }};



        init(Name, Title, Description, backOffice, Renderer);

        // Set the table to draw the data from.

        initiateTable( table, columnStructure);

        // Add actions to each element. In this example we have multiple actions.
        // They may however not all be present in all states.

        actions.add(new ListAction(Callback_Action_Delete,    ActionType.List, "Delete").setIcon(Icon.Trash));

        // Add button on page

        setAddAction(new ListAction(Callback_Action_Add, ActionType.Add, "Create Organization"));

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


        try{

            Organization organization = new Organization(new LookupByKey(key));

            switch(action){


                case Callback_Action_Check:

                    return "Error:Not impemented Check";

                case Callback_Action_Delete:

                    String name = organization.getName();
                    organization.delete();

                    return "Success:Deleted Organization " + name;

                case Callback_Action_View:

                    // This is a view type, so the return value will be the content of an html page.
                    // Returning null here would be an error

                    return "This is a detailed view on a separate page with  " + organization.getName() + " No info stored";
            }

        }catch(Exception e){

            // Handle exception here
        }

        return "Error:No action performed...";

    }


    @Override
    public boolean hasAction(int action, DataObjectInterface object){

        // No restrictions on the actions

        return true;

    }


    @Override
    public String addForm(String section)throws BackOfficeException{

        DataTableInterface table = new OrganizationTable();
        //String htmlOld = new TableEditForm(table, null, TableEditForm.FormType.ADD, backOffice, section, "&list=" + Name).renderForm("", 0);

        String html = new NewOrganizationForm(backOffice, section, Name).renderForm();

        return html;
    }

    /********************************************************************************'
     *
     *          Submit is the result of the add form input
     *
     *
     *
     * @param request
     * @return
     */


    @Override
    public String submit(HttpServletRequest request){

        try{

            // Get the parameters

            String name = request.getParameter("Name");
            String description = request.getParameter("Description");
            DBTimeStamp timeStamp = new DBTimeStamp();
            ContractRisk defaultRisk = ContractRisk.getUnknown();

            // Organization names have to be unique

            Organization existingOrganization = new Organization(new LookupItem().addFilter(new ColumnFilter(OrganizationTable.Columns.Name.name(), name)));
            if(existingOrganization.exists()){

                return("Error: Organization with name " + name + " already exists");

            }


            // Create an organization in the Login service

            String loginServer = ServerFactory.getLoginServer();
            String thisServer = ServerFactory.getLocalSystem();

            RequestHandler requestHandler = new RequestHandler(loginServer + "/Organization");
            String output = requestHandler.excutePost("name=" + name + "&description=" + description + "&link="+ thisServer);

            PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from login server" + output);

            JSONObject response = new JSONObject(output);

            if(response.has("error")){

                String errorMessage = ((JSONObject) response.getJSONArray("error").get(0)).getString("message");
                return("Error: Got error from login service " + errorMessage);
            }

            String token = response.getString("token");


            // Create a new Organization config

            OrganizationConf newConfig = new OrganizationConf(name);
            newConfig.store();

            Organization newOrganization = new Organization(name, timeStamp.getISODate(), description, token, newConfig.getKey());
            newOrganization.store();

            // Create default users for the organization

            PortalUser system = new PortalUser("itClarifies",   0, PortalUser.Type.SYSTEM.name(),   "no email", timeStamp.getISODate(), newOrganization.getKey(), true, false);
            system.store();
            PortalUser empty = new PortalUser("<< not set >>",  0, PortalUser.Type.EMPTY.name(),    "no email", timeStamp.getISODate(), newOrganization.getKey(), true, false);
            empty.store();
            PortalUser external = new PortalUser("External",    0, PortalUser.Type.EXTERNAL.name(), "no email", timeStamp.getISODate(), newOrganization.getKey(), true, false);
            external.store();



            return ("Success:A new Organization \""+newOrganization.getName()+"\"with id " + newOrganization.getKey() + " was created");

        }
        catch(BackOfficeException e){

            System.out.println("Error: Error adding element " + e.narration);
            e.printStackTrace(System.out);
        }
        catch(JSONException e){

            System.out.println("Error: Error parsing result from login server : ");
            e.printStackTrace(System.out);
        }

        return "Error: Could not create Item...";
    }






}
