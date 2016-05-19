package de.fzi.cep.sepa.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Method;

import de.fzi.cep.sepa.desc.RestletGenerator;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.transform.Transformer;


public abstract class ConsumableRestlet<D extends NamedSEPAElement, I extends InvocableSEPAElement> extends AbstractRestlet<D> {

	protected Class<I> clazz;
	protected Declarer<D, I> declarer;
	
	protected Map<String, Declarer<D, I>> instanceDeclarers;
	
	public ConsumableRestlet(D desc, Class<I> clazz, Declarer<D, I> declarer)
	{
		super(desc);
		this.clazz = clazz;
		this.declarer = declarer;
		this.instanceDeclarers = new HashMap<>();
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
			instanceDeclarers.put(graph.getElementId(), declarer.getClass().newInstance());
			createInstanceEndpoint(graph);
			de.fzi.cep.sepa.model.impl.Response streamPipesResp = instanceDeclarers.get(graph.getElementId()).invokeRuntime(graph);
			if (streamPipesResp == null) streamPipesResp = new de.fzi.cep.sepa.model.impl.Response(graph.getElementId(), true);
			sendStatus(resp, streamPipesResp);
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			sendStatus(resp, new de.fzi.cep.sepa.model.impl.Response("", false, e.getMessage()));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	protected void createInstanceEndpoint(I graph) {
		String instanceUri = graph.getUri().replaceFirst(RestletGenerator.REGEX, "");
		Server.INSTANCE
			.getComponent().getDefaultHost()
			.attach(instanceUri, instanceRestlet(graph));
	}
	
	protected abstract Restlet instanceRestlet(I graph);
	
}
