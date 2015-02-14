package api;

import adminServices.GenericAdminServlet;
import analysis2.AnalysisException;
import cache.ServiceCache;
import reclassification.MTClassification;
import classification.FragmentClass;
import classification.FragmentClassification;
import classifiers.ClassifierInterface;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import contractManagement.*;
import dataRepresentation.DBTimeStamp;
import diff.FragmentComparator;
import featureTypes.FeatureTypeInterface;
import language.English;
import language.LanguageAnalyser;
import language.LanguageCode;
import language.LanguageInterface;
import log.PukkaLogger;
import pukkaBO.condition.*;
import pukkaBO.exceptions.BackOfficeException;
import risk.ContractRisk;
import risk.ContractRiskTable;
import risk.RiskClassification;
import services.FragmentServlet;
import userManagement.Organization;
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


    private static final MTClassification[] testFileDemoCommens = {


    };

    private static final MTClassification[] swedishDemoCommens = {




    };

    /***********************************************************
        *
        *      Regeneration of risk, comment and annotation
        *         Project:  EHM-2
        *
        *
       private static final MTClassification[] anbudsförfråganITDrift = {

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new MTClassification("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 11,
                               "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                               "Not set", "", "", "linus"),

               new MTClassification("#TERM", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 42,
                               "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#RESTRICTION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 61,
                               "Genom att lämna anbud förbinder sig anbudsgivare att, i händelse av att anbudsgivaren tilldelas avtal, underteckna och återsända av Beställaren översänt avtal inom fem dagar från mottagande av avtalet. Avtalet är bindande för Beställaren först när Beställaren undertecknat detta.",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 25,
                               "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new MTClassification("#ACCEPTANCE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 108,
                               "6. Prövning och utvärdering",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 59,
                               "2.11 Tilldelningsbeslut och avslutande av upphandling",
                               "Not set", "", "", "linus"),

               new MTClassification("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 105,
                               "5. Krav på Tjänsten",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 105,
                               "5. Krav på Tjänsten",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 46,
                               "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                               "Not set", "", "", "linus"),

               new MTClassification("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 96,
                               "3. Nulägesbeskrivning",
                               "Not set", "", "", "linus"),

               new MTClassification("#Fulfilment", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 96,
                               "3. Nulägesbeskrivning",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 23,
                               "Upphandlingen genomförs som ett selektivt förfarande (avseende tjänster) enligt lagen (2007:1091) om offentlig upphandling (LOU). Bestämmelserna för detta upphandlingsförfarande tillåter inte att parterna genomför några förhandlingar av innehållet i lämnade anbud. Beställaren har därmed endast möjlighet att anta ett anbud som uppfyller samtliga absoluta krav (ska-krav) som är uppställda i förfrågningsunderlaget och som innehåller i förfrågningsunderlaget efterfrågad information. Notera att de kommersiella villkor som anges i Bilaga 2 (Avtalsvillkor) med Avtalsbilagor, utgör ska-krav, dvs. ska accepteras av anbudsgivare.",
                               "Not set", "", "", "linus"),

               new MTClassification("#OPTIONAL", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 25,
                               "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new MTClassification("#DEFINITION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 16,
                               "Definitioner som används i denna tjänstebeskrivning och dess bilagor har den betydelse som anges i Avtalsbilaga 1 (Definitioner).",
                               "Not set", "", "", "linus"),

               new MTClassification("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new MTClassification("#COMPENSATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 55,
                               "2.9 Ingen ersättning för anbud",
                               "Not set", "", "", "linus"),

               new MTClassification("#Fulfilment", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 11,
                               "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                               "Not set", "", "", "linus"),

               new MTClassification("#ACCEPTANCE_CRITERIA", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 12,
                               "Leverantörens beskrivning av Tjänsten kommer att utvärderas enligt kapitel 6 och inkluderas i avtalet som Avtalsbilaga 3 (Tjänste- och processbeskrivningar) och Avtalsbilaga 4 (Övertagandeprojekt). Även Leverantörens beskrivning av Samverkansmodellen ska ingå i Leverantörens anbud såsom Bilaga 6 (Samverkan och Ändringshantering) och kommer att utvärderas enligt kapitel 6.",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 46,
                               "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                               "Not set", "", "", "linus"),

               new MTClassification("#TERM", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 14,
                               "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new MTClassification("#DEFINITION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 14,
                               "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                               "Not set", "Upphandlingen", "", "linus"),

               new MTClassification("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 7,
                               "1.2 Upphandlingens omfattning",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new MTClassification("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 102,
                               "4. Krav på Leverantören",
                               "Not set", "", "", "linus"),

               new MTClassification("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 63,
                               "Som statlig myndighet omfattas Beställaren av offentlighetsprincipen. Detta innebär att anbudshandlingar, efter att tilldelningsbeslut fattats i upphandlingen, kan komma att lämnas ut till den som så begär. ",
                               "Not set", "innebär", "", "linus"),

               new MTClassification("#REGULATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 64,
                               "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                               "Not set", "", "", "linus"),

               new MTClassification("#REGULATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 64,
                               "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                               "Not set", "(2009:400)", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 102,
                               "4. Krav på Leverantören",
                               "Not set", "", "", "linus"),

               new MTClassification("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 55,
                               "2.9 Ingen ersättning för anbud",
                               "Not set", "", "", "linus"),

               new MTClassification("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 26,
                               "Svar på frågor publiceras senast sex dagar före sista anbudsdag.",
                               "Not set", "", "", "linus"),

               new MTClassification("#ADDRESS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 47,
                               "Anbud kan skickas med post till: 	{270}Alternativt kan anbud lämnas med bud till:",
                               "Not set", "", "", "linus"),

               new MTClassification("#DEADLINE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 42,
                               "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new MTClassification("#ACCEPTANCE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 59,
                               "2.11 Tilldelningsbeslut och avslutande av upphandling",
                               "Not set", "", "", "linus"),

       };

         */

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

             // Get the application to be able to set different actions

        resp.setContentType("text/html");



        resp.getWriter().println("<html>");
        resp.getWriter().println("<h1>Demo Generation</h1>");

        // Check access right

        if(!googleAuthenticate(req, resp))
            return;

        DBTimeStamp analysisTime = new DBTimeStamp();

        resp.getWriter().println("<p>deprecated</p>");


        //resp.getWriter().println(generateDemoComments(anbudsförfråganITDrift, analysisTime));


        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");

        resp.flushBuffer();



    }


}
