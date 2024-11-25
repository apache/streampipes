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

package org.apache.streampipes.extensions.connectors.opcua.config;

import org.apache.streampipes.extensions.connectors.opcua.utils.SecurityUtils;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter.PULL_GROUP;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.HOST_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PASSWORD;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME_GROUP;

public class SharedUserConfiguration {

  public static final String INCOMPLETE_EVENT_HANDLING_KEY = "incomplete-event-handling";
  public static final String INCOMPLETE_OPTION_IGNORE = "ignore-event";
  public static final String INCOMPLETE_OPTION_SEND = "send-event";

  public static final String SECURITY_MODE = "securityMode";
  public static final String SECURITY_POLICY = "securityPolicy";
  public static final String USER_AUTHENTICATION = "userAuthentication";
  public static final String USER_AUTHENTICATION_ANONYMOUS = "anonymous";

  public static void appendSharedOpcUaConfig(AbstractConfigurablePipelineElementBuilder<?, ?> builder,
                                             boolean adapterConfig) {

    var dependsOn = getDependsOn(adapterConfig);

    builder
        .requiredSingleValueSelection(
            Labels.withId(SECURITY_MODE),
            SecurityUtils.getAvailableSecurityModes().stream().map(mode -> new Option(mode.k, mode.v)).toList()
        )
        .requiredSingleValueSelection(
            Labels.withId(SECURITY_POLICY),
            SecurityUtils.getAvailableSecurityPolicies().stream().map(p -> new Option(p.name())).toList()
        )
        .requiredAlternatives(Labels.withId(USER_AUTHENTICATION),
            Alternatives.from(Labels.withId(USER_AUTHENTICATION_ANONYMOUS)),
            Alternatives.from(Labels.withId(USERNAME_GROUP),
                StaticProperties.group(
                    Labels.withId(USERNAME_GROUP),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(USERNAME)),
                    StaticProperties.secretValue(Labels.withId(PASSWORD))
                ))
        )
        .requiredAlternatives(Labels.withId(OPC_HOST_OR_URL),
            Alternatives.from(
                Labels.withId(OPC_URL),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(OPC_SERVER_URL), "opc.tcp://localhost:4840"))
            ,
            Alternatives.from(Labels.withId(OPC_HOST),
                StaticProperties.group(
                    Labels.withId(HOST_PORT),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OPC_SERVER_HOST)),
                    StaticProperties.integerFreeTextProperty(
                        Labels.withId(OPC_SERVER_PORT))
                ))
        )
        .requiredRuntimeResolvableTreeInput(
            Labels.withId(AVAILABLE_NODES.name()),
            dependsOn,
            true,
            adapterConfig
        );
  }

  public static StaticPropertyGroup getPullModeGroup() {
    var group = StaticProperties.group(
        Labels.withId(PULL_GROUP),
        false,
        StaticProperties.integerFreeTextProperty(
            Labels.withId(PULLING_INTERVAL)),
        getIncompleteEventConfig()
    );
    group.setHorizontalRendering(false);
    return group;
  }

  public static OneOfStaticProperty getIncompleteEventConfig() {
    return StaticProperties.singleValueSelection(
        Labels.withId(INCOMPLETE_EVENT_HANDLING_KEY),
        List.of(
            new Option("Ignore (only complete messages are sent)", INCOMPLETE_OPTION_IGNORE),
            new Option("Send (incomplete messages are sent)", INCOMPLETE_OPTION_SEND)
        )
    );
  }

  public static List<String> getDependsOn(boolean adapterConfig) {
    return adapterConfig ? List.of(
        ADAPTER_TYPE.name(),
        SECURITY_MODE,
        SECURITY_POLICY,
        OPC_HOST_OR_URL.name()
    ) : List.of(
        SECURITY_MODE,
        SECURITY_POLICY,
        OPC_HOST_OR_URL.name());
  }
}
