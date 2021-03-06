package backend;

import project.Project;
import databaseLayer.DatabaseAbstractionFactory;
import log.PukkaLogger;
import overviewExport.OverviewGenerator;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.condition.LookupByKey;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.*;
import userManagement.PortalUser;
import userManagement.SessionManagement;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *      Form for configuring the export
 *
 *      It is located in the Project detail page
 */


public class ExportForm extends Form implements FormInterface {

    public ExportForm(BackOfficeInterface bo, Project project, String list){

        this.name = "exportOverview";
        title = "Configure Project Export";

        String projectName = "";

        if(project != null && project.exists())
            projectName = project.getKey().toString();

        try {

            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            elements.add(new HiddenField("MagicKey")
                    .setSize(0, 100, 20)
                    .withValue(SessionManagement.MagicKey)
            );


            elements.add(new HiddenField("Project")
                    .setSize(0, 100, 20)
                    .withValue(projectName)
            );

            elements.add(new TextField("Tags")
                    .setSize(0, 100, 20)
                    .withFieldName("tags")
                    .withPlaceholder("[#TAG1, #TAG2]")
            );


            elements.add(new Button("Generate", FormPlacement.NEW_LINE, false));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "Projects", ""));


            setActionURL("?list="+list + "&action=Item", "Create", true);

        } catch (Exception e) {

            PukkaLogger.log( e );

        }
    }

    /*****************************************************************************
     *
     *
     *          Generate the values for the overview export
     *
     * @param request
     * @param backOffice
     * @return
     * @throws BackOfficeException
     */

    @Override
    public String submitCallBack(HttpServletRequest request, BackOfficeInterface backOffice) throws BackOfficeException{

        String _projectKey = request.getParameter("Project");

        if(_projectKey == null){

            return "Error: Parameter Project is missing";
        }
        Project project = new Project(new LookupByKey(new DatabaseAbstractionFactory().createKey(_projectKey)));

        if(!project.exists()){

            return "Error: Project " + _projectKey + " does not exist";
        }

        String tagJSON = request.getParameter("tags");
        if(tagJSON == null)
            tagJSON = "[]";

        OverviewGenerator extractor = new OverviewGenerator(project, PortalUser.getSystemUser(), "generated from back office", tagJSON);
        extractor.preCalculate( tagJSON );

        return "Extraction cached in database";
    }
}
