package test;

import document.AbstractClause;
import document.AbstractFragment;
import document.AbstractStructureItem;
import document.StructureType;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;



/**
 * Simple tests for the time stamp datatype.
 */


public class AbstractFragmentTest {

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){

        String text = "This is the fragment text";

        AbstractStructureItem structure = new AbstractStructureItem().setId( 4711 );
        AbstractFragment fragment = new AbstractFragment(StructureType.TEXT, text).setStructureParent(structure);

        assertThat("Fragment number", fragment.getStructureItem().getID(), is( 4711 ));
        assertThat(fragment.getBody(), is(text));

    }




}

