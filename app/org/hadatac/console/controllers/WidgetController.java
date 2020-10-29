package org.hadatac.console.controllers;

import controllers.routes;
import org.hadatac.console.models.SysUser;
import org.hadatac.console.models.Widget;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.hadatac.utils.CollectionUtil;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static play.libs.Scala.asScala;

/**
 * An example of form processing.
 *
 * https://playframework.com/documentation/latest/JavaForms
 */
@Singleton
public class WidgetController extends Controller {

    private final Form<WidgetData> form;
    private MessagesApi messagesApi;
    private final List<Widget> widgets;

    private final Logger logger = LoggerFactory.getLogger(getClass()) ;

    @Inject
    public WidgetController(FormFactory formFactory, MessagesApi messagesApi){
        this.form = formFactory.form(WidgetData.class);
        this.messagesApi = messagesApi;
        this.widgets = com.google.common.collect.Lists.newArrayList(
                new Widget("Data 1", "a", "a","a"),
                new Widget("Data 2", "b","b","b"),
                new Widget("Data 3", "c", "c","c")
        );
    }

    public Result listWidgets(Http.Request request) throws TechnicalException {
        return ok(org.hadatac.console.views.html.listWidgets.render(asScala(widgets), form, request, messagesApi.preferred(request)));

    }

    public Result createWidget(Http.Request request) throws TechnicalException {
        final Form<WidgetData> boundForm = form.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(org.hadatac.console.views.html.listWidgets.render(asScala(widgets), boundForm, request, messagesApi.preferred(request)));
        } else {
            WidgetData data = boundForm.get();
            if (data.validate()!=null){
//                messagesApi.preferred(request).at("Your e-mail has already been validated.");
                return redirect(org.hadatac.console.controllers.routes.WidgetController.listWidgets())
                        .flashing("error",data.validate());
            }
            widgets.add(new Widget(data.getName(), data.getEmail(), data.getPassword(), data.getRepeatPassword()));
            //Adding to DB
            SolrClient solrClient = new HttpSolrClient.Builder(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.AUTHENTICATE_USERS)).build();
            String query = "active_bool:true";
            SolrQuery solrQuery = new SolrQuery(query);
            List<SysUser> users = new ArrayList<SysUser>();

            try {
                QueryResponse queryResponse = solrClient.query(solrQuery);
//                solrClient.close();
                SolrDocumentList list = queryResponse.getResults();
                Iterator<SolrDocument> i = list.iterator();

                while (i.hasNext()) {
                    System.out.println("User at i :"+i.next());
                    if(i.next().containsValue(data.getEmail()))
                    System.out.println("Email already validated");
//				SysUser user = SysUser.convertSolrDocumentToUser(i.next());
//                System.out.println("Users:"+user);
//				users.add(user);
	    			}
            SolrInputDocument newUser = new SolrInputDocument();
            newUser.addField( "id_str", UUID.randomUUID().toString());
            newUser.addField( "email", data.getEmail());
            newUser.addField( "name_str", data.getName());
            newUser.addField("active_bool",true);
            newUser.addField("email_validated_bool", true);
            solrClient.add(newUser);
            solrClient.commit();
            System.out.println("commit done");
            solrClient.close();
            } catch (Exception e) {
                System.out.println("[ERROR] User.getAuthUserFindSolr - Exception message: " + e.getMessage());
            }

            return redirect(org.hadatac.console.controllers.routes.Portal.index());
                    //routes.WidgetController.listWidgets())
                    //.flashing("info", "User added!");
        }
    }
}