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

package org.apache.streampipes.processors.enricher.jvm.processor.expression;

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
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.apache.commons.jexl3.JexlException;

import java.util.List;

public class MathExpressionProcessor implements
    IStreamPipesDataProcessor,
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String ID = "org.apache.streampipes.processors.enricher.jvm.processor.expression";
  static final String ENRICHED_FIELDS = "enriched-fields";
  static final String FIELD_NAME = "field-name";
  static final String EXPRESSION = "expression";

  private List<JexlEvaluator> jexlEvaluators;
  private JexlContextGenerator jexlContextGenerator;
  private EventProcessorRuntimeContext context;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        MathExpressionProcessor::new,
        ProcessingElementBuilder.create(ID, 0)
            .category(DataProcessorType.ENRICH)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder.create()
                .requiredProperty(EpRequirements.numberReq())
                .build())
            .requiredStaticProperty(makeCollection())
            .outputStrategy(OutputStrategies.customTransformation())
            .build()
    );
  }

  private CollectionStaticProperty makeCollection() {
    return StaticProperties.collection(
        Labels.withId(ENRICHED_FIELDS),
        false,
        StaticProperties.freeTextProperty(Labels.withId(FIELD_NAME), Datatypes.String),
        StaticProperties.codeStaticProperty(Labels.withId(EXPRESSION), CodeLanguage.None, getJexlComment()));
  }

  private String getJexlComment() {
    return "## Provide JEXL syntax here";
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params,
                                SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    var extractor = new MathExpressionFieldExtractor(params.getModel());
    var engine = new JexlEngineProvider().getEngine();
    var scripts = extractor.getAdditionalFields();
    jexlEvaluators = scripts.stream().map(script -> new JexlEvaluator(script, engine)).toList();
    jexlContextGenerator = new JexlContextGenerator(extractor);
    context = runtimeContext;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    var ctx = jexlContextGenerator.makeContext(event);
    jexlEvaluators.forEach(evaluator -> {
      try {
        evaluator.evaluate(ctx, event);
      } catch (JexlException e) {
        context.getLogger().error(e);
      }
    });
    collector.collect(event);
  }

  @Override
  public void onPipelineStopped() {

  }


  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor parameterExtractor) {
    var fieldExtractor = new MathExpressionFieldExtractor(processingElement, parameterExtractor);
    var existingFields = fieldExtractor.getInputProperties();
    var additionalFields = fieldExtractor.getAdditionalFields();
    existingFields.addAll(additionalFields.stream().map(JexlDescription::ep).toList());
    return new EventSchema(processingElement.getInputStreams().get(0).getEventSchema());
  }
}
