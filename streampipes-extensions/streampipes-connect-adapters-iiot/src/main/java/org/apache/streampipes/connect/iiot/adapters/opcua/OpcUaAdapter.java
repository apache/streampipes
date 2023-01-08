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

package org.apache.streampipes.connect.iiot.adapters.opcua;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.connect.iiot.adapters.PullAdapter;
import org.apache.streampipes.connect.iiot.adapters.opcua.configuration.SpOpcUaConfigBuilder;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class OpcUaAdapter extends PullAdapter implements SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.opcua";
  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAdapter.class);

  private int pullingIntervalMilliSeconds;
  private SpOpcUaClient spOpcUaClient;
  private List<OpcNode> allNodes;
  private List<NodeId> allNodeIds;
  private int numberProperties;
  private final Map<String, Object> event;

  /**
   * This variable is used to map the node ids during the subscription to the labels of the nodes
   */
  private final Map<String, String> nodeIdToLabelMapping;

  public OpcUaAdapter() {
    super();
    this.numberProperties = 0;
    this.event = new HashMap<>();
    this.nodeIdToLabelMapping = new HashMap<>();
  }

  public OpcUaAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
    this.numberProperties = 0;
    this.event = new HashMap<>();
    this.nodeIdToLabelMapping = new HashMap<>();
  }

  @Override
  protected void before() throws AdapterException {

    this.allNodeIds = new ArrayList<>();
    List<String> deleteKeys = this.adapterDescription
        .getSchemaRules()
        .stream()
        .filter(rule -> rule instanceof DeleteRuleDescription)
        .map(rule -> ((DeleteRuleDescription) rule).getRuntimeKey())
        .collect(Collectors.toList());

    try {
      this.spOpcUaClient.connect();
      OpcUaNodeBrowser browserClient =
          new OpcUaNodeBrowser(this.spOpcUaClient.getClient(), this.spOpcUaClient.getSpOpcConfig());
      this.allNodes = browserClient.findNodes(deleteKeys);


      for (OpcNode node : this.allNodes) {
        this.allNodeIds.add(node.nodeId);
      }

      if (spOpcUaClient.inPullMode()) {
        this.pullingIntervalMilliSeconds = spOpcUaClient.getPullIntervalMilliSeconds();
      } else {
        this.numberProperties = this.allNodeIds.size();
        this.spOpcUaClient.createListSubscription(this.allNodeIds, this);
      }

      this.allNodes.forEach(node -> this.nodeIdToLabelMapping.put(node.getNodeId().toString(), node.getLabel()));


    } catch (Exception e) {
      throw new AdapterException("The Connection to the OPC UA server could not be established.", e.getCause());
    }
  }

  @Override
  public void startAdapter() throws AdapterException {

    this.spOpcUaClient = new SpOpcUaClient(SpOpcUaConfigBuilder.from(this.adapterDescription));

    if (this.spOpcUaClient.inPullMode()) {
      super.startAdapter();
    } else {
      this.before();
    }
  }

  @Override
  public void stopAdapter() throws AdapterException {
    // close connection
    this.spOpcUaClient.disconnect();

    if (this.spOpcUaClient.inPullMode()) {
      super.stopAdapter();
    }
  }

  @Override
  protected void pullData() throws ExecutionException, RuntimeException, InterruptedException, TimeoutException {
    var response =
        this.spOpcUaClient.getClient().readValues(0, TimestampsToReturn.Both, this.allNodeIds);
    boolean badStatusCodeReceived = false;
    boolean emptyValueReceived = false;
    List<DataValue> returnValues =
        response.get(this.getPollingInterval().getValue(), this.getPollingInterval().getTimeUnit());
    if (returnValues.size() == 0) {
      emptyValueReceived = true;
      LOG.warn("Empty value object returned - event will not be sent");
    } else {
      for (int i = 0; i < returnValues.size(); i++) {
        var status = returnValues.get(i).getStatusCode();
        if (StatusCode.GOOD.equals(status)) {
          Object value = returnValues.get(i).getValue().getValue();
          this.event.put(this.allNodes.get(i).getLabel(), value);
        } else {
          badStatusCodeReceived = true;
          LOG.warn("Received status code {} for node label: {} - event will not be sent",
              status,
              this.allNodes.get(i).getLabel());
        }
      }
    }
    if (!badStatusCodeReceived && !emptyValueReceived) {
      adapterPipeline.process(this.event);
    }
  }

  public void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

    String key = this.nodeIdToLabelMapping.get(item.getReadValueId().getNodeId().toString());

    OpcNode currNode = this.allNodes.stream()
        .filter(node -> key.equals(node.getLabel()))
        .findFirst()
        .orElse(null);

    if (currNode != null) {
      event.put(currNode.getLabel(), value.getValue().getValue());

      // ensure that event is complete and all opc ua subscriptions transmitted at least one value
      if (event.keySet().size() >= this.numberProperties) {
        Map<String, Object> newEvent = new HashMap<>();
        // deep copy of event to prevent preprocessor error
        for (String k : event.keySet()) {
          newEvent.put(k, event.get(k));
        }
        adapterPipeline.process(newEvent);
      }
    } else {
      LOG.error("No event is produced, because subscription item {} could not be found within all nodes", item);
    }
  }

  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, this.pullingIntervalMilliSeconds);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {

    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder
        .create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredAlternatives(Labels.withId(OpcUaLabels.ADAPTER_TYPE.name()),
            Alternatives.from(Labels.withId(OpcUaLabels.PULL_MODE.name()),
                StaticProperties.integerFreeTextProperty(
                    Labels.withId(OpcUaLabels.PULLING_INTERVAL.name()))),
            Alternatives.from(Labels.withId(OpcUaLabels.SUBSCRIPTION_MODE.name())))
        .requiredAlternatives(Labels.withId(OpcUaLabels.ACCESS_MODE.name()),
            Alternatives.from(Labels.withId(OpcUaLabels.UNAUTHENTICATED.name())),
            Alternatives.from(Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                StaticProperties.group(
                    Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OpcUaLabels.USERNAME.name())),
                    StaticProperties.secretValue(Labels.withId(OpcUaLabels.PASSWORD.name()))
                ))
        )
        .requiredAlternatives(Labels.withId(OpcUaLabels.OPC_HOST_OR_URL.name()),
            Alternatives.from(
                Labels.withId(OpcUaLabels.OPC_URL.name()),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(OpcUaLabels.OPC_SERVER_URL.name()), "opc.tcp://localhost:4840"))
            ,
            Alternatives.from(Labels.withId(OpcUaLabels.OPC_HOST.name()),
                StaticProperties.group(
                    Labels.withId("host-port"),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OpcUaLabels.OPC_SERVER_HOST.name())),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OpcUaLabels.OPC_SERVER_PORT.name()))
                ))
        )
        .requiredTextParameter(Labels.withId(OpcUaLabels.NAMESPACE_INDEX.name()))
        .requiredTextParameter(Labels.withId(OpcUaLabels.NODE_ID.name()))
        .requiredRuntimeResolvableTreeInput(
            Labels.withId(OpcUaLabels.AVAILABLE_NODES.name()),
            Arrays.asList(OpcUaLabels.NAMESPACE_INDEX.name(), OpcUaLabels.NODE_ID.name())
        )
        .build();

    description.setAppId(ID);

    return description;
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new OpcUaAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return OpcUaUtil.getSchema(adapterDescription);
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             StaticPropertyExtractor extractor) throws SpConfigurationException {
    return OpcUaUtil.resolveConfiguration(staticPropertyInternalName, extractor);
  }
}
