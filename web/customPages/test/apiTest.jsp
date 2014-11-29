<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="pukkaBO.exceptions.BackOfficeException" %>
<%@ page import="log.PukkaLogger" %>
<%@ page import="pukkaBO.condition.LookupItem" %>
<%@ page import="pukkaBO.condition.ColumnFilter" %>
<%@ page import="contractManagement.*" %>
<%@ page import="actions.ActionStatusTable" %>
<%@ page import="userManagement.PortalUserTable" %>
<%@ page import="risk.ContractRiskTable" %>
<html>



<%
    // Check if we want to use real access tokens or pre-populate it

    boolean useRealToken = (request.getParameter("useToken") != null);

    String hostParameter = request.getParameter("host");
    String host = "";

    if(hostParameter != null)
        host = hostParameter;

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

    String demoDocumentKey = "";
    Contract demoDoc = null;

    try {

        demoDoc = new Contract(new LookupItem().addFilter(new ColumnFilter(ContractTable.Columns.Name.name(), "Cannon")));
        if(demoDoc.exists()){

            demoDocumentKey = demoDoc.getKey().toString();
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "Demo Document key " + demoDocumentKey);
        }
        else{
            PukkaLogger.log(PukkaLogger.Level.INFO, "Could not find demo document");

        }

    } catch (Exception e) {
        PukkaLogger.log(PukkaLogger.Level.INFO, "No demo project found " + e.getMessage());
    }




    String riskDropdown = new ContractRiskTable().getDropDown().generate("risk", null, "", "", false);
    String classDropdown = new FragmentClassTable().getDropDown().generate("class", null, "", "", false);
    String statusDropdown = new ActionStatusTable().getDropDown().generate("status", null, "", "", false);
    String userDropdown = new PortalUserTable().getDropDown().generate("assignee", null, "", "", false);

%>


    <!-- <a href="Transaction?User=LinusD&Game=slots&Bet=1000&Win=800&TimeStamp=2013-08-25">Create Slot Transaction</a><br> -->

    <head>
        <title>itclarifies web Service documentation and Test</title>
        <link rel="stylesheet" type="text/css" href="../../doc/docstyle.css" title="Style">
    </head>

<body>

<h1>Test for the itClarifies web services</h1>

<div style="float:left; width:67%">

    <p>For all the services there are some generic parameters:</p>

    <ul>
        <li><span class="code">&session=&lt;token&gt;</span>, (Mandatory)To identify the user</li>
        <li><span class="code">&html=on</span>, (Optional) and only for test to set the content type to html to be able to easily load in the browser (for testing)</li>
        <li><span class="code">&pp=on</span>, (Optional) and only for test to add pretty printing to easier be able to read the json output(for testing)</li>

    </ul>

</div>

<div style="float:right; width:33%">

    <ul>
        <li><a href="#ProjectLink">Projects</a></li>
        <li><a href="#DocumentLink">Documents</a></li>
        <li><a href="#FragmentLink">Fragments</a></li>
        <li><a href="#AnnotationLink">Annotations, Classifications and Risk</a></li>
        <li><a href="#ActionLink">Actions</a></li>
        <li><a href="#SearchLink">Search</a></li>
        <li><a href="#UploadLink">Upload</a></li>
        <li><a href="#ReferencesLink">References</a></li>

    </ul>


</div>

<div style="clear:both">&nbsp;</div>

<div style="width:100%">

    <h1>User and Login</h1>
    <p>Create and login a user</p>
</div>

<div style="float:left; width:33%">

<FORM METHOD=POST action="<% out.print(host); %>/User" id="newUser" name="newUser">
    <fieldset style="height:320px">
        <h3>Add User</h3>
        <p>Creates a new user and returns a key. The user will automatically belong to the same organization as the creator.</p>

        <p>	<label for="UserName">UserName</label>
            <input type="text" id="UserName" name="username">
        </p>

        <p>	<label for="Email">Email</label>
            <input type="text" id="Email" name="email">
        </p>

        <p>	<label for="Pwd">Password</label>
            <input type="text" id="Pwd" name="password">
        </p>

        <% out.print(getTokenParameter(useRealToken, "post_user"));%>
        <input type="hidden" name="html" value="on">

        <p>
            <input type="submit" value="Post" class="btn primary" id="submit_user" />
        </p>
        <h3>Response json:</h3>
        <p class="code">{ "PortalUser" : "&lt;key&gt;" }</p>
    </fieldset>

</FORM>


