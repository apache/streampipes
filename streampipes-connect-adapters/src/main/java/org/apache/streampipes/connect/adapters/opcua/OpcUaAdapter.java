package org.apache.streampipes.connect.adapters.opcua;

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.util.PollingSettings;
import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil;
import org.apache.streampipes.connect.adapters.opcua.utils.OpcUaUtil.OpcUaLabels;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.Option;
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
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OpcUaAdapter extends PullAdapter implements ResolvesContainerProvidedOptions {

    public static final String ID = "org.apache.streampipes.connect.adapters.opcua";

    private double pullingIntervalInSeconds;
    private OpcUa opcUa;
    private List<OpcNode> allNodes;
    private List<NodeId> allNodeIds;
    private int numberProperties;
    private Map<String, Object> event;

    public OpcUaAdapter() {
        super();
        this.numberProperties = 0;
        this.event = new HashMap<>();
    }

    public OpcUaAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
        super(adapterStreamDescription);
        this.numberProperties = 0;
        this.event = new HashMap<>();
    }

    @Override
    protected void before() throws AdapterException {

        this.allNodeIds = new ArrayList<>();
        try {
            this.opcUa.connect();
            this.allNodes = this.opcUa.browseNode(true);


                for (OpcNode node : this.allNodes) {
                    this.allNodeIds.add(node.nodeId);
                }

            if (opcUa.inPullMode()) {
                this.pullingIntervalInSeconds = opcUa.getPullIntervalSeconds();
            } else {
                this.numberProperties = this.allNodeIds.size();
                this.opcUa.createListSubscription(this.allNodeIds, this);
            }


        } catch (Exception e) {
            throw new AdapterException("The Connection to the OPC UA server could not be established.");
        }
    }

        @Override
    public void startAdapter() throws AdapterException {

        this.opcUa = OpcUa.from(this.adapterDescription);

        if (this.opcUa.inPullMode()) {
            super.startAdapter();
        } else {
            this.before();
        }
    }

    @Override
    public void stopAdapter() throws AdapterException {
        // close connection
        this.opcUa.disconnect();

        if (this.opcUa.inPullMode()){
            super.stopAdapter();
        }
    }

    @Override
    protected void pullData() {

            CompletableFuture<List<DataValue>> response = this.opcUa.getClient().readValues(0, TimestampsToReturn.Both, this.allNodeIds);
            try {

            List<DataValue> returnValues = response.get();
                for (int i = 0; i<returnValues.size(); i++) {

                    Object value = returnValues.get(i).getValue().getValue();
                    this.event.put(this.allNodes.get(i).getLabel(), value);

                }
             } catch (InterruptedException | ExecutionException ie) {
                ie.printStackTrace();
             }

            adapterPipeline.process(this.event);

    }

    public void onSubscriptionValue(UaMonitoredItem item, DataValue value) {

        String key = OpcUaUtil.getRuntimeNameOfNode(item.getReadValueId().getNodeId());

        OpcNode currNode = this.allNodes.stream()
                .filter(node -> key.equals(node.getNodeId().getIdentifier().toString()))
                .findFirst()
                .orElse(null);

        event.put(currNode.getLabel(), value.getValue().getValue());

        // ensure that event is complete and all opc ua subscriptions transmitted at least one value
        if (event.keySet().size() >= this.numberProperties) {
            Map <String, Object> newEvent = new HashMap<>();
            // deep copy of event to prevent preprocessor error
            for (String k : event.keySet()) {
                newEvent.put(k, event.get(k));
            }
            adapterPipeline.process(newEvent);
        }
    }

    @Override
    protected PollingSettings getPollingInterval() {
        return PollingSettings.from(TimeUnit.MILLISECONDS, (int) this.pullingIntervalInSeconds * 1000);
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
                                StaticProperties.integerFreeTextProperty(Labels.withId(OpcUaLabels.PULLING_INTERVAL.name()))),
                        Alternatives.from(Labels.withId(OpcUaLabels.SUBSCRIPTION_MODE.name())))
                .requiredAlternatives(Labels.withId(OpcUaLabels.ACCESS_MODE.name()),
                        Alternatives.from(Labels.withId(OpcUaLabels.UNAUTHENTICATED.name())),
                        Alternatives.from(Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                                StaticProperties.group(
                                        Labels.withId(OpcUaLabels.USERNAME_GROUP.name()),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.USERNAME.name())),
                                        StaticProperties.secretValue(Labels.withId(OpcUaLabels.PASSWORD.name()))
                                ))
                )
                .requiredAlternatives(Labels.withId(OpcUaLabels.OPC_HOST_OR_URL.name()),
                        Alternatives.from(
                                Labels.withId(OpcUaLabels.OPC_URL.name()),
                                StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_URL.name())))
                        ,
                        Alternatives.from(Labels.withId(OpcUaLabels.OPC_HOST.name()),
                                StaticProperties.group(
                                        Labels.withId("host-port"),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_HOST.name())),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(OpcUaLabels.OPC_SERVER_PORT.name()))
                                ))
                )
                .requiredTextParameter(Labels.withId(OpcUaLabels.NAMESPACE_INDEX.name()))
                .requiredTextParameter(Labels.withId(OpcUaLabels.NODE_ID.name()))
                .requiredMultiValueSelectionFromContainer(
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
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {

        return OpcUaUtil.getSchema(adapterDescription);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {

        return OpcUaUtil.resolveOptions(requestId, parameterExtractor);

    }

}
