package org.apache.streampipes.manager.matching;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.v2.pipeline.PipelineValidator;
import org.apache.streampipes.manager.matching.v2.pipeline.SpValidationException;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.PipelineEdgeValidation;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.pipeline.PipelineModification;

import java.util.*;
import java.util.stream.Collectors;

public class PipelineModificationGenerator {

  private final PipelineGraph pipelineGraph;
  private final Map<String, PipelineModification> pipelineModifications;
  private Map<String, PipelineEdgeValidation> edgeValidations;
  private final PipelineValidator pipelineValidator;

  public PipelineModificationGenerator(PipelineGraph pipelineGraph) {
    this.pipelineGraph = pipelineGraph;
    this.pipelineModifications = new HashMap<>();
    this.pipelineValidator = new PipelineValidator();
    this.edgeValidations = new HashMap<>();
  }

  public PipelineModificationMessage buildPipelineModificationMessage() {
    List<SpDataStream> streams = PipelineGraphHelpers.findStreams(pipelineGraph);

    for (SpDataStream stream : streams) {
      Set<InvocableStreamPipesEntity> connectedElements = getConnections(stream);
      addModification(stream, connectedElements);
    }

    PipelineModificationMessage message = new PipelineModificationMessage();
    message.setPipelineModifications(getModifications());
    message.setEdgeValidations(toList(this.edgeValidations));
    return message;
  }

  private void addModification(NamedStreamPipesEntity source,
                               Set<InvocableStreamPipesEntity> targets) {

    targets.forEach(t -> {
      PipelineModification modification = new PipelineModification();
      List<PipelineElementValidationInfo> validationInfos = new ArrayList<>();
      modification.setDomId(t.getDOM());
      modification.setElementId(t.getElementId());
      try {
        pipelineValidator.apply(source, t, targets, validationInfos);
        buildModification(modification, t);
        edgeValidations.put(makeKey(source, t), PipelineEdgeValidation.complete(source.getDOM(), t.getDOM()));
      } catch (SpValidationException e) {
        //e.getErrorLog().forEach(log -> validationInfos.add(PipelineElementValidationInfo.error(log.toString())));
        edgeValidations.put(makeKey(source, t), PipelineEdgeValidation.invalid(source.getDOM(), t.getDOM(), toNotifications(e.getErrorLog())));
        modification.setPipelineElementValid(false);
      }
      modification.setValidationInfos(validationInfos);
      this.pipelineModifications.put(t.getDOM(), modification);

      addModification(t, getConnections(t));
    });
  }

  private String makeKey(NamedStreamPipesEntity source,
                         InvocableStreamPipesEntity t) {
    return source.getDOM() + "-" + t.getDOM();
  }

  private List<PipelineEdgeValidation> toList(Map<String,
          PipelineEdgeValidation> edgeValidations) {
    return new ArrayList<>(edgeValidations.values());
  }

  private void buildModification(PipelineModification modification,
                                 InvocableStreamPipesEntity t) {
    if (t instanceof DataProcessorInvocation) {
      modification.setOutputStrategies(((DataProcessorInvocation) t).getOutputStrategies());
    }
    modification.setInputStreams(t.getInputStreams());
    modification.setStaticProperties(t.getStaticProperties());
    modification.setPipelineElementValid(true);
  }

  private Set<InvocableStreamPipesEntity> getConnections(NamedStreamPipesEntity source) {
    Set<String> outgoingEdges = pipelineGraph.outgoingEdgesOf(source);
    return outgoingEdges
            .stream()
            .map(pipelineGraph::getEdgeTarget)
            .map(g -> (InvocableStreamPipesEntity) g)
            .collect(Collectors.toSet());
  }

  private List<PipelineModification> getModifications() {
    return new ArrayList<>(this.pipelineModifications.values());
  }

  private List<Notification> toNotifications(List<MatchingResultMessage> matchingResultMessages) {
    return matchingResultMessages
            .stream()
            .map(m -> new Notification(m.getTitle(), m.toString()))
            .collect(Collectors.toList());
  }
}
