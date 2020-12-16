package org.hadatac.console.controllers;

import org.hadatac.console.models.SysUser;
import org.hadatac.console.models.TokenAction;
import org.hadatac.console.providers.MyService;
import org.hadatac.console.providers.MyUsernamePasswordAuthProvider;
import org.hadatac.console.views.html.account.signup.no_token_or_invalid;
import org.hadatac.console.views.html.account.signup.password_forgot;
import org.hadatac.console.views.html.account.signup.password_reset;
import play.api.libs.mailer.MailerClient;
import play.i18n.MessagesApi;
import play.data.validation.Constraints;
import play.mvc.Http;
import play.mvc.Result;
import play.data.Form;
import play.data.FormFactory;
import org.hadatac.console.models.TokenAction.Type;

import javax.inject.Inject;

import static play.mvc.Results.*;


public class Signup {

    @Constraints.Validate
    public static class PasswordReset extends Account.PasswordChange implements Constraints.Validatable<String> {

        public PasswordReset() {}

        public PasswordReset(MessagesApi messagesApi) {
            super(messagesApi);
        }

        public PasswordReset(final String token, MessagesApi messagesApi) {
            super(messagesApi);
            this.token = token;
        }

        public String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String validate() {
            return null;
        }
    }

    private final Form<PasswordReset> PASSWORD_RESET_FORM;

    private final Form<MyUsernamePasswordAuthProvider.MyIdentity> FORGOT_PASSWORD_FORM;
    private final MyUsernamePasswordAuthProvider userPaswAuthProvider;
    private final MessagesApi msg;
    MyService myService;

    @com.google.inject.Inject
    MailerClient mailerClient;


//    public void doMail() {
//        String cid = "1234";
//        Email email = new Email()
//                .setSubject("Simple email")
//                .setFrom("user21email@gmail.com")
//                .addTo("user21email@gmail.com")
////                .addAttachment("attachment.pdf", new File("/some/path/attachment.pdf"))
////                .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
////                .addAttachment("image.jpg", new File("/some/path/image.jpg"), cid)
//                .setBodyText("A text message")
//                .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
//        mailerClient.send(email);
//    }

    //TODO : fix this
    @Inject
    public Signup(//final PlayAuthenticate auth, final UserProvider userProvider,
                  final MyUsernamePasswordAuthProvider userPaswAuthProvider,
                  final FormFactory formFactory, final MessagesApi msg, MyService myService) {
//        this.auth = auth;
//        this.userProvider = userProvider;
        this.userPaswAuthProvider = userPaswAuthProvider;
        this.PASSWORD_RESET_FORM = formFactory.form(PasswordReset.class);
        this.FORGOT_PASSWORD_FORM = formFactory.form(MyUsernamePasswordAuthProvider.MyIdentity.class);
        this.msg = msg;
        this.myService = myService;
    }

    //TODO : fix this
    public Result forgotPassword(Http.Request request) {
        Form<MyUsernamePasswordAuthProvider.MyIdentity> form = FORGOT_PASSWORD_FORM;
        return ok(password_forgot.render(form, request,msg.preferred(request))).withHeader("Cache-Control", "no-cache");
    }

    //TODO : fix this
    public Result doResetPassword(Http.Request request) {
        final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM
                .bindFromRequest(request);
        if (filledForm.hasErrors()) {
            return badRequest(password_reset.render(filledForm,msg.preferred(request))).withHeader("Cache-Control", "no-cache");
        } else {
            final String token = filledForm.get().token;
            final String newPassword = filledForm.get().password;

            final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
            if (ta == null) {
                return badRequest(no_token_or_invalid.render());
            }
            final SysUser u = ta.targetUser;
//            try {
//                // Pass true for the second parameter if you want to
//                // automatically create a password and the exception never to
//                // happen
//                u.resetPassword(new MyUsernamePasswordAuthUser(newPassword),
//                        false);
//            } catch (final RuntimeException re) {
//                flash(AuthApplication.FLASH_MESSAGE_KEY,
//                        this.msg.preferred(request()).at("playauthenticate.reset_password.message.no_password_account"));
//            }
//            final boolean login = this.userPaswAuthProvider.isLoginAfterPasswordReset();
//            if (login) {
//                // automatically log in
//                flash(AuthApplication.FLASH_MESSAGE_KEY,
//                        this.msg.preferred(request()).at("playauthenticate.reset_password.message.success.auto_login"));
//
//                return this.auth.loginAndRedirect(ctx(),
//                        new MyLoginUsernamePasswordAuthUser(u.getEmail()));
//            } else {
//                // send the user to the login page
//                flash(AuthApplication.FLASH_MESSAGE_KEY,
//                        this.msg.preferred(request()).at("playauthenticate.reset_password.message.success.manual_login"));
//            }
            return redirect(routes.Application.loginForm());
        }
    }

