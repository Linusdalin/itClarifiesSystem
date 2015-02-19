package services;

import actions.Action;
import actions.ChecklistItem;
import analysis.Significance;
import classification.FragmentClassification;
import contractManagement.*;
import crossReference.Reference;
import databaseLayer.DBKeyInterface;
import log.PukkaLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pukkaBO.condition.*;

import pukkaBO.exceptions.BackOfficeException;
import risk.RiskClassification;
import risk.RiskClassificationTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/********************************************************
 *
 *          Fragment Detail Servlet
 *
 *          Retrieving the details of a fragment. The fragment data is divided
 *          in base information and detail information for performance reasons
 *
 *          The fragment details works with the fragment as a key and as
 *          different versions have a different set of fragments we don't
 *          need version handling on this
 */

public class FragmentDetailServlet extends ItClarifiesService{

    public static final String DataServletName = "FragmentDetail";

    /*****************************************************************************
     *
     *      Updating an existing fragment is done in /Fragment /Classification and /Risk servlets
     *
     */


    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Post not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);

     }

    /*************************************************************************
     *
     *          Get all fragment for a project (or matching other request criteria)
     *
     *          Parameters:
     *
     *          &key=<key>      (if left empty, it will return the entire list of contracts)
     *          &project=<key>  return all the fragments for all documents ina specific project(cant be empty)
     *
     *
     *          This method traverses the data structure, getting all documents fro a project, all clauses
     *          for a document and all fragments for the clauses, finally adding them all.
     *
     *
     * @throws java.io.IOException
     */


    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {


       try{

           logRequest(req);

           if(!validateSession(req, resp))
               return;

           if(blockedSmokey(sessionManagement, resp))
               return;

           setLoggerByParameters(req);

           Formatter formatter = getFormatFromParameters(req);

           // Getting fragments means getting all fragments for all clauses for all documents for the project
           // So we need to do a nested loop adding all fragments

           DBKeyInterface _fragment = getMandatoryKey("fragment", req);
           ContractFragment fragment = new ContractFragment(new LookupByKey(_fragment));

           Project project = fragment.getProject();

           if(!fragment.exists()){

               returnError("Fragment Missing", HttpServletResponse.SC_BAD_REQUEST, resp);
               resp.flushBuffer();
               return;

           }

           Contract contract = fragment.getVersion().getDocument();
           //PukkaLogger.log(PukkaLogger.Level.INFO, "Found Document " +  contract.getName() + " for fragment");

           // This is for the access rights. Unless there is something
           // wrong in the database, this would exist, but it may be restricted.

           if(!mandatoryObjectExists(contract, resp))
               return;

           List<ChecklistItem> checklistItemsInProject = project.getChecklistItemsForProject();

           JSONObject detailJSON = new JSONObject();

                try{  detailJSON.put("annotations",     getAnnotations(fragment));      }catch(Exception e){ PukkaLogger.log(e); }
                try{  detailJSON.put("references",      getReferences(fragment));       }catch(Exception e){ PukkaLogger.log(e); }
                try{  detailJSON.put("classifications", getClassifications(fragment));  }catch(Exception e){ PukkaLogger.log(e); }
                try{  detailJSON.put("risk",            getRisk(fragment));             }catch(Exception e){ PukkaLogger.log(e); }
                try{  detailJSON.put("action",          getActions(fragment));          }catch(Exception e){ PukkaLogger.log(e); }

                try{  detailJSON.put("checklist",       getChecklistReferences(fragment, checklistItemsInProject));
                                                                                        }catch(Exception e){ PukkaLogger.log(e); }


           JSONObject output = new JSONObject()
                   .put(DataServletName, detailJSON);


           sendJSONResponse(output, formatter, resp);

       }catch(BackOfficeException e){

           returnError(e.narration, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace();

       } catch ( Exception e) {

           returnError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp);
           e.printStackTrace();

       }
    }

            //TODO: Check if the fragment class is different from the classification class and update

    private JSONObject getRisk(ContractFragment fragment) {

        int riskId = 60;        // Default
        String classifier = "";
        String time = "";
        String comment = "";
        String pattern = "";
        long patternPos = 0;
        long severity = 0;

        try {

            riskId = fragment.getRisk().get__Id();
            List<RiskClassification> allClassifications =  fragment.getRiskClassificationsForFragment(new LookupList().addOrdering(RiskClassificationTable.Columns.Time.name(), Ordering.LAST));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found "+ allClassifications.size()+" risk classifications");

            if(allClassifications.size() > 0){

                RiskClassification latest = allClassifications.get(0);

                classifier = latest.getCreatorId().toString();
                time = latest.getTime().getSQLTime().toString();
                comment = latest.getComment();
                pattern = latest.getPattern();
                patternPos = latest.getPatternPos();
                severity = new Integer(latest.getRisk().getSeverity());
            }

        } catch (BackOfficeException e) {

            PukkaLogger.log( e );

        }

        return new JSONObject()
                .put("class", "" + riskId)
                .put("severity", severity)
                .put("classifier", classifier)
                .put("time", time)
                .put("comment", comment)
                .put("pattern", pattern)
                .put("patternPos", patternPos);
    }


    private JSONArray getClassifications(ContractFragment fragment) {

        JSONArray classifications = new JSONArray();

        try{

            List<FragmentClassification> classificationList = fragment.getClassificationsForFragment();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found over "+ classificationList.size()+" classifications");

            for(FragmentClassification classification : classificationList){

                // Only add classification if the significance is high enough

                if(classification.getSignificance() > Significance.DISPLAY_SIGNIFICANCE){

                    JSONObject classificationJSON = new JSONObject()


                            .put("id",          classification.getKey().toString())
                            //.put("class", classification.getName())
                            .put("class",       classification.getClassTag())
                            .put("classifier",  classification.getCreatorId().toString())
                            .put("time",        classification.getTime().getSQLTime().toString())
                            .put("pattern",     classification.getPattern())
                            .put("patternPos",  classification.getPos())
                            .put("comment",     classification.getComment());

                    classifications.put(classificationJSON);

                }

            }


        }catch (BackOfficeException e){

            e.logError("Fail to get details for annotation for fragment " + fragment.getText());

        }

        return classifications;

    }


    /*************************************************************************************************
     *
     *      Get the references for a fragment
     *
     * @param fragment - the source fragment
     * @return - list of strings with the keys from the destination clauses
     */

    private JSONArray getReferences(ContractFragment fragment) {

        JSONArray references = new JSONArray();
        try{

            List<Reference> referenceList = fragment.getReferencesForFragment();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found "+ referenceList.size()+" references");


            for(Reference reference : referenceList){

                JSONObject referenceJSON = new JSONObject()
                        .put("id", reference.getToId().toString())
                        .put("pattern",    reference.getPattern())
                        .put("patternPos", reference.getPatternPos())
                        .put("type", reference.getType().getName());

                references.put(referenceJSON);
            }

        }catch (BackOfficeException e){

            e.logError("Fail to get details for annotation for fragment " + fragment.getText());

        }

        return references;
    }

    /*************************************************************************************************
     *
     *      Get the annotations for a fragment
     *
     * @param fragment - the source fragment
     * @return - list of strings with the keys for the annotation descriptions (text)
     *
     */


    public JSONArray getAnnotations(ContractFragment fragment) {

        JSONArray annotations = new JSONArray();

        try{

            List<ContractAnnotation> annotationList = fragment.getAnnotationsForFragment(new LookupItem().addOrdering(ContractAnnotationTable.Columns.Ordinal.name(), Ordering.FIRST));

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found "+ annotationList.size()+" annotations");


            for(ContractAnnotation annotation : annotationList){

                JSONObject annotationJSON = new JSONObject()
                        .put("id",          annotation.getKey().toString() )
                        .put("text",        annotation.getDescription())
                        .put("annotator",   annotation.getCreatorId().toString())
                        .put("pattern",     annotation.getPattern())
                        .put("patternPos",  annotation.getPatternPos())
                        .put("time",        annotation.getTime().getSQLTime().toString());

                annotations.put(annotationJSON);

            }

        }catch (BackOfficeException e){

            e.logError("Fail to get details for annotation for fragment " + fragment.getText());

        }

        return annotations;
    }



    public JSONArray getActions(ContractFragment fragment) {

        JSONArray actions = new JSONArray();

        try{

            List<Action> actionList = fragment.getActionsForFragment();

            PukkaLogger.log(PukkaLogger.Level.INFO, "Found "+ actionList.size()+" actions");

            for(Action action: actionList){

                JSONObject annotationJSON = new JSONObject()
                        .put("id",              action.getKey().toString())
                        .put("text",            action.getDescription())
                        .put("creator",         action.getIssuerId().toString())
                        .put("creationDate",    action.getCreated().getISODate())
                        .put("completionDate",  action.getCompleted().getISODate())
                        .put("assignee",        action.getAssigneeId().toString())
                        .put("status",          "" + action.getStatus().getId());

                actions.put(annotationJSON);

            }

        }catch (BackOfficeException e){

            e.logError("Fail to get details for action for fragment " + fragment.getText());

        }

        return actions;
    }


    public void doDelete(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        returnError("Delete not supported in " + DataServletName, HttpServletResponse.SC_METHOD_NOT_ALLOWED, resp);
        resp.flushBuffer();

    }



}
