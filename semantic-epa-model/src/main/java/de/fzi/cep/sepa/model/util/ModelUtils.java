package de.fzi.cep.sepa.model.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelUtils {

	public static URI createRandomUri(String ns)
	{
		return ValueFactoryImpl.getInstance().createURI(ns + UUID.randomUUID());
	}
	
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
		else return null;
	}
	
	public static Class<?> getPrimitiveClassAsArray(String propertyType)
	{
		String xmlBaseURI = "http://www.w3.org/2001/XMLSchema#";
		if (propertyType.equals(xmlBaseURI+"string")) return String[].class;
		else if (propertyType.equals(xmlBaseURI+"double")) return Double[].class;
		else if (propertyType.equals(xmlBaseURI+"long")) return Long[].class;
		else if (propertyType.equals(xmlBaseURI+"integer") )return Integer[].class;
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
	

	
	private static boolean contains(String value, String[] list)
	{
		return Arrays.asList(list).contains(value);
	}
	
}
