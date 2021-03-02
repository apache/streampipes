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
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OpcUaPullAdapter extends PullAdapter implements ResolvesContainerProvidedOptions {

    public static final String ID = "org.apache.streampipes.connect.adapters.opcua.pull";

    private int pollingIntervalInSeconds;
    private OpcUa opcUa;
    private List<OpcNode> allNodes;

    public OpcUaPullAdapter() {
        super();
    }

    public OpcUaPullAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
        super(adapterStreamDescription);
    }

    @Override
    protected void before() throws AdapterException {

        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(this.adapterDescription.getConfig(), new ArrayList<>());

        this.pollingIntervalInSeconds = extractor.singleValueParameter(OpcUaLabels.POLLING_INTERVAL.name(), int.class);

        this.opcUa = OpcUa.from(this.adapterDescription);

        try {
            this.opcUa.connect();
            this.allNodes = this.opcUa.browseNode(true);
        } catch (Exception e) {
            throw new AdapterException("The Connection to the OPC UA server could not be established.");
        }

    }

    @Override
    protected void pullData() {

        Map<String, Object> event = new HashMap<>();

        for (OpcNode opcNode : this.allNodes) {
            CompletableFuture response = this.opcUa.getClient().readValue(0, TimestampsToReturn.Both, opcNode.getNodeId());
            try {

                Object value = ((DataValue) response.get()).getValue().getValue();

                event.put(opcNode.getLabel(), value);

            } catch (InterruptedException | ExecutionException ie) {
                ie.printStackTrace();
            }
        }

        adapterPipeline.process(event);

    }

    @Override
    protected PollingSettings getPollingInterval() {
        return PollingSettings.from(TimeUnit.SECONDS, this.pollingIntervalInSeconds);
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder
                .create(ID)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .requiredIntegerParameter(Labels.withId(OpcUaLabels.POLLING_INTERVAL.name()))
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
                        Arrays.asList(OpcUaLabels.POLLING_INTERVAL.name(), OpcUaLabels.NAMESPACE_INDEX.name(), OpcUaLabels.NODE_ID.name())
                )
                .build();

        description.setAppId(ID);

        return description;
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new OpcUaPullAdapter(adapterDescription);
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
