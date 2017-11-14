package org.streampipes.manager.matching;

import org.streampipes.config.backend.BackendConfig;
import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphHelpers;
import org.streampipes.manager.matching.output.OutputSchemaFactory;
import org.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationGraphBuilder {

    private PipelineGraph pipelineGraph;
    private String pipelineId;

    List<InvocableStreamPipesEntity> graphs;

    public InvocationGraphBuilder(PipelineGraph pipelineGraph, String pipelineId) {
        this.graphs = new ArrayList<>();
        this.pipelineGraph = pipelineGraph;
        this.pipelineId = pipelineId;
    }

    public List<InvocableStreamPipesEntity> buildGraphs() {

        List<SpDataStream> streams = PipelineGraphHelpers.findStreams(pipelineGraph);

        for (SpDataStream stream : streams) {
            Set<InvocableStreamPipesEntity> connectedElements = getConnections(stream);
            configure(stream, connectedElements);
        }

        return graphs;
    }

    public void configure(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {

        EventGrounding inputGrounding = new GroundingBuilder(source, targets)
                .getEventGrounding();

        if (source instanceof InvocableStreamPipesEntity) {
            if (source instanceof DataProcessorInvocation) {

                DataProcessorInvocation dataProcessorInvocation = (DataProcessorInvocation) source;
                EventSchema outputSchema = new EventSchema();
                OutputSchemaGenerator schemaGenerator = new OutputSchemaFactory(dataProcessorInvocation
                        .getOutputStrategies()).getOuputSchemaGenerator();

                if (((DataProcessorInvocation) source).getInputStreams().size() == 1)
                    outputSchema = schemaGenerator.buildFromOneStream(dataProcessorInvocation.getInputStreams().get(0));
                else if (graphExists(dataProcessorInvocation.getDOM())) {
                    DataProcessorInvocation existingInvocation = (DataProcessorInvocation) find(dataProcessorInvocation.getDOM());

                    outputSchema = schemaGenerator.buildFromTwoStreams(existingInvocation.getInputStreams().get(0), dataProcessorInvocation.getInputStreams().get(1));
                    graphs.remove(existingInvocation);
                }

                dataProcessorInvocation.setOutputStrategies(Arrays.asList(schemaGenerator.getModifiedOutputStrategy(dataProcessorInvocation.getOutputStrategies().get(0))));

                SpDataStream outputStream = new SpDataStream();
                outputStream.setEventSchema(outputSchema);
                outputStream.setEventGrounding(inputGrounding);

                ((DataProcessorInvocation) source).setOutputStream(outputStream);
            }

            if (!graphExists(source.getDOM())) {
                graphs.add((InvocableStreamPipesEntity) source);
            }
        }

        targets.forEach(t -> {
            t.getInputStreams()
                    .get(getIndex(t))
                    .setEventGrounding(inputGrounding);

            t.getInputStreams()
                    .get(getIndex(t))
                    .setEventSchema(getInputSchema(source, getIndex(t)));

            String elementIdentifier = makeElementIdentifier(pipelineId, inputGrounding
                    .getTransportProtocol().getTopicName(), t.getName());

            t.setUri(t.getBelongsTo() + "/" + elementIdentifier);
            t.setCorrespondingPipeline(pipelineId);
            t.setStatusInfoSettings(makeStatusInfoSettings(elementIdentifier));

            configure(t, getConnections(t));
        });

    }

    private ElementStatusInfoSettings makeStatusInfoSettings(String elementIdentifier) {
        ElementStatusInfoSettings statusSettings = new ElementStatusInfoSettings();
        statusSettings.setKafkaHost(BackendConfig.INSTANCE.getKafkaHost());
        statusSettings.setKafkaPort(BackendConfig.INSTANCE.getKafkaPort());
        statusSettings.setErrorTopic(elementIdentifier +".error");
        statusSettings.setStatsTopic(elementIdentifier +".stats");
        statusSettings.setElementIdentifier(elementIdentifier);

        return statusSettings;
    }

    private String makeElementIdentifier(String pipelineId, String topic, String elementName) {
        return pipelineId
                + "-"
                + topic +"-" +elementName.replaceAll(" ", "").toLowerCase() +"-" +
                RandomStringUtils.randomAlphabetic(5);
    }

    private EventSchema getInputSchema(NamedStreamPipesEntity source, Integer index) {
        if (source instanceof SpDataStream) {
            return ((SpDataStream) source).getEventSchema();
        } else if (source instanceof DataProcessorInvocation) {
            return ((DataProcessorInvocation) source)
                    .getOutputStream()
                    .getEventSchema();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Set<InvocableStreamPipesEntity> getConnections(NamedStreamPipesEntity source) {
        Set<String> outgoingEdges = pipelineGraph.outgoingEdgesOf(source);
        return outgoingEdges
                .stream()
                .map(o -> pipelineGraph.getEdgeTarget(o))
                .map(g -> (InvocableStreamPipesEntity) g)
                .collect(Collectors.toSet());

    }

    private Integer getIndex(InvocableStreamPipesEntity element) {
        if (element.getStreamRequirements().size() == 1) return 0;
        else return graphExists(element.getDOM()) ? 1 : 0;
    }

    private boolean graphExists(String domId) {
        return graphs
                .stream()
                .anyMatch(g -> g.getDOM().equals(domId));
    }

    private InvocableStreamPipesEntity find(String domId) {
        return graphs
                .stream()
                .filter(g -> g.getDOM().equals(domId))
                .findFirst()
                .get();
    }
}