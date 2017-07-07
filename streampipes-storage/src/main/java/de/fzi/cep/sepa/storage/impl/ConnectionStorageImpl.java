package de.fzi.cep.sepa.storage.impl;

import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fzi.cep.sepa.model.client.connection.Connection;
import de.fzi.cep.sepa.model.client.pipeline.PipelineElementRecommendation;
import de.fzi.cep.sepa.storage.api.ConnectionStorage;
import de.fzi.cep.sepa.storage.util.Utils;
import org.apache.http.client.fluent.Request;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConnectionStorageImpl extends Storage<PipelineElementRecommendation> implements ConnectionStorage {


	public ConnectionStorageImpl() {
		super(PipelineElementRecommendation.class);
	}
	@Override
	public void addConnection(Connection connection) {
		CouchDbClient dbClient = getCouchDbClient();
		dbClient.save(connection);
		dbClient.shutdown();
	}

	@Override
	public List<PipelineElementRecommendation> getRecommendedElements(String from) {
		// doesn't work as required object array is not created by lightcouch
		//List<JsonObject> obj = dbClient.view("connection/frequent").startKey(from).endKey(from, new Object()).group(true).query(JsonObject.class);
		String query;
		query = buildQuery(from);
		System.out.println(query);
		Optional<JsonObject> jsonObjectOpt = getFrequentConnections(query);
		if (jsonObjectOpt.isPresent()) return handleResponse(jsonObjectOpt.get());
		else return Collections.emptyList();
		
	}
	
	private String buildQuery(String from)  {
			CouchDbClient dbClient = getCouchDbClient();
			String escapedPath = UrlEscapers.urlPathSegmentEscaper().escape("startkey=[\"" +from +"\"]&endkey=[\"" +from +"\", {}]&group=true");
		return dbClient.getDBUri() +"_design/connection/_view/frequent?" + escapedPath ;
	}

	private List<PipelineElementRecommendation> handleResponse(JsonObject jsonObject) {
		List<PipelineElementRecommendation> recommendations = new ArrayList<>();
		JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
		jsonArray.forEach(resultObj ->
				recommendations.add(makeRecommendation(resultObj)));
		return recommendations;
	}

	private PipelineElementRecommendation makeRecommendation(JsonElement resultObj) {
		PipelineElementRecommendation recommendation = new PipelineElementRecommendation();
		recommendation.setElementId(resultObj
				.getAsJsonObject()
				.get("key")
				.getAsJsonArray()
				.get(1).getAsString());

		recommendation.setCount(resultObj
				.getAsJsonObject()
				.get("value")
				.getAsInt());

		return recommendation;
	}

	private Optional<JsonObject> getFrequentConnections(String query) {
		try {
			return Optional.of((JsonObject)new JsonParser().parse(Request.Get(query).execute().returnContent().asString()));
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@Override
	protected CouchDbClient getCouchDbClient() {
		return Utils.getCouchDbConnectionClient();
	}
}