</div>
<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/User" id="getUser" name="getUser">
        <fieldset style="height:320px">
            <h3>Get User Details</h3>
            <p>Retrieve the details for a user given the session token. This is used to publish information like name on the site</p>
            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "get_user"));%>

            <p>
                <input type="submit" value="Get User" class="btn primary" id="submit_get_user" />
            </p>

            <h3>Response json:</h3>
            <p class="code">{ "PortalUser" : [ <br/>
                "name" : "&lt;name&gt;", "email" : "&lt;mail&gt;", "organization" : "&lt;name&gt;" }</p>

        </fieldset>
    </FORM>

</div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/UserExternal" id="getUserExternal" name="getUserExternal">
        <fieldset style="height:320px">
            <h3>Get User External Details</h3>
            <p>Retrieve the external profile information for any user given the session token. This is used for information like owner of documents, annotations etc.</p>

            <p>	<label for="ExternalUser">UserName</label>
                <input type="text" id="ExternalUser" name="user">
            </p>


            <% out.print(getTokenParameter(useRealToken, "post_project"));%>
            <input type="hidden" name="html" value="on">

            <p>
                <input type="submit" value="Get User" class="btn primary" id="submit_get_user_external" />
            </p>

            <h3>Response json:</h3>
            <p class="code">{ "PortalUserExternal" : [ <br/>
                {"name" : "&lt;name&gt;", "id" : "&lt;key&gt;"} <br/> ] }</p>


            <FORM METHOD=GET action="<% out.print(host); %>/UserExternal" id="getUserExternalAll" name="getUserExternal">
                    <% out.print(getTokenParameter(useRealToken, "get_external"));%>
                    <input type="hidden" name="html" value="on">

                    <p>
                        <input type="submit" value="Get All" class="btn primary" id="submit_get_user_external_all" />
                    </p>

            </FORM>


        </fieldset>
    </FORM>

</div>



<!--

<div style="float:left; width:33%">

<FORM METHOD=DELETE action="<% out.print(host); %>/User" id="deleteUser" name="deleteUser">
    <fieldset style="height:150px">
        <h3>Delete User</h3>
        <p>Deletes the user for a given session. This should not be used in the frontend</p>

        <input type="hidden" name="html" value="on">
        <% out.print(getTokenParameter(useRealToken, "post_project"));%>

        <input type="hidden" name="html" value="on">

        <p>
            <input type="submit" value="Delete User" class="btn primary" id="submit_delete_user" />
        </p>
    </fieldset>

</FORM>


</div>

        -->

<div style="clear:both">&nbsp;</div>


    <div style="float:left; width:33%">

        <FORM METHOD=POST action="<% out.print(host); %>/Login" id="postLoginForm" name="LoginForm">

            <fieldset style="height:300px">
                <h3>Login</h3>
                <p>Login a user and returns a session id</p>
                <p class="code">GET: /Login?user=&lt;name&gt;&password=&lt;password&gt;</p>

                <p>	<label for="name">name</label>
                    <input type="text" id="name" name="user"> </p>

                <p>
                <p>	<label for="password">password</label>
                    <input type="text" id="password" name="password"> </p>

                <input type="hidden" name="html" value="on">

                <p>
                    <input type="submit" value="Login" class="btn primary" id="submit_login" />
                </p>

                <h3>Response json:</h3>
                <p class="code">{ "user" : "&lt;key&gt;" "Token" : "&lt;session token&gt;" }</p>

            </fieldset>

        </form>
    </div>

    <div style="float:left; width:33%">

        <FORM METHOD=POST action="<% out.print(host); %>/Logout" id="postLogoutForm" name="LogoutForm">

            <fieldset style="height:300px">
                <h3>Logout</h3>
                <p>Log out a user given a sessionToken</p>
                <p class="code">POST: /Logout?session=&lt;key&gt;</p>

                <p>	<label for="logout_token">token</label>
                    <input type="text" id="logout_token" name="session"> </p>

                <input type="hidden" id="html_login" name="html" value="on">

                <p>
                    <input type="submit" value="Logout" class="btn primary" id="submit_logout" />
                </p>

                <h3>Response json:</h3>
                <p class="code">{ "Status" : "&lt;Closed | Implicit &gt;" }</p>

            </fieldset>

        </form>
    </div>

    <div style="float:left; width:33%">

        <FORM METHOD=POST action="<% out.print(host); %>/Validate" id="validateSessionForm" name="LogoutForm">

            <fieldset style="height:300px">
                <h3>Validate Session</h3>
                <p>Validate a session token</p>
                <p class="code">POST: /Validate?token=&lt;key&gt;</p>

                <p>	<label for="validate_token">token</label>
                    <input type="text" id="validate_token" name="token"> </p>

                <input type="hidden" id="html_validae" name="html" value="on">

                <p>
                    <input type="submit" value="Validate" class="btn primary" id="submit_validate" />
                </p>

                <h3>Response json:</h3>
                <p class="code">{ "Status" : "&lt;Closed | Implicit &gt;" }</p>

            </fieldset>

        </form>
    </div>



    <div style="clear:both">&nbsp;</div>

    <div style="width:100%">

        <a id="ProjectLink"></a>
        <h1>Project</h1>
        <p>Create and look up projects</p>
    </div>

<div style="float:left; width:33%">


<FORM METHOD=POST action="<% out.print(host); %>/Project" id="postProjectForm" name="getProjectForm" accept-charset="UTF-8">

    <fieldset style="height:400px">
        <h3>Create Project</h3>
        <p>Create a new project</p>

        <p class="code">POST /Project?session=&lt;token&gt;&name=&lt;name&gt;&description=&lt;description&gt;</p>

        <p>	<label for="PostProject">Name</label>
            <input type="text" id="PostProject" name="name"></p>
        <p>	<label for="PostProjectDescription">Description</label>
            <input type="text" id="PostProjectDescription" name="description"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "post_project"));%>

        </p>

        <p>
            <input type="submit" value="Create" class="btn primary" id="submit_post_project" />
        </p>
        <h4>Response json:</h4>
        <p class="code">{ "Project" : &lt;key&gt; }</p>

    </fieldset>

</form>


    </div>
    <div style="float:left; width:33%">

<FORM METHOD=POST action="<% out.print(host); %>/Project" id="updateProjectForm" name="getProjectForm" accept-charset="UTF-8">

    <fieldset style="height:400px">
        <h3>Update Project</h3>
        <p>Update only base data. Not affecting uploaded documents</p>

        <p>The post request will either create a new entry or update an existing entry. For the update operation, all parameters has to be provided.</p>

        <p class="code">POST /Project?key=&lt;key&gt;&session=&lt;token&gt;&name=&lt;name&gt;&description=&lt;description&gt;</p>



        <p>	<label for="updateProjectId">Project</label>
            <input type="text" id="updateProjectId" name="key" size=50></p>
        <p>	<label for="updateProjectName">Name</label>
            <input type="text" id="updateProjectName" name="name"></p>
        <p>	<label for="updateProjectDescription">Description</label>
            <input type="text" id="updateProjectDescription" name="description"></p>

            <input type="hidden" name="html" value="on">
        <% out.print(getTokenParameter(useRealToken, "update_project"));%>

        </p>

        <p>
            <input type="submit" value="Update" class="btn primary" id="submit_update_project" />
        </p>

        <h4>Response json:</h4>
        <p class="code">{ "Project" : &lt;key&gt; }</p>

    </fieldset>

