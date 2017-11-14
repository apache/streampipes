package org.streampipes.pe.sources.samples.random;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;

import java.util.Optional;

public class RandomNumberStreamJson extends RandomNumberStream {

	public static final String TOPIC = "SEPA.SEP.Random.Number.Json";

	public RandomNumberStreamJson() {
		super(TOPIC);
	}

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(TOPIC, MessageFormat.Json);
		stream.setName("Random Number Stream (JSON)");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/numberjson");
		
		return stream;
	}
	
	@Override
	protected Optional<byte[]> getMessage(long nanoTime, int randomNumber, int counter) {
		try {
			return Optional.of(
					buildJson(nanoTime, randomNumber, counter)
						.toString()
						.getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	private JSONObject buildJson(long timestamp, int randomNumber, int counter) throws JSONException {
		JSONObject json = new JSONObject();

		json.put("timestamp", timestamp);
		json.put("randomValue", randomNumber);
		json.put("randomString", randomString());
		json.put("count", counter);
		return json;
	}
	
	public static void main(String[] args) {
		new RandomNumberStreamJson().executeStream();
	}

}

