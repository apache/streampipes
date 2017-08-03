package org.streampipes.manager.matching;

import org.streampipes.commons.config.old.Configuration;
import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphHelpers;
import org.streampipes.manager.matching.output.OutputSchemaFactory;
import org.streampipes.manager.matching.output.OutputSchemaGenerator;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.ElementStatusInfoSettings;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationGraphBuilder {

    private PipelineGraph pipelineGraph;
    private String pipelineId;

    List<InvocableSEPAElement> graphs;

    public InvocationGraphBuilder(PipelineGraph pipelineGraph, String pipelineId) {
        this.graphs = new ArrayList<>();
        this.pipelineGraph = pipelineGraph;
        this.pipelineId = pipelineId;
    }

    public List<InvocableSEPAElement> buildGraphs() {

        List<EventStream> streams = PipelineGraphHelpers.findStreams(pipelineGraph);

        for (EventStream stream : streams) {
            Set<InvocableSEPAElement> connectedElements = getConnections(stream);
            configure(stream, connectedElements);
        }

        return graphs;
    }

    public void configure(NamedSEPAElement source, Set<InvocableSEPAElement> targets) {

        EventGrounding inputGrounding = new GroundingBuilder(source, targets)
                .getEventGrounding();

        if (source instanceof InvocableSEPAElement) {
            if (source instanceof SepaInvocation) {

                SepaInvocation sepaInvocation = (SepaInvocation) source;
                EventSchema outputSchema = new EventSchema();
                OutputSchemaGenerator schemaGenerator = new OutputSchemaFactory(sepaInvocation
                        .getOutputStrategies()).getOuputSchemaGenerator();

                if (((SepaInvocation) source).getInputStreams().size() == 1)
                    outputSchema = schemaGenerator.buildFromOneStream(sepaInvocation.getInputStreams().get(0));
                else if (graphExists(sepaInvocation.getDOM())) {
                    SepaInvocation existingInvocation = (SepaInvocation) find(sepaInvocation.getDOM());

                    outputSchema = schemaGenerator.buildFromTwoStreams(existingInvocation.getInputStreams().get(0), sepaInvocation.getInputStreams().get(1));
                    graphs.remove(existingInvocation);
                }

                sepaInvocation.setOutputStrategies(Arrays.asList(schemaGenerator.getModifiedOutputStrategy(sepaInvocation.getOutputStrategies().get(0))));

                EventStream outputStream = new EventStream();
                outputStream.setEventSchema(outputSchema);
                outputStream.setEventGrounding(inputGrounding);

                ((SepaInvocation) source).setOutputStream(outputStream);
            }

            if (!graphExists(source.getDOM())) {
                graphs.add((InvocableSEPAElement) source);
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
        statusSettings.setKafkaHost(Configuration.getInstance().getBrokerConfig().getKafkaHost());
        statusSettings.setKafkaPort(Configuration.getInstance().getBrokerConfig().getKafkaPort());
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

    private EventSchema getInputSchema(NamedSEPAElement source, Integer index) {
        if (source instanceof EventStream) {
            return ((EventStream) source).getEventSchema();
        } else if (source instanceof SepaInvocation) {
            return ((SepaInvocation) source)
                    .getOutputStream()
                    .getEventSchema();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Set<InvocableSEPAElement> getConnections(NamedSEPAElement source) {
        Set<String> outgoingEdges = pipelineGraph.outgoingEdgesOf(source);
        return outgoingEdges
                .stream()
                .map(o -> pipelineGraph.getEdgeTarget(o))
                .map(g -> (InvocableSEPAElement) g)
                .collect(Collectors.toSet());

    }

    private Integer getIndex(InvocableSEPAElement element) {
        if (element.getStreamRequirements().size() == 1) return 0;
        else return graphExists(element.getDOM()) ? 1 : 0;
    }

    private boolean graphExists(String domId) {
        return graphs
                .stream()
                .anyMatch(g -> g.getDOM().equals(domId));
    }

    private InvocableSEPAElement find(String domId) {
        return graphs
                .stream()
                .filter(g -> g.getDOM().equals(domId))
                .findFirst()
                .get();
    }
}