</form>

    </div>
    <div style="float:left; width:33%">

        <fieldset style="height:400px">

<FORM METHOD=GET action="<% out.print(host); %>/Project" id="getProjectForm" name="getProjectForm">
        <h3>Get/List Projects</h3>

        <p class="code">GET /Project?session=&lt;token&gt;&key=&lt;key&gt;</p>


        <p>	<label for="GetProject">Project</label>
            <input type="text" id="GetProject" name="key" size="50">

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "getProject"));%>
        </p>

        <p>
            <input type="submit" value="Get" class="btn primary" id="submit_get_project" />

        <!-- Inline quick form for get all -->
</form>

        <FORM METHOD=GET action="<% out.print(host); %>/Project" id="getAllProjectsForm" name="getAllProjectsForm">

                    <input type="hidden" name="html" value="on">
                    <% out.print(getTokenParameter(useRealToken, "projectFragment"));%>

                <input type="submit" value="Get All" class="btn primary" id="submit_fragment_for_project" />
        </FORM>

        </p>
            <h4>Response json:</h4>


            <p class="code">{ Project : [
                { "id" : "&lt;key&gt;", "name" : "&lt;project name&gt;", "description" : "&lt;description text&gt;". "owner" : "&lt;key&gt;", "creation" : "&lt;timestamp&gt;", "status" : "&lt;ANALYSIS STATUS&gt;", "noDocs" : "&lt;int&gt;",}
              ]
            }
            </p>

    </fieldset>



    </div>
    <div style="float:left; width:33%">

        <!--

<fieldset>


<FORM METHOD=GET action="/Project" id="getAllProjectsForm" name="getAllProjectsForm">
<h3>List All Projects</h3>

<input type="hidden" id="html_all_projects" name="html" value="on">
<input type="hidden" id="session_all_projects" name="session" value="DummySessionToken">

<p><input type="submit" value="List" class="btn primary" id="submit_fragment_for_project" /></p>
</FORM>
</fieldset>


-->


    </div>

        <div style="clear:both">&nbsp;</div>

        <div style="width:100%">

            <a id="DocumentLink"></a>
            <h1>Documents</h1>
            <p>Create and list documents</p>
        </div>

    <div style="float:left; width:33%">

        <FORM METHOD=GET action="<% out.print(host); %>/Document" id="getDocumentForm" name="getFragmentsForm">

            <fieldset style="height:330px">
                <h3>List Documents for Project</h3>

                <p><span class="code">GET /Document?session=&lt;token&gt;&project=&lt;key&gt;</span></p>


                <p>	<label for="GetDocumentsForProject">Project</label>
                    <input type="text" id="GetDocumentsForProject" name="project" value="<% out.print(demoProjectKey);%>" size="50"> </p>

                    <input type="hidden" name="html" value="on">
                <% out.print(getTokenParameter(useRealToken, "getDocuments"));%>
                </p>

                <p>
                    <input type="submit" value="All" class="btn primary" id="submit_get_documents" />
                </p>

                <h3>Response json:</h3>


        <p><span class="code">{ Document : [
            { "id" : "&lt;key1&gt;", "name" : "&lt;document name1&gt;", "description" : "&lt;description text&gt;", "project" : "&lt;project name&gt;"},
            { "id" : "&lt;key2&gt;", "name" : "&lt;document name2&gt;", "description" : "&lt;description text&gt;", "project" : "&lt;project name&gt;"}
          ]
        }

        </span></p>

            </fieldset>

        </FORM>



    </div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/Preview" id="viewDocumentForm" name="viewDocumentForm" accept-charset="UTF-8">
        <fieldset style="height:145px">
            <h3>View Document</h3>
            <p> Internal view of the document (without frontend)</p>

            <p>	<label for="ViewDocument">Document</label>
                <input type="text" id="ViewDocument" name="document" value="" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            <p>
                <input type="submit" value="View" class="btn primary" id="submit_viewDocumentForm" />
            </p>
        </fieldset>
    </FORM>



    <FORM METHOD=GET action="<% out.print(host); %>/Export" id="exportDocumentForm" name="exportDocumentForm" accept-charset="UTF-8">
        <fieldset style="height:145px">
            <h3>Export Document</h3>
            <p> Internal view of the document (without frontend)</p>

            <p>	<label for="exportDocument">Document</label>
                <input type="text" id="exportDocument" name="document" value="" size="50"></p>

            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            <p>
                <input type="submit" value="Export" class="btn primary" id="submit_exportDocumentForm" />
            </p>
        </fieldset>
    </FORM>



</div>

<div style="float:left; width:33%">


    <FORM METHOD=DELETE action="<% out.print(host); %>/Document" id="deleteDocumentForm" name="deleteDocumentForm" accept-charset="UTF-8">
        <fieldset style="height:330px">
            <h3>Delete Document</h3>

            <p>Recursively delete a document with all of the instances, clauses and fragments</p>
            <p>	<label for="deleteDocument">Document</label>
                <input type="text" id="deleteDocument" name="key" value="" size="50"></p>

            <input type="hidden" name="html" value="on">
            <input type="hidden" name="_method" value="DELETE" />
            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            </p>

            <p>
                <input type="submit" value="Delete" class="btn primary" id="submit_deleteDocumentForm" />
            </p>

            <h3>Response json:</h3>

                    <p><span class="code">{ Deleted : { "fragments" : n, "instances" : n, "clauses" : n}}</span>

        </fieldset>
    </FORM>

