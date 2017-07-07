package org.streampipes.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import org.streampipes.model.client.messages.AutocompleteItem;
import org.streampipes.model.client.messages.AutocompleteResult;
import org.streampipes.model.client.ontology.OntologyQuery;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.storage.ontology.QueryExecutor;
import org.streampipes.storage.sparql.QueryBuilder;

@Path("/v2/autocomplete")
public class AutoComplete extends AbstractRestInterface {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@QueryParam("propertyName") String propertyName, @QueryParam("term") String term) {
        AutocompleteResult result = new AutocompleteResult();
        String query = QueryBuilder.getAutocompleteSuggestion(propertyName);

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
        return ok(result);
    }

    @POST
    @Path("/domain")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOntologyQueryResult(OntologyQuery ontologyQuery) {
        return ok(StorageManager
                .INSTANCE
                .getBackgroundKnowledgeStorage().getOntologyResult(ontologyQuery));
    }

}