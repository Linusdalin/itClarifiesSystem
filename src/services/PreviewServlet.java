package services;


import actions.Action;
import analysis.DocumentAnalysisException;
import backend.DocumentList;
import backend.ItClarifies;
import com.google.appengine.api.datastore.KeyFactory;
import contractManagement.*;
import crossReference.Definition;
import crossReference.Reference;
import crossReference.ReferenceType;
import databaseLayer.AppEngine.AppEngineKey;
import databaseLayer.DBKeyInterface;
import document.*;
import language.LanguageCode;
import log.PukkaLogger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.POIXMLException;
import pukkaBO.backOffice.BackOfficeInterface;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.style.Html;
import system.Analyser;
import userManagement.PortalUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/***************************************************************************************
 *
 *          Servlet to upload a document of type .docx and preview the outcome
 *
 *          This is mostly an in-house tool for the back office
 *
 */


public class PreviewServlet extends DocumentService{

    public static final String DataServletName = "Preview";

    public static final BackOfficeInterface backOffice = new ItClarifies();

    private static final String[] types = { "HEADING", "TEXT", "LISTITEM"};


    /******************************************************
     *
     *      Uploading a file and parsing the result
     *
     *      See also uploadTest.jsp
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     */



    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {


        int maxFileSize = 5000 * 1024;
        int maxMemSize = 5000 * 1024;

        String text = "";

        try{

            logRequest(req);

            /*
            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;



            setLoggerByParameters(req);

            */

            Formatter formatter = getFormatFromParameters(req);

            String sessionToken;
            PortalUser portalUser = null;


            Formatter jsonExport = new Formatter()
                     .setFormat(Formatter.OutputFormat.HTML);


            // Verify the content type
            String contentType = req.getContentType();

            if((contentType.contains("multipart/form-data"))) {

                DiskFileItemFactory factory = new DiskFileItemFactory();

                // maximum size that will be stored in memory
                factory.setSizeThreshold(maxMemSize);

                // Location to save data that is larger than maxMemSize.
                factory.setRepository(new File("c:\\temp"));


                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                // maximum file size to be uploaded.
                upload.setSizeMax( maxFileSize );

                // Parse the request to get file items.
                List fileItems = upload.parseRequest(req);

                // Process the uploaded file items
                Iterator i = fileItems.iterator();


                while ( i.hasNext () ){

                    FileItem fi = (FileItem)i.next();

                    //TODO: Make these mandatory

                    if ( fi.isFormField () ){

                        /*

                        if(fi.getFieldName().equals("session")){

                            sessionToken = new String(fi.get());
                            if(!sessionManagement.validate(sessionToken)){

                                returnError("No Session", ErrorType.SESSION, HttpServletResponse.SC_FORBIDDEN, resp);
                                resp.flushBuffer();
                                return;
                            }
                            portalUser = sessionManagement.getUser();
                            PukkaLogger.log(PukkaLogger.Level.INFO, "Found user " + portalUser.getName());

                        }

                        */

                        if(fi.getFieldName().equals("project")){

                            String _project = new String(fi.get());
                            Project project = new Project(new LookupByKey( new AppEngineKey(KeyFactory.stringToKey(_project))));

                            if(!project.exists()){

                                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                resp.getWriter().print("Project with id: " + _project + " does not exist");
                                resp.flushBuffer();
                                return;
                            }

                        }

                    }
                     else{
                        // Get the uploaded file parameters

                        String fieldName = fi.getFieldName();
                        String fileName = fi.getName();
                        boolean isInMemory = fi.isInMemory();
                        long sizeInBytes = fi.getSize();



                        InputStream stream = fi.getInputStream();

                        FragmentSplitterInterface splitter = new DocumentManager("test.docx", stream);

                        text += getAnalysisPreview(splitter);


                    }

                 }


                setRespHeaders(jsonExport, resp);
                resp.getWriter().print(text);
                resp.setContentType(jsonExport.getContentType());
                resp.flushBuffer();
                return;


           }else{
               resp.getWriter().print("<html><head><title>Servlet upload</title></head><body><p>No file uploaded</p> </body></html>");

           }

          }catch(POIXMLException ex) {

              resp.getWriter().print("<html><head><title>Servlet upload</title></head><body><p>This was not a docx file!</p> </body></html>");

          }catch(BackOfficeException e) {

                e.logError("In Upload:");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(e.narration);
                resp.flushBuffer();
                return;

          } catch (FileUploadException e) {

                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
          } catch (DocumentAnalysisException e) {
              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
          }
    }