</div>



<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/DocumentDetails" id="documentDetailsForm" name="documentDeatailsForm" accept-charset="UTF-8">
        <fieldset style="height:330px">
            <h3>Get Document Details</h3>

            <p>Get details for a document</p>
            <p>	<label for="documentWithDetails">Document</label>
                <input type="text" id="documentWithDetails" name="key" value="" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            </p>

            <p>
                <input type="submit" value="Get" class="btn primary" id="submit_documentDetailForm" />
            </p>

            <h3>Response json:</h3>

                    <p><span class="code">{ DocumentDetails : { "id" : key, "name" : ..., "description" : .. , "project" : key,
                        "visibility" : .., "access" : .., "owner" : key, "version" : .., "history" : [], "creation" : date,
                        "modified" : date, "modified by" : key }}</span>


        </fieldset>
    </FORM>

</div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/VersionHistory" id="documentHistoryForm" name="documentHistoryForm" accept-charset="UTF-8">
        <fieldset style="height:330px">
            <h3>VersionHistory</h3>

            <p>	<label for="HistoryDocument">Document</label>
                <input type="text" id="HistoryDocument" name="document" value="<% out.print(demoDocumentKey);%>" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            </p>

            <p>
                <input type="submit" value="Get" class="btn primary" id="submit_documentHistoryForm" />
            </p>
        </fieldset>
    </FORM>

</div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/Diff" id="diffForm" name="diffForm" accept-charset="UTF-8">
        <fieldset style="height:330px">
            <h3>Document Diff</h3>
            <p>Gett the diff between two versions of the same document</p>

            <p>	<label for="ActiveVersion">Active</label>
                <input type="text" id="ActiveVersion" name="active" value="" size="50"></p>

            <p>	<label for="ReferenceVersion">Reference</label>
                <input type="text" id="ReferenceVersion" name="active" value="" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postDocument"));%>
            </p>

            <p>
                <input type="submit" value="Get Diff" class="btn primary" id="submit_diffForm" />
            </p>
        </fieldset>
    </FORM>

</div>



        <div style="clear:both">&nbsp;</div>

        <div style="width:100%">

            <a id="FragmentLink"></a>
            <h1>Fragments</h1>
            <p>Create, modify and look at fragments</p>
        </div>

    <div style="float:left; width:33%">


<FORM METHOD=GET action="<% out.print(host); %>/Fragment" id="getFragmentsForm" name="getFragmentsForm" accept-charset="UTF-8">

    <fieldset style="height:320px">
        <h3>List Fragments for Project</h3>

        <p><span class="code">GET /Fragment?session={token}&project={key}</span></p>


        <p>	<label for="GetFragmentsForProject">Project</label>
            <input type="text" id="GetFragmentsForProject" name="project" value="<% out.print(demoProjectKey);%>" size="50"> </p>

            <input type="hidden" name="html" value="on">
        <% out.print(getTokenParameter(useRealToken, "getProject"));%>
        </p>

        <p>
            <input type="submit" value="All in Proj" class="btn primary" id="submit_get_all_project" />
        </p>

        <h3>Response json:</h3>

                <p><span class="code">{ Fragment : [<br/>
        { "id" : "&lt;key1&gt;", "no" : n, "Text" : "&lt;fragment text1&gt;", "Document" : "&lt;Document name&gt;", "project" : "&lt;project name&gt;", "annotations" : n, "references" : n, "type" : "&lt;type&gt;", "classification" : "&lt;classification&gt;"},
      ]
    }
                    </span></p>


    </fieldset>

</FORM>

    </div>


<div style="float:left; width:33%">


    <FORM METHOD=GET action="<% out.print(host); %>/Fragment" id="getFragmentsForm2" name="getFragmentsForm" accept-charset="UTF-8">

        <fieldset style="height:320px">
            <h3>List Fragments for Document</h3>

            <p><span class="code">GET /Fragment?session={token}&document={key}</span></p>


            <p>	<label for="GetFragmentsForDocument">Document</label>
                <input type="text" id="GetFragmentsForDocument" name="document" value="<% out.print(demoDocumentKey);%>" size="50"> </p>

                <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "getProject"));%>
            </p>

            <p>
                <input type="submit" value="All in Doc" class="btn primary" id="submit_get_all_document" />
            </p>

            <h3>Response json:</h3>

            <p><span class="code">{ Fragment : [ <br/>
                { "id" : "&lt;key&gt;", "risk" : "&lt;key&gt;","text" : "&lt;fragment text1&gt;", "document" : "&lt;key&gt;",
                "project" : "&lt;key&gt;", "annotations" : int, "type" : "&lt;type&gt;", "classifications" : int, "references" : int,
                "no" : int, "display" : {"col" : int, "row" : int, "indentation" : int}}},
                ]
                }
               </span>
            </p>


        </fieldset>

    </FORM>

</div>



<div style="float:left; width:33%">


<FORM METHOD=GET action="<% out.print(host); %>/FragmentDetails" id="getFragmentDetailsForm" name="getFragmentsForm" accept-charset="UTF-8">

