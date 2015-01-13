package test.UnitTests;

import analysis.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *          Simple tests for the auto numberer
 */


public class AutonumberTest {

    /******************************************************************
     *
     *
     *
     */



    @Test
    public void basicTest(){


        AutoNumberer autoNumberer = new AutoNumberer();
        String number;

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("1"));

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("2"));

        number = autoNumberer.getNewNumber( 2 , false);
        assertThat(number, is("2.1"));

        number = autoNumberer.getNewNumber( 2 , false);
        assertThat(number, is("2.2"));

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("3"));

        number = autoNumberer.getNewNumber( 3 , false);
        assertThat(number, is("3.1.1"));

        number = autoNumberer.getNewNumber( 2 , false);
        assertThat(number, is("3.2"));

    }

    @Test
    public void startAtTest(){


        AutoNumberer autoNumberer = new AutoNumberer().startAt(3);
        String number;

        number = autoNumberer.getNewNumber( 1 , false);

        assertThat(number, is("3"));


    }


    @Test
    public void level0(){

        AutoNumberer autoNumberer = new AutoNumberer();
        String number;

        number = autoNumberer.getNewNumber( 0 , false);
        assertThat(number, is(""));

        number = autoNumberer.getNewNumber( 0 , false);
        assertThat(number, is(""));

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("1"));


    }

    @Test
    public void restart(){

        AutoNumberer autoNumberer = new AutoNumberer();
        String number;

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("1"));

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("2"));

        number = autoNumberer.getNewNumber( 1 , true);
        assertThat(number, is("1"));

        number = autoNumberer.getNewNumber( 1 , false);
        assertThat(number, is("2"));



    }

    @Test
    public void specialCases(){


        AutoNumberer autoNumberer = new AutoNumberer();
        String number;

        number = autoNumberer.getNewNumber( 5 , false);
        assertThat(number, is("1.1.1.1.1"));


        autoNumberer = new AutoNumberer();

        number = autoNumberer.getNewNumber( 2 , false);
        assertThat(number, is("1.1"));

        number = autoNumberer.getNewNumber( 2 , false);
        assertThat(number, is("1.2"));

        number = autoNumberer.getNewNumber( 5 , false);
        assertThat(number, is("1.2.1.1.1"));


        number = autoNumberer.getNewNumber( 0 , false);
        assertThat(number, is(""));


    }


}

