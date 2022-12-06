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
package org.apache.streampipes.sinks.databases.jvm.ditto;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

import java.util.List;

public class DittoController extends StandaloneEventSinkDeclarer<DittoParameters> {

  private static final String DITTO_API_ENDPOINT_KEY = "dittoApiEndpointKey";
  private static final String DITTO_USER_KEY = "dittoUserKey";
  private static final String DITTO_PASSWORD_KEY = "dittoPasswordKey";

  private static final String DITTO_THING_ID_KEY = "dittoThingIdKey";
  private static final String DITTO_FEATURE_ID_KEY = "dittoFeatureIdKey";

  private static final String SELECTED_FIELDS_KEY = "selectedFieldsKey";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.ditto")
        .category(DataSinkType.FORWARD)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithNaryMapping(
            EpRequirements.anyProperty(),
            Labels.withId(SELECTED_FIELDS_KEY),
            PropertyScope.NONE).build())
        .requiredTextParameter(Labels.withId(DITTO_API_ENDPOINT_KEY))
        .requiredTextParameter(Labels.withId(DITTO_USER_KEY))
        .requiredSecret(Labels.withId(DITTO_PASSWORD_KEY))
        .requiredTextParameter(Labels.withId(DITTO_THING_ID_KEY))
        .requiredTextParameter(Labels.withId(DITTO_FEATURE_ID_KEY))
        .build();
  }

  @Override
  public ConfiguredEventSink<DittoParameters> onInvocation(DataSinkInvocation graph,
                                                           DataSinkParameterExtractor extractor) {
    String dittoApiEndpoint = extractor.textParameter(DITTO_API_ENDPOINT_KEY);
    String dittoUser = extractor.textParameter(DITTO_USER_KEY);
    String dittoPassword = extractor.secretValue(DITTO_PASSWORD_KEY);
    String dittoThingId = extractor.textParameter(DITTO_THING_ID_KEY);
    String dittoFeatureId = extractor.textParameter(DITTO_FEATURE_ID_KEY);

    List<String> selectedFieldSelectors = extractor.mappingPropertyValues(SELECTED_FIELDS_KEY);
    DittoParameters params = new DittoParameters(graph, dittoApiEndpoint, dittoUser,
        dittoPassword, dittoThingId, dittoFeatureId, selectedFieldSelectors);

    return new ConfiguredEventSink<>(params, Ditto::new);
  }

}
