package de.fzi.cep.sepa.storage.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.fluent.Request;
import org.lightcouch.CouchDbClient;

import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.messages.ElementRecommendation;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.connection.Connection;
import de.fzi.cep.sepa.storage.api.ConnectionStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class ConnectionStorageImpl implements ConnectionStorage {

	@Override
	public void addConnection(Connection connection) {
		CouchDbClient dbClient = Utils.getCouchDbConnectionClient();
		dbClient.save(connection);
		dbClient.shutdown();
	}

	@Override
	public List<ElementRecommendation> getRecommendedElements(String from) {
		CouchDbClient dbClient = Utils.getCouchDbConnectionClient();
		// doesn't work as required object array is not created by lightcouch
		//List<JsonObject> obj = dbClient.view("connection/frequent").startKey(from).endKey(from, new Object()).group(true).query(JsonObject.class);
		String query;
		query = buildQuery(dbClient, from);
		System.out.println(query);
		Optional<JsonObject> jsonObjectOpt = getFrequentConnections(query);
		if (jsonObjectOpt.isPresent()) return handleResponse(jsonObjectOpt.get());
		else return Collections.emptyList();
		
	}
	
	private String buildQuery(CouchDbClient dbClient, String from)  {
			String escapedPath = UrlEscapers.urlPathSegmentEscaper().escape("startkey=[\"" +from +"\"]&endkey=[\"" +from +"\", {}]&limit=10&group=true");
			return dbClient.getDBUri() +"_design/connection/_view/frequent?" + escapedPath ;
	}

	private List<ElementRecommendation> handleResponse(JsonObject jsonObject) {
		List<ElementRecommendation> recommendations = new ArrayList<>();
		JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
		jsonArray.forEach(resultObj -> recommendations.add(new ElementRecommendation(resultObj.getAsJsonObject().get("key").getAsJsonArray().get(1).getAsString(), "", "")));
		return recommendations;
	}

	public static void main(String[] args)
	{
		List<ElementRecommendation> recs = new ConnectionStorageImpl().getRecommendedElements("http://localhost:8089/taxi/sample");
		recs.forEach(rec -> System.out.println(rec.getElementId()));
	}
	
	private Optional<JsonObject> getFrequentConnections(String query) {
		try {
			return Optional.of((JsonObject)new JsonParser().parse(Request.Get(query).execute().returnContent().asString()));
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
