package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.manager.data.PipelineGraph;
import de.fzi.cep.sepa.manager.data.PipelineGraphHelpers;
import de.fzi.cep.sepa.manager.matching.output.OutputSchemaFactory;
import de.fzi.cep.sepa.manager.matching.output.OutputSchemaGenerator;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

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

            t.setUri(t.getBelongsTo() + "/" + pipelineId + "-" + inputGrounding.getTransportProtocol().getTopicName());
            t.setCorrespondingPipeline(pipelineId);

            configure(t, getConnections(t));
        });

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