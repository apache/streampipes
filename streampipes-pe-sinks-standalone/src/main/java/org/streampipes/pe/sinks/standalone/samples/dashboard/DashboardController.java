package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.Utils;

import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.GsonSerializer;
import org.streampipes.model.vocabulary.MessageFormat;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController extends ActionController {

    private static String DB_NAME = "visualizablepipeline";
    private static int DB_PORT = ActionConfig.INSTANCE.getCouchDbPort();
    private static String DB_HOST = ActionConfig.INSTANCE.getCouchDbHost();
    private static String DB_PROTOCOL = "http";


    private String visualizationId;
    private String visualizationRev;

    @Override
    public SecDescription declareModel() {
        EventStream stream = StreamBuilder.createStream("", "", "").schema(SchemaBuilder.create().build()).build();
        SecDescription desc = new SecDescription("dashboard_sink", "Dashboard Sink", "This sink will be used to define that the data can be vizualized");
//        desc.setIconUrl(ActionConfig.iconBaseUrl + "/Table_Icon_HQ.png");
        desc.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
        stream.setUri(ActionConfig.serverUrl + "/" + Utils.getRandomString());
        desc.addEventStream(stream);
        desc.setIconUrl(ActionConfig.getIconUrl("dashboard-icon"));

        List<StaticProperty> staticProperties = new ArrayList<>();
        desc.setStaticProperties(staticProperties);

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(new KafkaTransportProtocol(ActionConfig.INSTANCE.getKafkaHost(),
                ActionConfig.INSTANCE.getKafkaPort(), "", ActionConfig.INSTANCE.getZookeeperHost(),
                ActionConfig.INSTANCE.getZookeeperPort()));
        grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
        desc.setSupportedGrounding(grounding);
        return desc;
    }

    @Override
    public boolean isVisualizable() {
        return false;
    }

    @Override
    public String getHtml(SecInvocation graph) {
        return null;
    }

    @Override
    public Response invokeRuntime(SecInvocation invocationGraph) {
        if (!saveToCouchDB(invocationGraph)) {
            return new Response(invocationGraph.getElementId(), false, "The schema couldn't be stored in the couchDB");
        } else {
            String consumerTopic = invocationGraph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();

            startKafkaConsumer(ActionConfig.INSTANCE.getKafkaUrl(), consumerTopic, new Dashboard(invocationGraph.getCorrespondingPipeline()));
        }

        return new Response(invocationGraph.getElementId(), true);
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
    public Response detachRuntime(String pipelineId) {
        if (!removeFromCouchDB()) {
            return new Response(pipelineId, false, "There was an error while deleting pipeline: '" + pipelineId + "'");
        } else {
            stopKafkaConsumer();
            return new Response(pipelineId, true);
        }
    }
}
