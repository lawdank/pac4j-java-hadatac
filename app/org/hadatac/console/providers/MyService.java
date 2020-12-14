package org.hadatac.console.providers;

import akka.actor.Cancellable;
import com.feth.play.module.mail.IMailer;
import com.feth.play.module.mail.Mailer;
import com.google.inject.Inject;
import org.apache.commons.mail.EmailAttachment;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.Environment;



public class MyService {
    private static IMailer mailer;
    @Inject
    private MailerClient mailerClient;


    public void doMail() {
        String cid = "1234";
        Email email = new Email()
                .setSubject("Simple email")
                .setFrom("user21email@yahoo.com")
                .addTo("user21email@yahoo.com")
//                .addAttachment("attachment.pdf", new File("/some/path/attachment.pdf"))
//                .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
//                .addAttachment("image.jpg", new File("/some/path/image.jpg"), cid)
                .setBodyText("A text message")
                .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
        mailerClient.send(email);
    }

    protected static Cancellable sendMail(final String subject, final Mailer.Mail.Body body,
                                          final String recipient) {
        return sendMail(new Mailer.Mail(subject, body, recipient));
    }

    /**
     * Send a pre-assembled mail.
     *
     * @param mail
     *            The mail to be sent.
     * @return The {@link akka.actor.Cancellable} that can be used to cancel the
     *         action.
     */
    protected static Cancellable sendMail(final Mailer.Mail mail) {
        return mailer.sendMail(mail);
    }
}
