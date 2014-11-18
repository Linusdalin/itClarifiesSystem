<%@ page import="pukkaBO.acs.AccessControlSystem" %>
<%@ page import="pukkaBO.acs.ACS_LoginMethod" %>

<jsp:useBean id="cookieManager" scope="session" class="pukkaBO.acs.CookieBean"/>

<%
    // Verify that the user is logged in and otherwise redirect to the correct alternative page.


    /****************************************************************************************

        Check if the user is logged in and if so get the userId from the player management system.

    */

    final String sessionKeyCookie = "adminLoginKey";
    final String userNameCookie = "adminLoginName";
    String sessionCookie = "";


    // parameters to access

    String loginUsername = request.getParameter("loginUsername");
    String loginPassword = request.getParameter("loginPassword");

    System.out.println("-> Got username:" + loginUsername + " and pwd " + loginPassword);

    boolean logInSuccess = false;
    boolean logout = false;



    try{

        logout = request.getParameter("logout").equals("true");

    }
    catch (Exception e) {}


/*
        Cookie cookie = new Cookie("connectToLocal", "connectToLocal");
        cookie.setMaxAge(60 * 60); //1 h in seconds
        response.addCookie(cookie);

*/


    if(loginUsername != null && loginPassword != null && !loginUsername.equals("") && !loginPassword.equals("")){

       // We have a username and a password. Try to login

       adminUser = acsSystem.login(loginUsername, loginPassword);

       if(adminUser < 1){

           //not logged in. Remove cookies

           System.out.println(loginUsername + " failed login");

           response.addCookie(cookieManager.expireCookie(request.getCookies(), sessionKeyCookie));
           response.addCookie(cookieManager.expireCookie(request.getCookies(), userNameCookie));

       }
       else
       {
           //logged in successfully

           System.out.println(loginUsername + " successfully logged in. User = " + adminUser);


           //We need to store the sessionCookie

           sessionCookie = acsSystem.getSessionKey();

           response.addCookie(cookieManager.createCookie(sessionKeyCookie, sessionCookie, 5));
           response.addCookie(cookieManager.createCookie(userNameCookie, loginUsername, 10*24));

           adminUserName = loginUsername; //Store the username

           // Read default section

           selection = cookieManager.readCookie(request.getCookies(), "startSection");
           System.out.println("**** Starting with  section " + selection);

           listParameter = cookieManager.readCookie(request.getCookies(), "startList");
           if(listParameter != null && !listParameter.equals("")){

               list = backOffice.getListByName(listParameter);
               if(list != null)
                   pageTitle = list.getTitle();

               action = "list";
               //System.out.println("**** Starting with  list " + list.getName());
           }

           tableParameter = cookieManager.readCookie(request.getCookies(), "startTable");

           if(tableParameter != null && !tableParameter.equals("")){

               table = backOffice.getTableByName(tableParameter);
               if(table != null)
                   pageTitle = table.getTitle();

               action = "list";
               //System.out.println("**** Starting with  table " + table.getTableName());
           }

           pageParameter = cookieManager.readCookie(request.getCookies(), "startPage");

           if(pageParameter != null && !pageParameter.equals("")){

               pageComponent = backOffice.getPageByName(pageParameter);
               if(pageComponent != null)
                   pageTitle = pageComponent.getTitle();

           }


           formParameter = cookieManager.readCookie(request.getCookies(), "startForm");

           if(formParameter != null && !formParameter.equals("")){

               form = backOffice.getFormByName(formParameter);
               if(form != null)
                   pageTitle = form.getTitle();

               //System.out.println("**** Starting with  form " + form.getName());
           }

       }
    }
    else{

        // No username given, get the username from the cookie

        System.out.print("Getting user from cookie " + userNameCookie);


        adminUserName = cookieManager.readCookie(request.getCookies(), userNameCookie);
        sessionCookie = cookieManager.readCookie(request.getCookies(), sessionKeyCookie);

        if(logout){

            System.out.println("Logging out...");

            acsSystem.logout(adminUserName);

            response.addCookie(cookieManager.expireCookie(request.getCookies(), sessionKeyCookie));
            response.addCookie(cookieManager.expireCookie(request.getCookies(), userNameCookie));

        }
        else{

            System.out.println(" Verifying session " + sessionCookie + " for user " + adminUser);

            // Verify the session

            adminUser = acsSystem.validateSession(adminUserName, sessionCookie);

            if(adminUser > 0){

                //There is a user

                response.addCookie(cookieManager.createCookie(sessionKeyCookie, sessionCookie, 5));
                response.addCookie(cookieManager.createCookie(userNameCookie, adminUserName, 10*24));

            }

        }
    }

    System.out.println("Admin user = " + adminUser);

    if(adminUser <= 0){

        String notLogedInPage = "login.jsp";

        try{

            if(selection != null)
                notLogedInPage = backOffice.getFrontPage(selection);
        }
        catch(BackOfficeException e){

            e.logError("Unknown section " + selection + " given. Using default login.");
        }

        //System.out.println("brk2");

        // Add an error message

        if(adminUser < AccessControlSystem.SESSION_TIMEOUT){

            notLogedInPage +="Session has expired";

        }else if(adminUser < AccessControlSystem.WRONG_PWD){

            notLogedInPage +="Incorrect password";

        }else if(adminUser < AccessControlSystem.NO_USER){

            notLogedInPage +="Unknown user";

        }else if(adminUser < 0){

            notLogedInPage +="Wrong user or password";
        }

        System.out.println("Redirecting with parameter: " + notLogedInPage);




        if(selection == null || selection.equals(""))
            selection = "Home";


        // Set cookies

        if( !selection.equals("")){

            response.addCookie(cookieManager.createCookie("startSection", selection, 10*24));
            System.out.println("**** Setting the section to " + selection);
        }

        if( tableParameter != null && !tableParameter.equals("")){

            response.addCookie(cookieManager.createCookie("startTable", tableParameter, 10*24));
            System.out.println("**** Setting the table to " + tableParameter);
        }

        if( pageParameter != null && !pageParameter.equals("")){

            response.addCookie(cookieManager.createCookie("startPage", pageParameter, 10*24));
            System.out.println("**** Setting the page to " + pageParameter);
        }


        if( listParameter != null && !listParameter.equals("")){

            response.addCookie(cookieManager.createCookie("startList", listParameter, 10*24));
            System.out.println("**** Setting the list to " + listParameter);
        }

        if( formParameter != null && !formParameter.equals("")){

            response.addCookie(cookieManager.createCookie("startForm", formParameter, 10*24));
            System.out.println("**** Setting the form to " + formParameter);
        }



        %>
            <jsp:forward page="login.jsp">


                <jsp:param name="error" value="<%=notLogedInPage %>" />
            </jsp:forward>

          <%




        //System.out.println("Still here");

    }



    //Login or session verified correctly. Return to the including page and continue


%>