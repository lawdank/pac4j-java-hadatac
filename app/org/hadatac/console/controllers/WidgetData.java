package org.hadatac.console.controllers;

import javax.inject.Inject;

import org.springframework.security.crypto.bcrypt.BCrypt;
import play.api.i18n.MessagesApi;
import play.data.validation.Constraints;
import play.mvc.Http;
import providers.AuthUser;
import providers.AuthUserIdentity;

/**
 * A form processing DTO that maps to the widget form.
 *
 * Using a class specifically for form binding reduces the chances
 * of a parameter tampering attack and makes code clearer, because
 * you can define constraints against the class.
 */
public class WidgetData {

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


    @Inject
    public WidgetData(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    public WidgetData() {
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
}