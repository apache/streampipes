package org.apache.streampipes.processors.filters.jvm.processor.duration;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConditionalTimeFilterProcessor implements IStreamPipesDataProcessor {
  // --- STATIC CONFIGURATION ---
  public static final String PROCESSOR_ID = "";
  public static final int PROCESSOR_VERSION = 0;

  // Input Parameter Keys
  private static final String FILTER_FIELD_ID = "filterField";
  private static final String TIMESTAMP_FIELD_ID = "timestampField";
  private static final String DURATION_ID = "duration";
  private static final String DURATION_UNIT_ID = "durationUnit";
  private static final String CONDITION_VALUE_ID = "conditionValue";
  private static final String OUTPUT_MODE_ID = "outputMode";
  private static final String TRIGGER_MODE_ID = "triggerMode";

  // Output Field Keys
  private static final String ORIGINAL_TIMESTAMP_FIELD = "originalTimestamp";
  private static final String PROCESSING_TIMESTAMP_FIELD = "processingTimestamp";
  private static final String TIME_DIFFERENCE_FIELD = "timeDifference";
  private enum State {
    WAITING_FOR_CONDITION,
    TIMER_RUNNING,
    FIRING
  }

  private static final String TRIGGER_MODE_ON_EVENT = "On Event Arrival";
  private static final String TRIGGER_MODE_ON_TIMER = "On Timer";

  // --- RUNTIME CONFIGURATION ---
  private String filterFieldSelector;
  private String timestampFieldSelector;
  private Integer duration;
  private String timeUnit;
  private String outputMode;
  private String triggerMode;
  private boolean conditionToMeet;
  // --- STATE VARIABLES ---
  private State currentState;
  private long originalEventTimestamp;
  private Event lastEvent;

  // --- SCHEDULER (for "On Timer" mode) ---
  public ScheduledExecutorService executorService;
  private ScheduledFuture<?> scheduledFuture;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        ConditionalTimeFilterProcessor::new,
        ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.filters.jvm.duration.conditionaltimefilter",
                PROCESSOR_VERSION)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.VALUE_OBSERVER)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_FIELD_ID),
                    PropertyScope.NONE
                )
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.booleanReq(),
                    Labels.withId(FILTER_FIELD_ID),
                    PropertyScope.NONE
                )
                .build()
            )
            .requiredSingleValueSelection(
                Labels.withId(DURATION_UNIT_ID),
                Options.from(
                    "Seconds",
                    "Minutes",
                    "Hours"
                )
            )
            .requiredIntegerParameter(
                Labels.withId(DURATION_ID)
            )
            .requiredSlideToggle(Labels.withId(CONDITION_VALUE_ID), true)
            .requiredSingleValueSelection(Labels.withId(TRIGGER_MODE_ID),
                Options.from(
                    TRIGGER_MODE_ON_EVENT,
                    TRIGGER_MODE_ON_TIMER
                )
            )
            .requiredSingleValueSelection(
                Labels.withId(OUTPUT_MODE_ID),
                Options.from(
                    "Fire Once",
                    "Fire Continuously"
                )
            )
            .outputStrategy(OutputStrategies.append(
                EpProperties.timestampProperty(ORIGINAL_TIMESTAMP_FIELD),
                EpProperties.timestampProperty(PROCESSING_TIMESTAMP_FIELD),
                EpProperties.longEp(Labels.withId(TIME_DIFFERENCE_FIELD), TIME_DIFFERENCE_FIELD, SO.NUMBER)
            ))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params,
                                SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    IDataProcessorParameterExtractor extractor = params.extractor();
    this.filterFieldSelector = extractor.mappingPropertyValue(FILTER_FIELD_ID);
    this.timestampFieldSelector = extractor.mappingPropertyValue(TIMESTAMP_FIELD_ID);
    this.duration = extractor.singleValueParameter(DURATION_ID, Integer.class);
    this.timeUnit = extractor.selectedSingleValue(DURATION_UNIT_ID, String.class);
    this.outputMode = extractor.selectedSingleValue(OUTPUT_MODE_ID, String.class);
    this.triggerMode = extractor.selectedSingleValue(TRIGGER_MODE_ID, String.class);
    this.conditionToMeet = extractor.slideToggleValue(CONDITION_VALUE_ID);

    if (this.triggerMode.equals(TRIGGER_MODE_ON_TIMER)) {
      this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    this.resetState();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {
    try {
      boolean currentCondition = event.getFieldBySelector(this.filterFieldSelector).getAsPrimitive().getAsBoolean();

      if (currentCondition == this.conditionToMeet) {
        if (triggerMode.equals(TRIGGER_MODE_ON_TIMER)) {
          handleOnTimer(event, out);
        } else {
          handleOnEventArrival(event, out);
        }
      } else {
        resetState();
      }
    } catch (SpRuntimeException e) {
      // Handle exceptions, e.g., by logging
    }
  }

  private void handleOnEventArrival(Event event, SpOutputCollector out) {
    long currentEventTimestamp = event.getFieldBySelector(this.timestampFieldSelector).getAsPrimitive().getAsLong();

    switch (currentState) {
      case WAITING_FOR_CONDITION:
        this.currentState = State.TIMER_RUNNING;
        this.originalEventTimestamp = currentEventTimestamp;
        break;

      case TIMER_RUNNING:
        if (currentEventTimestamp - this.originalEventTimestamp >= getDurationInMillis()) {
          this.currentState = State.FIRING;
          out.collect(enrichEvent(event, currentEventTimestamp));
        }
        break;

      case FIRING:
        if (this.outputMode.equals("Fire Continuously")) {
          out.collect(enrichEvent(event, currentEventTimestamp));
        }
        break;
    }
  }

  private void handleOnTimer(Event event, SpOutputCollector out) {
    this.lastEvent = event;
    if (currentState == State.WAITING_FOR_CONDITION) {
      this.originalEventTimestamp = this.lastEvent.getFieldBySelector(this.timestampFieldSelector)
          .getAsPrimitive().getAsLong();
      startTimer(out);
    } else if (currentState == State.FIRING && outputMode.equals("Fire Continuously")) {
      long currentTimestamp = event.getFieldBySelector(this.timestampFieldSelector).getAsPrimitive().getAsLong();
      out.collect(enrichEvent(event, currentTimestamp));
    }
  }

  private void startTimer(SpOutputCollector out) {
    this.currentState = State.TIMER_RUNNING;
    Runnable task = () -> {
      if (this.lastEvent != null) {
        long processingTimestamp = System.currentTimeMillis();
        out.collect(enrichEvent(this.lastEvent, processingTimestamp));
        this.currentState = State.FIRING;
      }
    };
    this.scheduledFuture = this.executorService.schedule(task, getDurationInMillis(), TimeUnit.MILLISECONDS);
  }

  private Event enrichEvent(Event event, long processingTimestamp) {
    Event enrichedEvent = new Event(
        new HashMap<>(event.getFields()),
        event.getSourceInfo(),
        event.getSchemaInfo()
    );
    long timeDifference = processingTimestamp - this.originalEventTimestamp;

    // This ensures the addField(AbstractField) method is called,
    // which correctly adds the stream prefix (e.g., "s0::") to the key.
    enrichedEvent.addField(
        new PrimitiveField(ORIGINAL_TIMESTAMP_FIELD, ORIGINAL_TIMESTAMP_FIELD, this.originalEventTimestamp)
    );
    enrichedEvent.addField(
        new PrimitiveField(PROCESSING_TIMESTAMP_FIELD, PROCESSING_TIMESTAMP_FIELD, processingTimestamp)
    );
    enrichedEvent.addField(
        new PrimitiveField(TIME_DIFFERENCE_FIELD, TIME_DIFFERENCE_FIELD, timeDifference)
    );

    return enrichedEvent;
  }

  private void resetState() {
    if (this.scheduledFuture != null && !this.scheduledFuture.isDone()) {
      this.scheduledFuture.cancel(true);
    }
    this.currentState = State.WAITING_FOR_CONDITION;
    this.lastEvent = null;
    this.originalEventTimestamp = 0;
  }

  private long getDurationInMillis() {
    long duration = this.duration;
    switch (this.timeUnit) {
      case "Minutes":
        return duration * 60000;
      case "Hours":
        return duration * 3600000;
      default: // "Seconds"
        return duration * 1000;
    }
  }

  @Override
  public void onPipelineStopped() {
    if (this.executorService != null && !this.executorService.isShutdown()) {
      this.executorService.shutdownNow();
      try {
        if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS)) {
          System.err.println("Scheduler did not terminate in time.");
        }
      } catch (InterruptedException e) {
        this.executorService.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    this.resetState();
  }
}