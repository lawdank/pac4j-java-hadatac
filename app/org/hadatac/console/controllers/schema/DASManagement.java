package controllers.schema;

import java.util.List;

import org.hadatac.entity.pojo.DataAcquisitionSchema;
import org.hadatac.entity.pojo.PossibleValue;

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