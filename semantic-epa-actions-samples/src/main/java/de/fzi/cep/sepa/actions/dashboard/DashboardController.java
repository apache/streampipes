package de.fzi.cep.sepa.actions.dashboard;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.*;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.*;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController extends ActionController {
    private static String DB_NAME = "visualization";
    private static int DB_PORT = ClientConfiguration.INSTANCE.getCouchDbPort();
   private static String DB_HOST = ClientConfiguration.INSTANCE.getCouchDbHost();
    private static String DB_PROTOCOL = "http";

    private KafkaConsumerGroup kafkaConsumerGroup;

    private String visualizationId;
    private String visualizationRev;

    @Override
    public SecDescription declareModel() {
        EventStream stream = StreamBuilder.createStream("", "","").schema(SchemaBuilder.create().build()).build();
        SecDescription desc = new SecDescription("dashboard_sink", "Dashboard Sink", "This sink will be used to define that the data can be vizualized");
//        desc.setIconUrl(ActionConfig.iconBaseUrl + "/Table_Icon_HQ.png");
        desc.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
        stream.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
        desc.addEventStream(stream);

        List<StaticProperty> staticProperties = new ArrayList<>();
        desc.setStaticProperties(staticProperties);

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
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

            kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
                    new String[]{consumerTopic}, new Dashboard(invocationGraph.getCorrespondingPipeline()));
            kafkaConsumerGroup.run(1);

        }

        return new Response(invocationGraph.getElementId(), true);
    }

    private boolean saveToCouchDB(SecInvocation invocationGraph) {
        CouchDbClient dbClient = new CouchDbClient(new CouchDbProperties(DB_NAME, true, DB_PROTOCOL, DB_HOST, DB_PORT, null, null));
        SecInvocation inv = new SecInvocation(invocationGraph);
        dbClient.setGsonBuilder(de.fzi.cep.sepa.model.util.GsonSerializer.getGsonBuilder());
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
            kafkaConsumerGroup.shutdown();
            return new Response(pipelineId, true);
        }
    }
}
