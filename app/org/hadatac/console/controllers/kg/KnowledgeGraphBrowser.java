//package controllers.kg;
//
//import play.mvc.Controller;
//import play.mvc.Result;
//import views.html.kg.knowledgeGraphBrowser;
//
////import org.hadatac.org.hadatac.entity.pojo.Measurement;
////import org.hadatac.org.hadatac.entity.pojo.ObjectCollection;
////import org.hadatac.org.hadatac.entity.pojo.Study;
////import org.hadatac.org.hadatac.entity.pojo.Agent;
////import org.hadatac.org.hadatac.entity.pojo.StudyObject;
////import org.hadatac.org.hadatac.metadata.loader.URIUtils;
////import org.hadatac.console.controllers.AuthApplication;
////import org.hadatac.console.controllers.objectcollections.OCForceFieldGraph;
////import org.hadatac.console.controllers.triplestore.UserManagement;
////import org.hadatac.console.org.hadatac.console.org.hadatac.console.http.SPARQLUtils;
////import org.hadatac.console.org.hadatac.console.models.SysUser;
////import org.hadatac.org.hadatac.utils.CollectionUtil;
////import org.hadatac.org.hadatac.utils.ConfigProp;
////import org.hadatac.org.hadatac.utils.NameSpaces;
//
//public class KnowledgeGraphBrowser extends Controller {
//
////    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result index(boolean autoRefresh, boolean includeOntologies, boolean includeIndicators, boolean includeDeployments, boolean includeSDDs, boolean includeDASpecs) {
//
//        KGForceFieldGraph graph = new KGForceFieldGraph(includeOntologies, includeIndicators, includeDeployments, includeSDDs, includeDASpecs);
//
//        return ok(knowledgeGraphBrowser.render(graph, autoRefresh, includeOntologies, includeIndicators, includeDeployments, includeSDDs, includeDASpecs));
//    }
//
////    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
//    public Result postIndex(boolean autoRefresh, boolean includeOntologies, boolean includeIndicators, boolean includeDeployments, boolean includeSDDs, boolean includeDASpecs) {
//        return index(autoRefresh, includeOntologies, includeIndicators, includeDeployments, includeSDDs, includeDASpecs);
//    }
//}
