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

package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.connectors.opcua.client.SpOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.model.OpcNode;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtil.getSchema;

public class OpcUaAdapter implements StreamPipesAdapter, IPullAdapter, SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.opcua";
  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAdapter.class);

  private int pullingIntervalMilliSeconds;
  private SpOpcUaClient<OpcUaAdapterConfig> spOpcUaClient;
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
        this.allNodeIds.add(node.getNodeId());
      }

      if (spOpcUaClient.getSpOpcConfig().inPullMode()) {
        this.pullingIntervalMilliSeconds = spOpcUaClient.getSpOpcConfig().getPullIntervalMilliSeconds();
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
    this.spOpcUaClient = new SpOpcUaClient<>(
        SpOpcUaConfigExtractor.extractAdapterConfig(extractor.getStaticPropertyExtractor())
    );
    this.collector = collector;
    this.prepareAdapter(extractor);

    if (this.spOpcUaClient.getSpOpcConfig().inPullMode()) {
      this.pullAdapterScheduler = new PullAdapterScheduler();
      this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.spOpcUaClient.disconnect();

    if (this.spOpcUaClient.getSpOpcConfig().inPullMode()) {
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
    var builder = AdapterConfigurationBuilder.create(ID, OpcUaAdapter::new)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredAlternatives(Labels.withId(ADAPTER_TYPE),
            Alternatives.from(Labels.withId(PULL_MODE),
                StaticProperties.integerFreeTextProperty(
                    Labels.withId(PULLING_INTERVAL))),
            Alternatives.from(Labels.withId(SUBSCRIPTION_MODE)));
    SharedUserConfiguration.appendSharedOpcUaConfig(builder, true);
    return builder.buildConfiguration();
  }


  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    return getSchema(extractor);
  }
}
