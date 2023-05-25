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
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.iiot.adapters.opcua.configuration.SpOpcUaConfigBuilder;
import org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.extensions.api.connect.AdapterInterface;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
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

import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.NAMESPACE_INDEX;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.NODE_ID;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_HOST;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.connect.iiot.adapters.opcua.utils.OpcUaUtil.getSchema;

public class OpcUaAdapter implements AdapterInterface, IPullAdapter, SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.opcua";
  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAdapter.class);

  private int pullingIntervalMilliSeconds;
  private SpOpcUaClient spOpcUaClient;
  private List<OpcNode> allNodes;
  private List<NodeId> allNodeIds;
  private int numberProperties;
  private final Map<String, Object> event;

  private IEventCollector collector;
  private PullAdapterScheduler pullAdapterScheduler;

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

  private void prepareAdapter(IAdapterParameterExtractor extractor) throws AdapterException {

    this.allNodeIds = new ArrayList<>();
    List<String> deleteKeys = extractor
        .getAdapterDescription()
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
  public void pullData() throws ExecutionException, RuntimeException, InterruptedException, TimeoutException {
    var response =
        this.spOpcUaClient.getClient().readValues(0, TimestampsToReturn.Both, this.allNodeIds);
    boolean badStatusCodeReceived = false;
    boolean emptyValueReceived = false;
    List<DataValue> returnValues =
        response.get(this.getPollingInterval().value(), this.getPollingInterval().timeUnit());
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
      collector.collect(this.event);
    }
  }

  public void onSubscriptionValue(UaMonitoredItem item,
                                  DataValue value) {

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
        collector.collect(newEvent);
      }
    } else {
      LOG.error("No event is produced, because subscription item {} could not be found within all nodes", item);
    }
  }

  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.MILLISECONDS, this.pullingIntervalMilliSeconds);
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.spOpcUaClient = new SpOpcUaClient(SpOpcUaConfigBuilder.from(extractor.getStaticPropertyExtractor()));
    this.collector = collector;
    this.prepareAdapter(extractor);

    if (this.spOpcUaClient.inPullMode()) {
      this.pullAdapterScheduler = new PullAdapterScheduler();
      this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.spOpcUaClient.disconnect();

    if (this.spOpcUaClient.inPullMode()) {
      this.pullAdapterScheduler.shutdown();
    }
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
    return OpcUaUtil.resolveConfiguration(staticPropertyInternalName, extractor);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, OpcUaAdapter::new)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
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
        .requiredAlternatives(Labels.withId(OPC_HOST_OR_URL.name()),
            Alternatives.from(
                Labels.withId(OPC_URL.name()),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(OPC_SERVER_URL.name()), "opc.tcp://localhost:4840"))
            ,
            Alternatives.from(Labels.withId(OPC_HOST.name()),
                StaticProperties.group(
                    Labels.withId("host-port"),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OPC_SERVER_HOST.name())),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(OPC_SERVER_PORT.name()))
                ))
        )
        .requiredTextParameter(Labels.withId(NAMESPACE_INDEX.name()))
        .requiredTextParameter(Labels.withId(NODE_ID.name()))
        .requiredRuntimeResolvableTreeInput(
            Labels.withId(AVAILABLE_NODES.name()),
            Arrays.asList(NAMESPACE_INDEX.name(), NODE_ID.name())
        )
        .buildConfiguration();
  }


  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    return getSchema(extractor);
  }
}
