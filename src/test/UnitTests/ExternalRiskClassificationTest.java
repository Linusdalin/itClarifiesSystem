package test.UnitTests;

import analysis.ImportedRiskClassification;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import risk.ContractRiskTable;
import risk.RiskClassification;
import risk.RiskClassificationTable;
import test.PukkaTest;

import static org.hamcrest.CoreMatchers.is;

/************************************************************************
 *
 *              Tests for the imported classifications
 *
 *
 */
public class ExternalRiskClassificationTest  extends PukkaTest{

    @BeforeClass
    public static void preAmble(){

        ContractRiskTable table = new ContractRiskTable();

    }



    @Test
    public void basicTest(){


        ImportedRiskClassification riskClassification = new ImportedRiskClassification("#Risk Blocker No can do");

        assertVerbose("This is a risk", riskClassification.isRiskClassification, is(true));
        assertVerbose("It is a blocker", riskClassification.getRisk().getName(), is("Blocker"));
        assertVerbose("It has a description", riskClassification.getDescription(), is("No can do"));


        ImportedRiskClassification riskClassification2 = new ImportedRiskClassification("#Risk CrazyCrazy No can do");

        assertVerbose("This is a risk", riskClassification2.isRiskClassification, is(true));
        assertVerbose("It is a unknown(potential)", riskClassification2.getRisk().getName(), is("Potential"));
        assertVerbose("It has a description", riskClassification2.getDescription(), is("No can do"));

    }

    @Test
    public void testTheTest(){

        assertVerbose("This is a risk",     ImportedRiskClassification.isRisk("#Risk Blocker No can do"), is(true));
        assertVerbose("This is not risk",   ImportedRiskClassification.isRisk("#Blocker Risk No can do"), is(false));


    }

    @Test
    public void failToCreateTest(){


        ImportedRiskClassification riskClassification = new ImportedRiskClassification("#Risk");
        assertVerbose("This is not a risk", riskClassification.isRiskClassification, is(false));

        ImportedRiskClassification riskClassification2 = new ImportedRiskClassification("#Blocker Risk No can do");
        assertVerbose("This is not a risk", riskClassification2.isRiskClassification, is(false));

    }


}
