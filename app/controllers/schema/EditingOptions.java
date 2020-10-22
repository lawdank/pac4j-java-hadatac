package controllers.schema;

import play.mvc.Controller;
import play.mvc.Result;

//import views.html.schema.*;
//import controllers.AuthApplication;
import model.OtMSparqlQueryResults;
import entity.pojo.Entity;
import entity.pojo.Attribute;
import entity.pojo.Unit;
import util.Hierarchy;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;


public class EditingOptions extends Controller {

	public Result entityOptions() {
		return ok(views.html.schema.entityOptions.render(getEntities()));
	}

	public Result postEntityOptions() {
		return ok(views.html.schema.entityOptions.render(getEntities()));
	}

	public static OtMSparqlQueryResults getEntities() {
		Entity entity = new Entity();
		String json = entity.getHierarchyJson();
		OtMSparqlQueryResults entities = new OtMSparqlQueryResults(json);
		return entities;
	}

	public static OtMSparqlQueryResults getAttributes() {
		Attribute attribute = new Attribute();
		String json = attribute.getHierarchyJson();
		OtMSparqlQueryResults attributes = new OtMSparqlQueryResults(json);
		return attributes;
	}

	public static OtMSparqlQueryResults getUnits() {
		Unit unit = new Unit();
		String json = unit.getHierarchyJson();
		OtMSparqlQueryResults units = new OtMSparqlQueryResults(json);
		return units;
	}
	
	public static OtMSparqlQueryResults getHierarchy(String className) {
		Hierarchy hierarchy = new Hierarchy(className);
		String json = hierarchy.getHierarchyJson();
		//System.out.println("JSON results" + json);
		OtMSparqlQueryResults hierarchies = new OtMSparqlQueryResults(json);
		//System.out.println("tree results:" + hierarchies.treeResults);
		return hierarchies;
	}

}
