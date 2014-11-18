package test;

import diff.DiffStructure;
import diff.FragmentComparator;
import log.PukkaLogger;
import maintenance.Smokey;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *          Simple tests for the static smokey function
 */


public class SmokeyTest {

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){

        assertThat("Is not in smokey at start", Smokey.isInSmokey("anyone"), is(false));

        Smokey.enterSmokey();

        assertThat("Should now be in smokey", Smokey.isInSmokey("anyone"), is(true));

        assertThat("Smokey user should dodge block", Smokey.isInSmokey("admin"), is(false));

        Smokey.exitSmokey();

        assertThat("Should now be open", Smokey.isInSmokey("anyone"), is(false));


    }

}