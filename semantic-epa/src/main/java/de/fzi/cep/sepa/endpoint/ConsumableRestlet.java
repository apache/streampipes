package de.fzi.cep.sepa.endpoint;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Method;

import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.transform.Transformer;


public abstract class ConsumableRestlet<D extends NamedSEPAElement, I extends InvocableSEPAElement> extends AbstractRestlet<D> {

	protected Class<I> clazz;
	protected Declarer<D, I> declarer;
	
	public ConsumableRestlet(D desc, Class<I> clazz, Declarer<D, I> declarer)
	{
		super(desc);
		this.clazz = clazz;
		this.declarer = declarer;
	}
	
	
	@Override
	public void handle(Request req, Response resp)
	{
		if (req.getMethod().equals(Method.GET)) super.handle(req, resp);
		else if (req.getMethod().equals(Method.POST)) {
			Form form = new Form(req.getEntity());
			onPost(resp, form.getFirstValue("json"));
		}
	}
	
	protected void onPost(Response resp, String payload) {
		try {
			I graph = Transformer.fromJsonLd(clazz, payload);
			createInstanceEndpoint(graph);
			declarer.invokeRuntime(graph);
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			sendStatus(resp);
		}
	}
	
	protected void createInstanceEndpoint(I graph) {
		Server.INSTANCE
			.getComponent()
			.getDefaultHost()
			.attach(declarer.declareModel().getUri() +"/" +graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), instanceRestlet(graph));
	}
	
	protected abstract Restlet instanceRestlet(I graph);
	
}
