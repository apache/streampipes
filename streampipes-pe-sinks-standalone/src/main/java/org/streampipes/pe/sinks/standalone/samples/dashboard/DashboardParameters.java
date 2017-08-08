package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.graph.SecInvocation;

public class DashboardParameters {
    private String pipelineId;
    private EventSchema schema;
    private String broker;

    public DashboardParameters(SecInvocation invocationGraph) {
        this.schema = invocationGraph.getInputStreams().get(0).getEventSchema();
        this.pipelineId = invocationGraph.getCorrespondingPipeline();
        //this.broker = removeProtocol(ClientConfiguration.INSTANCE.getJmsHost()+ ":61614");
        ClientConfiguration config = ClientConfiguration.INSTANCE;
        this.broker = "ws://" +config.getWebappHost() +":" +config.getWebappPort() +"/streampipes/ws";
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
