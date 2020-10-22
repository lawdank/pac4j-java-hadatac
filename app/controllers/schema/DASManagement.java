package controllers.schema;

import java.util.List;

import entity.pojo.DataAcquisitionSchema;
import entity.pojo.PossibleValue;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

//import controllers.AuthApplication;

import org.pac4j.play.java.Secure;
import play.mvc.Result;
import play.mvc.Controller;

public class DASManagement extends Controller {

//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    @Secure
    public Result index() {
        List<DataAcquisitionSchema> sdds = DataAcquisitionSchema.findAll();
        return ok(views.html.schema.DASManagement.render(sdds));
    }

//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    @Secure
    public Result postIndex() {
        return index();
    }


}