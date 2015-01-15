package api;

import adminServices.GenericAdminServlet;
import cache.ServiceCache;
import classifiers.Classification;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import language.English;
import language.LanguageInterface;
import maintenance.Smokey;
import pukkaBO.condition.ColumnFilter;
import pukkaBO.condition.LookupItem;
import pukkaBO.condition.ReferenceFilter;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import risk.RiskClassificationTable;
import services.FragmentServlet;
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
 *
 *              // TODO: Add checks if this is already run
 *              // TODO: Add heuristic check if the fragment is same/similar or has changed
 *
 */

public class DemogenerationServlet extends GenericAdminServlet {

    protected enum Environment {UNKNOWN, LOCAL, STAGE, LIVE}

        // Use english names of classifications as default
    // When implementing language support, this should be taken from the user settings

    protected static final LanguageInterface defaultLanguage = new English();



    /****************************************************''
     *
     *          This is the demo comments that should be added to the Swedish demo
     *
     *
     */


    private static final DemoComment[] demoCommentList = {

            // The demo comments are using the english names of the tags

            new DemoComment("#ACCEPTANCE_CRITERIA", 0, 0, "Test document.docx", 2, "Medium", "later chapter", "var är detta???", "itClarifies"),

            // Swedish demo

            new DemoComment("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       369, "Potential",   "minst likvärdig kompetens", "Subjektivt och ensidigt", "itClarifies"),
            new DemoComment("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       452, "Not set", "fast pris", "Fast Pris", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       387, "Not set",     "flera utbildningar samtidigt", "Oklar numrering", "itClarifies"),

            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        65, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        73, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        79, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        96, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       112, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       113, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       136, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       137, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       138, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       148, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       167, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       177, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       183, "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       220, "Not set", "", "#contracting", "itClarifies"),


            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  184, "Potential",   "kritisk karaktär", "Odefinierat: kritisk", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  190, "Not set",     "backup på all lagrad data", "Krav för restore saknas", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  244, "Not set",     "ska informationen nedan lagras", "Ej avgränsad tid", "itClarifies"),

            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  113, "Not set",     "utan tillkommande kostnad", "Uttrycket (etc.)", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  220, "Potential",   "vid var tillfälle", "Odefinierat: tillfälle", "itClarifies"),
            //new DemoComment("#AMBIGUITY",   0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  229, "Not set",     "besvaras", "Tvetydigt: besvaras", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  243, "Not set",     "kan härledas till Las Vegass server/it-miljö eller handhavande", "Odefinierat : Responsibility", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 3A - Ramavtal Interaktiva utbildningar.docx",            41, "Not set",     "avser samtliga de resultat", "Ej avgränsad", "itClarifies"),



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

    /*********************************************************************
     *
     *          Generate risks, annotations and classifications for all the risks
     *
     *
     * @param comments
     * @param analysisTime
     * @return
     */


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

    /**************************************************************************************
     *
     *
     * @param comment
     * @param analysisTime
     * @return
     * @throws BackOfficeException
     *
     *
     *      TODO: There are a lot of lookup in this service that could be optimized getting once and for all
     */


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

        String translatedClassification = defaultLanguage.getLocalizedClassification(comment.classification);

        int patternPos = fragment.getText().indexOf(comment.pattern);

        // Get all existing classifications for the


        // Now create annotation, classification and risk


        FragmentClassification classification = new FragmentClassification(
                fragment.getKey(),
                comment.classification,
                comment.requirementLevel,
                comment.applicablePhase,
                comment.comment,
                "#" + translatedClassification,
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

            ContractRisk risk = new ContractRiskTable().getValueForName(comment.riskLevel);

            if(!risk.exists()){

                feedback.append("<br/> - ! Risk level " + comment.riskLevel + " does not exist! </p>");
                return feedback.toString();

            }



            // Add a risk attribute too

            RiskClassification riskClassification = new RiskClassification(
                    fragment.getKey(),
                    risk,
                    "demo",
                    "#RISK",
                    user.getKey(),
                    version.getKey(),
                    document.getProjectId(),
                    comment.pattern,
                    patternPos,
                    analysisTime.getISODate());


            riskClassification.store();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println("RiskClassification: " + riskClassification.getKey().toString());
            fragment.setRisk(risk);

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
        fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
        feedback.append("<br/> - Adding classification \""+ translatedClassification + "\" ("+ classification.getClassTag()+") to fragment no "+ comment.ordinal+" in document " + document.getName());

        annotation.store();
        fragment.setAnnotationCount(ordinal);
        feedback.append("<br/> - Adding annotation \""+ comment.comment + "\" to fragment no "+ comment.ordinal+" in document " + document.getName());


        fragment.update();
        feedback.append("</p>");

        // Invalidate the cache. The document overview is changed.

        invalidateFragmentCache(version);


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
        private final int requirementLevel;
        private final int applicablePhase;
        private final String document;
        private final int ordinal;
        private final String riskLevel;
        private final String pattern;
        private final String comment;
        private final String user;




        public DemoComment(String classification, int requirementLevel, int applicablePhase, String document, int ordinal, String riskLevel, String pattern, String comment, String user) {

            this.classification = classification;
            this.requirementLevel = requirementLevel;
            this.applicablePhase = applicablePhase;
            this.document = document;
            this.ordinal = ordinal;
            this.riskLevel = riskLevel;
            this.pattern = pattern;
            this.comment = comment;
            this.user = user;

        }
    }


    /*************************************************************************
     *
     *      Clearing  "fragment for document" cache
     *
     * @param version - the updated version of the contract
     * @throws BackOfficeException
     */

    public static void invalidateFragmentCache(ContractVersionInstance version) throws BackOfficeException {

        if(version == null)
            throw new BackOfficeException(BackOfficeException.General, "Trying to invalidate cache for non-existing version instance");

        if(version.getKey() == null)
            throw new BackOfficeException(BackOfficeException.General, "Trying to invalidate cache for non stored version instance");



        ServiceCache cache = new ServiceCache(FragmentServlet.DataServletName);
        cache.clearKey(version.getKey().toString());          // Fragment is not qualified on user


    }


}
