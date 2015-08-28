package de.fzi.cep.sepa.endpoint;

import java.lang.reflect.InvocationTargetException;

import org.openrdf.model.Graph;
import org.openrdf.rio.RDFHandlerException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.StringRepresentation;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.transform.Transformer;

public abstract class AbstractRestlet<D extends NamedSEPAElement> extends Restlet {

	protected D desc;
	
	public AbstractRestlet(D desc)
	{
		super();
		this.desc = desc;
	}
	
	protected String asString(Graph graph) throws RDFHandlerException {
		return Utils.asString(graph);
	}
	
	protected void sendStatus(Response httpResp, de.fzi.cep.sepa.model.impl.Response streamPipesResp)
	{
		httpResp.setEntity(new StringRepresentation(new Gson().toJson(streamPipesResp), MediaType.APPLICATION_JSON));
		httpResp.setStatus(new Status(200));
	}
	
	@Override
	public void handle(Request req, Response resp)
	{
		Graph rdfGraph;	
		try {
			System.out.println(req.getEntityAsText());
			System.out.println(req.getReferrerRef().toString());
			rdfGraph = Transformer.toJsonLd(desc);
			System.out.println(asString(rdfGraph));
			resp.setEntity(asString(rdfGraph), MediaType.APPLICATION_JSON);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException
				| ClassNotFoundException | InvalidRdfException | RDFHandlerException e) {
			e.printStackTrace();
			resp.setStatus(new Status(200));
		}	
	}	
}
