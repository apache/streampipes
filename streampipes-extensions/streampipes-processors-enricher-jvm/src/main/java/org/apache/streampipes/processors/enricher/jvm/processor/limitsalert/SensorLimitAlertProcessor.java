/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.processors.enricher.jvm.processor.limitsalert;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;

public class SensorLimitAlertProcessor implements IStreamPipesDataProcessor {

  protected static final String SENSOR_VALUE_LABEL = "sensorValue";
  protected static final String UPPER_CONTROL_LIMIT_LABEL = "upperControlLimit";
  protected static final String UPPER_WARNING_LIMIT_LABEL = "upperWarningLimit";
  protected static final String LOWER_WARNING_LIMIT_LABEL = "lowerWarningLimit";
  protected static final String LOWER_CONTROL_LIMIT_LABEL = "lowerControlLimit";

  // Property names that are appended to the resulting event
  protected static final String ALERT_STATUS = "alertStatus";
  protected static final String LIMIT_BREACHED = "limitBreached";

  protected static final String ALERT = "ALERT";
  protected static final String WARNING = "WARNING";
  protected static final String UPPER_LIMIT = "UPPER_LIMIT";
  protected static final String LOWER_LIMIT = "LOWER_LIMIT";

  private String sensorField;
  private String upperControlLimitField;
  private String upperWarningLimitField;
  private String lowerWarningLimitField;
  private String lowerControlLimitField;


  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        SensorLimitAlertProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.enricher.jvm.processor.limitsalert", 0)
            .category(DataProcessorType.ENRICH)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(SENSOR_VALUE_LABEL),
                                    PropertyScope.MEASUREMENT_PROPERTY
                                )
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(UPPER_CONTROL_LIMIT_LABEL),
                                    PropertyScope.MEASUREMENT_PROPERTY
                                )
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(UPPER_WARNING_LIMIT_LABEL),
                                    PropertyScope.MEASUREMENT_PROPERTY
                                )
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(LOWER_WARNING_LIMIT_LABEL),
                                    PropertyScope.MEASUREMENT_PROPERTY
                                )
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(LOWER_CONTROL_LIMIT_LABEL),
                                    PropertyScope.MEASUREMENT_PROPERTY
                                )
                                .build())
            .outputStrategy(
                OutputStrategies.append(
                    EpProperties.stringEp(Labels.empty(), ALERT_STATUS, SO.TEXT),
                    EpProperties.stringEp(Labels.empty(), LIMIT_BREACHED, SO.TEXT)
                ))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) {
    var extractor = params.extractor();

    sensorField = extractor.mappingPropertyValue(SENSOR_VALUE_LABEL);
    upperControlLimitField = extractor.mappingPropertyValue(UPPER_CONTROL_LIMIT_LABEL);
    upperWarningLimitField = extractor.mappingPropertyValue(UPPER_WARNING_LIMIT_LABEL);
    lowerWarningLimitField = extractor.mappingPropertyValue(LOWER_WARNING_LIMIT_LABEL);
    lowerControlLimitField = extractor.mappingPropertyValue(LOWER_CONTROL_LIMIT_LABEL);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    var sensorValue = event.getFieldBySelector(sensorField)
                           .getAsPrimitive()
                           .getAsDouble();
    var upperControlLimit = event.getFieldBySelector(upperControlLimitField)
                                 .getAsPrimitive()
                                 .getAsDouble();
    var upperWarningLimit = event.getFieldBySelector(upperWarningLimitField)
                                 .getAsPrimitive()
                                 .getAsDouble();
    var lowerWarningLimit = event.getFieldBySelector(lowerWarningLimitField)
                                 .getAsPrimitive()
                                 .getAsDouble();
    var lowerControlLimit = event.getFieldBySelector(lowerControlLimitField)
                                 .getAsPrimitive()
                                 .getAsDouble();

    String alertStatus = null;
    String limitBreached = null;

    if (sensorValue > upperControlLimit) {
      alertStatus = ALERT;
      limitBreached = UPPER_LIMIT;
    } else if (sensorValue > upperWarningLimit) {
      alertStatus = WARNING;
      limitBreached = UPPER_LIMIT;
    } else if (sensorValue < lowerControlLimit) {
      alertStatus = ALERT;
      limitBreached = LOWER_LIMIT;
    } else if (sensorValue < lowerWarningLimit) {
      alertStatus = WARNING;
      limitBreached = LOWER_LIMIT;
    }

    if (alertStatus != null && limitBreached != null) {
      event.addField(ALERT_STATUS, alertStatus);
      event.addField(LIMIT_BREACHED, limitBreached);
      collector.collect(event);
    }

  }

  @Override
  public void onPipelineStopped() {

  }

}
