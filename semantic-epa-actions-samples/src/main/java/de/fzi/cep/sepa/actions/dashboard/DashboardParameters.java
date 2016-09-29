package de.fzi.cep.sepa.actions.dashboard;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class DashboardParameters {
    private String pipelineId;
    private EventSchema schema;
    private String broker;

    public DashboardParameters(SecInvocation invocationGraph) {
        this.schema = invocationGraph.getInputStreams().get(0).getEventSchema();
        this.pipelineId = invocationGraph.getCorrespondingPipeline();
        this.broker = removeProtocol(ClientConfiguration.INSTANCE.getJmsHost()+ ":61614");
    }

    private String removeProtocol(String url) {
       return url.replaceFirst("^(tcp://|ws://)","");
    }
    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public EventSchema getSchema() {
        return schema;
    }

    public void setSchema(EventSchema schema) {
        this.schema = schema;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}
