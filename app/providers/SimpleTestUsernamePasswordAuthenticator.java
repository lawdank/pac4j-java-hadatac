package providers;

import controllers.routes;
import org.hadatac.console.models.SysUser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hadatac.console.views.html.error_page;
import org.hadatac.utils.CollectionUtil;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import util.SecurityPasswordEncoder;
//import util.SecurityPasswordEncoder;
import static play.mvc.Results.*;


public class SimpleTestUsernamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

//    SecurityPasswordEncoder securityPasswordEncoder;
    PasswordEncoder passwordEncoder;

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) {
        if (credentials == null) {
            throw new CredentialsException("No credential");
        }
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }
        if (CommonHelper.isBlank(password)) {
            throw new CredentialsException("Password cannot be blank");
        }
        //Querying from DB
        SolrClient solrClient = new HttpSolrClient.Builder(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.AUTHENTICATE_USERS)).build();
        String query = "active_bool:true";
        System.out.println("solrClient:"+solrClient);
        SolrQuery solrQuery = new SolrQuery(query);
        System.out.println("solrQuery:"+solrQuery);
        List<SysUser> users = new ArrayList<SysUser>();


        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);
            SolrDocumentList list = queryResponse.getResults();
            Iterator<SolrDocument> i = list.iterator();
            System.out.println("i:"+i);
            boolean userExists = false;
            int userSize = list.size();
            while (userSize > 0 ) {
                System.out.println("Inside while:"+userSize);
                if(i.hasNext() && i.next().containsValue(username)) {
                    //TODO: delete later
                    System.out.println("User exists:");
                    userExists=true;
                    break;
                }
                userSize --;

            }
            solrClient.close();
            if(!userExists){
                System.out.println("User does not exists:");
                redirect(org.hadatac.console.controllers.routes.Application.loginForm())
                        .flashing("error", "user does not exist");
                return;
            }
            else {
                final CommonProfile profile = new CommonProfile();
                profile.setId(username);
                profile.addAttribute(Pac4jConstants.USERNAME, username);
                credentials.setUserProfile(profile);
                profile.addRole("Admin");
                profile.setRemembered(true);

            }
        } catch (Exception e) {
            System.out.println("[ERROR] SimpleTestUsernamePasswordAuthenticator - Exception message: " + e.getMessage());
        }

    }
}
