package test;

import org.junit.Test;
import pukkaBO.email.PlainMail;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *          Simple tests for sending an email
 */


public class MailTest {

    /******************************************************************
     *
     *
     *  //TODO: Figure out how to test this
     *



    @Test
    public void basicSendTest(){

        try{

        PlainMail mail = new PlainMail("linus.dalin@itclarifies.com", "ItClarifies Action Admin")
                .withSubject("This is a test message")
                .withBody("new action...");


        mail.sendTo("Linus", "linusdalin@gmail.com");

        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }
    }


      */
}