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

import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.HttpServerAdapterManagement;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpServerProtocol extends Protocol {

  private ProtocolDescription adapterDescription;

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

  public HttpServerProtocol(ProtocolDescription adapterDescription, IParser parser, IFormat format) {
    super(parser, format);
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());
    this.adapterDescription = adapterDescription;
    this.endpointId = extractor.singleValueParameter(ENDPOINT_NAME, String.class);
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    return new HttpServerProtocol(protocolDescription, parser, format);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
        .withLocales(Locales.EN)
        .sourceType(AdapterSourceType.STREAM)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Generic)
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
        .build();
  }

  @Override
  public GuessSchema getGuessSchema() throws ParseException {
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());
    GuessSchemaBuilder schemaBuilder = GuessSchemaBuilder.create();

    String selectedImportMode = extractor.selectedAlternativeInternalId(CONFIGURE);

    if (selectedImportMode.equals(MANUALLY)) {
      CollectionStaticProperty sp = (CollectionStaticProperty) extractor.getStaticPropertyByName(EP_CONFIG);

      for (StaticProperty member : sp.getMembers()) {
        StaticPropertyExtractor memberExtractor =
            StaticPropertyExtractor.from(((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>());
        schemaBuilder.property(makeProperty(memberExtractor));
      }
    }

    return schemaBuilder.build();
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
    switch (type) {
      case "String":
        return Datatypes.String.toString();
      case "Boolean":
        return Datatypes.Boolean.toString();
      case "Integer":
        return Datatypes.Integer.toString();
      default:
        return Datatypes.Double.toString();
    }
  }

  @Override
  public List<Map<String, Object>> getNElements(int n) throws ParseException {
    return null;
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    InternalEventProcessor<byte[]> receiver = new HttpServerEventReceiver(stk);
    HttpServerAdapterManagement.INSTANCE.addAdapter(this.endpointId, receiver);
  }

  @Override
  public void stop() {
    HttpServerAdapterManagement.INSTANCE.removeAdapter(this.endpointId);
  }

  @Override
  public String getId() {
    return ID;
  }

}
