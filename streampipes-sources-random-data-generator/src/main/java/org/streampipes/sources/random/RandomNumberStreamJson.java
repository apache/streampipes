/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sources.random;

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

