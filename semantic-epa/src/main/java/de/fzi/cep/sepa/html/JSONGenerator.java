package de.fzi.cep.sepa.html;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

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

	private JsonElement getJsonElement(Description d) {
		JsonPrimitive uri = new JsonPrimitive(d.getUri().toString());
		return uri;
	}
}