<fieldset style="height:320px">
    <h3>Get Details for a fragment</h3>

    <p><span class="code">GET /Fragment?session={token}&fragment={key}</span></p>


    <p>	<label for="GetDetailsFragment">Fragment</label>
        <input type="text" id="GetDetailsFragment" name="fragment" value="" size="50"> </p>

        <input type="hidden" name="html" value="on">
    <% out.print(getTokenParameter(useRealToken, "getProject"));%>

    <p>
        <input type="submit" value="All" class="btn primary" id="submit_get_details" />
    </p>

    <h3>Response json:</h3>

            <p><span class="code">

                { "FragmentDetail" : [
                {"risk":{"class":&lt;key&gt;,"time":&lt;date&gt;,"pattern":&lt;text&gt;,"classifier":&lt;key&gt;,"severity":&lt;0..100&gt;,"comment":&lt;text&gt;},<br/>
                "classifications":[{"id":&lt;key&gt;,"class":&lt;key&gt;,"pattern":&lt;text&gt;,"classifier":&lt;key&gt;,"comment":&lt;text&gt;}],<br/>
                "references":[&lt;key&gt;],<br/>
                "annotations":[{"annotation":&lt;text&gt;,"pattern":&lt;text&gt;,"annotator":&lt;key&gt;,"id":&lt;key1&gt;}]}]}<br/>


}
                </span></p>


</fieldset>

</FORM>

</div>



<div style="float:left; width:33%">

    <FORM METHOD=POST action="<% out.print(host); %>/Fragment" id="FragmentForm" name="postDocumentForm" accept-charset="UTF-8">
    <fieldset>
        <h3>Update Fragment</h3>
        <p>This is for moving fragments to a different clause</p>
        <p>	<label for="UpdateFragment">Fragment</label>
            <input type="text" id="UpdateFragment" name="fragment" size="50"></p>

        <p>	<label for="UpdateFragmentClause">Clause (optional)</label>
            <input type="text" id="UpdateFragmentClause" name="clause" size="50"></p>

        <p>	<label for="UpdateFragmentType">Type (optional)</label>
            <input type="text" id="UpdateFragmentType" name="type" size="50"></p>

        <input type="hidden" name="html" value="on">
        <% out.print(getTokenParameter(useRealToken, "updateFragment"));%>
        </p>

        <p>
            <input type="submit" value="Upload" class="btn primary" id="submit_updateFragmentForm" />
        </p>

        <h4>Response json:</h4>
        <p class="code">{ "Fragment" : &lt;key&gt;, "update" : &lt;true/false&gt; }</p>

    </fieldset>



    </FORM>


</div>