    /**
     * Returns a token object if valid, null if not
     *
     * @param token
     * @param type
     * @return
     */
    private static TokenAction tokenIsValid(final String token, final Type type) {
        TokenAction ret = null;
        if (token != null && !token.trim().isEmpty()) {
            final TokenAction ta = TokenAction.findByToken(token, type);
            if (ta != null && ta.isValid()) {
                ret = ta;
            }
        }

        return ret;
    }

    //todo: fix this
    public Result doForgotPassword(Http.Request request) {
        final Form<MyUsernamePasswordAuthProvider.MyIdentity> filledForm = FORGOT_PASSWORD_FORM
                .bindFromRequest(request);
        if (filledForm.hasErrors()) {
            // User did not fill in his/her email
            return badRequest(password_forgot.render(filledForm, request, msg.preferred(request))).withHeader("Cache-Control", "no-cache");
        } else {
            // The email address given *BY AN UNKNWON PERSON* to the form - we
            // should find out if we actually have a user with this email
            // address and whether password login is enabled for him/her. Also
            // only send if the email address of the user has been verified.
            final String email = filledForm.get().email;

            // We don't want to expose whether a given email address is signed
            // up, so just say an email has been sent, even though it might not
            // be true - that's protecting our user privacy.
//            flash(Constants.FLASH_MESSAGE_KEY,
//                    this.msg.preferred(request).at(
//                            "Instructions on how to reset your password have been sent to {0}.",
//                            email));

            final SysUser user = SysUser.findByEmail(email);
            if (user != null) {
                // yep, we have a user with this email that is active - we do
                // not know if the user owning that account has requested this
                // reset, though.
                final MyUsernamePasswordAuthProvider provider = this.userPaswAuthProvider;
                // User exists
                if (user.getEmailValidated()) {
                    provider.sendPasswordResetMailing(user, request);
//                    myService.doMail();
                    // In case you actually want to let (the unknown person)
                    // know whether a user was found/an email was sent, use,
                    // change the flash message
                } else {
                    // We need to change the message here, otherwise the user
                    // does not understand whats going on - we should not verify
                    // with the password reset, as a "bad" user could then sign
                    // up with a fake email via OAuth and get it verified by an
                    // a unsuspecting user that clicks the link.
//                    flash(AuthApplication.FLASH_MESSAGE_KEY,
//                            this.msg.preferred(request()).at("playauthenticate.reset_password.message.email_not_verified"));

                    // You might want to re-send the verification email here...
//                    provider.sendVerifyEmailMailingAfterSignup(user, ctx());
                }
            }

            return redirect(routes.Portal.index());
        }
    }
//    protected String getEmailTemplate(final String template,
//                                      final String langCode, final String url, final String token,
//                                      final String name, final String email) {
//        Class<?> cls = null;
//        String ret = null;
//        try {
//            cls = Class.forName(template + "_" + langCode);
//        } catch (ClassNotFoundException e) {
//            Logger.warn("Template: '"
//                    + template
//                    + "_"
//                    + langCode
//                    + "' was not found! Trying to use English fallback template instead.");
//        }
//        if (cls == null) {
//            try {
//                cls = Class.forName(template + "_"
//                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
//            } catch (ClassNotFoundException e) {
//                Logger.error("Fallback template: '" + template + "_"
//                        + EMAIL_TEMPLATE_FALLBACK_LANGUAGE
//                        + "' was not found either!");
//            }
//        }
//        if (cls != null) {
//            Method htmlRender = null;
//            try {
//                htmlRender = cls.getMethod("render", String.class,
//                        String.class, String.class, String.class);
//                ret = htmlRender.invoke(null, url, token, name, email)
//                        .toString();
//
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
//        return ret;
//    }

    //TODO :test this
    public Result resetPassword(final String token, Http.Request request) {
        final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
        final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM.fill(new PasswordReset(token, this.msg)).bindFromRequest(request);
//                .bindFromRequest(request);
        if (ta == null) {
            return badRequest(no_token_or_invalid.render());
        }

        return ok(password_reset.render(filledForm,msg.preferred(request))).withHeader("Cache-Control", "no-cache");
    }
}
