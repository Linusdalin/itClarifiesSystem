package reclassification;

import backend.ReclassificationImportPage;
import log.PukkaLogger;
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
 *
 */


public class ImportForm extends Form implements FormInterface {

    public ImportForm(BackOfficeInterface bo, String name){

        this.name = "importReclassification";
        title = "Import";

        try {


            List<FormFieldInterface> elements = new ArrayList<FormFieldInterface>();

            elements.add(new Button("Submit", FormPlacement.LEFT, false)
                    .withFieldName("Text")
                    .withTooltip("Tooltip"));

            setElements(elements);
            setRenderer(new StarlightFormRenderer());
            setBackOfficeLocation(new BackOfficeLocation(bo, "User", ReclassificationImportPage.name));
            setInline(true);

            setActionURL("?reclassification=" + name, "Import", true);

        } catch (Exception e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Error creating form");
        }
    }
}
