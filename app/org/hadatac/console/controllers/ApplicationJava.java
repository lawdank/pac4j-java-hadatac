package org.hadatac.console.controllers;

import org.apache.commons.mail.EmailAttachment;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;
import play.Environment;

import javax.inject.Inject;
import java.io.File;

public class ApplicationJava extends Controller {

    private final Environment environment;
    private final MailerClient mailer;

    @Inject
    public ApplicationJava(Environment environment, MailerClient mailer) {
        this.environment = environment;
        this.mailer = mailer;
    }

    public Result send() {
        String cid = "1234";
        final Email email = new Email()
                .setSubject("Simple email")
                .setFrom("Mister FROM <user21email@yahoo.com>")
                .addTo("Miss TO <user21email@yahoo.com>")
//                .addAttachment("favicon.png", new File(environment.getFile("public/images/favicon.png"), cid))
//                .addAttachment("org.hadatac.data.txt", "org.hadatac.data".getBytes(), "text/plain", "Simple org.hadatac.data", EmailAttachment.INLINE)
                .setBodyText("A text message")
                .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
        String id = mailer.send(email);
        System.out.println("environment:"+environment+",mailer:"+mailer);
        return ok("Email " + id + " sent!");
    }
}