package org.streampipes.pe.sources.samples.random;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.helpers.ValueSpecifications;
import org.streampipes.vocabulary.SO;

public class ComplexRandomStream implements EventStreamDeclarer {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("complex-stream","Complex Stream", "Stream used for testing list-based " +
            "properties and " +
            "value " +
            "specifications")
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    "org.streampipes.test.complex"))
            .format(Formats.jsonFormat())
            .property(EpProperties.timestampProperty("timestamp"))
            .property(EpProperties.stringEp(Labels.withTitle("string", "string description"), "testString",
                    "http://test.de", ValueSpecifications.from("A", "B", "C")))
            .property(EpProperties.stringEp(Labels.withTitle("string2", "string description"), "testString2",
                    "http://test.de", ValueSpecifications.from("A", "B", "C", "D")))
            .property(EpProperties.integerEp(Labels.withTitle("integer2", "integerDescription"), "testInteger2",
                    SO.Number, ValueSpecifications.from(0.0f, 1.0f, 1.f)))
            .property(EpProperties.integerEp(Labels.withTitle("integer", "integerDescription"), "testInteger",
                    SO.Number, ValueSpecifications.from(10.0f, 100.0f, 10.0f)))
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
