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

package org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.List;

public class StaticMetaDataEnrichmentProcessor
    implements IStreamPipesDataProcessor,
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String ID = "org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata";

  public static final String STATIC_METADATA_INPUT = "static-metadata-input";
  protected static final String STATIC_METADATA_INPUT_RUNTIME_NAME = "static-metadata-input-runtime-name";
  protected static final String STATIC_METADATA_INPUT_VALUE = "static-metadata-input-value";
  protected static final String STATIC_METADATA_INPUT_DATATYPE = "static-metadata-input-datatype";
  public static final String STATIC_METADATA_INPUT_LABEL = "static-metadata-input-label";
  public static final String STATIC_METADATA_INPUT_DESCRIPTION = "static-metadata-input-description";

  protected static final String OPTION_BOOL = "Bool";
  protected static final String OPTION_STRING = "String";
  protected static final String OPTION_FLOAT = "Float";
  protected static final String OPTION_INTEGER = "Integer";


  private List<StaticMetaDataConfiguration> staticMetaDataConfigurations = new ArrayList<>();

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        StaticMetaDataEnrichmentProcessor::new,
        ProcessingElementBuilder.create(
                ID,
                2
            )
            .category(
                DataProcessorType.ENRICH)
            .withLocales(
                Locales.EN)
            .withAssets(
                ExtensionAssetType.DOCUMENTATION,
                ExtensionAssetType.ICON
            )
            .requiredStaticProperty(
                StaticProperties.collection(
                    Labels.withId(
                        STATIC_METADATA_INPUT),
                    false,
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(
                            STATIC_METADATA_INPUT_RUNTIME_NAME)),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(
                            STATIC_METADATA_INPUT_VALUE)),
                    StaticProperties.singleValueSelection(
                        Labels.withId(
                            STATIC_METADATA_INPUT_DATATYPE),
                        Options.from(
                            OPTION_BOOL,
                            OPTION_STRING,
                            OPTION_FLOAT,
                            OPTION_INTEGER
                        )
                    ),
                    makeFreeTextInput(STATIC_METADATA_INPUT_LABEL, true),
                    makeFreeTextInput(STATIC_METADATA_INPUT_DESCRIPTION, true)
                )
            )
            .requiredStream(
                StreamRequirementsBuilder.any())
            .outputStrategy(
                OutputStrategies.customTransformation())
            .build()
    );
  }

  private FreeTextStaticProperty makeFreeTextInput(String label,
                                                   boolean optional) {
    var sp = StaticProperties.stringFreeTextProperty(
        Labels.withId(
            label));
    sp.setOptional(optional);
    return sp;
  }

  @Override
  public EventSchema resolveOutputStrategy(
      DataProcessorInvocation processingElement,
      ProcessingElementParameterExtractor parameterExtractor
  ) {

    var metaDataConfigurations = getMetaDataConfigurations(parameterExtractor);

    var eventSchema = processingElement.getInputStreams()
        .get(0)
        .getEventSchema();

    addMetaDataConfigurationPropertiesToEventSchema(metaDataConfigurations, eventSchema);

    return eventSchema;
  }


  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) {
    this.staticMetaDataConfigurations = getMetaDataConfigurations(params.extractor());
  }


  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    appendMetadataToEvent(event);

    collector.collect(event);
  }

  private void appendMetadataToEvent(Event event) {
    for (StaticMetaDataConfiguration staticMetaDataConfiguration : this.staticMetaDataConfigurations) {
      var value = castValueOfMetaDataConfiguration(staticMetaDataConfiguration);
      event.addField(staticMetaDataConfiguration.runtimeName(), value);
    }
  }

  @Override
  public void onPipelineStopped() {

  }

  private List<StaticMetaDataConfiguration> getMetaDataConfigurations(IDataProcessorParameterExtractor extractor) {
    List<StaticMetaDataConfiguration> configurations = new ArrayList<>();
    var csp = (CollectionStaticProperty) extractor.getStaticPropertyByName(STATIC_METADATA_INPUT);

    for (StaticProperty member : csp.getMembers()) {
      var memberExtractor = getMemberExtractor(member);

      var metadataConfiguration = getMetaDataConfiguration(memberExtractor);

      configurations.add(metadataConfiguration);
    }
    return configurations;
  }

  private StaticPropertyExtractor getMemberExtractor(StaticProperty member) {
    return StaticPropertyExtractor.from(
        ((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>()
    );
  }

  private StaticMetaDataConfiguration getMetaDataConfiguration(StaticPropertyExtractor memberExtractor) {
    var runtimeName = memberExtractor.textParameter(STATIC_METADATA_INPUT_RUNTIME_NAME);
    var value = memberExtractor.textParameter(STATIC_METADATA_INPUT_VALUE);
    var dataType = memberExtractor.selectedSingleValue(STATIC_METADATA_INPUT_DATATYPE, String.class);
    var label = memberExtractor.textParameter(STATIC_METADATA_INPUT_LABEL);
    var description = memberExtractor.textParameter(STATIC_METADATA_INPUT_DESCRIPTION);
    return new StaticMetaDataConfiguration(runtimeName, value, dataType, label, description);
  }

  protected Object castValueOfMetaDataConfiguration(StaticMetaDataConfiguration staticMetaDataConfiguration) {
    return switch (staticMetaDataConfiguration.dataType()) {
      case OPTION_BOOL -> Boolean.parseBoolean(staticMetaDataConfiguration.value());
      case OPTION_FLOAT -> Float.parseFloat(staticMetaDataConfiguration.value());
      case OPTION_INTEGER -> Integer.parseInt(staticMetaDataConfiguration.value());
      default -> staticMetaDataConfiguration.value();
    };
  }

  protected Datatypes transformToStreamPipesDataType(String option) {
    return switch (option) {
      case StaticMetaDataEnrichmentProcessor.OPTION_BOOL -> Datatypes.Boolean;
      case StaticMetaDataEnrichmentProcessor.OPTION_STRING -> Datatypes.String;
      case StaticMetaDataEnrichmentProcessor.OPTION_FLOAT -> Datatypes.Float;
      case StaticMetaDataEnrichmentProcessor.OPTION_INTEGER -> Datatypes.Integer;
      default -> throw new IllegalArgumentException("Invalid option: " + option);
    };
  }

  private void addMetaDataConfigurationPropertiesToEventSchema(
      List<StaticMetaDataConfiguration> metaDataConfigurations,
      EventSchema eventSchema
  ) {
    for (StaticMetaDataConfiguration metaDataConfiguration : metaDataConfigurations) {
      var metaDataEventProperty = getMetaDataEventProperty(metaDataConfiguration);
      eventSchema.getEventProperties()
          .add(metaDataEventProperty);
    }
  }

  private EventPropertyPrimitive getMetaDataEventProperty(
      StaticMetaDataConfiguration metaDataConfiguration
  ) {
    return PrimitivePropertyBuilder
        .create(
            transformToStreamPipesDataType(metaDataConfiguration.dataType()),
            metaDataConfiguration.runtimeName()
        )
        .label(metaDataConfiguration.label())
        .description(metaDataConfiguration.description())
        .scope(PropertyScope.MEASUREMENT_PROPERTY)
        .build();
  }


}

