package de.fzi.cep.sepa.endpoint;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.transform.Transformer;

public class SecRestlet extends ConsumableRestlet<SecDescription, SecInvocation> {

	private static final String HEADERS_KEY = "org.restlet.http.headers";
	
	public SecRestlet(SecDescription desc, SemanticEventConsumerDeclarer declarer)
	{
		super(desc, SecInvocation.class, declarer);
	}
	
	@Override
	protected Restlet instanceRestlet(SecInvocation graph) {
		return new Restlet() {
			
			public void handle(Request request, Response response)
			{
				if (request.getMethod().equals(Method.GET))
				{	
					response.setEntity(((SemanticEventConsumerDeclarer) declarer).getHtml(graph) , MediaType.TEXT_HTML);
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
					Server.INSTANCE.getComponent().getDefaultHost().detach(this);
				}
			}
		};
	} 
	
	@SuppressWarnings("unchecked")
	private Series<Header> getMessageHeaders(Message message) {

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