    /*************************************************************************
     *
     *          GET will return a document from the database
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        try {

            logRequest(req);

            if(!validateSession(req, resp))
                return;

            if(blockedSmokey(sessionManagement, resp))
                return;

            setLoggerByParameters(req);


            Formatter formatter = getFormatFromParameters(req);

            Formatter jsonExport = new Formatter()
                     .setFormat(Formatter.OutputFormat.HTML);

            if(!validateSession(req, resp))
                return;


            DBKeyInterface _document = getMandatoryKey("document", req);

            Contract contract = new Contract(new LookupByKey(_document));

            if(!mandatoryObjectExists(contract, resp))
                return;

            String text = getDocumentView(contract, true);

            setRespHeaders(jsonExport, resp);
            resp.getWriter().print(text);
            resp.setContentType(jsonExport.getContentType());
            setRespHeaders(jsonExport, resp);
            resp.flushBuffer();
            return;




        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            returnError("Error in get Preview", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
            resp.flushBuffer();
        }


    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in Preview", HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }


    /********************************************************************************
     *
     *          Creates a preview of the file with TextTags and Analysis Tags attached.
     *
     *          //TODO: (Improvement) Add information on Runs and explanations on failed matches
     *
     * @param fragmenter
     * @return
     * @throws BackOfficeException
     */


    protected String getAnalysisPreview(FragmentSplitterInterface fragmenter) throws BackOfficeException {

        return "not implemented";
    }

    /**********************************************************************************************'
     *
     *
     *
     *
     * @param contract
     * @param editable - is it possible to edit (adding form for changing style)
     * @return
     * @throws BackOfficeException
     *
     */

    public String getDocumentView(Contract contract, boolean editable) throws BackOfficeException {

        StringBuffer html = new StringBuffer();

        ContractVersionInstance version = contract.getHeadVersion();

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "Getting fragments for version " + version.getVersion());
        html.append("<p>Version: "+ version.getVersion()+"</p>");

        // Load all the fragments for the document

