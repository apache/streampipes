package de.fzi.cep.sepa.desc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFHandlerException;
import org.restlet.Component;
import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.html.EventConsumerWelcomePage;
import de.fzi.cep.sepa.html.EventProcessingAgentWelcomePage;
import de.fzi.cep.sepa.html.EventProducerWelcomePage;
import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.transform.Transformer;

@SuppressWarnings("deprecation")
public class ModelSubmitter {
	
	private static final String HEADERS_KEY = "org.restlet.http.headers";

	public static boolean submitProducer(
			List<SemanticEventProducerDeclarer> producers, String baseUri,
			int port) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		
		component.getDefaultHost().attach("", new EventProducerWelcomePage(baseUri, producers));

		for (SemanticEventProducerDeclarer producer : producers) {
			SepDescription sep = producer.declareModel();
			String currentPath = sep.getUri();
			sep.setUri(baseUri + currentPath);

			for (EventStreamDeclarer declarer : producer.getEventStreams()) {
				EventStream stream = declarer.declareModel(sep);
				// stream.setUri(baseUri + stream.getUri());
				sep.addEventStream(stream);
				if (declarer.isExecutable())
					declarer.executeStream();
			}
			component.getDefaultHost().attach(currentPath,
					generateSEPRestlet(sep));
		}

		component.start();

		// Start runtime
		return true;
	}

	public static boolean submitAgent(
			List<SemanticEventProcessingAgentDeclarer> declarers,
			String baseUri, int port) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach("", new EventProcessingAgentWelcomePage(baseUri, declarers));

		for (SemanticEventProcessingAgentDeclarer declarer : declarers) {
			SepaDescription sepa = declarer.declareModel();
			sepa.setUri(baseUri + sepa.getPathName());
			component.getDefaultHost().attach(sepa.getPathName(),
					generateSEPARestlet(component, sepa, declarer));
		}

		component.start();
		return true;
	}

	public static boolean submitConsumer(
			List<SemanticEventConsumerDeclarer> declarers, String baseUri,
			int port) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach("", new EventConsumerWelcomePage(baseUri, declarers));

		for (SemanticEventConsumerDeclarer declarer : declarers) {

			SecDescription sec = declarer.declareModel();
			String pathName = sec.getUri();
			sec.setUri(baseUri + sec.getUri());
			component.getDefaultHost().attach(pathName,
					generateSECRestlet(component, sec, declarer));
		}

		component.start();
		return true;
	}

	private static Restlet generateSECRestlet(Component component, SecDescription sec,
			SemanticEventConsumerDeclarer declarer) {

		List<Restlet> restlets = new ArrayList<>();

		return new Restlet() {
			public void handle(Request request, Response response) {

				try {
					if (request.getMethod().equals(Method.GET)) {
						Graph rdfGraph = Transformer.toJsonLd(sec);
						response.setEntity(asString(rdfGraph),
								MediaType.APPLICATION_JSON);
					} else if (request.getMethod().equals(Method.POST)) {
						String pathName = declarer.declareModel().getUri();
						Form form = new Form(request.getEntity());
						SecInvocation graph = Transformer.fromJsonLd(
								SecInvocation.class,
								form.getFirstValue("json"));
						// TODO: extract HTTP parameters
						Restlet restlet = generateConcreteActionRestlet(graph, 
								declarer, component);
						component.getDefaultHost().attach(pathName +"/" +graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), restlet);
						restlets.add(restlet);
					} else if (request.getMethod().equals(Method.DELETE)) {
						//TODO
						//declarer.detachRuntime(new SECInvocationGraph());
						restlets.clear();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private static Restlet generateConcreteActionRestlet(SecInvocation graph, SemanticEventConsumerDeclarer declarer, Component component)
	{
		declarer.invokeRuntime(graph);
		return new Restlet() {
			
			public void handle(Request request, Response response)
			{
				if (request.getMethod().equals(Method.GET))
				{	
					response.setEntity(declarer.invokeRuntime(graph) , MediaType.TEXT_HTML);
					getMessageHeaders(response).add("Access-Control-Allow-Origin", "*"); 
					getMessageHeaders(response).add("Access-Control-Allow-Methods", "POST,OPTIONS,GET");
					getMessageHeaders(response).add("Access-Control-Allow-Headers", "Content-Type"); 
					getMessageHeaders(response).add("Access-Control-Allow-Credentials", "true"); 
					getMessageHeaders(response).add("Access-Control-Max-Age", "60"); 
				}
				if (request.getMethod().equals(Method.OPTIONS))
				{
					getMessageHeaders(response).add("Access-Control-Allow-Origin", "*"); 
					getMessageHeaders(response).add("Access-Control-Allow-Methods", "POST,OPTIONS,GET");
					getMessageHeaders(response).add("Access-Control-Allow-Headers", "Content-Type"); 
					getMessageHeaders(response).add("Access-Control-Allow-Credentials", "true"); 
					getMessageHeaders(response).add("Access-Control-Max-Age", "60"); 
				}
				
				if (request.getMethod().equals(Method.DELETE))
				{
					component.getDefaultHost().detach(this);
				}
			}
			
		};
	}

	private static Restlet generateSEPARestlet(Component component, SepaDescription sepa,
			SemanticEventProcessingAgentDeclarer declarer) {
		return new Restlet() {
			public void handle(Request request, Response response) {

				
				try {
					if (request.getMethod().equals(Method.GET)) {
						Graph rdfGraph = Transformer.toJsonLd(sepa);
						response.setEntity(asString(rdfGraph),
								MediaType.APPLICATION_JSON);
					} else if (request.getMethod().equals(Method.POST)) {
						String pathName = declarer.declareModel().getUri();
						Form form = new Form(request.getEntity());
						// TODO: extract HTTP parameters
						SepaInvocation graph = Transformer.fromJsonLd(
								SepaInvocation.class,
								form.getFirstValue("json"));
						Restlet restlet = generateConcreteSEPARestlet(graph, 
								declarer, component);
	
						component.getDefaultHost().attach(pathName +"/" +graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), restlet);	
					} 

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	protected static Restlet generateConcreteSEPARestlet(
			SepaInvocation graph,
			SemanticEventProcessingAgentDeclarer declarer, Component component) {
		
		declarer.invokeRuntime(graph);
		
		return new Restlet() {
			
			public void handle(Request request, Response response)
			{
				if (request.getMethod().equals(Method.DELETE))
				{
					declarer.detachRuntime();
					component.getDefaultHost().detach(this);
				}
			}
			
		};
	}

	private static <T extends AbstractSEPAElement> Restlet generateSEPRestlet(
			T sepaElement) {
		return new Restlet() {

			@Override
			public void handle(Request request, Response response) {
				
				try {	
				Graph rdfGraph = Transformer.toJsonLd(sepaElement);
				
				response.setEntity(asString(rdfGraph),
						MediaType.APPLICATION_JSON);
							
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
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidRdfException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
	}

	public static String asString(Graph graph) throws RDFHandlerException {
		return Utils.asString(graph);
	}

	@SuppressWarnings("unchecked")
	static Series<Header> getMessageHeaders(Message message) {

		ConcurrentMap<String, Object> attrs = message.getAttributes();

		Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY);
		if (headers == null) {
			headers = new Series<Header>(Header.class);
			Series<Header> prev = (Series<Header>) attrs.putIfAbsent(
					HEADERS_KEY, headers);
			if (prev != null) {
				headers = prev;
			}
		}
		return headers;
	}

}
