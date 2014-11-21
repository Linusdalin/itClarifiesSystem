package test.UnitTests;

import diff.FragmentComparator;
import diff.DiffStructure;
import log.PukkaLogger;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *          Simple tests for the diff function
 */


public class DiffTest {

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){


        FragmentComparator comparator = new FragmentComparator();
        String active, ref;
        int distance;
        active = "This is the active text. It shall be compared to be the same as reference text.";

        // Test the same

        ref = "This is the active text. It shall be compared to be the same as reference text.";
        assertTrue(comparator.isSame(active, ref));

        // Test some miss spelling

        ref = "This is teh active txt. It shalll be compared to bee the sejm as reference text.";
        assertTrue(comparator.isSame(active, ref));

        // Test some changed words

        ref = "This is an active text. It must be compared to the same as compared text.";
        assertTrue(comparator.isSame(active, ref));

        // Added and removed some words

        ref = "This is the new active test. It shall now be be compared to be the same text.";
        assertTrue(comparator.isSame(active, ref));

        // And something completely different

        ref = "The active text is compared differently. Shall it really be compared to any text.";
        assertFalse(comparator.isSame(active, ref));

        // Short should fail

        active = "One";
        ref = "Other";
        assertFalse(comparator.isSame(active, ref));

    }

    @Test
    public void testDiff(){

        String[] active     = {"zero", "one", "two", "three", "four"};
        String[] reference  = {"zero", "one", "two", "three", "four"};

        FragmentComparator comparator = new FragmentComparator();

        DiffStructure diffStructure= comparator.diff(active, reference);
        PukkaLogger.log(PukkaLogger.Level.INFO, "Structure: " + diffStructure.toString());

        assertThat(diffStructure.getNoOrphansActive(),      is(0));
        assertThat(diffStructure.getNoOrphansReferenced(),  is(0));


    }


    @Test
    public void testDiff2(){

        String[] active     = {"zero", "added", "one", "two", "three", "four"};
        String[] reference  = {"zero", "one", "two", "three", "four"};

        FragmentComparator comparator = new FragmentComparator();

        DiffStructure diffStructure= comparator.diff(active, reference);
        PukkaLogger.log(PukkaLogger.Level.INFO, "Structure: " + diffStructure.toString());

        assertThat(diffStructure.getNoOrphansActive(),      is(1));
        assertThat(diffStructure.getNoOrphansReferenced(),  is(0));

    }

    @Test
    public void testDiff3(){

        String[] active     = {"zero", "one", "two", "three", "four"};
        String[] reference  = {"zero", "added", "one", "two", "three", "four"};

        FragmentComparator comparator = new FragmentComparator();

        DiffStructure diffStructure= comparator.diff(active, reference);
        PukkaLogger.log(PukkaLogger.Level.INFO, "Structure: " + diffStructure.toString());

        assertThat(diffStructure.getNoOrphansActive(),      is(0));
        assertThat(diffStructure.getNoOrphansReferenced(),  is(1));


    }

    @Test
    public void testDiff4(){

        String[] active     = {"zero", "one", "added", "two", "three", "four"};
        String[] reference  = {"zero", "added", "one", "two", "three", "four"};

        FragmentComparator comparator = new FragmentComparator();

        DiffStructure diffStructure= comparator.diff(active, reference);
        PukkaLogger.log(PukkaLogger.Level.INFO, "Structure: " + diffStructure.toString());

        assertThat(diffStructure.getNoOrphansActive(),      is(1));
        assertThat(diffStructure.getNoOrphansReferenced(),  is(1));


    }



}