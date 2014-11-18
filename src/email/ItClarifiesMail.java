package email;

import pukkaBO.email.GeneralMail;

/**********************************************************************************************
 *
 *          Itclarifies mail.
 *
 *           - Set css style
 *           - Set html = true
 *           - Set the sender name and address
 *
 *
 *
 */
public class ItClarifiesMail extends GeneralMail {

    //TODO: Add tool specific css styling here

    private static final String ItClarifiesCSS = "";

    /********************************************************************************
     *
     *        Initiate the mail
     *
     *
     */


    public ItClarifiesMail(){

        super("linus.dalin@itclarifies.com", "itClarifies Action Admin");

        css = ItClarifiesCSS;
        html = true;



    }


}
