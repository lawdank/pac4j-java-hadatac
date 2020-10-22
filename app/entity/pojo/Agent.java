package entity.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import http.SPARQLUtils;
import util.CollectionUtil;
import util.NameSpaces;

import com.typesafe.config.ConfigFactory;

public class Agent implements Comparable<Agent> {

    private String uri;
    private String agentType;
    private String label;
    private String name;
    private String familyName;
    private String givenName;

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getType() {
        return agentType;
    }
    public void setType(String agentType) {
        this.agentType = agentType;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public static List<Agent> findOrganizations() {
        String query = 
                " SELECT ?uri WHERE { " +
                        " ?uri a foaf:Group ." + 
                        "} ";
        return findByQuery(query);
    }

    public static List<Agent> findPersons() {
        String query = 
                " SELECT ?uri WHERE { " +
                        " ?uri a foaf:Person ." + 
                        "} ";
        return findByQuery(query);
    }

    private static List<Agent> findByQuery(String requestedQuery) {
        List<Agent> agents = new ArrayList<Agent>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + requestedQuery;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.METADATA_SPARQL), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String resp_uri = soln.getResource("uri").getURI();
            Agent agent = Agent.find(resp_uri);
            agents.add(agent);
        }			

        java.util.Collections.sort((List<Agent>) agents);
        return agents;
    }

    public static Agent find(String agent_uri) {
        Agent agent = null;
        Statement statement;
        RDFNode object;
        String queryString;

        if (agent_uri.startsWith("<")) {
            queryString = "DESCRIBE " + agent_uri + " ";
        } else {
            queryString = "DESCRIBE <" + agent_uri + ">";
        }
        
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.METADATA_SPARQL), queryString);

        agent = new Agent();
        StmtIterator stmtIterator = model.listStatements();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            if (statement.getPredicate().getURI().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                agent.setLabel(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals("http://www.w3.org/2000/01/rdf-schema#type")) {
                agent.setType(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals("http://xmlns.com/foaf/0.1/name")) {
                agent.setName(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals("http://xmlns.com/foaf/0.1/familyName")) {
                agent.setFamilyName(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals("http://xmlns.com/foaf/0.1/givenName")) {
                agent.setGivenName(object.asLiteral().getString());
            }
        }

        agent.setUri(agent_uri);

        return agent;
    }

    @Override
    public int compareTo(Agent another) {
        if (this.getName() == null || another == null || another.getName() == null) {
            return 0;
        }
        return this.getName().compareTo(another.getName());
    }

}
