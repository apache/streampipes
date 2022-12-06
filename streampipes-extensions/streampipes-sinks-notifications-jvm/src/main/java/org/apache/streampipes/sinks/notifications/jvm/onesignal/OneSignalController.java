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

package org.apache.streampipes.sinks.notifications.jvm.onesignal;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class OneSignalController extends StandaloneEventSinkDeclarer<OneSignalParameters> {

  private static final String CONTENT_KEY = "content";
  private static final String APP_ID = "app_id";
  private static final String REST_API_KEY = "api_key";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.onesignal")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredHtmlInputParameter(Labels.withId(CONTENT_KEY))
        .requiredTextParameter(Labels.withId(APP_ID))
        .requiredTextParameter(Labels.withId(REST_API_KEY))
        .build();
  }

  @Override
  public ConfiguredEventSink<OneSignalParameters> onInvocation(DataSinkInvocation graph,
                                                               DataSinkParameterExtractor extractor) {

    String content = extractor.singleValueParameter(CONTENT_KEY, String.class);
    String appId = extractor.singleValueParameter(APP_ID, String.class);
    String apiKey = extractor.singleValueParameter(REST_API_KEY, String.class);

    OneSignalParameters params = new OneSignalParameters(graph, content, appId, apiKey);

    return new ConfiguredEventSink<>(params, OneSignalProducer::new);
  }

}
