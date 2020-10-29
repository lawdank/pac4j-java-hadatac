package providers;

import controllers.routes;
import org.hadatac.console.models.SysUser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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

import static play.mvc.Results.redirect;

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
        System.out.println("credentials hashcode:"+credentials.hashCode());
//        System.out.println( "securityPasswordEncoder.encode(password))" + securityPasswordEncoder.encode(password));
        //Querying from DB
        SolrClient solrClient = new HttpSolrClient.Builder(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.AUTHENTICATE_USERS)).build();
        String query = "active_bool:true";
        SolrQuery solrQuery = new SolrQuery(query);
        List<SysUser> users = new ArrayList<SysUser>();

        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);
            SolrDocumentList list = queryResponse.getResults();
            Iterator<SolrDocument> i = list.iterator();

            boolean userExists = false;
            while (i.hasNext()) {
                if(i.next().containsValue(username)) {
                    //TODO: delete later
                    System.out.println("User exists:" +i.next());
                    userExists=true;
                    break;
                }

            }
            solrClient.close();
            if(!userExists){
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
            System.out.println("[ERROR] User.getAuthUserFindSolr - Exception message: " + e.getMessage());
        }

    }
}