<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/FragmentType" id="FragmentTypeForm" name="getFragmentForm" accept-charset="UTF-8">
    <fieldset>
        <h3>Get Fragment Types</h3>

        <p>	<label for="UpdateFragment">Fragment</label>

        <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "getFragment"));%>
        </p>

        <p>
            <input type="submit" value="All" class="btn primary" id="submit_getFragmentTypeForm" />
        </p>

        <h4>Response json:</h4>
        <p class="code">{ "FragmentType" : [ <br/>
            {id : &lt;key&gt;, "name" : "name", "description" : "descr..." }<br/>
            {id : &lt;key&gt;, "name" : "name", "description" : "descr..." } ]</p>


    </fieldset>



    </FORM>

    </div>

    <div style="clear:both">&nbsp;</div>

    <div style="width:100%">

        <a id="AnnotationLink"></a>
        <h1>Annotate, classify and Risk</h1>
        <p>Set data for the fragment</p>
    </div>



    <div style="float:left; width:33%">

        <FORM METHOD=POST action="<% out.print(host); %>/Annotation" id="postAnnotationForm" name="postAnnotationForm" accept-charset="UTF-8">
        <fieldset style="height:320px">
            <h3>Annotate</h3>
            <p>For update - fill in a annotation key. Pattern is optional.</p>

            <p>	<label for="Annotation">Annotation</label>
                <input type="text" id="Annotation" name="annotation" size="50"></p>
            <p>	<label for="Fragment">Fragment</label>
                <input type="text" id="Fragment" name="fragment" size="50"></p>
            <p>	<label for="Pattern">Pattern</label>
                <input type="text" id="Pattern" name="pattern" size="50"></p>
            <p> <label for="AnnotationBody">Body</label> <textarea cols=30 rows=3 id="AnnotationBody" name="body"></textarea></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>

            <p>
                <input type="submit" value="Edit" class="btn primary" id="submit_postAnnotationForm" />
            </p>
        </fieldset>

        </FORM>

    </div>

<div style="float:left; width:33%">

    <FORM METHOD=POST action="<% out.print(host); %>/Risk" id="postRiskForm" name="postRiskForm" accept-charset="UTF-8">
    <fieldset style="height:320px">
        <h3>Classify Risk</h3>
        <p>Comment aand Pattern are optional.</p>

        <p>	<label for="RiskFragment">Fragment</label>
            <input type="text" id="RiskFragment" name="fragment" size="50"></p>
        <p>	<label for="CommentR">Comment</label>
            <input type="text" id="CommentR" name="comment" size="50"></p>
        <p>	<label for="PatternR">Pattern</label>
            <input type="text" id="PatternR" name="pattern" size="50"></p>
        <p> <label for="risk">Risk</label> <% out.print(riskDropdown);%></p>

        <input type="hidden" name="html" value="on">
        <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>
        </p>

        <p>
            <input type="submit" value="Edit" class="btn primary" id="submit_postRiskForm" />
        </p>
    </fieldset>

    </FORM>

</div>

<div style="float:left; width:33%">

    <FORM METHOD=POST action="<% out.print(host); %>/Classification" id="postClassificationForm" name="postClassificationForm" accept-charset="UTF-8">

        <fieldset style="height:320px">
            <h3>Add classification</h3>
            <p>Adding a classifcation to a fragment. Comment aand Pattern are optional.</p>

            <p>	<label for="FragmentC">Fragment</label>
                <input type="text" id="FragmentC" name="fragment" size="50"></p>
            <p>	<label for="CommentC">Comment</label>
                <input type="text" id="CommentC" name="comment" size="50"></p>
            <p>	<label for="PatternC">Pattern</label>
                <input type="text" id="PatternC" name="pattern" size="50"></p>
            <p> <label for="class">Class</label> <% out.print(classDropdown);%></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>

            <p>
                <input type="submit" value="Add" class="btn primary" id="submit_postClassificationForm" />
            </p>
        </fieldset>

    </FORM>

</div>


<div style="float:left; width:33%">

    <FORM METHOD=DELETE action="<% out.print(host); %>/Annotation" id="deleteAnnotationForm" name="postDocumentForm" accept-charset="UTF-8">
        <fieldset style="height:340px">
            <h3>Delete Annotation</h3>

            <p>	<label for="DeleteAnnotation">Annotation</label>
                <input type="text" id="DeleteAnnotation" name="annotation" size="50"></p>
            <input type="hidden" name="html" value="on">
            <input type="hidden" name="_method" value="DELETE" />
            <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>
            </p>

            <p>
                <input type="submit" value="Delete" class="btn primary" id="submit_deleteAnnotationForm" />
            </p>
        </fieldset>
    </FORM>
</div>

<div style="float:left; width:33%">

    <FORM METHOD=DELETE action="<% out.print(host); %>/Classification" id="deleteClassificationForm" name="postDocumentForm" accept-charset="UTF-8">
        <fieldset style="height:340px">
            <h3>Delete Classification</h3>

            <p>	<label for="DeleteClassification">Classification</label>
                <input type="text" id="DeleteClassification" name="key" size="50"></p>
            <input type="hidden" name="html" value="on">
            <input type="hidden" name="_method" value="DELETE" />
            <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>
            </p>

            <p>
                <input type="submit" value="Delete" class="btn primary" id="submit_deleteClassificationForm" />
            </p>
        </fieldset>
    </FORM>
</div>



<div style="float:left; width:33%">

    <FORM METHOD=POST action="<% out.print(host); %>/Class" id="postClassForm" name="postClassForm" accept-charset="UTF-8">

        <fieldset style="height:340px">
            <h3>Create a new Class</h3>
            <p>Adding a class to use with the classification of fragments</p>

            <p>	<label for="className">name</label>
                <input type="text" id="className" name="name" size="50"></p>
            <p>	<label for="classDescription">Comment</label>
                <input type="text" id="classDescription" name="description" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postAnnotation"));%>

            <p>
                <input type="submit" value="Edit" class="btn primary" id="submit_postClassForm" />
            </p>
        </fieldset>

    </FORM>

</div>


<div style="float:left; width:33%">


    <FORM METHOD=DELETE action="<% out.print(host); %>/Class" id="deleteClassForm" name="deleteClassForm" accept-charset="UTF-8">
        <fieldset style="height:340px">
            <h3>Delete Class</h3>

            <p>	<label for="DeleteClassification">Classification</label>
                <input type="text" id="DeleteClass" name="key" size="50"></p>
            <input type="hidden" name="html" value="on">
            <input type="hidden" name="_method" value="DELETE" />
            <% out.print(getTokenParameter(useRealToken, "deleteClass"));%>
            </p>

            <p>
                <input type="submit" value="Delete" class="btn primary" id="submit_deleteClassForm" />
            </p>
        </fieldset>
    </FORM>
</div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/Class" id="getClassificationForm" name="getClassificationForm" accept-charset="UTF-8">
        <fieldset style="height:155px">
            <h3>Get All Classes</h3>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "getClassification"));%>
            <p>
                <input type="submit" value="All" class="btn primary" id="submit_getClassificationForm" />
            </p>
        </fieldset>
    </FORM>

    <FORM METHOD=GET action="<% out.print(host); %>/Risk" id="getRiskForm" name="getRiskForm" accept-charset="UTF-8">
        <fieldset style="height:155px">
            <h3>Get All Risks</h3>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "getRisk"));%>

            <p>
                <input type="submit" value="All" class="btn primary" id="submit_getRiskForm" />
            </p>
        </fieldset>
    </FORM>
</div>



<div style="clear:both">&nbsp;</div>

<div style="width:100%">

    <a id="ActionLink"></a>
    <h1>Actions</h1>
    <p>Collaboration actions</p>
</div>


