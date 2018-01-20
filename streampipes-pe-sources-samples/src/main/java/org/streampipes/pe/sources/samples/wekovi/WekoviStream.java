package org.streampipes.pe.sources.samples.wekovi;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;

public class WekoviStream implements DataStreamDeclarer {

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        return DataStreamBuilder.create("wekovi_stream", "Wekovi Stream", "Generic stream for the StreamConnect adapter")
                .property(EpProperties.timestampProperty("timestamp"))
                .format(Formats.jsonFormat())
                .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                        SourcesConfig.INSTANCE.getStreamConnectTopic()))
                .build();
    }

    @Override
    public void executeStream() {

    }

    @Override
    public boolean isExecutable() {
        return false;
    }
}
