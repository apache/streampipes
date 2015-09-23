package de.fzi.cep.sepa.model;

import java.io.IOException;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

import pl.zientarski.SchemaMapper;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class TestJsonSchema {

	public static void main(String[] args)
	{
		try {
			new TestJsonSchema().getJsonSchemaJackson(SepaInvocation.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getJsonSchema(Class clazz) throws IOException {
		SchemaMapper schemaMapper = new SchemaMapper();
		JSONObject jsonObject = schemaMapper.toJsonSchema4(clazz, true);
		System.out.println(jsonObject.toString(4));
	 
	}
	
	private void getJsonSchemaJackson(Class clazz) throws JsonProcessingException
	{
		ObjectMapper m = new ObjectMapper();
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
		JsonSchema jsonSchema = visitor.finalSchema();
		System.out.println(m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema));
		   
		
	}
}
