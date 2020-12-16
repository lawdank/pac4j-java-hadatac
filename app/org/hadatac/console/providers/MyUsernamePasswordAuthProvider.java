package org.hadatac.console.providers;

import javax.inject.Inject;

import akka.actor.Cancellable;
import com.feth.play.module.mail.IMailer;
import com.feth.play.module.mail.Mailer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.hadatac.Constants;
import org.hadatac.console.controllers.routes;
import org.hadatac.console.models.SysUser;
import org.hadatac.console.models.TokenAction;
import org.springframework.security.crypto.bcrypt.BCrypt;
import play.Logger;
import play.api.i18n.Messages;
import play.api.i18n.MessagesApi;
import play.api.libs.mailer.MailerClient;
import play.api.mvc.RequestHeader;
import play.data.validation.Constraints;
import play.mvc.Http;
import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.mail.Mailer.MailerFactory;
import play.i18n.Lang;
import scala.collection.Seq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import static org.hadatac.Constants.EMAIL_TEMPLATE_FALLBACK_LANGUAGE;

/**
 * A form processing DTO that maps to the widget form.
 *
 * Using a class specifically for form binding reduces the chances
 * of a parameter tampering attack and makes code clearer, because
 * you can define constraints against the class.
 */
public class MyUsernamePasswordAuthProvider {

    @Constraints.Required
    private String name;
    @Constraints.Required
    @Constraints.Email
    private String email;
    @Constraints.Required
    @Constraints.MinLength(5)
    private String password;
    private String repeatPassword;

    private MessagesApi messagesApi;
    private Config config;
    MyService myService;


    @Inject
    public MyUsernamePasswordAuthProvider(MessagesApi messagesApi, MyService myService) {
        this.messagesApi = messagesApi;
        this.myService = myService;
    }

    public MyUsernamePasswordAuthProvider() {
    }
    public static class MyIdentity {

        public MyIdentity() {
        }

        public MyIdentity(final String email) {
            this.email = email;
        }

        @Constraints.Required
        @Constraints.Email
        public String email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

//    public static IMailer getMailer() {
//        return mailer;
//    }

    public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
				return "Passwords do not match";
			}
			return null;
		}

	public String getHashedPassword() {
        return createPassword(this.password);
    }

    protected String createPassword(final String clearString) {
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }


    public void sendPasswordResetMailing(final SysUser user, final Http.Request request) {
        final String token = generatePasswordResetRecord(user);
        final String subject = getPasswordResetMailingSubject(user, request);
        final Body body = getPasswordResetMailingBody(token, user, request);
        myService.sendMail(subject, body, getEmailName(user));
    }



    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    protected String generatePasswordResetRecord(final SysUser u) {
        final String token = generateToken();
        TokenAction.create(TokenAction.Type.PASSWORD_RESET, token, u);
        return token;
    }

    //TODO : fix this
    protected String getPasswordResetMailingSubject(final SysUser user,
                                                    final Http.Request request) {
        return "How to reset your password";
//        Messages messages = this.messagesApi.preferred((RequestHeader) request.acceptLanguages());
//        messages.apply("test");
//        return this.messagesApi.preferred((Http.RequestHeader) request.acceptLanguages()).at(
//                "How to reset your password");
    }

    protected String getEmailTemplate(final String template,
                                      final String langCode, final String url, final String token,
                                      final String name, final String email) {
        Class<?> cls = null;
        String ret = null;
        try {
            cls = Class.forName(template + "_" + langCode);
        } catch (ClassNotFoundException e) {
            Logger.warn("Template: '"
                    + template
                    + "_"
                    + langCode
                    + "' was not found! Trying to use English fallback template instead.");
        }
        if (cls == null) {
            try {
                cls = Class.forName(template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
            } catch (ClassNotFoundException e) {
                Logger.error("Fallback template: '" + template + "_"
                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE
                        + "' was not found either!");
            }
        }
        if (cls != null) {
            Method htmlRender = null;
            try {
                htmlRender = cls.getMethod("render", String.class,
                        String.class, String.class, String.class);
                ret = htmlRender.invoke(null, url, token, name, email)
                        .toString();

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    //todo : fix it
    protected Body getPasswordResetMailingBody(final String token,
                                               final SysUser user, final Http.Request request) {

        final boolean isSecure = true; //getConfiguration().getBoolean(Constants.SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
        final String url = routes.Signup.resetPassword(token).absoluteURL(
                isSecure, ConfigFactory.load().getString("hadatac.console.base_url"));

//        final Lang lang = (Lang) this.messagesApi.preferred((Http.RequestHeader) request.acceptLanguages()).lang();
        final String langCode = "en"; //lang.code();

        final String html = getEmailTemplate(
                "org.hadatac.console.views.html.account.email.password_reset", langCode, url,
                token, user.getName(), user.getEmail());
        final String text = getEmailTemplate(
                "org.hadatac.console.views.txt.account.email.password_reset", langCode, url, token,
                user.getName(), user.getEmail());

        return new Body(text, html);
    }
    private String getEmailName(final SysUser user) {
        return Mailer.getEmailName(user.getEmail(), user.getName());
    }
}