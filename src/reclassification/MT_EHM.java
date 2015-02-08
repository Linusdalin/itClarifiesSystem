package reclassification;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-03
 * Time: 07:36
 * To change this template use File | Settings | File Templates.
 */
public class MT_EHM extends MTDocument implements MechanicalTurkInterface {

    private static final String document = "EHM Project";

    private static final MTClassification[] classifications = {

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 44,
                            "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                            "Not set", "", "", "linus"),

            new MTClassification("#REQUIREMENT", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 11,
                            "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                            "Not set", "", "", "linus"),

            new MTClassification("#TERM", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 44,
                            "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 42,
                            "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#RESTRICTION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 61,
                            "Genom att lämna anbud förbinder sig anbudsgivare att, i händelse av att anbudsgivaren tilldelas avtal, underteckna och återsända av Beställaren översänt avtal inom fem dagar från mottagande av avtalet. Avtalet är bindande för Beställaren först när Beställaren undertecknat detta.",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 25,
                            "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 28,
                            "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 29,
                            "2.4 Anbudets innehåll",
                            "Not set", "", "", "linus"),

            new MTClassification("#ACCEPTANCE", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 108,
                            "6. Prövning och utvärdering",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 59,
                            "2.11 Tilldelningsbeslut och avslutande av upphandling",
                            "Not set", "", "", "linus"),

            new MTClassification("#SCOPE_AND_GOALS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 9,
                            "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#REQUIREMENT", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 105,
                            "5. Krav på Tjänsten",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 105,
                            "5. Krav på Tjänsten",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 46,
                            "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                            "Not set", "", "", "linus"),

            new MTClassification("#SCOPE_AND_GOALS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 96,
                            "3. Nulägesbeskrivning",
                            "Not set", "", "", "linus"),

            new MTClassification("#Fulfilment", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 96,
                            "3. Nulägesbeskrivning",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 23,
                            "Upphandlingen genomförs som ett selektivt förfarande (avseende tjänster) enligt lagen (2007:1091) om offentlig upphandling (LOU). Bestämmelserna för detta upphandlingsförfarande tillåter inte att parterna genomför några förhandlingar av innehållet i lämnade anbud. Beställaren har därmed endast möjlighet att anta ett anbud som uppfyller samtliga absoluta krav (ska-krav) som är uppställda i förfrågningsunderlaget och som innehåller i förfrågningsunderlaget efterfrågad information. Notera att de kommersiella villkor som anges i Bilaga 2 (Avtalsvillkor) med Avtalsbilagor, utgör ska-krav, dvs. ska accepteras av anbudsgivare.",
                            "Not set", "", "", "linus"),

            new MTClassification("#OPTIONAL", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 53,
                            "2.8 Svenska språket",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 28,
                            "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 9,
                            "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 28,
                            "Detta dokument är utformat som ett anbudsformulär, i vilket en del av de uppgifter som Beställaren efterfrågar kan lämnas. Övrig efterfrågad information ska lämnas i en eller flera bilagor. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 25,
                            "Eventuella frågor avseende upphandlingen ska ställas skriftligen till itdrift2014@ehalsomyndigheten.se senast tio dagar innan anbudstidens utgång, för att Beställaren ska kunna garantera att svar kan lämnas. Endast skriftliga frågor kommer att besvaras och endast skriftliga svar är bindande för Beställaren. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 44,
                            "Anbud ska vara giltigt i nio månader från och med anbudstidens utgång.",
                            "Not set", "", "", "linus"),

            new MTClassification("#DEFINITION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 16,
                            "Definitioner som används i denna tjänstebeskrivning och dess bilagor har den betydelse som anges i Avtalsbilaga 1 (Definitioner).",
                            "Not set", "", "", "linus"),

            new MTClassification("#SCOPE_AND_GOALS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 29,
                            "2.4 Anbudets innehåll",
                            "Not set", "", "", "linus"),

            new MTClassification("#COMPENSATION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 55,
                            "2.9 Ingen ersättning för anbud",
                            "Not set", "", "", "linus"),

            new MTClassification("#Fulfilment", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 11,
                            "För krav på Tjänsten hänvisas till Avtalsbilaga 2 (EHM:s krav på Tjänsten). ",
                            "Not set", "", "", "linus"),

            new MTClassification("#ACCEPTANCE_CRITERIA", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 12,
                            "Leverantörens beskrivning av Tjänsten kommer att utvärderas enligt kapitel 6 och inkluderas i avtalet som Avtalsbilaga 3 (Tjänste- och processbeskrivningar) och Avtalsbilaga 4 (Övertagandeprojekt). Även Leverantörens beskrivning av Samverkansmodellen ska ingå i Leverantörens anbud såsom Bilaga 6 (Samverkan och Ändringshantering) och kommer att utvärderas enligt kapitel 6.",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 29,
                            "2.4 Anbudets innehåll",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 53,
                            "2.8 Svenska språket",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 46,
                            "Anbud lämnas in i digital version på USB-minne, dvs. ej i pappersform i anonymt slutet kuvert märkt \"Ansökan - IT driftupphandling\" och insändes till:",
                            "Not set", "", "", "linus"),

            new MTClassification("#TERM", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 14,
                            "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 53,
                            "2.8 Svenska språket",
                            "Not set", "", "", "linus"),

            new MTClassification("#DEFINITION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 14,
                            "Upphandlingen avser en avtalstid på fyra år med möjlighet för Beställaren att begära förlängning i ytterligare som längst fyra år, med två år i taget. Avtalet kan således maximalt löpa i totalt åtta år.  ",
                            "Not set", "Upphandlingen", "", "linus"),

            new MTClassification("#SCOPE_AND_GOALS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 7,
                            "1.2 Upphandlingens omfattning",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 53,
                            "2.8 Svenska språket",
                            "Not set", "", "", "linus"),

            new MTClassification("#REQUIREMENT", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 102,
                            "4. Krav på Leverantören",
                            "Not set", "", "", "linus"),

            new MTClassification("#SCOPE_AND_GOALS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 63,
                            "Som statlig myndighet omfattas Beställaren av offentlighetsprincipen. Detta innebär att anbudshandlingar, efter att tilldelningsbeslut fattats i upphandlingen, kan komma att lämnas ut till den som så begär. ",
                            "Not set", "innebär", "", "linus"),

            new MTClassification("#REGULATION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 64,
                            "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                            "Not set", "", "", "linus"),

            new MTClassification("#REGULATION", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 64,
                            "Under upphandlingen gäller s.k. absolut sekretess enligt 19 kap. 3 § andra stycket offentlighets- och sekretesslagen (2009:400) fram till dess att tilldelningsbeslut har fattats. För tid därefter kan delar av ansökningar eller anbud omfattas av sekretess enligt andra bestämmelser i offentlighets- och sekretesslagen. Huvudregeln är dock att alla handlingar efter fattat tilldelningsbeslut blir att betrakta som allmänna offentliga handlingar.",
                            "Not set", "(2009:400)", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 102,
                            "4. Krav på Leverantören",
                            "Not set", "", "", "linus"),

            new MTClassification("#MUST", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 29,
                            "2.4 Anbudets innehåll",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 55,
                            "2.9 Ingen ersättning för anbud",
                            "Not set", "", "", "linus"),

            new MTClassification("#PRE-SIGNING", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 26,
                            "Svar på frågor publiceras senast sex dagar före sista anbudsdag.",
                            "Not set", "", "", "linus"),

            new MTClassification("#ADDRESS", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 47,
                            "Anbud kan skickas med post till: 	{270}Alternativt kan anbud lämnas med bud till:",
                            "Not set", "", "", "linus"),

            new MTClassification("#DEADLINE", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 42,
                            "Anbudet ska vara Beställaren tillhanda senast 2014-09-10. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#DELIVERABLES", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 9,
                            "Tjänsten ska utföras på ett sådant sätt att EHM får en säker, samordnad och ändamålsenlig leverans. Tjänsten ska vara skalbar, tillgänglig och ha hög prestanda. Tjänsten och Leverantörens agerande under Avtalet ska präglas av proaktivitet och Leverantören ska arbeta med ständiga förbättringar för att säkerställa en kostnadseffektiv och driftsäker leverans. Leverantören förväntas genom samarbete med EHM få en god förståelse för EHM:s verksamheter. ",
                            "Not set", "", "", "linus"),

            new MTClassification("#ACCEPTANCE", 0, 0, "Anbudsförfrågan IT-drift 2014.docx", 59,
                            "2.11 Tilldelningsbeslut och avslutande av upphandling",
                            "Not set", "", "", "linus"),

            

            //Huvudavtal

   


            new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 22,
                               "2.7 Leverantören äger för tillhandahållande av Tjänsten vederlagsfritt nyttja EHM:s befintliga driftsmiljö såsom beskriven i Bilaga 2a samt fysiskt flytta denna i enlighet med vad som anges i Bilaga 4 (Övertagandeprojektet). EHM:s befintliga driftmiljö tillhandahålls Leverantören i befintligt skick utan några som helst utfästelser från EHM angående skick eller funktion eller andra egenskaper. Således kan eventuella brister i EHM:s befintliga driftmiljö inte befria Leverantören från ansvar under Avtalet. EHM:s befintliga driftmiljö ska återlämnas till EHM vid Avtalets upphörande eller dessförinnan i de fall och i den utsträckning som Leverantören använder annan utrustning för produktion och tillhandahållande av Tjänsten eller del därav.",
                               "Not set", "", "", "linus"),
   
              new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 115,
                            "10.2.1 Leverantören äger efter skriftligt medgivande av EHM rätt att ersätta i Anbudet redovisad personal samt personal namngiven under avtalsperioden med annan person och rätt att, om avtalat åtagande så kräver, lägga till ny person. Som underlag för EHM:s ställningstagande ska Leverantören överlämna redovisning som visar att aktuell person uppfyller tillämpliga krav. Vidare ska motivet för önskad förändring anges.",
                            "Not set", "", "", "linus"),

               new MTClassification("#SOLUTION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 10,
                               "2. Syfte med och grundläggande förutsättningar och principer för Avtalet",
                               "Not set", "", "", "linus"),
   



               new MTClassification("#DELIVERABLES", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 73,
                               "6.5 Leverantören ska fortlöpande tillhandahålla konkreta förslag till förbättringar och effektiviseringar. Baserat på Leverantörens förslag ska Parterna gemensamt definiera förbättringsprojekt för att effektivisera Tjänsten.",
                               "Not set", "", "", "linus"),
   
               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 252,
                               "21.4 Revision ska ske under Leverantörens kontorstid. Revision ska påkallas senast fem (5) Arbetsdagar före planerat genomförande. Revision av Leverantören kan även påkallas och utföras av myndighet som utövar tillsyn över den verksamhet som bedrivs av EHM, varvid begränsningen ovan inte ska äga tillämpning, utan Leverantören ska medverka fullt ut vid revision enligt myndighetens krav. Vid Incidenter som innebär att samhällskritiska funktioner störs i väsentlig mån, kan revision påkallas utan de begräsningar som anges ovan.",
                               "Not set", "", "", "linus"),


               new MTClassification("#LEGAL_ENTITY", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 8,
                               "1.2 [LEVERANTÖREN AB], org. nr. [XXXXXX-XXXX], [Postnummer] [Postadress] nedan kallat \"Leverantören\".",
                               "Not set", "", "", "linus"),
   
               new MTClassification("#SOULUTION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 59,
                               "6. Avtalets omfattning och Tjänsten",
                               "Not set", "", "", "linus"),
   


               new MTClassification("#PERCENTAGE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 282,
                               "(50) % av ansvarsbegränsningen i punkten 22.1 ovan och under förutsättning att Leverantören inte accepterar att nollställa den avtalade ansvarsbegränsningen enligt punkt 22.1 ovan; eller",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#CHANGE_MGMNT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 75,
                               "6.7 För det fall Leverantören anser att viss aktivitet, funktion eller annan åtgärd inte omfattas av Tjänsten utan istället utgör en Konsulttjänst eller annan åtgärd och för vilken Leverantören anser sig berättigad till ersättning utöver vad som anges i Bilaga 8 (Ersättning och Priser), åligger det Leverantören att snarast - och senast inom tio (10) kalenderdagar från den tidpunkt då Leverantören uppmärksammade eller borde ha uppmärksammat behovet av den aktuella aktiviteten, funktionen eller åtgärden - skriftligen meddela EHM sin uppfattning. En eventuell förändring av Tjänsten, om behov av sådan förändring konstateras föreligga, ska överenskommas genom processen",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 174,
                               "ii) Leverantören ska avhjälpa Fel enligt punkt 14.1, EHM äger rätt att i skälig utsträckning närvara vid och observera Leverantörens felavhjälpande;",
                               "Not set", "", "", "linus"),
   

            new MTClassification("#RESTRICTION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 74,
                            "6.6 Parterna är införstådda med att Avtalet utgör en grundförutsättning för att EHM ska kunna uppfylla sitt lagstadgade ansvar samt att detta ansvar inte är möjligt att överflytta på annan. Mot den bakgrunden avstår Leverantören under avtalstiden från att innehålla eller begränsa någon del av sina åtaganden eller prestationer under Avtalet, oavsett skäl, om detta skulle kunna medföra att EHM skulle komma att brista i sitt ansvar.",
                            "Not set", "", "", "linus"),


               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 176,
                               "iv) EHM har, från och med tidpunkten för Felet/avtalsbrottet, rätt till avdrag på ersättning för de åtaganden som berörs av Felet/avtalsbrottet i den utsträckning som svarar mot Felet/avtalsbrottet alternativt i Avtalet särskilt angivet belopp;",
                               "Not set", "", "", "linus"),
   
