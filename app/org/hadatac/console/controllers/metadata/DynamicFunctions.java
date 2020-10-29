package org.hadatac.console.controllers.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hadatac.utils.CollectionUtil;
import play.mvc.Controller;
import play.mvc.Result;
import org.hadatac.utils.NameSpaces;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hadatac.console.http.SPARQLUtils;
import org.hadatac.entity.pojo.Indicator;
import org.hadatac.metadata.loader.*;
import org.hadatac.utils.NameSpace;


public class DynamicFunctions extends Controller {

    public static String getPrefixes() {
        String prefixString = NameSpaces.getInstance().printSparqlNameSpaceList().replaceAll("\n", " ");
        return prefixString;
    }

    public static Map<String,String> getPrefixMap() {
        NameSpaces.getInstance();
        Map<String,String> prefixMap = new HashMap<String,String>();
        for (Map.Entry<String, NameSpace> entry : NameSpaces.getInstance().getNamespaces().entrySet()) {
            String abbrev = entry.getKey().toString();
            NameSpace ns = entry.getValue();
            prefixMap.put(abbrev, ns.getName());
        }
        return prefixMap;
    }

    public static String replaceURLWithPrefix(String label) {
        Map<String,String> prefixMap = getPrefixMap();
        for (Map.Entry<String, String> prefixes : prefixMap.entrySet()) {
            if (label.contains(prefixes.getValue())) {
                label = label.replaceAll(prefixes.getValue(), prefixes.getKey() + ":");
            }
        }
        return label;
    }

    public static List<String> getIndicatorURIs() {
        List<String> indicatorURIs = new ArrayList<String>();

        Map<String, String> indicatorTypes = getIndicatorTypes();
        Map<String, Map<String, String>> valueMapWithLabels = Indicator.getValuesAndLabels(indicatorTypes);

        for (String key : valueMapWithLabels.keySet()) {
            for (String k : valueMapWithLabels.get(key).keySet()) {
                indicatorURIs.add(URIUtils.replacePrefixEx(k));
            }
        }

        return indicatorURIs;
    }

    public static String getConceptUriByTabName(String tabName) {
        Map<String, String> indicatorTypes = getIndicatorTypes();
        Map<String, Map<String, String>> valueMapWithLabels = Indicator.getValuesAndLabels(indicatorTypes);

        System.out.println("valueMapWithLabels: " + valueMapWithLabels);

        String uri = "";
        for (String key : valueMapWithLabels.keySet() ){
            for (String k : valueMapWithLabels.get(key).keySet()) {
                if (tabName.equals(valueMapWithLabels.get(key).get(k).replace(" ", "").replace(",", ""))) {
                    uri = k;
                    return uri;
                }
            }
        }

        return uri;
    }

    public static String replacePrefixWithURL(String label) {
        Map<String,String> prefixMap = getPrefixMap();
        for (Map.Entry<String, String> prefixes : prefixMap.entrySet()){
            if (label.contains(prefixes.getKey() + ":")) {
                label = label.replaceAll(prefixes.getKey() + ":", prefixes.getValue());
            }
        }
        return label;
    }

    public static Map<String, String> getIndicatorTypes() {
        String indicatorQuery= getPrefixes()
                + "SELECT DISTINCT ?indicatorType ?label ?comment WHERE { "
                + "?indicatorType rdfs:subClassOf hasco:Indicator . "
                + "?indicatorType rdfs:label ?label . "
                + "}";
        Map<String, String> indicatorMap = new HashMap<String, String>();
        try {
            ResultSetRewindable resultsrwIndc = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), indicatorQuery);

            String indicatorLabel = "";
            while (resultsrwIndc.hasNext()) {
                QuerySolution soln = resultsrwIndc.next();
                indicatorLabel = "";
                if (soln.contains("label"))
                    indicatorLabel = soln.get("label").toString();
                if (soln.contains("indicatorType")){
                    String indicatorType = replaceURLWithPrefix(soln.get("indicatorType").toString());
                    indicatorMap.put(indicatorType, indicatorLabel);
                }
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        Map<String, String> indicatorMapSorted = new TreeMap<String, String>(indicatorMap);
        return indicatorMapSorted;
    }

