<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>

<html>

<head>
    <link type="text/css" rel="stylesheet" href="style/test.css" />
  </head>

  <body>

<%

    String guestbookName = request.getParameter("guestbookName");
        if (guestbookName == null) {
            guestbookName = "default";
        }

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    if(user != null){


        %> <p>New Guest book: Hello, <% out.print(user.getNickname()); %>! (You can <a href="<% out.print(userService.createLogoutURL(request.getRequestURI())); %>">sign out</a>.)</p> <%

    } else {

        %> <p>Hello! <a href="<% out.print(userService.createLoginURL(request.getRequestURI())); %>">Sign in</a> to include your name with greetings you post.</p> <%

    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);

    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
    Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);

    List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));

    if (greetings.isEmpty()) {

        out.print("<p>Guestbook " + guestbookName + " has no entries.</p>");

    } else {

        out.print("<p>Messages in Guestbook " + guestbookName + ":</p>");

        for (Entity greeting : greetings) {

            if (greeting.getProperty("user") == null) {

                out.print("<p>An anonymous person wrote:</p>");

            } else {

                out.print("<p><b>" + greeting.getProperty("user") + " wrote:</b></p>");

            }

            out.print("<blockquote>" + greeting.getProperty("content") + "</blockquote>");

        }
    }
%>

        <form action="/sign" method="post">
          <div><textarea name="content" rows="3" cols="60"></textarea></div>
          <div><input type="submit" value="Post Greeting" /></div>
          <input type="hidden" name="guestbookName" value="<%out.print(guestbookName); %>"/>
        </form>


  </body>
</html>