package reclassification;

import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.backOffice.BackOfficeLocation;
import pukkaBO.dropdown.DropDownInterface;
import pukkaBO.form.DropDownList;

/********************************************************************************
 *
 *          Storing manual classifications, references, risks and annotations
 *
 *          that then can be imported to another project
 *
 */

public class MTDocument implements MechanicalTurkInterface{

    private String name;
    private MTClassification[] classifications;

    public MTDocument(String name, MTClassification[] classifications){

        this.name = name;
        this.classifications = classifications;
    }

    public String getName(){

        return name;
    }

    public MTClassification[] getMTClassifications() {
        return this.classifications;
    }

    public String toString(){

        return name + " ("+ classifications.length+" classifications)";
    }

    public String getForm(DropDownInterface projectDropdown, BackOfficeLocation backOfficeLocation){

        StringBuffer html = new StringBuffer();

        html.append("<form method=POST action=\"?page=" + backOfficeLocation.page + "&section="+ backOfficeLocation.section+"&mt="+ name +"\">");

        html.append("<tr>");
        html.append("<td width=\"300px\">");
        html.append(toString());
        html.append("</td>");
        html.append("<td width=\"150px\">");
        html.append(projectDropdown.render());
        html.append("</td>");
        html.append("<td>");
        html.append("<input style=\"\" name=\"Submit\" value=\"Submit\" class=\"btn primary\"  type=\"submit\">");
        html.append("</td>");
        html.append("</tr>");
        html.append("</form>");

        return html.toString();

    }


}
