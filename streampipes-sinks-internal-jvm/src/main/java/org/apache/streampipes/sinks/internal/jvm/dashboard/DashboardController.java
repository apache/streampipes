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

package org.apache.streampipes.sinks.internal.jvm.dashboard;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class DashboardController extends StandaloneEventSinkDeclarer<DashboardParameters> {

  private static final String VISUALIZATION_NAME_KEY = "visualization-name";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.internal.jvm.dashboard")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.VISUALIZATION_CHART)
            .requiredStream(StreamRequirementsBuilder.any())
            .requiredTextParameter(Labels.withId(VISUALIZATION_NAME_KEY))
            .build();
  }

  @Override
  public ConfiguredEventSink<DashboardParameters> onInvocation(DataSinkInvocation invocationGraph,
                                                               DataSinkParameterExtractor extractor) {
    String visualizationName = extractor.singleValueParameter(VISUALIZATION_NAME_KEY, String.class);
    return new ConfiguredEventSink<>(new DashboardParameters(invocationGraph, visualizationName), Dashboard::new);
  }

}
