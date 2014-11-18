<%@ page import="static org.junit.Assert.assertTrue" %>
<%@ page import="pukkaBO.email.PlainMail" %>
<%@ page import="pukkaBO.email.MailInterface" %>
<%
    // Test sending an email

    try{

        MailInterface mail = new PlainMail("linus.dalin@itclarifies.com", "Action Admin")
                .withSubject("Subject")
                .withBody("Body...");
        mail.sendTo("Linus", "linusdalin@gmail.com");

    }catch(Exception e){

        e.printStackTrace();
    }


%>


<p>Email sent. Check the log and inbox</p>