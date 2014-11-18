<%@ page import="pukkaBO.backOffice.EventHandlerInterface" %>
<html>
<%@ include file="../../bean.inc" %>

<%
    String type = request.getParameter("type");


    // Handle the event type

    if(type != null && type.equals("event")){

        EventHandlerInterface eventHandler = backOffice.getEventHandler();
        out.print("<ul>");
        for(String eventDescription : eventHandler.getEvents()){

            out.println(eventDescription);
        }

        out.print("</ul>");

    }
    else{

        System.out.println("<p>No type defined</p>");

    }


%>
</html>