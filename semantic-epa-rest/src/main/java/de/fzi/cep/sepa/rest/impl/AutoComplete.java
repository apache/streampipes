package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.messages.AutocompleteItem;
import de.fzi.cep.sepa.messages.AutocompleteResult;
import de.fzi.cep.sepa.model.client.ontology.OntologyQuery;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.ontology.QueryExecutor;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;

@Path("/autocomplete")
public class AutoComplete extends AbstractRestInterface {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getItem(@QueryParam("propertyName") String propertyName, @QueryParam("term") String term) {
        AutocompleteResult result = new AutocompleteResult();
        String query = QueryBuilder.getAutocompleteSuggestion(propertyName);
        System.out.println(query);
        try {
            TupleQueryResult queryResult = new QueryExecutor(StorageManager.INSTANCE.getRepository()).executeQuery(query);
            while (queryResult.hasNext()) {
                BindingSet set = queryResult.next();
                AutocompleteItem item = new AutocompleteItem(set.getValue("label").stringValue(), set.getValue("value").stringValue());
                if (item.getLabel().startsWith(term)) result.add(item);
            }
        } catch (QueryEvaluationException | RepositoryException
                | MalformedQueryException e) {

            e.printStackTrace();
        }
        return toJson(result);
    }

    @POST
    @Path("/domain")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOntologyQueryResult(String ontologyQuery) {
        OntologyQuery query = fromJson(ontologyQuery, OntologyQuery.class);

        return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getOntologyResult(query));
    }

}