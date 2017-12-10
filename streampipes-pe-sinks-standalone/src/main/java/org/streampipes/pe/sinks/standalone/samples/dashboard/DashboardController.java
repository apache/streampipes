package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class DashboardController extends StandaloneEventSinkDeclarer<DashboardParameters> {

    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("dashboard_sink", "Dashboard Sink", "This sink will be used to visualize data" +
                " " +
                "streams in the StreamPipes dashboard")
                .category(DataSinkType.VISUALIZATION_CHART)
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .iconUrl(ActionConfig.getIconUrl("dashboard-icon"))
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public ConfiguredEventSink<DashboardParameters, EventSink<DashboardParameters>> onInvocation(DataSinkInvocation invocationGraph) {
         return new ConfiguredEventSink<>(new DashboardParameters(invocationGraph), Dashboard::new);
    }

}
