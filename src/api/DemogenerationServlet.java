package api;

import adminServices.GenericAdminServlet;
import analysis2.AnalysisException;
import cache.ServiceCache;
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

            new DemoComment("#ACCEPTANCE_CRITERIA", 0, 0, "Linus", "Test document.docx", 2, "", "Medium", "later chapter", "var är detta???", "itClarifies"),

    };

    private static final DemoComment[] swedishDemoCommens = {

            // Swedish demo

            new DemoComment("#RISK",        0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx", 351,
                    "Vid byte av person ska ersättaren ha minst likvärdig kompetens och godkännas av Las Vegas",
                    "Potential",   "minst likvärdig kompetens", "Subjektivt och ensidigt", "itClarifies"),
            new DemoComment("#RISK",        0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       434,
                    "Tjänster som efterfrågas i denna upphandling ska kunna avropas som pris per timma, per dag, per vecka, per månad eller som fast pris. Vilken prissättning som är bäst lämpad för det specifika uppdraget beslutas av uppdragsgivaren vid varje uppdrag. När pris begärs på annat än enbart timmar ska timpriset framgå.",
                    "Potential", "fast pris", "Fast Pris", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       369,
                    "Anbudsgivaren ska ha kapacitet att producera flera utbildningar samtidigt.",
                    "Potential",     "flera utbildningar samtidigt", "Oklar numrering", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",        63,
                    "Förfrågningsunderlaget - svarsformulär för anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",        71,
                    "Frågor rörande förfrågningsunderlaget",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",        76,
                    "Uppgifter om anbudsgivaren ",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",        90,
                    "Presentation av anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",        92,
                    "Anbudets omfattning",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       105,
                    "Anbudets språk",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       107,
                    "Undertecknat anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       132,
                    "Sista anbudsdag och sena anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       135,
                    "Anbudets och tilldelningsbeslutets rättsliga betydelse",
                    "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       138,
                    "",
                    "Not set", "", "#contracting", "itClarifies"),


            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       143,
                    "Kostnader associerade med anbudet", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       160,
                    "Avbrytande av upphandling", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       178,
                    "En bekräftelse att leverantören förfogar över underleverantör som krävs för att uppfylla de krav som gäller för det åtagande som upphandlingen omfattar och att Las Vegas vid begäran kan få intyg på detta.", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       179,
                    "Uteslutande från deltagande i upphandlingen", "Not set", "", "#contracting", "itClarifies"),
            new DemoComment("#PRE_SIGNING", 0, 0, null ,"Förfrågningsunderlag for Interaktiva utbildningar.docx",       215,
                    "Referensuppdrag, anbudsgivande företag", "Not set", "", "#contracting", "itClarifies"),

            // Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx

            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  108,
                    "Mindre fel i utbildningen, såsom stavfel, buggar, etc ska ändras av leverantören utan tillkommande kostnad.",
                    "Potential",     "utan tillkommande kostnad", "Uttrycket (etc.)", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  178,
                    "Supportärenden av teknisk och kritisk karaktär ska besvaras inom 24 timmar på vardagar. För helgdagar ska ärenden besvaras närmast följande vardag. Under 15 juni - 15 augusti kan andra svarstider överenskommas. ",
                    "Potential",   "kritisk karaktär", "Odefinierat: kritisk", "itClarifies"),

            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  184,
                    "För det fall att utbildningen hostas av leverantören ska leverantören ha rutiner avseende backup på all lagrad data",
                    "Potential",     "backup på all lagrad data", "Krav för restore saknas", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  238,
                    "För att kunna uppfylla Las Vegass behov av statistik och uppföljning ska informationen nedan lagras och tillhandahållas. Om leverantören ansvarar för hosting av utbildningen så ska informationen nedan kunna hämtas in genom den webbtjänst som tillhandahåller information om deltagarnas resultat. Om utbildningen levereras i SCORM-format ska utbildningen struktureras så att motsvarande information går att ta fram.",
                    "Potential",     "ska informationen nedan lagras", "Ej avgränsad tid", "itClarifies"),

            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  214,
                    "Vid brist i kapacitetskraven, har Las Vegas rätt till ett vite vid var tillfälle om tio (10) procent av månadsavgiften för hostingen av den aktuella utbildningen. ",
                    "Potential",   "vid var tillfälle", "Odefinierat: tillfälle", "itClarifies"),
            //new DemoComment("#AMBIGUITY",   0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  229, "", "Not set",     "besvaras", "Tvetydigt: besvaras", "itClarifies"),
            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  192,
                    "Leverantören garanterar att av Leverantören hostad interaktiv ansvarsutbildning har en tillgänglighet mätt per kalendermånad på minst 99 % under tiden måndag-söndag kl 06.00-24.00 (\"Avtalad Tillgänglighet\"). Dock gäller inte Avtalad Tillgänglighet om nertid kan härledas till Las Vegass server/it-miljö eller handhavande, d v s faktorer som Leverantören inte har kontroll över, eller om Las Vegas och Leverantören kommit överens om planerad nertid.",
                    "Potential",     "kan härledas till Las Vegass server/it-miljö eller handhavande", "Odefinierat : Responsibility", "itClarifies"),


            //TODO: Find this in the document

            new DemoComment("#UNSPECIFIC",  0, 0, null ,"Bilaga 3A - Ramavtal Interaktiva utbildningar.docx",            41, "", "Not set",     "avser samtliga de resultat", "Ej avgränsad", "itClarifies"),



    };

    /***********************************************************
        *
        *      Regeneration of risk, comment and annotation
        *         Project:  EHM-2
        *
        */
       private static final DemoComment[] anbudsförfråganITDrift = {

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new DemoComment("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 11,
                               "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                               "Not set", "", "", "linus"),

               new DemoComment("#TERM", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 42,
                               "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#RESTRICTION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 61,
                               "Genom att lämna anbud förbinder sig anbudsgivare att, i händelse av att anbudsgivaren tilldelas avtal, underteckna och återsända av Beställaren översänt avtal inom fem dagar från mottagande av avtalet. Avtalet är bindande för Beställaren först när Beställaren undertecknat detta.",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 25,
                               "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new DemoComment("#ACCEPTANCE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 108,
                               "6. Prövning och utvärdering",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 59,
                               "2.11 Tilldelningsbeslut och avslutande av upphandling",
                               "Not set", "", "", "linus"),

               new DemoComment("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 105,
                               "5. Krav på Tjänsten",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 105,
                               "5. Krav på Tjänsten",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 46,
                               "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                               "Not set", "", "", "linus"),

               new DemoComment("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 96,
                               "3. Nulägesbeskrivning",
                               "Not set", "", "", "linus"),

               new DemoComment("#Fulfilment", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 96,
                               "3. Nulägesbeskrivning",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 23,
                               "Upphandlingen genomförs som ett selektivt förfarande (avseende tjänster) enligt lagen (2007:1091) om offentlig upphandling (LOU). Bestämmelserna för detta upphandlingsförfarande tillåter inte att parterna genomför några förhandlingar av innehållet i lämnade anbud. Beställaren har därmed endast möjlighet att anta ett anbud som uppfyller samtliga absoluta krav (ska-krav) som är uppställda i förfrågningsunderlaget och som innehåller i förfrågningsunderlaget efterfrågad information. Notera att de kommersiella villkor som anges i Bilaga 2 (Avtalsvillkor) med Avtalsbilagor, utgör ska-krav, dvs. ska accepteras av anbudsgivare.",
                               "Not set", "", "", "linus"),

               new DemoComment("#OPTIONAL", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 28,
                               "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 25,
                               "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 44,
                               "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                               "Not set", "", "", "linus"),

               new DemoComment("#DEFINITION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 16,
                               "Definitioner som används i denna tjänstebeskrivning och dess bilagor har den betydelse som anges i Avtalsbilaga 1 (Definitioner).",
                               "Not set", "", "", "linus"),

               new DemoComment("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new DemoComment("#COMPENSATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 55,
                               "2.9 Ingen ersättning för anbud",
                               "Not set", "", "", "linus"),

               new DemoComment("#Fulfilment", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 11,
                               "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                               "Not set", "", "", "linus"),

               new DemoComment("#ACCEPTANCE_CRITERIA", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 12,
                               "Leverantörens beskrivning av Tjänsten kommer att utvärderas enligt kapitel 6 och inkluderas i avtalet som Avtalsbilaga 3 (Tjänste- och processbeskrivningar) och Avtalsbilaga 4 (Övertagandeprojekt). Även Leverantörens beskrivning av Samverkansmodellen ska ingå i Leverantörens anbud såsom Bilaga 6 (Samverkan och Ändringshantering) och kommer att utvärderas enligt kapitel 6.",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 46,
                               "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                               "Not set", "", "", "linus"),

               new DemoComment("#TERM", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 14,
                               "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new DemoComment("#DEFINITION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 14,
                               "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                               "Not set", "Upphandlingen", "", "linus"),

               new DemoComment("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 7,
                               "1.2 Upphandlingens omfattning",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 53,
                               "2.8 Svenska språket",
                               "Not set", "", "", "linus"),

               new DemoComment("#REQUIREMENT", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 102,
                               "4. Krav på Leverantören",
                               "Not set", "", "", "linus"),

               new DemoComment("#SCOPE_AND_GOALS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 63,
                               "Som statlig myndighet omfattas Beställaren av offentlighetsprincipen. Detta innebär att anbudshandlingar, efter att tilldelningsbeslut fattats i upphandlingen, kan komma att lämnas ut till den som så begär. ",
                               "Not set", "innebär", "", "linus"),

               new DemoComment("#REGULATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 64,
                               "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                               "Not set", "", "", "linus"),

               new DemoComment("#REGULATION", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 64,
                               "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                               "Not set", "(2009:400)", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 102,
                               "4. Krav på Leverantören",
                               "Not set", "", "", "linus"),

               new DemoComment("#MUST", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 29,
                               "2.4 Anbudets innehåll",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 55,
                               "2.9 Ingen ersättning för anbud",
                               "Not set", "", "", "linus"),

               new DemoComment("#PRE-SIGNING", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 26,
                               "Svar på frågor publiceras senast sex dagar före sista anbudsdag.",
                               "Not set", "", "", "linus"),

               new DemoComment("#ADDRESS", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 47,
                               "Anbud kan skickas med post till: 	{270}Alternativt kan anbud lämnas med bud till:",
                               "Not set", "", "", "linus"),

               new DemoComment("#DEADLINE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 42,
                               "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#DELIVERABLES", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 9,
                               "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                               "Not set", "", "", "linus"),

               new DemoComment("#ACCEPTANCE", 0, 0, "EHM-3", "Anbudsförfrågan IT-drift 2014.docx", 59,
                               "2.11 Tilldelningsbeslut och avslutande av upphandling",
                               "Not set", "", "", "linus"),

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

        resp.getWriter().println(generateDemoComments(anbudsförfråganITDrift, analysisTime));


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

        Project project = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), comment.project)));
        if(!project.exists()){

            feedback.append("<br/> - ! Project " + comment.project + " does not exist! </p>");
            return feedback.toString();

        }

        String filename = comment.document;

        try {

            filename = new String (filename.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {

            // Swallow this
        }

        ConditionInterface lookupDocumentCondition = new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.File.name(), filename));

        // There may be an optional project name filter on it

        if(comment.project != null)
            lookupDocumentCondition.addFilter(new ReferenceFilter(ContractTable.Columns.Project.name(), project.getKey()));

        Contract document = new Contract(lookupDocumentCondition);
        if(!document.exists()){

            feedback.append("<br/> - ! Document " + filename + " does not exist! int project "+comment.project+"!</p>");

            List<Contract> availableDocuments = project.getContractsForProject();

            feedback.append("<br/> - Available documents: ");

            for (Contract availableDocument : availableDocuments) {

                feedback.append("<li>" + availableDocument.getFile() + " </li>");

            }


            return feedback.toString();

        }

        Organization organization = document.getProject().getOrganization();
        feedback.append("<br/> - Organization: " + organization.getName());


        ContractVersionInstance version = document.getHeadVersion();


        ContractFragment fragment = locateFragment(comment, version);

        if(!fragment.exists()){

            feedback.append("<br/> - ! Fragment id " + comment.ordinal + " does not exist! </p>");
            return feedback.toString();

        }

        LanguageCode documentLanguage = new LanguageCode(document.getLanguage());
        LanguageInterface languageForDocument = null;
        try {
            languageForDocument = new LanguageAnalyser().getLanguage(documentLanguage);
        } catch (AnalysisException e) {

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Could not get language for docuemtn. Using english as default");
            languageForDocument = defaultLanguage;
        }

        // Lookup the tag. Either in the static tree or custom tags in the database

        String classTag = getTag(comment.classification, organization, languageForDocument);
        String tagName = getTagName(comment.classification, organization, languageForDocument);
        FeatureTypeInterface featureType = getFeatureType(comment.classification, languageForDocument);

        String keyword = "";

        if(featureType != null)
          keyword = featureType.createKeywordString(keyword);



        int patternPos = fragment.getText().indexOf(comment.pattern);

        // Get all existing classifications for the


        // Now create annotation, classification and risk


        FragmentClassification classification = new FragmentClassification(
                fragment.getKey(),
                comment.classification,
                comment.requirementLevel,
                comment.applicablePhase,
                comment.comment,
                keyword,
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

        if(comment.comment != null && !comment.comment.equals("")){

        ContractAnnotation annotation = new ContractAnnotation(
                "demo",
                fragment.getKey(),
                ordinal,
                comment.comment,
                user.getKey(),
                version.getKey(),
                comment.pattern,
                0,                          //TODO: Anchor position not implemented
                analysisTime.getISODate());

            annotation.store();
            fragment.setAnnotationCount(ordinal);
            feedback.append("<br/> - Adding annotation \""+ comment.comment + "\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName());
        }

        classification.store();
        fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
        feedback.append("<br/> - Adding classification \""+ classification.getClassTag() + "\" to fragment no "+ fragment.getOrdinal()+" in document " + document.getName() + " in project " + comment.project);


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
        private String project;
        private final String document;
        private final int ordinal;
        private String fragment;
        private final String riskLevel;
        private final String pattern;
        private final String comment;
        private final String user;




        public DemoComment(String classification, int requirementLevel, int applicablePhase, String project, String document, int ordinal, String fragment, String riskLevel, String pattern, String comment, String user) {

            this.classification = classification;
            this.requirementLevel = requirementLevel;
            this.applicablePhase = applicablePhase;
            this.project = project;
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


    /***************************************************************************
     *
     *          looking up the tag from both the classification tree and custom tags in the database
     *
     *
     *
     * @param className
     * @param organization
     * @param language         -document language
     * @return
     *
     *
     *              TODO: These are copied from DocumentService due to different inheritance trees. Refactor to reuse
     */

    protected String getTag(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return className;
            }
        }


            // Look in the database for custom tags

        List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();
        try {
            customClasses.addAll(Organization.getnone().getCustomTagsForOrganization());
        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring global classifications");
        }

        for (FragmentClass customClass : customClasses) {

            if(customClass.getType().equals(className)){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                return customClass.getKey().toString();
            }
        }

        return null;
    }

    protected FeatureTypeInterface getFeatureType(String className, LanguageInterface languageForDocument) {



        ClassifierInterface[] classifiers = languageForDocument.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getType();
            }
        }

        return null;
    }



    protected String getTagName(String className, Organization organization, LanguageInterface language) {

        //String classTag = languageInterface.getClassificationForName(className);

        ClassifierInterface[] classifiers = language.getSupportedClassifiers();

        for (ClassifierInterface classifier : classifiers) {

            if(classifier.getType().getName().equals(className)){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Found static classTag " + className);
                return classifier.getClassificationName();
            }
        }


            // Look in the database for custom tags

        List<FragmentClass> customClasses = organization.getCustomTagsForOrganization();
        try {
            customClasses.addAll(Organization.getnone().getCustomTagsForOrganization());
        } catch (BackOfficeException e) {

            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring global classifications");
        }

        for (FragmentClass customClass : customClasses) {

            if(customClass.getKey().toString().equals(className)){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Found custom classTag " + customClass.getName());
                return customClass.getName();
            }
        }

        return null;
    }




}
