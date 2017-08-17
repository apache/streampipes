package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController extends StandaloneEventSinkDeclarer<DashboardParameters> {

    @Override
    public SecDescription declareModel() {
        DataSinkBuilder.create("dashboard_sink", "Dashboard Sink", "This sink will be used to visualize data " +
                "streams in the StreamPipes dashboard")
                .requires
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
    public ConfiguredEventSink<DashboardParameters, EventSink<DashboardParameters>> onInvocation(SecInvocation invocationGraph) {
         return new ConfiguredEventSink<>(new DashboardParameters(invocationGraph), Dashboard::new);
    }

}
