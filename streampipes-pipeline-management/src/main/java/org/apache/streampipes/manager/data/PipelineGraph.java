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

import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Directed graph of the elements of a pipeline. Vertices are pipeline elements, edges are identified
 * by a string (source dom + "-" + target dom). Vertices and edges keep their insertion order.
 */
public class PipelineGraph {

  private final Map<NamedStreamPipesEntity, Set<String>> outgoingEdges = new LinkedHashMap<>();
  private final Map<String, Edge> edges = new LinkedHashMap<>();

  private record Edge(NamedStreamPipesEntity source, NamedStreamPipesEntity target) {
  }

  /**
   * Adds the vertex if it is not yet part of the graph.
   *
   * @return true if the vertex was added
   */
  public boolean addVertex(NamedStreamPipesEntity vertex) {
    Objects.requireNonNull(vertex, "vertex must not be null");
    if (outgoingEdges.containsKey(vertex)) {
      return false;
    }
    outgoingEdges.put(vertex, new LinkedHashSet<>());
    return true;
  }

  /**
   * Adds a directed edge between two vertices that are already part of the graph.
   *
   * @return true if the edge was added, false if an edge with the same id already exists
   * @throws IllegalArgumentException if a vertex is not part of the graph or source equals target
   */
  public boolean addEdge(NamedStreamPipesEntity source, NamedStreamPipesEntity target, String edge) {
    Objects.requireNonNull(edge, "edge must not be null");
    assertVertexExists(source);
    assertVertexExists(target);
    if (source.equals(target)) {
      throw new IllegalArgumentException("loops not allowed");
    }
    if (edges.containsKey(edge)) {
      return false;
    }
    edges.put(edge, new Edge(source, target));
    outgoingEdges.get(source).add(edge);
    return true;
  }

  public Set<NamedStreamPipesEntity> vertexSet() {
    return Collections.unmodifiableSet(outgoingEdges.keySet());
  }

  public Set<String> edgeSet() {
    return Collections.unmodifiableSet(edges.keySet());
  }

  public Set<String> outgoingEdgesOf(NamedStreamPipesEntity vertex) {
    assertVertexExists(vertex);
    return Collections.unmodifiableSet(outgoingEdges.get(vertex));
  }

  public NamedStreamPipesEntity getEdgeTarget(String edge) {
    var e = edges.get(edge);
    if (e == null) {
      throw new IllegalArgumentException("no such edge in graph: " + edge);
    }
    return e.target();
  }

  private void assertVertexExists(NamedStreamPipesEntity vertex) {
    Objects.requireNonNull(vertex, "vertex must not be null");
    if (!outgoingEdges.containsKey(vertex)) {
      throw new IllegalArgumentException("no such vertex in graph: " + vertex.getDom());
    }
  }
}
