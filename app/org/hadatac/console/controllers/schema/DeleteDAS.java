package org.hadatac.console.controllers.schema;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.inject.Inject;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

//import controllers.AuthApplication;
import org.hadatac.console.models.DataAcquisitionSchemaForm;
import org.hadatac.entity.pojo.DataAcquisitionSchema;
import org.hadatac.utils.NameSpace;


public class DeleteDAS extends Controller {

    @Inject
    private FormFactory formFactory;

//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result index(String das_uri) {

        DataAcquisitionSchemaForm dasForm = new DataAcquisitionSchemaForm();
        DataAcquisitionSchema das = null;

        try {
            if (das_uri != null) {
                das_uri = URLDecoder.decode(das_uri, "UTF-8");
            } else {
                das_uri = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!das_uri.equals("")) {
            das = DataAcquisitionSchema.find(das_uri);
            System.out.println("delete org.hadatac.data acquisition schema");
            dasForm.setUri(das_uri);
            dasForm.setLabel(das.getLabel());
            return ok(org.hadatac.console.views.html.schema.deleteDAS.render(das_uri, dasForm));
        }

        return ok(org.hadatac.console.views.html.schema.deleteDAS.render(das_uri, dasForm));
    }

//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result postIndex(String das_uri) {
        return index(das_uri);
    }

//    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result processForm(String das_uri, Http.Request request) {

        DataAcquisitionSchema das = null;

        try {
            if (das_uri != null) {
                das_uri = URLDecoder.decode(das_uri, "UTF-8");
            } else {
                das_uri = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!das_uri.equals("")) {
            das = DataAcquisitionSchema.find(das_uri);
        }

        Form<DataAcquisitionSchemaForm> form = formFactory.form(DataAcquisitionSchemaForm.class).bindFromRequest(request);
        DataAcquisitionSchemaForm data = form.get();
        data.setLabel(das.getLabel());

        if (das != null) {
            NameSpace.deleteTriplesByNamedGraph(das_uri);
        }

        if (form.hasErrors()) {
            return badRequest(org.hadatac.console.views.html.schema.DASConfirm.render("ERROR Deleting Data Acquisition Schema ", "Error from form", data.getLabel()));
        } else {
            return ok(org.hadatac.console.views.html.schema.DASConfirm.render("Deleted Data Acquisition Schema ", "", data.getLabel()));
        }
    }
}
