package de.fzi.cep.sepa.client.json;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.util.GsonSerializer;

public class SchemaGenerator {

	public static <T> T fromJson(File file, Class<T> clazz) throws IOException
	{
		return fromJson(FileUtils.readFileToString(file), clazz);
	}
	
	public static <T> T fromJson(String json, Class<T> clazz)
	{
		return GsonSerializer.getGson().fromJson(json, clazz);
	}
	
	public static SepaDescription epaFromJson(String json)
	{
		return fromJson(json, SepaDescription.class);
	}
	
	public static SepDescription sourceFromJson(String json)
	{
		return fromJson(json, SepDescription.class);
	}
	
	public static SecDescription consumerFromJson(String json)
	{
		return fromJson(json, SecDescription.class);
	}
	
}
