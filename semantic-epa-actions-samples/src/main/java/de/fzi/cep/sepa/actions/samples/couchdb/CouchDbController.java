package de.fzi.cep.sepa.actions.samples.couchdb;

import java.util.Arrays;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public class CouchDbController  implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("couchdb", "CouchDB", "Stores events in a couchdb database.");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/couchdb_icon.png");
		sec.setCategory(Arrays.asList(EcType.STORAGE.name()));
		return sec;
	}

    @Override
    public Response invokeRuntime(SecInvocation invocationGraph) {
        String pipelineId = invocationGraph.getCorrespondingPipeline();
        return new Response(pipelineId, true);
    }

    @Override
    public Response detachRuntime(String pipelineId) {
        return new Response(pipelineId, true);
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
