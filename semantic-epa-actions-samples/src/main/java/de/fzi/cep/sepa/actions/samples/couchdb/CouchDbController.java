package de.fzi.cep.sepa.actions.samples.couchdb;

import java.util.Arrays;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public class CouchDbController  implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("couchdb", "CouchDB", "Stores events in a couchdb database.");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/couchdb_icon.png");
		sec.setEcTypes(Arrays.asList(EcType.STORAGE.name()));
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

}
