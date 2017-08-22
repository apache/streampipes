package org.streampipes.pe.sinks.standalone.samples.couchdb;

import java.util.Arrays;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;

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
