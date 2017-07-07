package de.fzi.cep.sepa.sources.samples.random;

import java.util.Optional;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class RandomNumberStreamJson extends RandomNumberStream {

	public static final String TOPIC = "SEPA.SEP.Random.Number.Json";

	public RandomNumberStreamJson() {
		super(TOPIC);
	}

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(TOPIC, MessageFormat.Json);
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

