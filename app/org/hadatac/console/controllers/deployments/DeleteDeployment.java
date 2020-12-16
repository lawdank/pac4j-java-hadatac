package org.hadatac.console.controllers.deployments;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import javax.inject.Inject;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.data.FormFactory;

import org.hadatac.console.controllers.AuthApplication;
import org.hadatac.console.models.DeploymentForm;
import org.hadatac.console.views.html.deployments.*;
import org.hadatac.entity.pojo.Deployment;
import org.hadatac.entity.pojo.Detector;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

public class DeleteDeployment extends Controller {

    @Inject
    private FormFactory formFactory;

    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result index(String deployment_uri) {
        DeploymentForm depForm = new DeploymentForm();
        Deployment dep = null;

        try {
            if (deployment_uri != null) {
                deployment_uri = URLDecoder.decode(deployment_uri, "UTF-8");
            } else {
                deployment_uri = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!deployment_uri.equals("")) {

            dep = Deployment.find(deployment_uri);
            /*
             *  Add deployment information into handler
             */
            depForm.setPlatform(dep.getPlatform().getLabel());
            depForm.setInstrument(dep.getInstrument().getLabel());
            if (dep.getDetectors() != null) {
                Iterator<Detector> iterDetectors = dep.getDetectors().iterator();
                while (iterDetectors.hasNext()) {
                    depForm.addDetector(((Detector)iterDetectors.next()).getLabel());
                }
            }
            depForm.setStartDateTime(dep.getStartedAt());
            if (dep.getEndedAt() != null) {
                depForm.setEndDateTime(dep.getEndedAt());
            }
            System.out.println("delete deployment");
            return ok(deleteDeployment.render(deployment_uri, depForm));
        }
        return ok(deleteDeployment.render(deployment_uri, depForm));
    }

    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result postIndex(String deployment_uri) {
        return index(deployment_uri);
    }

    @Restrict(@Group(AuthApplication.DATA_OWNER_ROLE))
    public Result processForm(String deployment_uri, Http.Request request) {
        Deployment dep = null;

        try {
            if (deployment_uri != null) {
                deployment_uri = URLDecoder.decode(deployment_uri, "UTF-8");
            } else {
                deployment_uri = "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!deployment_uri.equals("")) {
            dep = Deployment.find(deployment_uri);
        }

        Form<DeploymentForm> form = formFactory.form(DeploymentForm.class).bindFromRequest(request);
        DeploymentForm data = form.get();

        data.setPlatform(dep.getPlatform().getLabel());
        data.setInstrument(dep.getInstrument().getLabel());
        if (dep.getDetectors() != null) {
            Iterator<Detector> iterDetectors = dep.getDetectors().iterator();
            while (iterDetectors.hasNext()) {
                data.addDetector(((Detector)iterDetectors.next()).getLabel());
            }
        }
        data.setStartDateTime(dep.getStartedAt());
        data.setEndDateTime(dep.getEndedAt());

        dep.delete();

        //Deployment deployment = DataFactory.closeDeployment(deploymentUri, endDateString);
        if (form.hasErrors()) {
            System.out.println("HAS ERRORS");
            return badRequest(closeDeployment.render(deployment_uri, data));
        } else {
            return ok(deploymentConfirm.render("Delete Deployment", data, "/", "", "", 0));
        }
    }
}
