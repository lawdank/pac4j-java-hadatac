package providers;

import com.google.inject.Inject;
import org.apache.commons.mail.EmailAttachment;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;
import play.Environment;



public class MyService {
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
}
