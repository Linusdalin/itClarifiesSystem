package test.UnitTests;

import diff.DiffStructure;
import diff.FragmentComparator;
import log.PukkaLogger;
import org.junit.Test;
import services.ItClarifiesService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *          Simple tests
 */


public class ItClarifiesServiceTest{




    /******************************************************************
     *
     *
     *
     */



    @Test
    public void encoding(){

        String encodedText = ItClarifiesService.encodeToJSON("first tab\tSecond tab");
        assertThat(encodedText + "Should contain a span", encodedText.contains("&nbsp;"), is(true));

    }
}