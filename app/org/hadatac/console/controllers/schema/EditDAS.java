package org.hadatac.console.controllers.schema;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import play.mvc.Controller;
import play.mvc.Result;

//import views.schema.*;
//import controllers.AuthApplication;
import org.hadatac.entity.pojo.DataAcquisitionSchema;


public class EditDAS extends Controller {

//	@Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
	public Result index(String das_uri) {

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
		return ok(org.hadatac.console.views.html.schema.editDAS.render(das));
	}

//	@Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
	public Result postIndex(String das_uri) {
		return index(das_uri);
	}
}
