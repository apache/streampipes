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
import org.apache.streampipes.extensions.connectors.opcua.client.ConnectedOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaAdapterConfig;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.model.node.OpcUaNode;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtils;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.SUBSCRIPTION_MODE;

public class OpcUaAdapter implements StreamPipesAdapter, IPullAdapter, SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.opcua";
  public static final String PULL_GROUP = "pull-mode-group";
  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAdapter.class);

  private int pullingIntervalMilliSeconds;
  private final OpcUaClientProvider clientProvider;
  private ConnectedOpcUaClient connectedClient;
  private OpcUaAdapterConfig opcUaAdapterConfig;
  private OpcUaNodeProvider nodeProvider;
  private List<OpcUaNode> allNodes;
  private final Map<String, Object> event;

  private IEventCollector collector;
  private PullAdapterScheduler pullAdapterScheduler;
  private int numberOfEventProperties = 0;

  /**
   * This variable is used to map the node ids during the subscription to the labels of the nodes
   */
  private final Map<String, String> nodeIdToLabelMapping;

  public OpcUaAdapter(OpcUaClientProvider clientProvider) {
    this.clientProvider = clientProvider;
    this.event = new HashMap<>();
    this.nodeIdToLabelMapping = new HashMap<>();
  }

  private void prepareAdapter(IAdapterParameterExtractor extractor) throws AdapterException {
    List<String> deleteKeys = extractor
        .getAdapterDescription()
        .getSchemaRules()
        .stream()
        .filter(rule -> rule instanceof DeleteRuleDescription)
        .map(rule -> ((DeleteRuleDescription) rule).getRuntimeKey())
        .collect(Collectors.toList());

    try {
      this.connectedClient = clientProvider.getClient(this.opcUaAdapterConfig);
      OpcUaNodeBrowser browserClient =
          new OpcUaNodeBrowser(this.connectedClient.getClient(), this.opcUaAdapterConfig);
      this.nodeProvider = browserClient.makeNodeProvider(deleteKeys);
      this.allNodes = nodeProvider.getNodes();

      if (opcUaAdapterConfig.inPullMode()) {
        this.pullingIntervalMilliSeconds = opcUaAdapterConfig.getPullIntervalMilliSeconds();
      } else {
        var allNodeIds = this.allNodes.stream()
            .map(node -> node.nodeInfo().getNodeId()).toList();
        this.connectedClient.createListSubscription(allNodeIds, this);
      }

      this.allNodes.forEach(node -> this.nodeIdToLabelMapping
          .put(node.nodeInfo().getNodeId().toString(), node.nodeInfo().getDisplayName()));


    } catch (Exception e) {
      throw new AdapterException("The Connection to the OPC UA server could not be established.", e.getCause());
    }
  }

  @Override
  public void pullData() throws ExecutionException, RuntimeException, InterruptedException, TimeoutException {
    var response =
        this.connectedClient.getClient().readValues(
            0,
            TimestampsToReturn.Both,
            this.allNodes.stream().map(o -> o.nodeInfo().getNodeId()).toList());
    boolean badStatusCodeReceived = false;
    boolean emptyValueReceived = false;
    List<DataValue> returnValues =
        response.get(this.getPollingInterval().value(), this.getPollingInterval().timeUnit());
    if (returnValues.isEmpty()) {
      emptyValueReceived = true;
      LOG.warn("Empty value object returned - event will not be sent");
    } else {
      for (int i = 0; i < returnValues.size(); i++) {
        var status = returnValues.get(i).getStatusCode();
        if (StatusCode.GOOD.equals(status)) {
          var value = returnValues.get(i).getValue();
          this.allNodes.get(i).addToEvent(connectedClient.getClient(), this.event, value);
        } else {
          badStatusCodeReceived = true;
          LOG.warn("Received status code {} for node label: {}",
              status,
              this.allNodes.get(i).nodeInfo().getDisplayName());
        }
      }
    }
    if (!emptyValueReceived && !shouldSkipEvent(badStatusCodeReceived)) {
      collector.collect(this.event);
    }
  }

  private boolean shouldSkipEvent(boolean badStatusCodeReceived) {
    return badStatusCodeReceived
        && this.opcUaAdapterConfig.getIncompleteEventStrategy()
        .equalsIgnoreCase(SharedUserConfiguration.INCOMPLETE_OPTION_IGNORE);
  }

  public void onSubscriptionValue(UaMonitoredItem item,
                                  DataValue value) {

    String key = this.nodeIdToLabelMapping.get(item.getReadValueId().getNodeId().toString());

    var currNode = this.allNodes.stream()
        .filter(node -> key.equals(node.nodeInfo().getDisplayName()))
        .findFirst()
        .orElse(null);

    if (currNode != null) {
      currNode.addToEvent(connectedClient.getClient(), event, value.getValue());
      // ensure that event is complete and all opc ua subscriptions transmitted at least one value
      if (event.size() >= numberOfEventProperties) {
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
    this.opcUaAdapterConfig =
        SpOpcUaConfigExtractor.extractAdapterConfig(
            extractor.getStaticPropertyExtractor(),
            adapterRuntimeContext.getStreamPipesClient(),
            extractor.getAdapterDescription().getElementId()
        );
    this.collector = collector;
    this.prepareAdapter(extractor);
    this.numberOfEventProperties =
        nodeProvider.getNumberOfEventProperties(this.connectedClient.getClient());

    if (this.opcUaAdapterConfig.inPullMode()) {
      this.pullAdapterScheduler = new PullAdapterScheduler();
      this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    clientProvider.releaseClient(this.opcUaAdapterConfig);

    if (this.opcUaAdapterConfig.inPullMode()) {
      this.pullAdapterScheduler.shutdown();
    }
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
    return OpcUaUtils.resolveConfig(clientProvider, staticPropertyInternalName, extractor);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    var builder = AdapterConfigurationBuilder.create(ID, 6, () -> new OpcUaAdapter(clientProvider))
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .requiredAlternatives(Labels.withId(ADAPTER_TYPE),
            Alternatives.from(Labels.withId(PULL_MODE),
                SharedUserConfiguration.getPullModeGroup()
            ),
            Alternatives.from(Labels.withId(SUBSCRIPTION_MODE)));
    SharedUserConfiguration.appendSharedOpcUaConfig(builder, true);
    builder.requiredStaticProperty(SharedUserConfiguration.makeNamingStrategyOption());
    return builder.buildConfiguration();
  }


  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    return new OpcUaSchemaProvider().getSchema(clientProvider, extractor, adapterGuessSchemaContext.getStreamPipesClient());
  }
}