    public static Map<String, Map<String,String>> findStudy(String study_uri) {
        String studyQueryString = "";
        studyQueryString = getPrefixes() +
                "SELECT ?studyUri ?studyLabel ?proj ?studyDef ?studyComment ?agentName ?institutionName " +
                " WHERE {        ?subUri rdfs:subClassOf hasco:Study . " +
                "                       ?studyUri a ?subUri . " +
                "           ?studyUri rdfs:label ?studyLabel  . " +
                "			FILTER ( ?studyUri = " + study_uri + " ) . " +
                "		 OPTIONAL {?studyUri hasco:hasProject ?proj} . " +
                "        OPTIONAL { ?studyUri skos:definition ?studyDef } . " +
                "        OPTIONAL { ?studyUri rdfs:comment ?studyComment } . " +
                "        OPTIONAL { ?studyUri hasco:hasAgent ?agent . " +
                "                                   ?agent foaf:name ?agentName } . " +
                "        OPTIONAL { ?studyUri hasco:hasInstitution ?institution . " +
                "                                 ?institution foaf:name ?institutionName} . " +
                "                             }" ;
        Map<String, Map<String,String>> studyResult = new HashMap<String, Map<String,String>>();
        Map<String,String> values = new HashMap<String, String>();

        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), studyQueryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                values = new HashMap<String, String>();
                if (soln.contains("studyLabel"))
                    values.put("Label" , soln.get("studyLabel").toString());
                if (soln.contains("studyDef"))
                    values.put("Title" , soln.get("studyDef").toString());
                if (soln.contains("proj"))
                    values.put("Project" , replaceURLWithPrefix(soln.get("proj").toString()));
                if (soln.contains("studyComment"))
                    values.put("Comment" , soln.get("studyComment").toString());
                if (soln.contains("agentName"))
                    values.put("Agents" , soln.get("agentName").toString());
                if (soln.contains("institutionName"))
                    values.put("Institution" , soln.get("institutionName").toString());
                studyResult.put(replaceURLWithPrefix(soln.get("studyUri").toString()),values);
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return studyResult;
    }

    public static Map<String, Map<String,String>> findStudies() {
        Map<String, Map<String,String>> studyResult = new HashMap<String, Map<String,String>>();
        Map<String,String> values = new HashMap<String, String>();
        String studyQueryString = "";
        studyQueryString = getPrefixes() +
                "SELECT ?studyUri ?studyLabel ?proj ?studyDef ?studyComment ?agentName ?institutionName " +
                " WHERE {        ?subUri rdfs:subClassOf hasco:Study . " +
                "                       ?studyUri a ?subUri . " +
                "           ?studyUri rdfs:label ?studyLabel  . " +
                "		 OPTIONAL {?studyUri hasco:hasProject ?proj} . " +
                "        OPTIONAL { ?studyUri skos:definition ?studyDef } . " +
                "        OPTIONAL { ?studyUri rdfs:comment ?studyComment } . " +
                "        OPTIONAL { ?studyUri hasco:hasAgent ?agent . " +
                "                                   ?agent foaf:name ?agentName } . " +
                "        OPTIONAL { ?studyUri hasco:hasInstitution ?institution . " +
                "                                 ?institution foaf:name ?institutionName} . " +
                "                             }" ;
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), studyQueryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                values = new HashMap<String, String>();
                if (soln.contains("studyLabel"))
                    values.put("Label" , soln.get("studyLabel").toString());
                if (soln.contains("studyDef"))
                    values.put("Title" , soln.get("studyDef").toString());
                if (soln.contains("proj"))
                    values.put("Project" , replaceURLWithPrefix(soln.get("proj").toString()));
                if (soln.contains("studyComment"))
                    values.put("Comment" , soln.get("studyComment").toString());
                if (soln.contains("agentName"))
                    values.put("Agents" , soln.get("agentName").toString());
                if (soln.contains("institutionName"))
                    values.put("Institution" , soln.get("institutionName").toString());
                studyResult.put(replaceURLWithPrefix(soln.get("studyUri").toString()),values);

            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return studyResult;
    }
    public static Map<String, Map<String,String>> findSubjects() {
        Map<String, Map<String,String>> subjectResult = new HashMap<String, Map<String,String>>();
        Map<String,String> values = new HashMap<String, String>();
        String subjectQueryString = "";
        subjectQueryString = getPrefixes() +
                "SELECT ?subjectUri ?subjectType ?subjectTypeLabel ?subjectLabel ?cohortLabel ?studyLabel " +
                "			 WHERE {        ?subjectUri hasco:isSubjectOf* ?cohort . " +
                "							?study rdfs:label ?studyLabel ." +
                "			        		?cohort hasco:isCohortOf ?study . " +
                "							?cohort rdfs:label ?cohortLabel" +
                "			        		OPTIONAL { ?subjectUri rdfs:label ?subjectLabel } . " +
                "			        		OPTIONAL { ?subjectUri a ?subjectType . " +
                "										?subjectType rdfs:label ?subjectTypeLabel} . " +
                "			                             }";
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), subjectQueryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                values = new HashMap<String, String>();
                if (soln.contains("subjectLabel"))
                    values.put("Label", soln.get("subjectLabel").toString());
                if (soln.contains("subjectType"))
                    values.put("Type", replaceURLWithPrefix(soln.get("subjectType").toString()));
                if (soln.contains("subjectTypeLabel"))
                    values.put("TypeLabel", soln.get("subjectTypeLabel").toString());
                if (soln.contains("cohortLabel"))
                    values.put("Cohort", soln.get("cohortLabel").toString());
                if (soln.contains("studyLabel"))
                    values.put("Study", replaceURLWithPrefix(soln.get("studyLabel").toString()));
                subjectResult.put(replaceURLWithPrefix(soln.get("subjectUri").toString()),values);
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return subjectResult;
    }

    public static Map<String, Map<String,String>> findSubject(String subject_uri) {
        Map<String, Map<String,String>> subjectResult = new HashMap<String, Map<String,String>>();
        Map<String,String> values = new HashMap<String, String>();
        String subjectQueryString = "";
        subjectQueryString = getPrefixes() +
                "SELECT ?subjectUri ?subjectType ?subjectTypeLabel ?subjectLabel ?cohortLabel ?studyLabel " +
                "			 WHERE {        ?subjectUri hasco:isSubjectOf* ?cohort . " +
                "							?study rdfs:label ?studyLabel ." +
                "			        		?cohort hasco:isCohortOf ?study . " +
                "							?cohort rdfs:label ?cohortLabel" +
                "			        		OPTIONAL { ?subjectUri rdfs:label ?subjectLabel } . " +
                "			        		OPTIONAL { ?subjectUri a ?subjectType . " +
                "										?subjectType rdfs:label ?subjectTypeLabel} . " +
                "			        		FILTER (?subjectUri = " + subject_uri + ") . " +
                "			                             }";
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), subjectQueryString);

            while (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                values = new HashMap<String, String>();
                if (soln.contains("subjectLabel"))
                    values.put("Label", soln.get("subjectLabel").toString());
                if (soln.contains("subjectType"))
                    values.put("Type", replaceURLWithPrefix(soln.get("subjectType").toString()));
                if (soln.contains("subjectTypeLabel"))
                    values.put("TypeLabel", soln.get("subjectTypeLabel").toString());
                if (soln.contains("cohortLabel"))
                    values.put("Cohort", soln.get("cohortLabel").toString());
                if (soln.contains("studyLabel"))
                    values.put("Study", replaceURLWithPrefix(soln.get("studyLabel").toString()));

                subjectResult.put(replaceURLWithPrefix(soln.get("subjectUri").toString()),values);
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return subjectResult;
    }

    public Result index() {
        return ok();
    }

    public Result postIndex() {
        return index();
    }
}
