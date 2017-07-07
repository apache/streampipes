package org.streampipes.container.html;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.streampipes.container.html.model.Description;
import org.streampipes.container.html.model.SemanticEventProducerDescription;

import java.util.List;

public class JSONGenerator {

private List<Description> description;
	
	public JSONGenerator(List<Description> description)
	{
		this.description = description;
	}
	
	public String buildJson()
	 {
		JsonArray jsonArray = new JsonArray();
		description.forEach(d -> jsonArray.add(getJsonElement(d)));
		return jsonArray.toString();
	 }

	private JsonObject getJsonElement(Description d) {
		JsonObject obj = makeDescription(d);
		if (d instanceof SemanticEventProducerDescription) {
			SemanticEventProducerDescription producerDesc = (SemanticEventProducerDescription) d;
			JsonArray array = new JsonArray();

			producerDesc.getStreams().forEach(s -> {
				array.add(makeDescription(s));
			});

			obj.add("streams", array);
		}
		return obj;
	}

	private JsonObject makeDescription(Description d) {
		JsonObject obj = new JsonObject();
		obj.add("uri", new JsonPrimitive(d.getUri().toString()));
		obj.add("name", new JsonPrimitive(d.getName()));
		obj.add("description", new JsonPrimitive(d.getDescription()));
		obj.add("type", new JsonPrimitive(d.getType()));
		return obj;
	}
}
