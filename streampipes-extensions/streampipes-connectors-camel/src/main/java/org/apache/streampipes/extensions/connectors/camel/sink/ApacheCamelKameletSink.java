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

package org.apache.streampipes.extensions.connectors.camel.sink;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.connectors.camel.kamelet.config.KameletConfigurationExtractor;
import org.apache.streampipes.extensions.connectors.camel.kamelet.config.KameletMessageMappingExtractor;
import org.apache.streampipes.extensions.connectors.camel.kamelet.config.KameletSinkStaticPropertyProvider;
import org.apache.streampipes.extensions.connectors.camel.kamelet.config.KameletStaticPropertyGenerator;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.CamelMessage;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletEventMessageMapper;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletMessageMapping;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;
import org.apache.streampipes.extensions.connectors.camel.kamelet.transform.KameletTransformRouteLoader;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class ApacheCamelKameletSink implements IStreamPipesDataSink, SupportsRuntimeConfig {

  public static final String KAMELET_PARAMETERS_GROUP_KEY = KameletSinkStaticPropertyProvider.KAMELET_PARAMETERS_GROUP_KEY;

  private static final Logger LOG = LoggerFactory.getLogger(ApacheCamelKameletSink.class);
  private static final String ENTRY_ENDPOINT_URI = "direct:streampipes-kamelet-input";
  private static final String ROUTE_ID = "streampipes-kamelet-sink-route";

  private final String appId;
  private final String templateName;
  private final AssetResolver assetResolver;
  private final JsonDataFormatDefinition jsonDataFormatDefinition;
  private final KameletTemplateProvider templateProvider;
  private final KameletSinkStaticPropertyProvider staticPropertyProvider;
  private final KameletStaticPropertyGenerator staticPropertyGenerator;
  private final KameletConfigurationExtractor configurationExtractor;
  private final KameletMessageMappingExtractor messageMappingExtractor;
  private final KameletEventMessageMapper eventMessageMapper;
  private final KameletTransformRouteLoader transformRouteLoader;

  private CamelContext camelContext;
  private ProducerTemplate producerTemplate;
  private String kameletEndpointUri;
  private KameletMessageMapping messageMapping;

  protected ApacheCamelKameletSink(String appId,
                                   String templateName,
                                   KameletTemplateProvider templateProvider,
                                   AssetResolver assetResolver) {
    this.appId = appId;
    this.templateName = templateName;
    this.templateProvider = templateProvider;
    this.assetResolver = assetResolver;
    this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
    this.staticPropertyProvider = new KameletSinkStaticPropertyProvider();
    this.staticPropertyGenerator = new KameletStaticPropertyGenerator();
    this.configurationExtractor = new KameletConfigurationExtractor();
    this.messageMappingExtractor = new KameletMessageMappingExtractor();
    this.eventMessageMapper = new KameletEventMessageMapper();
    this.transformRouteLoader = new KameletTransformRouteLoader();
  }

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        this::newInstance,
        DataSinkBuilder.create(appId, 0)
            .category(DataSinkType.FORWARD)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
            .requiredStaticProperty(staticPropertyProvider.createKameletParameterGroup())
            .requiredStaticProperty(staticPropertyProvider.createMessageMappingGroup())
            .requiredStaticProperty(staticPropertyProvider.createAdvancedTransformAlternatives())
            .build(),
        assetResolver
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    KameletTemplate template = resolveTemplate();
    RuntimeResolvableGroupStaticProperty parameterGroup = parameters.extractor().getStaticPropertyByName(
        KAMELET_PARAMETERS_GROUP_KEY,
        RuntimeResolvableGroupStaticProperty.class
    );

    this.messageMapping = messageMappingExtractor.extract(parameters.extractor());
    var params = configurationExtractor.buildParams(template, parameterGroup);
    this.kameletEndpointUri = template.endpointBaseUri();

    try {
      this.camelContext = createCamelContext(template, params);
      this.camelContext.start();

      this.producerTemplate = this.camelContext.createProducerTemplate();
      this.producerTemplate.start();

      LOG.info("Started Apache Camel Kamelet sink {} -> {}", template.name(), this.kameletEndpointUri);
    } catch (Exception e) {
      stopQuietly();
      throw new SpRuntimeException("Could not start Apache Camel Kamelet sink", e);
    }
  }

  @Override
  public void onEvent(Event event) {
    if (producerTemplate == null) {
      LOG.warn("Apache Camel Kamelet sink is not initialized yet. Dropping event.");
      return;
    }

    try {
      CamelMessage camelMessage = eventMessageMapper.mapEvent(event, messageMapping, jsonDataFormatDefinition);
      Exchange exchange = producerTemplate.send(ENTRY_ENDPOINT_URI, exchangeBuilder -> {
        exchangeBuilder.getIn().setBody(camelMessage.body());
        for (Map.Entry<String, Object> header : camelMessage.headers().entrySet()) {
          exchangeBuilder.getIn().setHeader(header.getKey(), header.getValue());
        }
      });

      if (exchange.getException() != null) {
        throw exchange.getException();
      }
    } catch (Exception e) {
      LOG.error("Could not route event through Camel Kamelet endpoint {}", kameletEndpointUri, e);
      throw new SpRuntimeException("Could not route event through Camel Kamelet endpoint", e);
    }
  }

  @Override
  public void onPipelineStopped() {
    stopQuietly();
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
    if (KAMELET_PARAMETERS_GROUP_KEY.equals(staticPropertyInternalName)) {
      RuntimeResolvableGroupStaticProperty group = extractor.getStaticPropertyByName(
          KAMELET_PARAMETERS_GROUP_KEY,
          RuntimeResolvableGroupStaticProperty.class
      );
      return staticPropertyGenerator.configureParameterGroup(group, resolveTemplate());
    }

    throw new SpConfigurationException("Unsupported runtime-resolvable property: " + staticPropertyInternalName);
  }

  protected abstract ApacheCamelKameletSink newInstance();

  protected String getAppId() {
    return appId;
  }

  protected String getTemplateName() {
    return templateName;
  }

  protected KameletTemplateProvider getTemplateProvider() {
    return templateProvider;
  }

  protected AssetResolver getAssetResolver() {
    return assetResolver;
  }

  protected void customizeCamelContext(CamelContext context,
                                       KameletTemplate template,
                                       Map<String, Object> params) throws Exception {
  }

  protected void onSinkStopped() throws Exception {
  }

  private KameletTemplate resolveTemplate() {
    return templateProvider.requireTemplate(templateName);
  }

  private CamelContext createCamelContext(KameletTemplate template,
                                          Map<String, Object> params) throws Exception {
    String routeId = ROUTE_ID + "-" + templateName;
    CamelContext context = new DefaultCamelContext();
    params.forEach((key, value) -> {
      String prefixKey = "camel.kamelet." + templateName + "." + key;
      context.getPropertiesComponent().addInitialProperty(prefixKey, String.valueOf(value));
    });
    customizeCamelContext(context, template, params);
    transformRouteLoader.addRoute(
        context,
        routeId,
        ENTRY_ENDPOINT_URI,
        kameletEndpointUri,
        messageMapping == null ? null : messageMapping.transformStepsYaml()
    );
    return context;
  }

  private void stopQuietly() {
    if (producerTemplate != null) {
      try {
        producerTemplate.stop();
      } catch (Exception e) {
        LOG.warn("Error while stopping Camel producer template", e);
      } finally {
        producerTemplate = null;
      }
    }

    if (camelContext != null) {
      try {
        camelContext.stop();
      } catch (Exception e) {
        LOG.warn("Error while stopping Camel context", e);
      } finally {
        camelContext = null;
      }
    }

    try {
      onSinkStopped();
    } catch (Exception e) {
      LOG.warn("Error while cleaning up Camel Kamelet sink resources", e);
    }
  }
}
