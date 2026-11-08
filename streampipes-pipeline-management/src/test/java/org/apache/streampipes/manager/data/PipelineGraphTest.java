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

package org.apache.streampipes.manager.data;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PipelineGraphTest {

  @Test
  public void buildsGraphFromPipelineConnections() {
    var stream = element(new SpDataStream(), "s1", List.of());
    var processor1 = element(new DataProcessorInvocation(), "p1", List.of("s1"));
    var processor2 = element(new DataProcessorInvocation(), "p2", List.of("s1"));
    var sink = element(new DataSinkInvocation(), "a1", List.of("p1", "p2"));

    var pipeline = new Pipeline();
    pipeline.setStreams(List.of(stream));
    pipeline.setSepas(List.of(processor1, processor2));
    pipeline.setActions(List.of(sink));

    var graph = new PipelineGraphBuilder(pipeline).buildGraph();

    assertEquals(List.of(stream, processor1, processor2, sink), List.copyOf(graph.vertexSet()));
    assertEquals(Set.of("s1-p1", "s1-p2", "p1-a1", "p2-a1"), graph.edgeSet());
    assertEquals(List.of("s1-p1", "s1-p2"), List.copyOf(graph.outgoingEdgesOf(stream)));
    assertTrue(graph.outgoingEdgesOf(sink).isEmpty());
    assertSame(sink, graph.getEdgeTarget("p1-a1"));
    assertEquals(List.of(stream), PipelineGraphHelpers.findStreams(graph));
  }

  @Test
  public void ignoresDuplicateVerticesAndEdges() {
    var graph = new PipelineGraph();
    var stream = element(new SpDataStream(), "s1", List.of());
    var processor = element(new DataProcessorInvocation(), "p1", List.of("s1"));

    assertTrue(graph.addVertex(stream));
    assertFalse(graph.addVertex(stream));
    graph.addVertex(processor);

    assertTrue(graph.addEdge(stream, processor, "s1-p1"));
    assertFalse(graph.addEdge(stream, processor, "s1-p1"));
    assertEquals(1, graph.edgeSet().size());
  }

  @Test
  public void rejectsUnknownVerticesEdgesAndLoops() {
    var graph = new PipelineGraph();
    var stream = element(new SpDataStream(), "s1", List.of());
    var processor = element(new DataProcessorInvocation(), "p1", List.of("s1"));
    graph.addVertex(stream);

    assertThrows(IllegalArgumentException.class, () -> graph.addEdge(stream, processor, "s1-p1"));
    assertThrows(IllegalArgumentException.class, () -> graph.addEdge(stream, stream, "s1-s1"));
    assertThrows(IllegalArgumentException.class, () -> graph.outgoingEdgesOf(processor));
    assertThrows(IllegalArgumentException.class, () -> graph.getEdgeTarget("unknown"));
    assertThrows(UnsupportedOperationException.class, () -> graph.vertexSet().clear());
  }

  private static <T extends NamedStreamPipesEntity> T element(T entity, String dom, List<String> connectedTo) {
    entity.setDom(dom);
    entity.setElementId("urn:test:" + dom);
    entity.setConnectedTo(connectedTo);
    return entity;
  }
}
