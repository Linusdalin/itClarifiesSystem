<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="log.PukkaLogger" %>
<%@ page import="project.ProjectTable" %>
<%@ page import="pukkaBO.condition.ColumnFilter" %>
<%@ page import="pukkaBO.condition.LookupItem" %>
<%@ page import="project.Project" %><%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    String demoProjectKey = "";
    Project demo;
    try {

        demo = new Project(new LookupItem().addFilter(new ColumnFilter(ProjectTable.Columns.Name.name(), "Demo")));
        if(demo.exists()){

            demoProjectKey = demo.getKey().toString();
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Demo project key " + demoProjectKey);
        }
        else{
            PukkaLogger.log(PukkaLogger.Level.INFO, "Could not find demo project");

        }

    } catch (Exception e) {
        PukkaLogger.log(PukkaLogger.Level.INFO, "No demo project found " + e.getMessage());
    }

%>


<html>
    <head>
        <title>Upload Test</title>
    </head>
    <body>
        <form action="<%= blobstoreService.createUploadUrl("/UploadFile") %>" method="post" enctype="multipart/form-data">
            <input type="file" name="theFile">
            <input type="text" id="GetDocumentsForProject" name="project" value="<% out.print(demoProjectKey);%>" size="50">
            <input type="hidden" name="session" value="DummyAdminToken">
            <input type="submit" value="Submit">
        </form>
    </body>
</html>