<div style="float:left; width:33%">

    <FORM METHOD=POST action="<% out.print(host); %>/Action" id="postActionForm" name="postActionForm" accept-charset="UTF-8">

        <fieldset style="height:420px">
            <h3>Add/Update Action</h3>
            <p>Adding or updating an action</p>

            <p>	<label for="ActionName">Name</label>
                <input type="text" id="ActionName" name="name" size="50"></p>
            <p>	<label for="Action">Action</label>
                <input type="text" id="Action" name="action" size="50"></p>
            <p>	<label for="ActionFragment">Fragment</label>
                <input type="text" id="ActionFragment" name="fragment" size="50"></p>
            <p>	<label for="ActionText">Description</label>
                <input type="text" id="ActionText" name="description" size="50"></p>
            <p>	<label for="Assignee">Assignee</label>
                <input type="text" id="Assignee" name="assignee" size="50"></p>
            <p>	<label for="Priority">Priority</label>
                <input type="text" id="Priority" name="priority" size="50"></p>
            <p> <label for="status">State</label> <% out.print(statusDropdown);%></p>
            <p>	<label for="DueDate">Due Date</label>
                <input type="text" id="DueDate" name="dueDate" size="50"></p>
            <p>	<label for="CompleteDate">Complete Date</label>
                <input type="text" id="CompleteDate" name="completeDate" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "postAction"));%>

            <p>
                <input type="submit" value="Edit" class="btn primary" id="submit_postActionForm" />
            </p>
        </fieldset>

    </FORM>

    </div>

    <div style="float:left; width:33%">


        <FORM METHOD=GET action="<% out.print(host); %>/Action" id="getActionForm" name="deleteActionForm" accept-charset="UTF-8">

            <fieldset style="height:200px">
                <h3>Get all Actions</h3>
                <p>Get all actions for a project</p>

                <p>	<label for="ActionProject">Project</label>
                    <input type="text" id="ActionProject" name="project" size="50" value="<% out.print(demoProjectKey);%>"></p>

                <input type="hidden" name="html" value="on">
                <% out.print(getTokenParameter(useRealToken, "getAction"));%>

                <p>
                    <input type="submit" value="Get" class="btn primary" id="submit_getActionForm" />
                </p>
            </fieldset>

        </FORM>


        <FORM METHOD=DELETE action="<% out.print(host); %>/Action" id="deleteActionForm" name="deleteActionForm" accept-charset="UTF-8">

            <fieldset style="height:200px">
                <h3>Delete Action</h3>
                <p>Deleting an action</p>

                <p>	<label for="DeleteAction">Action</label>
                    <input type="text" id="DeleteAction" name="action" size="50"></p>

                <input type="hidden" name="html" value="on">
                <input type="hidden" name="_method" value="DELETE" />
                <% out.print(getTokenParameter(useRealToken, "deleteAction"));%>

                <p>
                    <input type="submit" value="Delete" class="btn primary" id="submit_deleteActionForm" />
                </p>
            </fieldset>

        </FORM>


    </div>



<div style="float:left; width:33%">


    <FORM METHOD=POST action="<% out.print(host); %>/ConvertToAction" id="convertActionForm" name="convertActionForm" accept-charset="UTF-8">

        <fieldset style="height:200px">
            <h3>Convert To Action</h3>
            <p>Converting an annotation to an action by providing an assignee </p>

            <p>	<label for="ConvertAnnotation">Annotation</label>
                <input type="text" id="ConvertAnnotation" name="annotation" size="50"></p>

            <p>	<label for="ConvertAssignee">Assignee</label>
                <input type="text" id="ConvertAssignee" name="assignee" size="50"></p>


            <input type="hidden" name="html" value="on">
            <input type="hidden" name="_method" value="DELETE" />
            <% out.print(getTokenParameter(useRealToken, "deleteAction"));%>

            <p>
                <input type="submit" value="Convert" class="btn primary" id="submit_convertActionForm" />
            </p>
        </fieldset>

    </FORM>

    <FORM METHOD=GET action="<% out.print(host); %>/ActionStatus" id="ActionStatusForm" name="ActionStatusForm" accept-charset="UTF-8">

        <fieldset style="height:200px">
            <h3>Get Action Statuses</h3>
            <p>Return all the possible statuses for the actions</p>


            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "actionStatus"));%>

            <p>
                <input type="submit" value="Convert" class="btn primary" id="submit_ActionStatusForm" />
            </p>
        </fieldset>

    </FORM>


</div>

<div style="clear:both">&nbsp;</div>


<div style="width:100%">

    <a id="SearchLink"></a>
    <h1>Search</h1>
    <p>Search for a string in a project</p>
</div>



<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/Search" id="searchForm" name="searchForm" accept-charset="UTF-8">

        <fieldset style="height:440px">
            <h3>Search in project</h3>

            <p><span class="code">GET /Search?session={token}&project={key}&text=searchPhrase&project={key}</span></p>

                <p>	<label for="searchText">Search Phrase. </label>
                    <input type="text" id="searchText" name="text" size="50"> </p>

            <p>	<label for="searchProject">Project</label>
                <input type="text" id="searchProject" name="project" value="<% out.print(demoProjectKey);%>" size="50"> </p>

                <input type="hidden" name="html" value="on">
                <% out.print(getTokenParameter(useRealToken, "search"));%>
            </p>

            <p>
                <input type="submit" value="Search" class="btn primary" id="submit_search" />
            </p>

            <h3>Response json:</h3>

                <p>The response will contain fragment numbers and document key. For each response there is a list of matching patterns that are the words in the fragment that shall be highlighted.</p>
                    <p><span class="code">{ "fragments" : [
                        { "fragment" : "&lt;num1&gt;", "document" : "&lt;key&gt;", "patternlist" : ["&lt;match pattern&gt;", "&lt;match pattern&gt;"] }
                        { "fragment" : "&lt;num2&gt;", "document" : "&lt;key&gt;", "patternlist" : ["&lt;match pattern&gt;"] }
            ]
            }
                        </span></p>


        </fieldset>

    </FORM>

</div>


