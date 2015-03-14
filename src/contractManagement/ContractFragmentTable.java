package contractManagement;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    ContractFragment - Data Table
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractFragmentTable extends DataTable implements DataTableInterface{

    private static final String TITLE = "Contract Fragment";
    public static final String TABLE = "ContractFragment";
    private static final String DESCRIPTION = "Building blocks of a document";

    public enum Columns {Name, Version, Project, StructureNo, Ordinal, Text, Indentation, Type, Risk, AnnotationCount, ReferenceCount, ClassificatonCount, ActionCount, xPos, yPos, width, display, Image, }

    private static final ColumnStructureInterface[] DATA = new ColumnStructureInterface[] {

            new StringColumn("Name", DataColumn.noFormatting),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new IntColumn("StructureNo", DataColumn.noFormatting),
            new IntColumn("Ordinal", DataColumn.noFormatting),
            new BlobColumn("Text", DataColumn.noFormatting),
            new IntColumn("Indentation", DataColumn.noFormatting),
            new StringColumn("Type", DataColumn.noFormatting),
            new ConstantColumn("Risk", DataColumn.noFormatting, new TableReference("ContractRisk", "Name")),
            new IntColumn("AnnotationCount", DataColumn.noFormatting),
            new IntColumn("ReferenceCount", DataColumn.noFormatting),
            new IntColumn("ClassificatonCount", DataColumn.noFormatting),
            new IntColumn("ActionCount", DataColumn.noFormatting),
            new IntColumn("xPos", DataColumn.noFormatting),
            new IntColumn("yPos", DataColumn.noFormatting),
            new IntColumn("width", DataColumn.noFormatting),
            new StringColumn("display", DataColumn.noFormatting).setDefaultValue(new ImplicitValue( "{}" )),
            new StringColumn("Image", DataColumn.noFormatting).setDefaultValue(new ImplicitValue( "[]" )),
    };

    private static final ContractFragment associatedObject = new ContractFragment();
    public ContractFragmentTable(){

        init(DATA, associatedObject, TABLE, TITLE, DESCRIPTION, DefaultValues, TestValues);
        nameColumn = 1;
        // Not set as external
        // Not a constant table
    }

    public ContractFragmentTable(ConditionInterface condition){

        this();
        try{

            values = load(condition);
        }
        catch(BackOfficeException e){

            System.out.println("Error loading table values " + e.narration);
        }

    }
    private static final String[][] DefaultValues = {




    };
    private static final String[][] TestValues = {

          {"first fragment", "Cannon v1.0", "Demo", "1", "1", "Introduction text with åäö (written 2014-07-01) [ brackets ] and an unknown ref to Pricelist", "0", "TEXT", "Blocker", "1", "1", "1", "1", "1", "0", "0", "0", "0", "{}", "[]", "system"},
          {"Definition fragment", "Cannon v1.0", "Demo", "2", "2", "Definition text... and btw as stated in the introduction or in Test document.docx", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top1", "Google v1.0", "Demo", "1", "1", "These Google Analytics Terms of Service (this \"Agreement\") are entered into by Google Inc. (\"Google\") and the entity executing this Agreement (\"You\").", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "1", "2", "This Agreement governs Your use of the standard Google Analytics (the \"Service\").", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "1", "3", "BY CLICKING THE \"I ACCEPT\" BUTTON, COMPLETING THE REGISTRATION PROCESS, OR USING THE SERVICE, YOU ACKNOWLEDGE THAT YOU HAVE REVIEWED AND ACCEPT THIS AGREEMENT AND ARE AUTHORIZED TO ACT ON BEHALF OF, AND BIND TO THIS AGREEMENT, THE OWNER OF THIS ACCOUNT.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "1", "4", "In consideration of the foregoing, the parties agree as follows:", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top2", "Google v1.0", "Demo", "2", "5", "\"Account\" refers to the billing account for the Service.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "6", "All Profiles linked to a single Property will have their Hits aggregated before determining the charge for the Service for that Property.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "7", "\"Confidential Information\" includes any proprietary data and any other information disclosed by one party to the other in writing and marked \"confidential\" or disclosed orally and, within five business days, reduced to writing and marked \"confidential\".", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "8", "However, Confidential Information will not include any information that is or becomes known to the general public, which is already in the receiving party's possession prior to disclosure by a party or which is independently developed by the receiving party without the use of Confidential Information.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "9", "\"Customer Data\" means the data concerning the characteristics and activities of Visitors that is collected through use of the <span class=\"reference\">GATC</span> and then forwarded to the Servers and analyzed by the Processing Software.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "10", "\"Documentation\" means any accompanying documentation made available to You by Google for use with the Processing Software, including any documentation available online.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "11", "\"GATC\" means the Google Analytics Tracking Code, which is installed on a Property for the purpose of collecting Customer Data, together with any fixes, corrections and upgrades provided to You.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "12", "\"Hit\" means the base unit that the Google Analytics system processes.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "13", "A Hit may be a call to the Google Analytics system by various libraries, including, Javascript (ga.js, urchin.js), Silverlight, Flash, and Mobile.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "14", "A Hit may currently be a page view, a transaction, item, or event.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "15", "Hits may also be delivered to the Google Analytics system without using one of the various libraries by other Google Analytics-supported protocols and mechanisms the Service makes available to You.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "16", "\"Processing Software\" means the Google Analytics server-side software and any upgrades, which analyzes the Customer Data and generates the Reports.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "17", "\"Profile\" means the collection of settings that together determine the information to be included in, or excluded from, a particular Report.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "18", "For example, a Profile could be established to view a small portion of a web site as a unique Report.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "19", "There can be multiple Profiles established under a single Property.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "20", "\"Property\" means a group of web pages or apps that are linked to an Account and use the same GATC.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "21", "Each Property includes a default Profile that measures all pages within the Property.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "22", "\"Privacy Policy\" means the privacy policy on a Property.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "23", "\"Report\" means the resulting analysis shown at http://www.google.com/analytics for a Profile.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "24", "\"Servers\" means the servers controlled by Google (or its wholly owned subsidiaries) on which the Processing Software and Customer Data are stored.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "25", "\"Software\" means the GATC and the Processing Software.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "26", "\"Third Party\" means any third party ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "27", "(i) to which You provide access to Your Account or ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "28", "(ii) for which You use the Service to collect information on the third party's behalf.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "29", "\"Visitors\" means visitors to Your Properties.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "2", "30", "The words \"include\" and \"including\" mean \"including but not limited to.\"", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top3", "Google v1.0", "Demo", "3", "31", "Subject to Section 15, the Service is provided without charge to You for up to 10 million Hits per month per account.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "3", "32", "Google may change its fees and payment policies for the Service from time to time including the addition of costs for geographic data, the importing of cost data from search engines, or other fees charged to Google or its wholly-owned subsidiaries by third party vendors for the inclusion of data in the Service reports.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "3", "33", "The changes to the fees or payment policies are effective upon Your acceptance of those changes which will be posted at http://www.google.com/analytics.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "3", "34", "Unless otherwise stated, all fees are quoted in U.S. Dollars.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "3", "35", "Any outstanding balance becomes immediately due and payable upon termination of this Agreement and any collection expenses (including attorneys' fees) incurred by Google will be included in the amount owed, and may be charged to the credit card or other billing mechanism associated with Your AdWords account.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top4", "Google v1.0", "Demo", "4", "36", "To register for the Service, You must complete the registration process by providing Google with current, complete and accurate information as prompted by the registration form, including Your e-mail address (username) and password.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "4", "37", "You will protect Your passwords and take full responsibility for Your own, and third party, use of Your accounts.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "4", "38", "You are solely responsible for any and all activities that occur under Your Account.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "4", "39", "You will notify Google immediately upon learning of any unauthorized use of Your Account or any other breach of security.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "4", "40", "Google's (or its wholly-owned subsidiaries') support staff may, from time to time, log in to the Service under Your customer password in order to maintain or improve service, including to provide You assistance with technical or billing issues.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top5", "Google v1.0", "Demo", "5", "41", "Subject to the terms and conditions of this Agreement, ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "42", "(a) Google grants You a limited, revocable, non-exclusive, non-sublicensable license to install, copy and use the GATC solely as necessary for You to use the Service on Your Properties or Third Party's Properties; and ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "43", "(b) You may remotely access, view and download Your Reports stored at <a href=\"http://www.google.com/analytics\">http://www.google.com/analytics</a>.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "44", "You will not (and You will not allow any third party to) ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "45", "(i) copy, modify, adapt, translate or otherwise create derivative works of the Software or the Documentation; ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "46", "(ii) reverse engineer, decompile, disassemble or otherwise attempt to discover the source code of the Software, unless as expressly permitted by the law in effect in the jurisdiction in which You are located; ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "47", "(iii) rent, lease, sell, assign or otherwise transfer rights in or to the Software, the Documentation or the Service; ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "48", "(iv) remove any proprietary notices or labels on the Software or placed by the Service; ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "49", "(v) use, post, transmit or introduce any device, software or routine which interferes or attempts to interfere with the operation of the Service or the Software; or ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "50", "(vi) use data labeled as belonging to a third party in the Service for purposes other than generating, viewing, and downloading Reports.", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "5", "51", "You will comply with all applicable laws and regulations in Your use of and access to the Documentation, Software, Service and Reports.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top6", "Google v1.0", "Demo", "6", "52", "Neither party will use or disclose the other party's Confidential Information without the other's prior written consent unless for the purpose of performing its obligations under this Agreement or if required by law, regulation or court order; in which case, the party being compelled to disclose Confidential Information will give the other party as much notice as is reasonably practicable prior to disclosing the Confidential Information.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "6", "53", "Upon termination of this Agreement, the parties will promptly either return or destroy all Confidential Information and, upon request, provide written certification of such.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"top7", "Google v1.0", "Demo", "7", "54", "Google and its wholly owned subsidiaries may retain and use, subject to the terms of its Privacy located at http://www.google.com/privacy.html), information collected in Your use of the Service.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "7", "55", "Google will not share Your Customer Data or any Third Party's Customer Data with any third parties unless Google ", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "7", "56", "(i) has Your consent for any Customer Data or any Third Party's consent for the Third Party's Customer Data; ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "7", "57", "(ii) concludes that it is required by law or has a good faith belief that access, preservation or disclosure of Customer Data is reasonably necessary to protect the rights, property or safety of Google, its users or the public; or ", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "7", "58", "(iii) provides Customer Data in certain limited circumstances to third parties to carry out tasks on Google's behalf (e.g., billing or data storage) with strict restrictions that prevent the data from being used or shared unless as directed by Google.", "0", "Count Item", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},
          {"fragment...", "Google v1.0", "Demo", "7", "59", "When this is done, it is subject to agreements that oblige those parties to process Customer Data only on Google's instructions and in compliance with this Agreement and appropriate confidentiality and security measures.", "0", "TEXT", "Not set", "0", "0", "0", "0", "0", "0", "0", "{}", "[]", "system"},



    };

    @Override
    public void clearConstantCache(){

        ContractFragment.clearConstantCache();
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    public void addAll(java.util.List<ContractFragment> fragments) throws BackOfficeException {

        for(ContractFragment fragment : fragments){

            fragment.store();

        }

    }

    public List<ContractFragment> getAllValues(){


        List<DataObjectInterface> objects = getValues();

        List<ContractFragment> fragments = (List<ContractFragment>)(List<?>) objects;

        return fragments;
    }





}
