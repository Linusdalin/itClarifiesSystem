package api;

import adminServices.GenericAdminServlet;
import cache.ServiceCache;
import classification.FragmentClassification;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import diff.FragmentComparator;
import language.English;
import language.LanguageInterface;
import log.PukkaLogger;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
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


    private static final DemoComment[] testFileDemoCommens = {

            // The demo comments are using the english names of the tags

            new DemoComment("#ACCEPTANCE_CRITERIA", 0, 0, "Test document.docx", 2, "", "Medium", "later chapter", "var är detta???", "itClarifies"),

    };

    private static final DemoComment[] swedishDemoCommens = {

            // Swedish demo

            new DemoComment("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx", 351,
                    "Vid byte av person ska ersättaren ha minst likvärdig kompetens och godkännas av Las Vegas",
                    "Potential",   "minst likvärdig kompetens", "Subjektivt och ensidigt", "itClarifies"),
            new DemoComment("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       434,
                    "Tjänster som efterfrågas i denna upphandling ska kunna avropas som pris per timma, per dag, per vecka, per månad eller som fast pris. Vilken prissättning som är bäst lämpad för det specifika uppdraget beslutas av uppdragsgivaren vid varje uppdrag. När pris begärs på annat än enbart timmar ska timpriset framgå.",
                    "Potential", "fast pris", "Fast Pris", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       369,
                    "Anbudsgivaren ska ha kapacitet att producera flera utbildningar samtidigt.",
                    "Potential",     "flera utbildningar samtidigt", "Oklar numrering", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        63,
                    "Förfrågningsunderlaget - svarsformulär för anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        71,
                    "Frågor rörande förfrågningsunderlaget",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        76,
                    "Uppgifter om anbudsgivaren ",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        90,
                    "Presentation av anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        92,
                    "Anbudets omfattning",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       105,
                    "Anbudets språk",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       107,
                    "Undertecknat anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       132,
                    "Sista anbudsdag och sena anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       135,
                    "Anbudets och tilldelningsbeslutets rättsliga betydelse",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       138,
                    "",
                    "Not set", "", "#contracting", "itClarifies"),


            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       143,
                    "Kostnader associerade med anbudet", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       160,
                    "Avbrytande av upphandling", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       178,
                    "En bekräftelse att leverantören förfogar över underleverantör som krävs för att uppfylla de krav som gäller för det åtagande som upphandlingen omfattar och att Las Vegas vid begäran kan få intyg på detta.", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       179,
                    "Uteslutande från deltagande i upphandlingen", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       215,
                    "Referensuppdrag, anbudsgivande företag", "Not set", "", "#contracting", "itClarifies"),

            // Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx

            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  108,
                    "Mindre fel i utbildningen, såsom stavfel, buggar, etc ska ändras av leverantören utan tillkommande kostnad.",
                    "Potential",     "utan tillkommande kostnad", "Uttrycket (etc.)", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  178,
                    "Supportärenden av teknisk och kritisk karaktär ska besvaras inom 24 timmar på vardagar. För helgdagar ska ärenden besvaras närmast följande vardag. Under 15 juni - 15 augusti kan andra svarstider överenskommas. ",
                    "Potential",   "kritisk karaktär", "Odefinierat: kritisk", "itClarifies"),

            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  184,
                    "För det fall att utbildningen hostas av leverantören ska leverantören ha rutiner avseende backup på all lagrad data",
                    "Potential",     "backup på all lagrad data", "Krav för restore saknas", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  238,
                    "För att kunna uppfylla Las Vegass behov av statistik och uppföljning ska informationen nedan lagras och tillhandahållas. Om leverantören ansvarar för hosting av utbildningen så ska informationen nedan kunna hämtas in genom den webbtjänst som tillhandahåller information om deltagarnas resultat. Om utbildningen levereras i SCORM-format ska utbildningen struktureras så att motsvarande information går att ta fram.",
                    "Potential",     "ska informationen nedan lagras", "Ej avgränsad tid", "itClarifies"),

            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  214,
                    "Vid brist i kapacitetskraven, har Las Vegas rätt till ett vite vid var tillfälle om tio (10) procent av månadsavgiften för hostingen av den aktuella utbildningen. ",
                    "Potential",   "vid var tillfälle", "Odefinierat: tillfälle", "itClarifies"),
            //new DemoComment("#AMBIGUITY",   0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  229, "", "Not set",     "besvaras", "Tvetydigt: besvaras", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  192,
                    "Leverantören garanterar att av Leverantören hostad interaktiv ansvarsutbildning har en tillgänglighet mätt per kalendermånad på minst 99 % under tiden måndag-söndag kl 06.00-24.00 (\"Avtalad Tillgänglighet\"). Dock gäller inte Avtalad Tillgänglighet om nertid kan härledas till Las Vegass server/it-miljö eller handhavande, d v s faktorer som Leverantören inte har kontroll över, eller om Las Vegas och Leverantören kommit överens om planerad nertid.",
                    "Potential",     "kan härledas till Las Vegass server/it-miljö eller handhavande", "Odefinierat : Responsibility", "itClarifies"),


            //TODO: Find this in the document

            new DemoComment("#UNSPECIFIC",  0, 0, "Bilaga 3A - Ramavtal Interaktiva utbildningar.docx",            41, "", "Not set",     "avser samtliga de resultat", "Ej avgränsad", "itClarifies"),



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

        resp.getWriter().println(generateDemoComments(swedishDemoCommens, analysisTime));


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


        ContractFragment fragment = locateFragment(comment, version);

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

            feedback.append("<br/> - Adding risk \""+ risk.getName()+"\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());

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
        feedback.append("<br/> - Adding classification \""+ translatedClassification + "\" ("+ classification.getClassTag()+") to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());

        annotation.store();
        fragment.setAnnotationCount(ordinal);
        feedback.append("<br/> - Adding annotation \""+ comment.comment + "\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());

        if(fragment.getOrdinal() != comment.ordinal){

            // Fragment is found elsewhere. Prompt the user with a warning

            feedback.append("<br/><b> - !NOTE: The comments are added to fragment " + fragment.getOrdinal() + " but it was listed at fragment " + comment.ordinal + ". Consider revising the stored data.</b>");


        }

        fragment.update();
        feedback.append("</p>");

        // Invalidate the cache. The document overview is changed.

        invalidateFragmentCache(version);


        return feedback.toString();
    }

    /********************************************************************************
     *
     *          Locate fragment will find the closest matching fragment between the ordinal
     *          number in the comment and the fragments in the list
     *
     *
     * @param comment
     * @param version
     * @return
     *
     *          It will look 20 steps up and down in the list
     */

    private ContractFragment locateFragment(DemoComment comment, ContractVersionInstance version)  {

        try{

            FragmentComparator comparator = new FragmentComparator();
            List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));
            ContractFragment fragment  = fragmentsForDocument.get(comment.ordinal);
            int totalFragments = fragmentsForDocument.size();
            PukkaLogger.log(PukkaLogger.Level.INFO, "Looking for fragment " + comment.fragment + " to comment.");
            PukkaLogger.log(PukkaLogger.Level.INFO, "*** Start looking at " + comment.ordinal);

            // First check the fragment pointed out by the ordinal value

            if(comparator.isSame(comment.fragment, fragment.getText())){

                return fragment;
            }

            for(int offset = 1; offset <= 20; offset++){


                if(comment.ordinal + offset < totalFragments){

                    fragment = fragmentsForDocument.get(comment.ordinal + offset);

                    //System.out.println("  -> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(comment.fragment, fragment.getText())){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (comment.ordinal + offset) + "(" + offset + " off)");
                        return fragment;
                    }
                }

                if(comment.ordinal - offset >= 0){

                    fragment = fragmentsForDocument.get(comment.ordinal - offset);

                    //System.out.println(" --> Looking at fragment (" + fragment.getOrdinal() + ")" + fragment.getName());

                    if(comparator.isSame(comment.fragment, fragment.getText())){

                        // Found. Log and return
                        PukkaLogger.log(PukkaLogger.Level.INFO, "Matching fragment " + fragment.getName() + " at " + (comment.ordinal - offset) + "(" + -offset + " off)");
                        return fragment;
                    }

                }

            }

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not find fragment " + comment.fragment + " at " + comment.ordinal + "( +/- 20)");

            //TOD: Remove this. Only for reenginering
            return fragmentsForDocument.get(comment.ordinal);


        }catch(BackOfficeException e){

            PukkaLogger.log(e);
        }

        return new ContractFragment();


    }


    private ContractFragment locateFragmentOld(DemoComment comment, ContractVersionInstance version)  {

        try{

            List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion();

            ContractFragment fragment = new ContractFragment(new LookupItem()
                    .addFilter(new ColumnFilter(ContractFragmentTable.Columns.Ordinal.name(), comment.ordinal))
                    .addFilter(new ReferenceFilter(ContractFragmentTable.Columns.Version.name(), version.getKey())));

            return fragment;

        }catch(BackOfficeException e){

            PukkaLogger.log(e);
            return new ContractFragment();
        }


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
        private String fragment;
        private final String riskLevel;
        private final String pattern;
        private final String comment;
        private final String user;




        public DemoComment(String classification, int requirementLevel, int applicablePhase, String document, int ordinal, String fragment, String riskLevel, String pattern, String comment, String user) {

            this.classification = classification;
            this.requirementLevel = requirementLevel;
            this.applicablePhase = applicablePhase;
            this.document = document;
            this.ordinal = ordinal;
            this.fragment = fragment;
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