<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/SearchSelection" id="searchSelectionForm" name="searchSelectionForm" accept-charset="UTF-8">

        <fieldset style="height:440px">
            <h3>Search Export</h3>

            <p><span class="code">GET /Search?session={token}&project={key}&text=searchPhrase&project={key}</span></p>

                <p>	<label for="searchSelectionText">Search Phrase. </label>
                    <input type="text" id="searchSelectionText" name="text" size="50"> </p>

            <p>	<label for="searchSelectionProject">Project</label>
                <input type="text" id="searchSelectionProject" name="project" value="<% out.print(demoProjectKey);%>" size="50"> </p>

                <input type="hidden" name="html" value="on">
                <% out.print(getTokenParameter(useRealToken, "search"));%>
            </p>

            <p>
                <input type="submit" value="Search & Export" class="btn primary" id="submit_searchSelection" />
            </p>

            <h3>Response json:</h3>

                <p>The response will contain fragment numbers and document key. For each response there is a list of matching patterns that are the words in the fragment that shall be highlighted.</p>
                    <p><span class="code">{ "fragments" : [
                        { "fragment" : "&lt;num1&gt;", "document" : "&lt;key&gt;", "patternlist" : ["&lt;match pattern&gt;", "&lt;match pattern&gt;"] }
                        { "fragment" : "&lt;num2&gt;", "document" : "&lt;key&gt;", "patternlist" : ["&lt;match pattern&gt;"] }
            ]
            }
                        </span></p>


        </fieldset>

    </FORM>

</div>



<div style="float:left; width:33%">


<FORM METHOD=GET action="<% out.print(host); %>/Keywords" id="keywordForm" name="searchForm" accept-charset="UTF-8">

    <fieldset style="height:440px">

        <h3>Index keywords in project</h3>

        <p><span class="code">GET /Keywords?session={token}&project={key}&project={key}</span></p>


        <p>	<label for="keywordProject">Project</label>
            <input type="text" id="keywordProject" name="project" value="<% out.print(demoProjectKey);%>" size="50"> </p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "search"));%>
        </p>

        <p>
            <input type="submit" value="Get" class="btn primary" id="submit_keyword" />
        </p>

        <h3>Response json:</h3>

            <p>The response will contain a list of keywords</p>
                <p><span class="code">{ "keywords" : [
                    { "keyword" : "&lt;word1&gt;"},
                    { "keyword" : "&lt;word2&gt;"}
        ]
        }
                    </span></p>


    </fieldset>

</FORM>

</div>

<div style="clear:both">&nbsp;</div>

<div style="width:100%">

    <a id="UploadLink"></a>
    <h1>Upload</h1>
    <p>Upload documents in a project</p>
</div>

<div style="float:left; width:33%">

    <form action="<% out.print(host); %>/Upload" method="post" enctype="multipart/form-data" accept-charset="UTF-8">

        <fieldset style="height:400px">
            <h3>Upload a .docx document into a project</h3>

            <p>	<label for="upload_project">Project</label>
                <input type="text" id="upload_project" name="project" value="<% out.print(demoProjectKey);%>" size="50"></p>

            <p>	<label for="upload_title">Title</label>
                <input type="text" id="upload_title" name="title" value="" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "upload"));%>
            <p>
                <label for="upload_file">File</label>
                <input type="file" id="upload_file" name="file" size="50" /></p>

            <input type="submit" value="Upload File" />


        <h3>Response json:</h3>

            <p>The response:
                <p><span class="code">{ "document" : "&lt;file name&gt;" )</span></p>
        </fieldset>
    </form>

</div>


<div style="float:left; width:33%">

    <form action="<% out.print(host); %>/Upload" method="post" enctype="multipart/form-data" accept-charset="UTF-8">

        <fieldset style="height:400px">
            <h3>Replace an existing .docx document</h3>

            <!-- NOTE: The token must come before the document. Otherwise we cant validate the read access of the document-->

            <% out.print(getTokenParameter(useRealToken, "upload"));%>

            <p>	<label for="upload_document">Document</label>
                <input type="text" id="upload_document" name="document" value="<% out.print(demoDocumentKey);%>" size="50"></p>

            <input type="hidden" name="html" value="on">
            <p>
                <label for="replace_file">File</label>
                <input type="file" id="replace_file" name="file" size="50" /></p>

            <input type="submit" value="Upload File" />


        <h3>Response json:</h3>

            <p>The response:
                <p><span class="code">{ "document" : "&lt;file name&gt;" )</span></p>
        </fieldset>
    </form>

</div>

<div style="float:left; width:33%">

    <form action="<% out.print(host); %>/Ping" method="post" enctype="multipart/form-data">

        <fieldset style="height:400px">
            <h3>Send a quick ping</h3>


            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "ping"));%>

            <input type="submit" value="Ping" />


        <h3>Response json:</h3>

            <p>The response:
                <p><span class="code">{ "pong" : "&lt;&gt;" )</span></p>
        </fieldset>
    </form>

</div>

<div style="clear:both">&nbsp;</div>

<div style="width:100%">

    <a id="ReferencesLink"></a>
    <h1>References</h1>
    <p>Get references for bubble diagrams</p>
</div>

<div style="float:left; width:33%">

    <FORM METHOD=GET action="<% out.print(host); %>/DocumentReference" id="referenceForm" name="referenceForm" accept-charset="UTF-8">

        <fieldset style="height:400px">
            <h3>Get document references</h3>

            <p>	<label for="reference_project">Project</label>
                <input type="text" id="reference_project" name="project" value="<% out.print(demoProjectKey);%>" size="50"></p>

            <input type="hidden" name="html" value="on">
            <% out.print(getTokenParameter(useRealToken, "upload"));%>

            <input type="submit" value="Get References" />


        <h3>Response json:</h3>

            <p>The response:
                <p><span class="code">{ "document" : "&lt;file name&gt;" )</span></p>
        </fieldset>
    </form>

</div>



</body>

<%!
    String getTokenParameter(boolean realToken, String form){

        if(realToken)
            return "<p>\t<label for=\""+ form+"_token\">Token</label>\n" +
                   " <input type=\"text\" id=\""+ form+"token\" name=\"session\" size=\"30\"></p>\n";
        else
            return "<input type=\"hidden\" name=\"session\" value=\"DummyAdminToken\">\n";
}

%>

</html>