        List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));

        //TODO: This is only for printing.Remove for optimization

        StructureItem[] structureItemsForDocument = version.getStructureItemsForVersionAsArray(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));

        int j=0;
        for(StructureItem structureItem : structureItemsForDocument){

            System.out.println("SI:" + j + " no:" + structureItem.getOrdinal() + " (" + structureItem.getType() + ") top->" + structureItem.getFragmentForStructureItem().getName() );

            j++;
        }

        html.append("<table width=\"100%\">");

        int i =0;
        for(ContractFragment fragment : fragmentsForDocument){

            System.out.println("structureItem for fragment " + fragment.getOrdinal() + " = " + fragment.getStructureNo());

            StructureItem structureItem = structureItemsForDocument[(int)fragment.getStructureNo()];

            System.out.println(" Fragment " + i++ + ": (i:" + structureItem.getIndentation() + " style: " + fragment.getType() + " struct: " + structureItem.getType() + "/" +
                    " name: " + structureItem.getName() + ") " + fragment.getName());

        }


        if(fragmentsForDocument == null){

            html.append("Table is empty...");

        }
        else{

            for(ContractFragment fragment : fragmentsForDocument){

                StringBuffer style = new StringBuffer();
                StringBuffer body = new StringBuffer();
                StringBuffer comments = new StringBuffer();

                // Display Annotations

                List<ContractAnnotation> annotations = fragment.getAnnotationsForFragment();

                for(ContractAnnotation annotation : annotations){

                    comments.append(" Annotation: \"" + annotation.getDescription() + "\"( for " + annotation.getPattern() + ") by " + annotation.getCreator().getName() + "@" + annotation.getTime().getISODate() +
                            "<br/>");
                }



               //comments.append("Annotations: " + fragment.getAnnotationCount() + " (fragment ac:"+ fragment.getAnnotationCount()+")</br>");


                // Add classifications
                List<FragmentClassification> classifications = fragment.getClassificationsForFragment();

                for(FragmentClassification classification : classifications){

                    comments.append(" Classification: \"" + classification.getName() + "\"(" + classification.getClassification().getName() + "/" + classification.getSignificance() + ")(\""+  classification.getPattern() + "\" " +
                            classification.getPos() + "-" + (classification.getPos() + classification.getLength()) +  "\") by " + classification.getCreator().getName() + "@" + classification.getTime().getISODate() +
                            " comment: \"" + classification.getComment() + " keywords: \"" + classification.getKeywords() +"\"<br/>");

                }

                // Add Definitions
                List<Definition> definitions = fragment.getDefinitionsForFragment();

                for(Definition definition : definitions){

                    comments.append(" Definition of: \"" + definition.getName() + "\"<br/>");
                }

               // Add references
                List<Reference> references = fragment.getReferencesForFragment();

                for(Reference reference : references){

                    if(reference.getType().isSame(ReferenceType.getOpen()))
                        comments.append(" Open Reference " + reference.getName() +" <br/>");
                    else{

                        // Reference too. We are assuming that the "to"-fragment exists

                        if(reference.getTo() == null){

                            PukkaLogger.log(PukkaLogger.Level.FATAL, "No To-reference in the closed reference " + reference.getName());
                            comments.append("Invalid to-reference... " + reference.getName());

                        }
                        else{

                            comments.append(" Reference To: " + reference.getTo().getName() + " for \""+ reference.getName()+"\"( type "+ reference.getType().getName()+") <br/>");
                        }
                    }
                }


                // Display Actions

                List<Action> actions = fragment.getActionsForFragment();

                for(Action action : actions){

                    comments.append(" Action: \"" + action.getDescription() + "\"( for " + action.getPattern() + ") " + action.getIssuer().getName() + " -> " + action.getAssignee().getName() + "@" + action.getCreated().getISODate() +
                            "<br/>");
                }




                // Add risk
                try{

                    if(!fragment.getRisk().isSame(ContractRisk.getNone())){

                        RiskClassification classification = fragment.getLastRiskClassificationForFragment(RiskClassificationTable.Columns.Time.name());

                        comments.append(" Risk: Lvl \""+ classification.getRisk().getName()+"\" (\"" + classification.getPattern() + "\") by "+
                                classification.getCreator().getName() + "@" + classification.getTime().getISODate() + " comment: \"" + classification.getComment() + "\"<br/>");
                    }

                }catch(BackOfficeException e){


                    PukkaLogger.log(PukkaLogger.Level.WARNING, "No classification found for fragment " + fragment.getName());
                }


                if(fragment.getType().equals(StructureType.HEADING.name())){

                    body.append("<b>"+fragment.getText()+"</b>");

                }else if(fragment.getType().equals(StructureType.TEXT.name())){

                    body.append(fragment.getText());

                }else if(fragment.getType().equals(StructureType.IMPLICIT.name())){

                    body.append("");

                }else if(fragment.getType().equals(StructureType.LISTITEM.name())){

                    // This is a list item. However, only items with indentation are actually new list items. The rest is
                    // continuation of existing list bullets.

                    if(fragment.getIndentation() >= 0){

                        // Calculate an appropriate indentation for the css. This is a bit arbitrary for the display in the backoffice
                        long indentationInEm = 2 * (fragment.getIndentation() + 1);

                        body.append("<li style=\"margin-left: "+indentationInEm+"em;\">" + fragment.getText() + "</li>");

                    }
                    else{
                        // TODO: Indentation is not implemented
                        body.append(fragment.getText());

                    }

                }else{

                    //style = "Type: " + fragment.getType();
                    body.append(fragment.getText());

                }
                //style.append("id: " + fragment.getOrdinal());
                style.append(" ( i: " + fragment.getIndentation());
                style.append(" S: " + fragment.getStructureNo());
                style.append(")");

                if(editable)
                    html.append(createLine(contract, fragment, style, body , comments));
                else
                    html.append(createLine(null, null, style, body , comments));


            }
        }

        html.append("</table>");

        return html.toString();
    }

    /*************************************************************************'
     *
     *          Create one line in the preview
     *
     *              NOTE: The style form is a back-office function
     *
     *
     * @param contract
     * @param fragment
     * @param textTags
     * @param fragmentText
     * @param analysisTags   @return    */

    private String createLine(Contract contract, ContractFragment fragment, StringBuffer textTags, StringBuffer fragmentText, StringBuffer analysisTags){

        String styleForm = "";


        try{

            if(fragment != null){

                ContractFragmentTypeTable allTypes = new ContractFragmentTypeTable();
                styleForm = "<FORM METHOD=POST action=\"?id="+contract.getKey().toString()+
                        "&list=DocumentList" +
                        "&action=Item" +
                        "&callbackAction="+ DocumentList.Callback_Action_ChangeStyle+
                        "&section=Documents" +
                        "&fragment="+fragment.getKey().toString()+"\" name=\"styleForm\">\n";
                //styleForm += allTypes.getDropDown().generate("styleType", fragment.getType(), null, null, true);
                styleForm += Html.dropDown("styleType", types, fragment.getType(), " onchange='this.form.submit()'");
                //styleForm += fragment.getType();
                styleForm += "</FORM>";
            }

        }catch(Exception e){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not get style for fragment " + fragmentText);
        }


        return "<tr>" +
                  "<td width=\"10%\">"+ textTags.toString()+"</td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"10%\">"+ styleForm + "</td>" +
                  "<td width=\"30px\"></td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"40%\">"+ fragmentText.toString() +"</td>" +
                  "<td style=\"border-bottom:1pt solid black;\" width=\"40%\">"+ analysisTags.toString() +"</td>" +
                "</tr>";
    }



}
