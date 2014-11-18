package email;

import actions.Action;
import contractManagement.Project;
import pukkaBO.email.MailInterface;
import userManagement.PortalUser;

/********************************************************************************
 *
 *          Email sending action update notification
 *
 *
 */


public class NewActionMail extends ItClarifiesMail implements MailInterface {


    public NewActionMail(Project project, Action action, PortalUser updater){

        super();

        subject = ActionMailSubject
                .replaceAll("-PROJECT-", project.getName());


        body = ActionMailBody
                .replaceAll("-ACTION-", action.getName())
                .replaceAll("-UPDATER-", updater.getName())
                .replaceAll("-STATUS-", action.getStatus().getName());

    }


    private static final String ActionMailBody = "<p>A new action \"-ACTION-\" was created by user <b>-UPDATER-</b< and assigned to you.</p>";

    private static final String ActionMailSubject = "New Action  for you in Project -PROJECT-";


}
