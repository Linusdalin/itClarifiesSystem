package test.UnitTests;

import backend.ReclassificationList;
import maintenance.Smokey;
import org.junit.Test;
import test.ServletTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */


public class ReclassificationTest extends ServletTests {

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){

        String shortString      = "This is short";
        String shortStringOut      = "\"This is short\"";

        String longerString     = "This is a somewhat longer string. It should be over two lines (more than 40)";
        String longerStringOut  = "\"This is a somewhat longer string. It sho\"+\n    \"uld be over two lines (more than 40)\"";
        String veryLongString   = "This is a much longer string. It should be over more than two lines (multiple lines) This is a much longer string. It should be over multiple lines";
        String veryLongStringOut   = "\"This is a much longer string. It should \"+\n  \"be over more than two lines (multiple li\"+\n  \"nes) This is a much longer string. It sh\"+\n  \"ould be over multiple lines\"";


        String output = ReclassificationList.asSplitString(shortString, 11);
        System.out.println("Output:" + output);
        assertVerbose("Short String test", output, is(shortStringOut));

        output = ReclassificationList.asSplitString(longerString, 4);
        System.out.println("Output:" + output);
        assertVerbose("Longer String test", output, is(longerStringOut));

        output = ReclassificationList.asSplitString(veryLongString, 2);
        System.out.println("Output:" + output);
        assertVerbose("Very long String test", output, is(veryLongStringOut));


    }

}