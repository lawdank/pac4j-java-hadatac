package org.hadatac.console.controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import javax.inject.Inject;
import org.hadatac.console.models.JsonContent;
import module.SecurityModule;
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
import play.api.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;
//import providers.MyUsernamePasswordAuthProvider;
import org.hadatac.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import play.libs.Files.TemporaryFile;

public class Application extends Controller {
//    private final MyUsernamePasswordAuthProvider provider;


    @Inject
    private Config config;

    @Inject
    private PlaySessionStore playSessionStore;

//    public Application(MyUsernamePasswordAuthProvider provider) {
//        this.provider = provider;
//    }

    private List<CommonProfile> getProfiles(Http.Request request) {
        final PlayWebContext context = new PlayWebContext(request, playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager(context);
        return profileManager.getAll(true);
    }

    private CommonProfile getProfile(Http.Request request) {
        final PlayWebContext context = new PlayWebContext(request, playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager(context);
        return profileManager.getAll(true).get(0);
    }

    @Secure(clients = "AnonymousClient")
    public Result index(Http.Request request) throws Exception {
        final PlayWebContext context = new PlayWebContext(request, playSessionStore);
        final String sessionId = context.getSessionStore().getOrCreateSessionId(context);
        final String token = (String) context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null);
        // profiles (maybe be empty if not authenticated)
        return ok(org.hadatac.console.views.html.index.render(getProfiles(request), token, sessionId));
    }

    private Result protectedIndexView(Http.Request request) {
        // profiles
        return ok(org.hadatac.console.views.html.protectedIndex.render(getProfiles(request)));
    }

    @Secure(clients = "FacebookClient", matchers = "excludedPath")
    public Result facebookIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    private Result notProtectedIndexView(Http.Request request) {
        // profiles
        return ok(org.hadatac.console.views.html.notprotectedIndex.render(getProfiles(request)));
    }

    public Result facebookNotProtectedIndex(Http.Request request) {
        return notProtectedIndexView(request);
    }

    @Secure(clients = "FacebookClient", authorizers = "admin")
    public Result facebookAdminIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    @Secure(clients = "FacebookClient", authorizers = "custom")
    public Result facebookCustomIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    @Secure(clients = "TwitterClient,FacebookClient")
    public Result twitterIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    @SubjectPresent(forceBeforeAuthCheck = true)
    public Result protectedIndex(Http.Request request) {
        return protectedIndexView(request);
    }


    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result formIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    // Setting the isAjax parameter is no longer necessary as AJAX requests are automatically detected:
    // a 401 error response will be returned instead of a redirection to the login url.
    @Secure(clients = "FormClient")
    public Result formIndexJson(Http.Request request) {
        Content content = org.hadatac.console.views.html.protectedIndex.render(getProfiles(request));
        JsonContent jsonContent = new JsonContent(content.body());
        return ok(jsonContent);
    }

    @Secure(clients = "IndirectBasicAuthClient")
    public Result basicauthIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    @Secure(clients = "DirectBasicAuthClient,ParameterClient,DirectFormClient")
    public Result dbaIndex(Http.Request request) {

        Utils.block();

        return protectedIndexView(request);
    }

    @Secure(clients = "CasClient")
    public Result casIndex(Http.Request request) {
        final CommonProfile profile = getProfiles(request).get(0);
        final String service = "http://localhost:8080/proxiedService";
        String proxyTicket = null;
        if (profile instanceof CasProxyProfile) {
            final CasProxyProfile proxyProfile = (CasProxyProfile) profile;
            proxyTicket = proxyProfile.getProxyTicketFor(service);
        }
        return ok(org.hadatac.console.views.html.casProtectedIndex.render(profile, service, proxyTicket));
    }

    @Secure(clients = "SAML2Client")
    public Result samlIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    @Secure(clients = "OidcClient")
    public Result oidcIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    //@Secure(clients = "ParameterClient")
    //@SubjectPresent(handlerKey = "ParameterClient")
    public Result restJwtIndex(Http.Request request) {
        return protectedIndexView(request);
    }

    //@Secure(clients = "AnonymousClient", authorizers = "csrfCheck")
    public Result csrfIndex(Http.Request request) {
        return ok(org.hadatac.console.views.html.csrf.render(getProfiles(request)));
    }

    public Result loginForm() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("FormClient").get();
        return ok(org.hadatac.console.views.html.loginForm.render(formClient.getCallbackUrl()));
    }

    public Result jwt(Http.Request request) {
        final List<CommonProfile> profiles = getProfiles(request);
        final JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(SecurityModule.JWT_SALT));
        String token = "";
        if (CommonHelper.isNotEmpty(profiles)) {
            token = generator.generate(profiles.get(0));
        }
        return ok(org.hadatac.console.views.html.jwt.render(token));
    }

    public Result forceLogin(Http.Request request) {
        final PlayWebContext context = new PlayWebContext(request, playSessionStore);
        final Client client = config.getClients().findClient(context.getRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER).get()).get();
        try {
            final HttpAction action = (HttpAction) client.getRedirectionAction(context).get();
            return (Result) PlayHttpActionAdapter.INSTANCE.adapt(action, context);
        } catch (final HttpAction e) {
            throw new TechnicalException(e);
        }
    }

    public Result signUp() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("FormClient").get();
        return ok(org.hadatac.console.views.html.signUp.render(formClient.getCallbackUrl()));
    }

    public Result upload(Http.Request request) {
        Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("picture");
        System.out.println("picture:"+picture);
        if (picture != null) {
            String fileName = picture.getFilename();
            long fileSize = picture.getFileSize();
            String contentType = picture.getContentType();
            TemporaryFile file = picture.getRef();
            System.out.println("file:"+file);
            file.copyTo(Paths.get("/Users/kandws01/Desktop/tmp/uploaded_file.jpg"), true);
            return ok("File uploaded");
        } else {
            return badRequest().flashing("error", "Missing file");
        }
    }
//        File file = request.body().asRaw().asFile();
//        return ok("File uploaded");


}
