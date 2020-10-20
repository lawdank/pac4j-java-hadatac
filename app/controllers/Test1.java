package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import model.JsonContent;
import modules.SecurityModule;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.http.PlayHttpActionAdapter;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;
import util.Utils;

import javax.inject.Inject;
import java.util.List;
//TODO : delete test code
public class Test1 extends Controller {

    public Result index1()  {
        return ok(views.html.test.render("Test1",1));
    }

    public Result index2()  {
        return ok(views.html.test.render("Test1",2));
    }

}
