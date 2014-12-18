package test.UnitTests;

import analysis.OutcomeMap;
import analysis2.NewAnalysisOutcome;
import contractManagement.ContractFragment;
import document.AbstractFragment;
import document.AbstractStructureItem;
import document.SimpleStyle;
import document.StructureType;
import org.junit.Test;
import test.PukkaTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


/**
 *
 */


public class OutcomeMapTest extends PukkaTest{

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){

        NewAnalysisOutcome mockOutcome = mock(NewAnalysisOutcome.class);
        ContractFragment mockFragment = mock(ContractFragment.class);

        OutcomeMap map = new OutcomeMap(mockOutcome, mockFragment);
        assertVerbose("Retrieving the fragment back", map.fragment, is(mockFragment));

    }




}

