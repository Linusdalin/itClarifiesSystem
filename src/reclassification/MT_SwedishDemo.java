package reclassification;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-03
 * Time: 07:36
 * To change this template use File | Settings | File Templates.
 */
public class MT_SwedishDemo extends MTDocument implements MechanicalTurkInterface {

    private static final String document = "Swedish Demo";

    private static final MTClassification[] classifications = {

            // Swedish demo

            new MTClassification("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx", 351,
                    "Vid byte av person ska ersättaren ha minst likvärdig kompetens och godkännas av Las Vegas",
                    "Potential",   "minst likvärdig kompetens", "Subjektivt och ensidigt", "itClarifies"),
            new MTClassification("#RISK",        0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       434,
                    "Tjänster som efterfrågas i denna upphandling ska kunna avropas som pris per timma, per dag, per vecka, per månad eller som fast pris. Vilken prissättning som är bäst lämpad för det specifika uppdraget beslutas av uppdragsgivaren vid varje uppdrag. När pris begärs på annat än enbart timmar ska timpriset framgå.",
                    "Potential", "fast pris", "Fast Pris", "itClarifies"),
            new MTClassification("#UNSPECIFIC",  0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       369,
                    "Anbudsgivaren ska ha kapacitet att producera flera utbildningar samtidigt.",
                    "Potential",     "flera utbildningar samtidigt", "Oklar numrering", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        63,
                    "Förfrågningsunderlaget - svarsformulär för anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        71,
                    "Frågor rörande förfrågningsunderlaget",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        76,
                    "Uppgifter om anbudsgivaren ",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        90,
                    "Presentation av anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",        92,
                    "Anbudets omfattning",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       105,
                    "Anbudets språk",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       107,
                    "Undertecknat anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       132,
                    "Sista anbudsdag och sena anbud",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       135,
                    "Anbudets och tilldelningsbeslutets rättsliga betydelse",
                    "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       138,
                    "",
                    "Not set", "", "#contracting", "itClarifies"),


            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       143,
                    "Kostnader associerade med anbudet", "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       160,
                    "Avbrytande av upphandling", "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       178,
                    "En bekräftelse att leverantören förfogar över underleverantör som krävs för att uppfylla de krav som gäller för det åtagande som upphandlingen omfattar och att Las Vegas vid begäran kan få intyg på detta.", "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       179,
                    "Uteslutande från deltagande i upphandlingen", "Not set", "", "#contracting", "itClarifies"),
            new MTClassification("#PRE_SIGNING", 0, 0, "Förfrågningsunderlag for Interaktiva utbildningar.docx",       215,
                    "Referensuppdrag, anbudsgivande företag", "Not set", "", "#contracting", "itClarifies"),

            // Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx

            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  108,
                    "Mindre fel i utbildningen, såsom stavfel, buggar, etc ska ändras av leverantören utan tillkommande kostnad.",
                    "Potential",     "utan tillkommande kostnad", "Uttrycket (etc.)", "itClarifies"),
            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  178,
                    "Supportärenden av teknisk och kritisk karaktär ska besvaras inom 24 timmar på vardagar. För helgdagar ska ärenden besvaras närmast följande vardag. Under 15 juni - 15 augusti kan andra svarstider överenskommas. ",
                    "Potential",   "kritisk karaktär", "Odefinierat: kritisk", "itClarifies"),

            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  184,
                    "För det fall att utbildningen hostas av leverantören ska leverantören ha rutiner avseende backup på all lagrad data",
                    "Potential",     "backup på all lagrad data", "Krav för restore saknas", "itClarifies"),
            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  238,
                    "För att kunna uppfylla Las Vegass behov av statistik och uppföljning ska informationen nedan lagras och tillhandahållas. Om leverantören ansvarar för hosting av utbildningen så ska informationen nedan kunna hämtas in genom den webbtjänst som tillhandahåller information om deltagarnas resultat. Om utbildningen levereras i SCORM-format ska utbildningen struktureras så att motsvarande information går att ta fram.",
                    "Potential",     "ska informationen nedan lagras", "Ej avgränsad tid", "itClarifies"),

            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  214,
                    "Vid brist i kapacitetskraven, har Las Vegas rätt till ett vite vid var tillfälle om tio (10) procent av månadsavgiften för hostingen av den aktuella utbildningen. ",
                    "Potential",   "vid var tillfälle", "Odefinierat: tillfälle", "itClarifies"),
            //new DemoComment("#AMBIGUITY",   0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  229, "", "Not set",     "besvaras", "Tvetydigt: besvaras", "itClarifies"),
            new MTClassification("#UNSPECIFIC",  0, 0, "Bilaga 1 - Kravspecifikation webbutbildning,2014-04-28.docx",  192,
                    "Leverantören garanterar att av Leverantören hostad interaktiv ansvarsutbildning har en tillgänglighet mätt per kalendermånad på minst 99 % under tiden måndag-söndag kl 06.00-24.00 (\"Avtalad Tillgänglighet\"). Dock gäller inte Avtalad Tillgänglighet om nertid kan härledas till Las Vegass server/it-miljö eller handhavande, d v s faktorer som Leverantören inte har kontroll över, eller om Las Vegas och Leverantören kommit överens om planerad nertid.",
                    "Potential",     "kan härledas till Las Vegass server/it-miljö eller handhavande", "Odefinierat : Responsibility", "itClarifies"),


            };

    MT_SwedishDemo(){

        super(document, classifications);
    }
}
