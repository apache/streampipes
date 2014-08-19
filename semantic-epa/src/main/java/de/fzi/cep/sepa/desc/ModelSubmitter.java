package de.fzi.cep.sepa.desc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.clarkparsia.empire.annotation.RdfGenerator;

import de.fzi.cep.sepa.model.impl.AbstractSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEP;
import de.fzi.cep.sepa.model.impl.SEPA;
import de.fzi.cep.sepa.storage.util.Transformer;

public class ModelSubmitter {

	public static boolean submitProducer(List<SemanticEventProducerDeclarer> producers, String baseUri, int port) throws Exception
	{
		Component component = new Component();    
		component.getServers().add(Protocol.HTTP, port);	
		
		for (SemanticEventProducerDeclarer producer : producers)
		{		
			SEP sep = producer.declareModel();
			String currentPath = sep.getUri();
			sep.setUri(baseUri + sep.getUri());
			
			for(EventStreamDeclarer declarer : producer.getEventStreams())
			{
				EventStream stream = declarer.declareModel(sep);
				stream.setUri(baseUri + stream.getUri());
				sep.addEventStream(stream);	
				if (declarer.isExecutable()) declarer.executeStream();
			}
			component.getDefaultHost().attach(currentPath, generateSEPRestlet(sep));
		}
						
			component.start();
			
			// Start runtime
			return true;
	}
	
	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers, String baseUri, int port) throws Exception
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);	
		
		for(SemanticEventProcessingAgentDeclarer declarer : declarers)
		{
			SEPA sepa = declarer.declareModel();
			component.getDefaultHost().attach(sepa.getPathName(), generateSEPARestlet(sepa));
		}
		
		component.start();
		return true;
	}
	
	private static Restlet generateSEPARestlet(SEPA sepa)
	{
		return new Restlet() {
			public void handle(Request request, Response response)
			{
				
				try {
					if (request.getMethod().equals(Method.GET))
					{				
						Graph rdfGraph = Transformer.generateCompleteGraph(new GraphImpl(), sepa);
						response.setEntity(asString(rdfGraph), MediaType.APPLICATION_JSON);		
					}
					else if (request.getMethod().equals(Method.POST))
					{
						// TODO: invoke EPA
					}
					
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}
	
	private static <T extends AbstractSEPAElement> Restlet generateSEPRestlet(T sepaElement)
	{
		 return new Restlet() {
				
				@Override
				public void handle(Request request, Response response)
				{
					try {
						Graph rdfGraph = Transformer.generateCompleteGraph(new GraphImpl(), sepaElement);
						Rio.write(rdfGraph, System.out, RDFFormat.JSONLD);
						response.setEntity(asString(rdfGraph), MediaType.APPLICATION_JSON);
					} catch (RDFHandlerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		 };
	}
	
	public static String asString(Graph graph) throws RDFHandlerException
	{
		OutputStream stream = new ByteArrayOutputStream();
		Rio.write(graph, stream, RDFFormat.JSONLD);
		return stream.toString();
	}
	
	
}
