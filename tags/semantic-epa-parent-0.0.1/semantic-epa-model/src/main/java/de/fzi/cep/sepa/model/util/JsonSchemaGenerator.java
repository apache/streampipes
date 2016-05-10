package de.fzi.cep.sepa.model.util;

import org.json.JSONObject;

import pl.zientarski.SchemaMapper;

public class JsonSchemaGenerator {

	public String getJsonSchema(Class<?> clazz)
	{
		SchemaMapper schemaMapper = new SchemaMapper();
		JSONObject jsonObject = schemaMapper.toJsonSchema4(clazz, true);
		return jsonObject.toString();
	}
}
