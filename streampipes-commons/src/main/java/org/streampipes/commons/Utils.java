package org.streampipes.commons;

import org.apache.commons.lang.RandomStringUtils;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

	public static List<URI> createURI(String...uris)
	{
		List<URI> result = new ArrayList<>();
		for(String uri : uris)
		{
			result.add(URI.create(uri));
		}
		return result;
	}
	
	@SafeVarargs
	public static <T> List<T> createList(T...objects)
	{
		List<T> result = new ArrayList<>();
		Collections.addAll(result, objects);
		return result;
	}
	
	public static String getRandomString()
	{
		return RandomStringUtils.randomAlphabetic(10);
	}
	
	public static String asString(Graph graph) throws RDFHandlerException
	{
		OutputStream stream = new ByteArrayOutputStream();
		
		RDFWriter writer = Utils.getRioWriter(stream);
		
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


	public static RDFWriter getRioWriter(OutputStream stream) throws RDFHandlerException
	{
		RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);

		writer.handleNamespace("sepa", "http://sepa.event-processing.org/sepa#");
		writer.handleNamespace("ssn", "http://purl.oclc.org/NET/ssnx/ssn#");
		writer.handleNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
		writer.handleNamespace("empire", "urn:clarkparsia.com:empire:");
		writer.handleNamespace("fzi", "urn:fzi.de:sepa:");

		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
		writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);

		return writer;
	}
	
}
