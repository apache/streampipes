package org.apache.streampipes.processors.filters.jvm.processor.throughputmon;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class ThroughputMonitorProcessor extends StreamPipesDataProcessor {

  private final static String BATCH_WINDOW_KEY = "batch-window-key";

  private final static String TIMESTAMP_FIELD = "timestamp";
  private final static String START_TIME_FIELD = "starttime";
  private final static String END_TIME_FIELD = "endtime";
  private final static String DURATION_FIELD = "duration";
  private final static String EVENT_COUNT_FIELD = "eventcount";
  private final static String THROUGHPUT_FIELD = "throughput";

  private int batchSize;

  private StopWatch stopWatch;
  private long eventCount = 0;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.throughputmon")
            .category(DataProcessorType.FILTER)
            .withAssets(Assets.DOCUMENTATION)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty()).build())
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty(TIMESTAMP_FIELD),
                    EpProperties.longEp(Labels.withId(START_TIME_FIELD), START_TIME_FIELD, SO.DateTime),
                    EpProperties.longEp(Labels.withId(END_TIME_FIELD), END_TIME_FIELD, SO.DateTime),
                    EpProperties.longEp(Labels.withId(DURATION_FIELD), DURATION_FIELD, SO.Number),
                    EpProperties.longEp(Labels.withId(EVENT_COUNT_FIELD), EVENT_COUNT_FIELD, SO.Number),
                    EpProperties.doubleEp(Labels.withId(THROUGHPUT_FIELD), THROUGHPUT_FIELD, SO.Number)))
            .requiredIntegerParameter(Labels.withId(BATCH_WINDOW_KEY))
            .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.batchSize = parameters.extractor().singleValueParameter(BATCH_WINDOW_KEY, Integer.class);
    this.stopWatch = new StopWatch();
    this.restartTimer();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    this.eventCount++;
    if (this.eventCount == this.batchSize) {
      this.stopWatch.stop();
      Event outEvent = buildEvent();
      collector.collect(outEvent);
      this.eventCount = 0;
      this.restartTimer();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.stopWatch.stop();
  }

  private Event buildEvent() {
    Event event = new Event();
    event.addField(TIMESTAMP_FIELD, System.currentTimeMillis());
    event.addField(START_TIME_FIELD, this.stopWatch.getStartTime());
    event.addField(END_TIME_FIELD, this.stopWatch.getStopTime());
    event.addField(DURATION_FIELD, this.stopWatch.getTime());
    event.addField(EVENT_COUNT_FIELD, this.eventCount);
    event.addField(THROUGHPUT_FIELD, (this.eventCount / (((double) this.stopWatch.getTime()) / 1000)));

    return event;
  }

  private void restartTimer() {
    this.stopWatch.reset();
    this.eventCount = 0;
    this.stopWatch.start();
  }
}
