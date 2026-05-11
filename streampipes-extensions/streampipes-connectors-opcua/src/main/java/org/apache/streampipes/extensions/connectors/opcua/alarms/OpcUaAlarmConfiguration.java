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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

public class OpcUaAlarmConfiguration {

  public static final String ALARM_SOURCE_SCOPE = "alarm-source-scope";
  public static final String WHOLE_SERVER = "whole-server";
  public static final String SPECIFIC_AREA_OR_MACHINE = "specific-area-or-machine";
  public static final String NOTIFIER_NODE = "notifier-node";
  public static final String EVENT_TYPE = "event-type";
  public static final String ADDITIONAL_FIELDS = "additional-fields";

  public static final String ALARM_FILTER_MODE = "alarm-filter-mode";
  public static final String ALL_ALARMS = "all-alarms";
  public static final String SOURCE_NAME_CONTAINS = "source-name-contains";
  public static final String SOURCE_NAME_FILTER = "source-name-filter";

  public static final String MINIMUM_SEVERITY = "minimum-severity";
  public static final String MINIMUM_SEVERITY_ANY = "any";

  private OpcUaAlarmConfiguration() {
  }

  public static void appendFilterConfiguration(AbstractConfigurablePipelineElementBuilder<?, ?> builder) {
    builder.requiredAlternatives(
        Labels.withId(ALARM_SOURCE_SCOPE),
        Alternatives.from(Labels.withId(WHOLE_SERVER)),
        Alternatives.from(
            Labels.withId(SPECIFIC_AREA_OR_MACHINE),
            StaticProperties.runtimeResolvableTreeInput(
            Labels.withId(NOTIFIER_NODE),
                List.of(),
                false,
                false
            )
        )
    );

    builder.requiredRuntimeResolvableTreeInput(
        Labels.withId(EVENT_TYPE),
        List.of(),
        false,
        false
    );

    builder.requiredMultiValueSelectionFromContainer(
        Labels.withId(ADDITIONAL_FIELDS),
        List.of(EVENT_TYPE)
    );

    builder.requiredAlternatives(
        Labels.withId(ALARM_FILTER_MODE),
        Alternatives.from(Labels.withId(ALL_ALARMS)),
        Alternatives.from(
            Labels.withId(SOURCE_NAME_CONTAINS),
            StaticProperties.stringFreeTextProperty(Labels.withId(SOURCE_NAME_FILTER))
        )
    );

    builder.requiredSingleValueSelection(
        Labels.withId(MINIMUM_SEVERITY),
        List.of(
            new Option("Any", MINIMUM_SEVERITY_ANY),
            new Option("100+", "100"),
            new Option("300+", "300"),
            new Option("500+", "500"),
            new Option("700+", "700")
        )
    );
  }
}
