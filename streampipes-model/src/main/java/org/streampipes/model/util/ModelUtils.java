package org.streampipes.model.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelUtils {
	
	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		//gsonBuilder.registerTypeAdapter(StaticProperty.class, new StaticPropertySerializer());
		//gsonBuilder.registerTypeAdapter(EventProperty.class, new EventPropertySerializer());
		gsonBuilder.setPrettyPrinting();
		Gson gson = gsonBuilder.create();
		return gson;
		
	}
	
	public static Class<?> getPrimitiveClass(String propertyType)
	{
		String xmlBaseURI = "http://www.w3.org/2001/XMLSchema#";
		if (propertyType.equals(xmlBaseURI+"string")) return String.class;
		else if (propertyType.equals(xmlBaseURI+"double")) return Double.class;
		else if (propertyType.equals(xmlBaseURI+"long")) return Long.class;
		else if (propertyType.equals(xmlBaseURI+"integer") )return Integer.class;
		else if (propertyType.equals(xmlBaseURI+"boolean") )return Boolean.class;
		else if (propertyType.equals(xmlBaseURI+"float")) return Float.class;
		else return null;
	}
	
	public static Class<?> getPrimitiveClassAsArray(String propertyType)
	{
		String xmlBaseURI = "http://www.w3.org/2001/XMLSchema#";
		if (propertyType.equals(xmlBaseURI+"string")) return String[].class;
		else if (propertyType.equals(xmlBaseURI+"double")) return Double[].class;
		else if (propertyType.equals(xmlBaseURI+"long")) return Long[].class;
		else if (propertyType.equals(xmlBaseURI+"integer") )return Integer[].class;
		else if (propertyType.equals(xmlBaseURI+"boolean") )return Boolean[].class;
		else if (propertyType.equals(xmlBaseURI+"float")) return Float[].class;
		else return null;
	}
	
	public static String asString(Graph graph) throws RDFHandlerException
	{
		OutputStream stream = new ByteArrayOutputStream();
		Rio.write(graph, stream, RDFFormat.JSONLD);
		return stream.toString();
	}
	
	public static List<Map<String, Object>> asList(Map<String, Object> map)
	{
		List<Map<String, Object>> result = new ArrayList<>();
		result.add(map);
		return result;
	}
		
}
