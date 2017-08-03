package org.streampipes.commons;

import org.streampipes.commons.config.old.Configuration;
import org.apache.commons.lang.RandomStringUtils;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
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
	
	@SafeVarargs
	public static <T> List<T> createList(T...objects)
	{
		List<T> result = new ArrayList<T>();
		for(T object : objects)
		{
			result.add(object);
		}
		return result;
	}
	
	public static String getRandomString()
	{
		return RandomStringUtils.randomAlphabetic(10);
	}
	
	public static String asString(Graph graph) throws RDFHandlerException
	{
		OutputStream stream = new ByteArrayOutputStream();
		
		RDFWriter writer = Configuration.getInstance().getRioWriter(stream);
		
		//RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);
//		RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);
	
//		writer.handleNamespace("sepa", "http://sepa.event-processing.org/sepa#");
//		writer.handleNamespace("empire", "urn:clarkparsia.com:empire:");
//		writer.handleNamespace("fzi", "urn:fzi.de:sepa:");
//		
//		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
//		writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);
		
//		Rio.write(graph, stream, RDFFormat.JSONLD);
		Rio.write(graph, writer);
		return stream.toString();
	}
	
	public static String getHostname()
	{
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			System.out.println(addr.getCanonicalHostName());
			return addr.getCanonicalHostName();
		} catch (UnknownHostException e) {
			return "localhost";
		}	
	}
}
