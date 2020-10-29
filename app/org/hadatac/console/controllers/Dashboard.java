package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;

import views.html.main;
import views.html.dashboard;
import org.hadatac.utils.Repository;

public class Dashboard extends Controller {

    public Result index() {
        if (!Repository.operational(Repository.METADATA)) {
            return ok(main.render("Results", "",
                    new Html("<div class=\"container-fluid\"><h4>"
                            + "The triple store is NOT properly connected. "
                            + "Please restart it or check the hadatac configuration file!"
                            + "</h4></div>")));
        }
        if (!Repository.operational(Repository.DATA)) {
            return ok(main.render("Results", "",
                    new Html("<div class=\"container-fluid\"><h4>"
                            + "HADatAC Solr is down now. Ask Administrator for further information. "
                            + "</h4></div>")));
        }

        return ok(dashboard.render());
    }

    public Result postIndex() {
        return index();
    }
}
