package reclassification;

import httpRequest.RequestHandler;
import log.PukkaLogger;
import net.sf.json.JSONObject;
import pukkaBO.exceptions.BackOfficeException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/********************************************************************************
 *
 *          Storing manual classifications, references, definitions, risks and annotations
 *
 *          that then can be imported to another project
 *
 *          //TODO: Handle reply in some way.
 */

public class NewMTProject{

    private String projectName;
    private String server;

    private List<Reclassification>  classifications = new ArrayList<Reclassification>();
    private List<Rerisk>            risks           = new ArrayList<Rerisk>();
    private List<Reannotation>      annotations     = new ArrayList<Reannotation>();

    public NewMTProject(String name, String server){

        this.projectName = name;
        this.server = server;
    }

    public void setProjectName(String projectName){

        this.projectName = projectName;
    }

    public void setTargetServer(String targetServer){

        this.server = targetServer;
    }


    public String getProjectName(){

        return projectName;
    }

    protected void addClassification(Reclassification classification){

        classifications.add(classification);
    }

    protected void addRisk(Rerisk risk){

        risks.add(risk);
    }
    protected void addAnnotation(Reannotation annotation){

        annotations.add(annotation);
    }


    public void inject() {


        RequestHandler requestHandler = new RequestHandler( server + "/Reclassification");

        for (Reclassification classification : classifications) {

            try {

                String urlEncoded = classification.getPostRequest();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Trying to inject classification " + classification.getClassification());
                PukkaLogger.log(PukkaLogger.Level.INFO, "Request: " + server + "/Reclassification"+ urlEncoded);

                String responseString = requestHandler.excutePost(urlEncoded);
                PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from "+ server+": " + responseString);
                JSONObject response = new JSONObject(responseString);

            } catch (BackOfficeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }


        for (Rerisk risk : risks) {

            try {

                String urlEncoded = risk.getPostRequest();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Trying to inject risk " + risk.getRiskLevel());
                PukkaLogger.log(PukkaLogger.Level.INFO, "Request: " + server + "/Reclassification"+ urlEncoded);

                String responseString = requestHandler.excutePost(urlEncoded);
                PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from "+ server+": " + responseString);
                JSONObject response = new JSONObject(responseString);

            } catch (BackOfficeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        for (Reannotation annotation : annotations) {

            try {

                String urlEncoded = annotation.getPostRequest();

                PukkaLogger.log(PukkaLogger.Level.INFO, "Trying to inject annotaion \"" + annotation.getText() + "\"");
                PukkaLogger.log(PukkaLogger.Level.INFO, "Request: " + server + "/Reclassification"+ urlEncoded);

                String responseString = requestHandler.excutePost(urlEncoded);
                PukkaLogger.log(PukkaLogger.Level.INFO, "Got response from "+ server+": " + responseString);
                JSONObject response = new JSONObject(responseString);

            } catch (BackOfficeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }
}