               new MTClassification("ForceMajeur", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 262,
                               "23. Force Majeure",
                               "Not set", "", "", "linus"),
   


               new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 178,
                               "vi) EHM äger rätt att innehålla betalning till Leverantören så länge försening, Fel eller annat avtalsbrott består med belopp som med hänsyn till förekommande försening, Fel, eller avtalsbrott är skäligt; samt",
                               "Not set", "", "", "linus"),
   
               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 175,
                               "iii) Vid andra avtalsbrott än Fel gäller att Part kostnadsfritt ska vidta rättelse utan dröjsmål efter att avtalsbrottet uppmärksammats av Parten eller att avtalsbrottet påtalats av den andre Parten;",
                               "Potential", "", "", "linus"),
   

               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 75,
                               "6.7 För det fall Leverantören anser att viss aktivitet, funktion eller annan åtgärd inte omfattas av Tjänsten utan istället utgör en Konsulttjänst eller annan åtgärd och för vilken Leverantören anser sig berättigad till ersättning utöver vad som anges i Bilaga 8 (Ersättning och Priser), åligger det Leverantören att snarast - och senast inom tio (10) kalenderdagar från den tidpunkt då Leverantören uppmärksammade eller borde ha uppmärksammat behovet av den aktuella aktiviteten, funktionen eller åtgärden - skriftligen meddela EHM sin uppfattning. En eventuell förändring av Tjänsten, om behov av sådan förändring konstateras föreligga, ska överenskommas genom processen",
                               "Not set", "", "", "linus"),
   
            new MTClassification("#GOVERNANCE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 99,
                            "vii) avsätta erforderliga och adekvata resurser för uthålligt fungerande samverkan med Leverantören i enlighet med punkt 9 nedan; samt",
                            "Not set", "", "", "linus"),

               new MTClassification("#GOVERNANCE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 101,
                               "9. Parternas samverkan",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#DEFINITION_SOURCE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 7,
                               "1.1 E-hälsomyndigheten, org. nr. 202100-6552, Ringvägen 100, 118 60 Stockholm, nedan kallat \"EHM\"; och",
                               "Not set", "", "", "linus"),

               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 243,
                               "19.4 Leverantören ska kostnadsfritt upprätta, följa och vidmakthålla en kontinuitetplan (Disaster Recovery Plan) för Tjänsten. Kontinuitetsplanen ska överlämnas till EHM för godkännande senast tre (3) månader efter Effektiv Övertagandedag och minst uppfylla kraven i Bilaga 2 (EHM:s krav på Tjänsten) och Bilaga 3 (Tjänste-och processbeskrivningar). I den mån EHM gör befogad anmärkning på innehållet ska Leverantören snarast justera innehållet. Leverantören ska under avtalstiden föreslå de förändringar av kontinuitetsplanen i syfte att upprätthålla en god kontinuitet för Tjänsten Den av EHM godkända kontinuitetsplanen utgör en del av Dokumentationen. Om Parterna inte kan enas om innehållet kan frågan hänskjutas av endera Parten för omedelbar behandling enligt Samverkansmodellen.",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 247,
                               "20.3 Leverantören ska på egen bekostnad ombesörja, införskaffa och vidmakthålla samtliga tillstånd, bemyndiganden, dispenser och licenser som enligt tillämpliga författningar, normer eller föreskrifter erfordras för att Leverantören ska kunna utföra sina åtaganden under Avtalet.",
                               "Not set", "", "", "linus"),
   
               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 285,
                               "(10) dagars mellanrum fortfarande inte har betalat inom nittio (90) dagar från datum för den ursprungliga fakturan.",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 254,
                               "21.6 Vardera Part svarar för sina kostnader i anledning av revision, dock att Leverantören har rätt till skälig ersättning för nedlagd tid i samband med revisionen. Om revision utvisar att Leverantören i icke-oväsentlig mån har brutit mot sina åtaganden enligt Avtalet ska Leverantören ersätta EHM dess kostnader i samband med genomförande av revisionen (inklusive eventuellt utbetald ersättning till Leverantören för nedlagd tid i samband med revisionen) och EHM:s skada p.g.a. Leverantörens avtalsbrott.",
                               "Not set", "", "", "linus"),
   



               new MTClassification("#COMPENSATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 283,
                               "24.3 Uppsägning enligt punkten 24.1 ovan ska inte medföra några kostnader eller andra påföljder för EHM. Vidare gäller att Leverantören är skyldig att informera EHM om sådana omständigheter som har betydelse för EHM vid bedömning av punkten 24.1 ovan.",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 273,
                               "ii) Leverantören väsentligen bryter mot sina åtaganden enligt Avtalet och inte inom trettio (30) dagar efter skriftlig anmodan, ställd till Leverantören med hänvisning till denna punkt, har vidtagit rättelse;",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#DEFINITION_SOURCE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 8,
                               "1.2 [LEVERANTÖREN AB], org. nr. [XXXXXX-XXXX], [Postnummer] [Postadress] nedan kallat \"Leverantören\".",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 176,
                               "iv) EHM har, från och med tidpunkten för Felet/avtalsbrottet, rätt till avdrag på ersättning för de åtaganden som berörs av Felet/avtalsbrottet i den utsträckning som svarar mot Felet/avtalsbrottet alternativt i Avtalet särskilt angivet belopp;",
                               "Not set", "", "", "linus"),


               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 57,
                               "5.4 EHM ska skriftligen meddela Leverantören om en eventuell förlängning senast sex (6) månader innan respektive kontraktsperiods utgång.",
                               "Not set", "", "", "linus"),
   


               new MTClassification("#RESTRICTION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 287,
                               "24.5 Till förtydligande anges att Leverantören inte under några omständigheter har rätt att säga upp Avtalet p.g.a. EHM:s bristande betalning i fall där EHM enligt punkten 14.2.1vi) ovan innehåller betalning till Leverantören.",
                               "Not set", "", "", "linus"),
   

               new MTClassification("#RIGHT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 271,
                               "24.2 EHM har rätt att säga upp Avtalet helt eller delvis med den uppsägningstid som EHM anger (mellan noll (0) till tjugofyra (24) månader), om:",
                               "Not set", "", "", "linus"),
   
               new MTClassification("#DEADLINE", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 258,
                               "22.2 Parts skadeståndsansvar ska per tolvmånadersperiod vara begränsat till direkta skador och förluster till ett sammanlagt maximalt belopp motsvarande den totala ersättningen enligt Avtalet under närmast föregående tolvmånadersperiod. Om inte Avtalet varat under en tolvmånadersperiod vid skadetillfället är ansvaret begränsat till en uppskattad ersättning under Avtalets första tolv (12) månader. Parts skadeståndsansvar omfattar inte indirekta skador och förluster såsom utebliven vinst, utebliven förväntad besparing eller förlorad goodwill. Utbetalda viten och dröjsmålsränta inkluderas inte i angivna belopp.",
                               "Potential", "", "", "linus"),
   



               new MTClassification("#ARBITRATION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 215,
                               "18. Rättigheter och rättighetsintrång",
                               "Not set", "", "", "linus"),
   


            new MTClassification("#CHANGE_MGMNT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 76,
                            "för Ändringshantering. Om Leverantören utför en viss tjänst, aktivitet, funktion eller annan åtgärd utan att ha lämnat meddelande till EHM enligt denna punkt ska åtgärden i fråga alltid anses som en del av Tjänsten och omfattas av och ingå i avtalad ersättning.",
                            "Not set", "", "", "linus"),


            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 77,
                            "7. Några av Leverantörens särskilda åtaganden",
                            "Not set", "", "", "linus"),
               new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 59,
                               "6. Avtalets omfattning och Tjänsten",
                               "Not set", "", "", "linus"),


            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 108,
                            "10.1.1 Leverantören åtar sig att för fullgörande av avtalat åtagande använda erforderligt antal kvalificerade och lämpliga personer som uppfyller de krav som ställs i Avtalet. Således",
                            "Not set", "", "", "linus"),

               new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 168,
                               "14.1.3 Leverantören garanterar att avvikelser från Servicenivåerna avhjälps och att övriga Fel och avtalsbrott identifieras och avhjälps med den skyndsamhet som omständigheterna kräver. Sådan identifiering och avhjälpande av nämnda Fel ingår i ersättningen för Tjänsten och ska tillhandahållas utan extra ersättning.",
                               "Not set", "", "", "linus"),

            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 239,
                            "19. Säkerhet och kontinuitetsplan",
                            "Not set", "", "", "linus"),


               new MTClassification("#DELAY", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 163,
                               "14. Försening, Fel och avtalsbrott i övrigt",
                               "Not set", "", "", "linus"),

            new MTClassification("#PRECONDITION", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 91,
                         "8. Om EHM:s åtaganden",
                         "Not set", "", "", "linus"),


            new MTClassification("#SLA", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 128,
                            "12. Servicenivåer",
                            "Not set", "", "", "linus"),

            new MTClassification("#SLA", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 163,
                            "14. Försening, Fel och avtalsbrott i övrigt",
                            "Not set", "", "", "linus"),


            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 112,
                            "10.1.2 Förlust av enskild person eller resurs får inte medföra att Leverantörens möjligheter att tillhandahålla avtalad kompetens eller resurser påverkas.",
                            "Not set", "", "", "linus"),
            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 110,
                            "ska den personal som används för produktion och tillhandahållande av Tjänsten inneha för uppgiften lämplig utbildning, erfarenhet och kvalifikationer i övrigt.",
                            "Not set", "", "", "linus"),

            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 110,
                            "ska den personal som används för produktion och tillhandahållande av Tjänsten inneha för uppgiften lämplig utbildning, erfarenhet och kvalifikationer i övrigt.",
                            "Not set", "", "", "linus"),

            new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 166,
                            "14.1.1 Vid Fel ska Leverantören avhjälpa Felet i fråga, med beaktande av överenskomna Servicenivåer och med beaktande av punkt 14.1.2. Leverantörens skyldighet att avhjälpa Fel påverkas inte av att Tjänsten tidigare godkänts, att Tjänsten tagits i anspråk av EHM eller att Tjänsten på annat sätt använts.",
                            "Not set", "", "", "linus"),

               new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 248,
                               "21. Uppföljning av avtalat åtagande",
                               "Not set", "", "", "linus"),

               new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 10,
                               "2. Syfte med och grundläggande förutsättningar och principer för Avtalet",
                               "Not set", "", "", "linus"),


               new MTClassification("#FULFILLMENT", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 167,
                               "14.1.2 Leverantören är ansvarig för att identifiera Fel och orsak till Fel och ska meddela EHM om Leverantören anser att Felet beror på omständighet för vilken EHM svarar enligt Avtalet och därvid tillhandahålla överenskommen dokumentation som påvisar vad Felet beror på.",
                               "Not set", "", "", "linus"),



            new MTClassification("#DEFECTS", 0, 0, "Huvudavtal_EHM_IT_Drift - 2.docx", 163,
                            "14. Försening, Fel och avtalsbrott i övrigt",
                            "Not set", "", "", "linus"),




    };


    MT_EHM(){

        super(document, classifications);
    }
}
