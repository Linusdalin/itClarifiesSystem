package services;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import backend.ItClarifies;
import log.PukkaLogger;
import pukkaBO.backOffice.BackOfficeInterface;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*****************************************************************''
 *
 *          This is deprecated. We use the file in PukkaCore
 *
 *
 */

public class CreateDBServletOLD extends HttpServlet {

    // TODO: Add password protection here
    // TODO: Move this to PukkaBackoffice and parameterize the backOffice

    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        String action = req.getParameter("Action");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        BackOfficeInterface bo = new ItClarifies( );

        /*
        if(user == null){

            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));


        }else
        */
        {

            if(action == null){

                resp.sendRedirect("admin.jsp?message=Error Nob action given.");
                return;
            }

            if(action.equals("CREATE")){

                PukkaLogger.log(PukkaLogger.Level.MAJOR_EVENT, "Creating database");

                bo.createDb();

                resp.sendRedirect("admin.jsp?message=Database Created");

            }

            if(action.equals("CONSTANTS")){

                PukkaLogger.log(PukkaLogger.Level.ACTION, "Populating constants");

                bo.populateValues(false);
                bo.populateSpecificValues();


                resp.sendRedirect("admin.jsp?message=Constants Created");       //TODO: This should pass a message on the actual changes in the database

            }

            if(action.equals("TESTDATA")){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Populating test values");

                bo.populateValues(true);
                bo.populateSpecificValues();


                resp.sendRedirect("admin.jsp?message=Test values");       //TODO: This should pass a message on the actual changes in the database

            }



        }

    }
}