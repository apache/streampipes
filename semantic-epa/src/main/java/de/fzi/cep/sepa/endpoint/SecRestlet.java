package de.fzi.cep.sepa.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;

import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Header;
import org.restlet.util.Series;




import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

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
					response.setAccessControlAllowCredentials(true);
					response.setAccessControlAllowHeaders(new HashSet<String>(Arrays.asList("Content-Type")));
					response.setAccessControlAllowMethods(new HashSet<>(Arrays.asList(Method.GET, Method.POST, Method.OPTIONS)));
					response.setAccessControlAllowOrigin("*");
					response.setEntity(((SemanticEventConsumerDeclarer) declarer).getHtml(graph) , MediaType.TEXT_HTML);
					
				}
				if (request.getMethod().equals(Method.OPTIONS))
				{
					response.setAccessControlAllowCredentials(true);
					response.setAccessControlAllowHeaders(new HashSet<String>(Arrays.asList("Content-Type")));
					response.setAccessControlAllowMethods(new HashSet<>(Arrays.asList(Method.GET, Method.POST, Method.OPTIONS)));
					response.setAccessControlAllowOrigin("*");
				}
				
				if (request.getMethod().equals(Method.DELETE))
				{
					Server.INSTANCE.getComponent().getDefaultHost().detach(this);
					sendStatus(response, new de.fzi.cep.sepa.model.impl.Response(desc.getElementId(), true));
				}
			}
		};
	} 
}
