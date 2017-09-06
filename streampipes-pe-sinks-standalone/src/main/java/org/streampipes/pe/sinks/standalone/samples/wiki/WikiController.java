package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

/**
 * Created by riemer on 05.04.2017.
 */
public class WikiController extends StandaloneEventSinkDeclarer<WikiParameters> {
  @Override
  public SecDescription declareModel() {
    return DataSinkBuilder.create("wikisink", "Wiki Sink", "Store the optimal route in the wiki")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventSink<WikiParameters, EventSink<WikiParameters>> onInvocation(SecInvocation graph) {
    return new ConfiguredEventSink<>(new WikiParameters(graph), WikiPublisher::new);
  }

}
