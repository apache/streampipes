package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class WikiController extends StandaloneEventSinkDeclarer<WikiParameters> {
  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("wikisink", "Wiki Sink", "Store the optimal route in the wiki")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventSink<WikiParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {
    return new ConfiguredEventSink<>(new WikiParameters(graph), WikiPublisher::new);
  }

}
