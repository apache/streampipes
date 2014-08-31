package de.fzi.cep.sepa.desc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.storage.util.Transformer;

@SuppressWarnings("deprecation")
public class ModelSubmitter {

	public static boolean submitProducer(List<SemanticEventProducerDeclarer> producers, String baseUri, int port) throws Exception
	{
		Component component = new Component();    
		component.getServers().add(Protocol.HTTP, port);	
		
		for (SemanticEventProducerDeclarer producer : producers)
		{		
			SEP sep = producer.declareModel();
			String currentPath = sep.getUri();
			sep.setUri(baseUri + currentPath);
			
			for(EventStreamDeclarer declarer : producer.getEventStreams())
			{
				EventStream stream = declarer.declareModel(sep);
				//stream.setUri(baseUri + stream.getUri());
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
			sepa.setUri(baseUri + sepa.getPathName());
			component.getDefaultHost().attach(sepa.getPathName(), generateSEPARestlet(sepa, declarer));
		}
		
		component.start();
		return true;
	}
	
	public static boolean submitConsumer(List<SemanticEventConsumerDeclarer> declarers, String baseUri, int port) throws Exception
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);	
		
		for(SemanticEventConsumerDeclarer declarer : declarers)
		{
			
			SEC sec = declarer.declareModel();
			String pathName = sec.getUri();
			sec.setUri(baseUri + sec.getUri());
			component.getDefaultHost().attach(pathName, generateSECRestlet(sec, declarer));
		}
		
		component.start();
		return true;
	}
	
	private static Restlet generateSECRestlet(SEC sec, SemanticEventConsumerDeclarer declarer)
	{
		return new Restlet() {
			public void handle(Request request, Response response)
			{
				
				try {
					if (request.getMethod().equals(Method.GET))
					{				
						Graph rdfGraph = Transformer.generateCompleteGraph(new GraphImpl(), sec);
						response.setEntity(asString(rdfGraph), MediaType.APPLICATION_JSON);	
					}
					else if (request.getMethod().equals(Method.POST))
					{
						//TODO: extract HTTP parameters
						declarer.invokeRuntime();
					}
					else if (request.getMethod().equals(Method.DELETE))
					{
						declarer.detachRuntime();
					}
					
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}
	
	private static Restlet generateSEPARestlet(SEPA sepa, SemanticEventProcessingAgentDeclarer declarer)
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
						Form form = new Form(request.getEntity());
						//TODO: extract HTTP parameters
						declarer.invokeRuntime(Transformer.fromJsonLd(SEPAInvocationGraph.class, form.getFirstValue("json")));
					}
					else if (request.getMethod().equals(Method.DELETE))
					{
						declarer.detachRuntime(Transformer.fromJsonLd(SEPAInvocationGraph.class, request.getEntity().getText()));
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
		RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);
		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
		writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);
		//Rio.write(graph, stream, RDFFormat.JSONLD);
		Rio.write(graph, writer);
		return stream.toString();
	}
	
	
}
