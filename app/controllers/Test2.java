package controllers;

import play.mvc.Controller;
import play.mvc.Result;

//TODO : delete test code
public class Test2 extends Controller {

    public Result index3()  {
        return ok(views.html.test.render("Test2",3));
    }

    public Result index4()  {
        return ok(views.html.test.render("Test2",4));
    }

}
