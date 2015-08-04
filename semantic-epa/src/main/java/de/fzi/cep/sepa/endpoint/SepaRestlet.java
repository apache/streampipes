package de.fzi.cep.sepa.endpoint;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class SepaRestlet extends ConsumableRestlet<SepaDescription, SepaInvocation> {
	
	public SepaRestlet(SepaDescription desc, SemanticEventProcessingAgentDeclarer declarer)
	{
		super(desc, SepaInvocation.class, declarer);
	}
			
	protected Restlet instanceRestlet(SepaInvocation graph)
	{
		return new Restlet() {	
			public void handle(Request request, Response response)
			{
				if (request.getMethod().equals(Method.DELETE))
				{
					Server.INSTANCE.getComponent().getDefaultHost().detach(this);
					sendStatus(response, declarer.detachRuntime());
				}
			}	
		};	
	}

}
