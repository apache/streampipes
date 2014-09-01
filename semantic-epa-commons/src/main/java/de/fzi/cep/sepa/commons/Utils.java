package de.fzi.cep.sepa.commons;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static final String SERVER_URL;
	public static final String CONTEXT_PATH;
	public static final int PORT;
	public static final String IMG_DIR;

	static {
		SERVER_URL = "http://anemone06.fzi.de";
		CONTEXT_PATH = "/semantic-epa-backend";
		PORT = 8080;
		IMG_DIR = "img";

	}

	public static String getImageUrl() {
		return SERVER_URL + CONTEXT_PATH + "/" + IMG_DIR + "/";
	}

	public static List<URI> createURI(String...uris)
	{
		List<URI> result = new ArrayList<URI>();
		for(String uri : uris)
		{
			result.add(URI.create(uri));
		}
		return result;
	}
	
	public static <T> List<T> createList(T...objects)
	{
		List<T> result = new ArrayList<T>();
		for(T object : objects)
		{
			result.add(object);
		}
		return result;
	}

}
