package email;

import actions.Action;
import contractManagement.Project;
import pukkaBO.email.MailInterface;
import userManagement.PortalUser;

/********************************************************************************
 *
 *          Email sending action update notification
 *
 *          The mail is built by project, action and updater
 *
 *
 */


public class ActionUpdateMail extends ItClarifiesMail implements MailInterface {


    public ActionUpdateMail(Project project, Action action, PortalUser updater){

        super();

        subject = ActionMailSubject
                .replaceAll("-PROJECT-", project.getName());

        body = ActionMailBody
                .replaceAll("-ACTION-", action.getName())
                .replaceAll("-UPDATER-", updater.getName())
                .replaceAll("-STATUS-", action.getStatus().getName());

    }


    private static final String ActionMailBody = "<p>The status of action <b>\"-ACTION-\"</b> was updated by user -UPDATER- to status -STATUS-.<br>\n You are receiving this email because you are subscribing to the action.</p>";

    private static final String ActionMailSubject = "Project -PROJECT- Action Update";


}
