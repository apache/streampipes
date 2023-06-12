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
package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.utils.FileProtocolUtils;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.HttpServerAdapterManagement;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

public class HttpServerProtocol implements StreamPipesAdapter {

  private static final String ENDPOINT_NAME = "endpoint-name";
  private static final String CONFIGURE = "configure";

  private static final String MANUALLY = "manually";
  private static final String EP_CONFIG = "ep-config";
  private static final String EP_RUNTIME_NAME = "ep-runtime-name";
  private static final String EP_RUNTIME_TYPE = "ep-runtime-type";
  private static final String EP_DOMAIN_PROPERTY = "ep-domain-property";

  private static final String FILE_IMPORT = "file-import";
  private static final String FILE = "file";

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.httpserver";

  private String endpointId;

  public HttpServerProtocol() {

  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.endpointId = extractor.singleValueParameter(ENDPOINT_NAME, String.class);
  }

  private EventProperty makeProperty(StaticPropertyExtractor memberExtractor) {
    EventPropertyPrimitive primitive = new EventPropertyPrimitive();
    primitive.setRuntimeName(memberExtractor.singleValueParameter(EP_RUNTIME_NAME, String.class));
    primitive.setRuntimeType(extractRuntimeType(memberExtractor.selectedSingleValue(EP_RUNTIME_TYPE, String.class)));
    primitive
        .setDomainProperties(Collections
            .singletonList(URI.create(memberExtractor.singleValueParameter(EP_DOMAIN_PROPERTY, String.class))));
    return primitive;
  }

  private String extractRuntimeType(String type) {
    return switch (type) {
      case "String" -> Datatypes.String.toString();
      case "Boolean" -> Datatypes.Boolean.toString();
      case "Integer" -> Datatypes.Integer.toString();
      default -> Datatypes.Double.toString();
    };
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, HttpServerProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Generic)
        .requiredTextParameter(Labels.withId(ENDPOINT_NAME))
        .requiredAlternatives(Labels.withId(CONFIGURE),
            Alternatives.from(Labels.withId(MANUALLY),
                StaticProperties.collection(Labels.withId(EP_CONFIG),
                    StaticProperties.stringFreeTextProperty(Labels.withId(EP_RUNTIME_NAME)),
                    StaticProperties.singleValueSelection(Labels.withId(EP_RUNTIME_TYPE),
                        Options.from("String", "Integer", "Double", "Boolean")),
                    StaticProperties.stringFreeTextProperty(Labels.withId(EP_DOMAIN_PROPERTY)))),
            Alternatives.from(Labels.withId(FILE_IMPORT),
                StaticProperties.fileProperty(Labels.withId(FILE), Filetypes.CSV, Filetypes.JSON, Filetypes.XML)))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    applyConfiguration(extractor.getStaticPropertyExtractor());
    var processor = new BrokerEventProcessor(extractor.selectedParser(), collector);
    HttpServerAdapterManagement.INSTANCE.addAdapter(this.endpointId, processor);
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    HttpServerAdapterManagement.INSTANCE.removeAdapter(this.endpointId);
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor parameterExtractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    IStaticPropertyExtractor propertyExtractor = parameterExtractor.getStaticPropertyExtractor();
    applyConfiguration(propertyExtractor);
    GuessSchemaBuilder schemaBuilder = GuessSchemaBuilder.create();

    String selectedImportMode = propertyExtractor.selectedAlternativeInternalId(CONFIGURE);
    if (selectedImportMode.equals(MANUALLY)) {
      CollectionStaticProperty sp = (CollectionStaticProperty) propertyExtractor.getStaticPropertyByName(EP_CONFIG);

      for (StaticProperty member : sp.getMembers()) {
        StaticPropertyExtractor memberExtractor =
            StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());
        schemaBuilder.property(makeProperty(memberExtractor));
      }
      return schemaBuilder.build();
    } else if (selectedImportMode.equals(FILE_IMPORT)){
      String fileName = propertyExtractor.selectedFilename(FILE);
      InputStream dataInputStream = this.getDataFromEndpoint(fileName);

      return parameterExtractor.selectedParser().getGuessSchema(dataInputStream);
    } else {
      throw new AdapterException("Unknown import mode selected: " + selectedImportMode);
    }
  }


  private InputStream getDataFromEndpoint(String fileName) throws ParseException {
    try {
      return FileProtocolUtils.getFileInputStream(fileName);
    } catch (IOException e) {
      throw new ParseException("Could not find file: " + fileName);
    }
  }
}
