package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.util.GsonSerializer;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class Dashboard  implements EventSink<DashboardParameters> {

    private ActiveMQPublisher publisher;
    private JsonDataFormatDefinition jsonDataFormatDefinition;

    private static String DB_NAME = "visualizablepipeline";
    private static int DB_PORT = ActionConfig.INSTANCE.getCouchDbPort();
    private static String DB_HOST = ActionConfig.INSTANCE.getCouchDbHost();
    private static String DB_PROTOCOL = "http";


    private String visualizationId;
    private String visualizationRev;


    public Dashboard() {
        this.jsonDataFormatDefinition = new JsonDataFormatDefinition();

    }

    @Override
    public void bind(DashboardParameters parameters) {
        if (!saveToCouchDB(parameters.getGraph())) {
            throw new SpRuntimeException("The schema couldn't be stored in the couchDB");
        }
        this.publisher = new ActiveMQPublisher(ActionConfig.INSTANCE.getJmsUrl(), parameters.getGraph()
                .getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName());
    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {
        try {
            publisher.publish(jsonDataFormatDefinition.fromMap(event));
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

    private boolean saveToCouchDB(SecInvocation invocationGraph) {
        CouchDbClient dbClient = new CouchDbClient(new CouchDbProperties(DB_NAME, true, DB_PROTOCOL, DB_HOST, DB_PORT, null, null));
        SecInvocation inv = new SecInvocation(invocationGraph);
        dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
        org.lightcouch.Response res = dbClient.save(new DashboardParameters(inv));

        if (res.getError() == null) {
            visualizationId = res.getId();
            visualizationRev = res.getRev();
        }

        return res.getError() == null;
    }

    private boolean removeFromCouchDB() {
        CouchDbClient dbClient = new CouchDbClient(new CouchDbProperties(DB_NAME, true, DB_PROTOCOL, DB_HOST, DB_PORT, null, null));
        org.lightcouch.Response res = dbClient.remove(visualizationId, visualizationRev);

        return res.getError() == null;
    }

    @Override
    public void discard() {
        this.publisher.disconnect();
        if (!removeFromCouchDB()) {
            return new Response(pipelineId, false, "There was an error while deleting pipeline: '" + pipelineId + "'");
        } else {
            stopKafkaConsumer();
            return new Response(pipelineId, true);
        }
    }
}
