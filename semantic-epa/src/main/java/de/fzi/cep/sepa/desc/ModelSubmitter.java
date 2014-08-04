package de.fzi.cep.sepa.desc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

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
import org.restlet.data.Protocol;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.model.impl.AbstractSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEP;
import de.fzi.cep.sepa.util.SEPTransformer;

public class ModelSubmitter {

	public static <T extends EventStreamDeclarer, B extends SemanticEventProducerDeclarer> boolean submitProducer(B producer, T...declarers) throws Exception
	{
		String defaultPath = "/" +producer.declareURIPath();
		Component component = new Component();
				    
		component.getServers().add(Protocol.HTTP, producer.declarePort());	
		SEP sep = producer.declareSemanticEventProducer();
		
		component.getDefaultHost().attach(defaultPath, generateRestlet(producer.declareSemanticEventProducer()));
		
		for(T declarer : declarers)
		{
			
			EventStream stream = declarer.declareStream(producer.declareSemanticEventProducer());
			sep.setEventStreams(Arrays.asList(stream));
			String currentPath = defaultPath + "/" +"t";
			
			component.getDefaultHost().attach(currentPath, generateRestlet(sep));
			
		}
		
		component.start();
		
		// Start runtime
		return true;
		
	}
	
	private static <T extends AbstractSEPAElement> Restlet generateRestlet(T sepaElement)
	{
		 return new Restlet() {
				
				@Override
				public void handle(Request request, Response response)
				{
					try {
						Graph rdfGraph = SEPTransformer.generateCompleteSEPGraph(new GraphImpl(), sepaElement);
						System.out.println(asString(rdfGraph));
						response.setEntity(asString(rdfGraph), MediaType.APPLICATION_JSON);
					} catch (InvalidRdfException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
