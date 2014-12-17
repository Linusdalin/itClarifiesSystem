package api;

import adminServices.GenericAdminServlet;
import classifiers.Classification;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import maintenance.Smokey;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import risk.RiskClassificationTable;
import userManagement.PortalUser;
import userManagement.PortalUserTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/*************************************************************************'''
 *
 *              Generate Demo values
 *
 *              This servlet adds custom data to the demo project
 */

public class DemogenerationServlet extends GenericAdminServlet {

    protected enum Environment {UNKNOWN, LOCAL, STAGE, LIVE}


    private static final DemoComment[] demoCommentList = {
            new DemoComment("#RISK", "Test document.docx", 2, "Medium", "later chapter", "var är detta???", "admin"),


    };



    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

             // Get the application to be able to set different actions

        Environment environment = getEnvironment();
        resp.setContentType("text/html");


        String bgColour = getColourForEnvironment(environment);


        resp.getWriter().println("<html>");
        resp.getWriter().println("<head><title>"+environment.name()+" Demo Generation</title></head>");
        resp.getWriter().println("<body bgColor=\""+bgColour+"\">");
        resp.getWriter().println("<h1>Demo Generation</h1>");

        // Check access right

        if(!googleAuthenticate(req, resp))
            return;

        DBTimeStamp analysisTime = new DBTimeStamp();

        resp.getWriter().println(generateDemoComments(demoCommentList, analysisTime));


        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        resp.flushBuffer();



    }

    private String generateDemoComments(DemoComment[] comments, DBTimeStamp analysisTime) {

        StringBuffer feedback = new StringBuffer();

        try {

            for (DemoComment comment : comments) {

                feedback.append(generateDemoComment(comment, analysisTime));

            }
        } catch (BackOfficeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return feedback.toString();

    }

    private String generateDemoComment(DemoComment comment, DBTimeStamp analysisTime) throws BackOfficeException {

        StringBuffer feedback = new StringBuffer();
        feedback.append("<p>Adding comment...");

        PortalUser user = new PortalUser(new LookupItem().addFilter(new ColumnFilter(PortalUserTable.Columns.Name.name(), comment.user)));
        if(!user.exists()){

            feedback.append("<br/> - ! User " + comment.user + " does not exist! </p>");
            return feedback.toString();

        }

        String filename = comment.document;

        try {

            filename = new String (filename.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {

            // Swallow this
        }

        Contract document = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.File.name(), filename)));
        if(!document.exists()){

            feedback.append("<br/> - ! Document " + filename + " does not exist! </p>");
            return feedback.toString();

        }

        ContractVersionInstance version = document.getHeadVersion();


        ContractFragment fragment = new ContractFragment(new LookupItem()
                .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), comment.ordinal))
                .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey())));
        if(!fragment.exists()){

            feedback.append("<br/> - ! Fragment id " + comment.ordinal + " does not exist! </p>");
            return feedback.toString();

        }

        FragmentClass fragmentClass = new FragmentClass(new LookupItem()
                .addFilter(new ColumnFilter(FragmentClassTable.Columns.Type.name(), comment.classification)));

        if(!fragmentClass.exists()){

            feedback.append("<br/> - ! Class  " + comment.classification + " does not exist! </p>");
            return feedback.toString();

        }

        int patternPos = fragment.getText().indexOf(comment.pattern);



        // Now create annotation, classification and risk


        FragmentClassification classification = new FragmentClassification(
                fragment.getKey(),
                fragmentClass.getKey(),
                "demo",
                comment.comment,
                "",                 //keywords,
                user.getKey(),
                version.getKey(),
                document.getProjectId(),
                comment.pattern,
                patternPos,
                comment.pattern.length(),
                100, //significance
                "",
                analysisTime.getISODate());



        if(comment.classification.equals("#RISK")){

            ContractRisk risk = new ContractRisk(new LookupItem()
                    .addFilter(new ColumnFilter(ContractRiskTable.Columns.Name.name(), comment.riskLevel)));
            if(!risk.exists()){

                feedback.append("<br/> - ! Risk level " + comment.riskLevel + " does not exist! </p>");
                return feedback.toString();

            }



            // Add a risk attribute too

            RiskClassification riskClassification = new RiskClassification(
                    fragment.getKey(),
                    risk.getKey(),
                    "demo",
                    user.getKey(),
                    version.getKey(),
                    document.getProjectId(),
                    comment.pattern,
                    analysisTime.getISODate());


            riskClassification.store();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println("RiskClassification: " + riskClassification.getKey().toString());
            fragment.setRisk(risk.getKey());
            feedback.append("<br/> - Adding risk \""+ risk.getName()+"\" to fragment no "+ comment.ordinal+" in document " + document.getName());

        }
        int ordinal = (int)fragment.getAnnotationCount() + 1;


        ContractAnnotation annotation = new ContractAnnotation(
                "demo",
                fragment.getKey(),
                ordinal,
                comment.comment,
                user.getKey(),
                version.getKey(),
                comment.pattern,
                analysisTime.getISODate());

        classification.store();
        fragment.setClassificatonCount(fragment.getClassificatonCount());
        feedback.append("<br/> - Adding classification \""+ fragmentClass.getName()+"\" to fragment no "+ comment.ordinal+" in document " + document.getName());

        annotation.store();
        fragment.setAnnotationCount(ordinal);
        feedback.append("<br/> - Adding annotation \""+ comment.comment + "\" to fragment no "+ comment.ordinal+" in document " + document.getName());


        fragment.update();
        feedback.append("</p>");

        return feedback.toString();
    }


    /***********************************************************************
     *
     *              Detecting the environment can be used to indicate this to the user
     *              (or have different access levels on different environments)
     *
     *
     *
     * @return - the environment enum
     */



    protected Environment getEnvironment() {

        String ID = SystemProperty.applicationId.get();
        String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();


        if(serviceAccountName.contains("localhost")){

            return Environment.LOCAL;

        }
        else{

            if(ID.equals("itclarifiesapidemo") || ID.equals("itclarifiesapistage")){

                return Environment.STAGE;

            }

            if(ID.equals("itclarifiesapi") || ID.equals("itclarifiesapilogin")){

                return Environment.LIVE;

            }


        }

        return Environment.UNKNOWN;
    }



    protected String getColourForEnvironment(Environment environment) {

        switch (environment) {


            case UNKNOWN:

                return("#000000");       // pitch black...

            case LOCAL:

                return("#EEEEEE");

            case STAGE:

                return("#AAFFFF");

            case LIVE:
                return("#FFAA00");
        }

        return("#000000");       // pitch black...


    }


    private static class DemoComment {

        private final String classification;
        private final String document;
        private final int ordinal;
        private final String riskLevel;
        private final String pattern;
        private final String comment;
        private final String user;

        public DemoComment(String classification, String document, int ordinal, String riskLevel, String pattern, String comment, String user) {

            this.classification = classification;
            this.document = document;
            this.ordinal = ordinal;
            this.riskLevel = riskLevel;
            this.pattern = pattern;
            this.comment = comment;
            this.user = user;

        }
    }
}
