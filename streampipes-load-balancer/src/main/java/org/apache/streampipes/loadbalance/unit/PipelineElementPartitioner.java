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
package org.apache.streampipes.loadbalance.unit;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pipeline element partitioner for load balancing
 * Partitions pipeline elements into resource units based on dependencies and service availability
 */
public class PipelineElementPartitioner {

  /**
   * Partition result containing resource units and their compatible services
   */
  public static class PartitionResult {
    private final List<ResourceUnitWithServices> resourceUnits;

    public PartitionResult(List<ResourceUnitWithServices> resourceUnits) {
      this.resourceUnits = resourceUnits == null ? Collections.EMPTY_LIST : resourceUnits;
    }

    public List<ResourceUnitWithServices> getResourceUnits() {
      return resourceUnits;
    }

    public int size() {
      return resourceUnits.size();
    }

    public boolean isEmpty() {
      return resourceUnits.isEmpty();
    }
  }

  /**
   * Resource unit with its compatible services
   */
  public static class ResourceUnitWithServices {
    private final LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit;
    private final List<SpServiceRegistration> compatibleServices;

    public ResourceUnitWithServices(
        LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit,
        List<SpServiceRegistration> compatibleServices) {
      this.resourceUnit = resourceUnit;
      this.compatibleServices = compatibleServices;
    }

    public LoadBalanceResourceUnit<InvocableStreamPipesEntity> getResourceUnit() {
      return resourceUnit;
    }

    public List<SpServiceRegistration> getCompatibleServices() {
      return compatibleServices;
    }

    public boolean hasCompatibleServices() {
      return compatibleServices != null && !compatibleServices.isEmpty();
    }
  }

  /**
   * Adapter resource unit with its compatible services
   */
  public static class AdapterResourceUnitWithServices {
    private final LoadBalanceResourceUnit<AdapterDescription> resourceUnit;
    private final List<SpServiceRegistration> compatibleServices;

    public AdapterResourceUnitWithServices(
        LoadBalanceResourceUnit<AdapterDescription> resourceUnit,
        List<SpServiceRegistration> compatibleServices) {
      this.resourceUnit = resourceUnit;
      this.compatibleServices = compatibleServices;
    }

    public LoadBalanceResourceUnit<AdapterDescription> getResourceUnit() {
      return resourceUnit;
    }

    public List<SpServiceRegistration> getCompatibleServices() {
      return compatibleServices;
    }

    public boolean hasCompatibleServices() {
      return compatibleServices != null && !compatibleServices.isEmpty();
    }
  }

  /**
   * Partition a complete pipeline into resource units
   * @param pipeline Pipeline to partition
   * @return Partition result containing resource units and their compatible services
   */
  public static PartitionResult partitionPipeline(Pipeline pipeline) {
    if (pipeline == null) {
      throw new IllegalArgumentException("Pipeline cannot be null");
    }

    List<DataSinkInvocation> sinks = pipeline.getActions();
    List<DataProcessorInvocation> processors = pipeline.getSepas();

    PartitionResult result = partitionElements(sinks, processors, pipeline.getLabels());

    return result;
  }

  /**
   * Partition sinks and processors into resource units
   * @param sinks List of sink invocations
   * @param processors List of processor invocations
   * @param labels List of labels
   * @return Partition result containing resource units and their compatible services
   */
  public static PartitionResult partitionElements(
      List<DataSinkInvocation> sinks,
      List<DataProcessorInvocation> processors,
      List<String> labels) {

    if ((sinks == null || sinks.isEmpty()) && (processors == null || processors.isEmpty())) {
      return new PartitionResult(new ArrayList<>());
    }

    List<SpServiceRegistration> allServices = SpServiceDiscovery.getServiceDiscovery().findAll();
    
    // Initialize data structures
    Map<String, InvocableStreamPipesEntity> elementMap = new HashMap<>();
    Map<String, List<SpServiceRegistration>> elementCompatibleServices = new HashMap<>();
    UnionFindPartitioner partitioner = new UnionFindPartitioner();

    // Initialize all elements
    initializeElements(sinks, processors, elementMap, elementCompatibleServices, partitioner, allServices);

    // Merge elements that can run on same services
    mergeCompatibleElements(sinks, processors, elementMap, elementCompatibleServices, partitioner);

    // Build resource units from partitions
    List<ResourceUnitWithServices> resourceUnits = buildResourceUnits(
        elementMap, elementCompatibleServices, partitioner, labels);

    return new PartitionResult(resourceUnits);
  }

  /**
   * Create resource unit for a single adapter
   * Adapters don't need partitioning as each adapter is independent
   * @param adapter Adapter description
   * @return Adapter resource unit with compatible services
   */
  public static AdapterResourceUnitWithServices createAdapterResourceUnit(AdapterDescription adapter) {
    if (adapter == null) {
      throw new IllegalArgumentException("Adapter cannot be null");
    }

    LoadBalanceResourceUnit<AdapterDescription> resourceUnit = new LoadBalanceResourceUnit<>();
    resourceUnit.addElement(adapter);
    resourceUnit.setLabels(Collections.EMPTY_LIST);

    List<SpServiceRegistration> allServices = SpServiceDiscovery.getServiceDiscovery().findAll();
    List<SpServiceRegistration> compatibleServices = findCompatibleServices(
        SpServiceUrlProvider.ADAPTER.getServiceTag(adapter.getAppId()).asString(),
        allServices);

    return new AdapterResourceUnitWithServices(resourceUnit, compatibleServices);
  }

  /**
   * Initialize all elements with their compatible services
   */
  private static void initializeElements(
      List<DataSinkInvocation> sinks,
      List<DataProcessorInvocation> processors,
      Map<String, InvocableStreamPipesEntity> elementMap,
      Map<String, List<SpServiceRegistration>> elementCompatibleServices,
      UnionFindPartitioner partitioner,
      List<SpServiceRegistration> allServices) {

    // Initialize sinks
    if (sinks != null) {
      for (DataSinkInvocation sink : sinks) {
        String elementId = sink.getDom();
        elementMap.put(elementId, sink);
        partitioner.makeSet(elementId);
        
        List<SpServiceRegistration> services = findCompatibleServices(
          SpServiceUrlProvider.DATA_SINK.getServiceTag(sink.getAppId()).asString(),
          allServices);
        elementCompatibleServices.put(elementId, services);
      }
    }

    // Initialize processors
    if (processors != null) {
      for (DataProcessorInvocation processor : processors) {
        String elementId = processor.getDom();
        elementMap.put(elementId, processor);
        partitioner.makeSet(elementId);
        
        List<SpServiceRegistration> services = findCompatibleServices(
          SpServiceUrlProvider.DATA_PROCESSOR.getServiceTag(processor.getAppId()).asString(),
          allServices);
        elementCompatibleServices.put(elementId, services);
      }
    }
  }

  /**
   * Merge elements that can run on the same services
   */
  private static void mergeCompatibleElements(
      List<DataSinkInvocation> sinks,
      List<DataProcessorInvocation> processors,
      Map<String, InvocableStreamPipesEntity> elementMap,
      Map<String, List<SpServiceRegistration>> elementCompatibleServices,
      UnionFindPartitioner partitioner) {

    // Process sinks - merge with connected processors if they share services
    if (sinks != null) {
      for (DataSinkInvocation sink : sinks) {
        mergeWithConnectedElements(sink, elementMap, elementCompatibleServices, partitioner);
      }
    }

    // Process processors - merge with connected elements if they share services
    if (processors != null) {
      for (DataProcessorInvocation processor : processors) {
        mergeWithConnectedElements(processor, elementMap, elementCompatibleServices, partitioner);
      }
    }
  }

  /**
   * Merge element with its connected elements if they can run on same services
   */
  private static void mergeWithConnectedElements(
      InvocableStreamPipesEntity element,
      Map<String, InvocableStreamPipesEntity> elementMap,
      Map<String, List<SpServiceRegistration>> elementCompatibleServices,
      UnionFindPartitioner partitioner) {

    String elementId = element.getDom();
    List<SpServiceRegistration> elementServices = elementCompatibleServices.get(elementId);

    if (element.getConnectedTo() != null) {
      for (String connectedId : element.getConnectedTo()) {
        InvocableStreamPipesEntity connectedElement = elementMap.get(connectedId);
        if (connectedElement == null) {
          continue;
        }

        List<SpServiceRegistration> connectedServices = elementCompatibleServices.get(connectedId);
        
        // If both elements can run on the same services, merge them
        if (servicesOverlap(elementServices, connectedServices)) {
          partitioner.union(elementId, connectedId);
        }
      }
    }
  }

  /**
   * Build resource units from partitions
   */
  private static List<ResourceUnitWithServices> buildResourceUnits(
      Map<String, InvocableStreamPipesEntity> elementMap,
      Map<String, List<SpServiceRegistration>> elementCompatibleServices,
      UnionFindPartitioner partitioner,
      List<String> labels) {

    // Group elements by their partition root
    Map<String, List<String>> partitions = new HashMap<>();
    for (String elementId : elementMap.keySet()) {
      String root = partitioner.find(elementId);
      partitions.computeIfAbsent(root, k -> new ArrayList<>()).add(elementId);
    }

    // Build resource units
    List<ResourceUnitWithServices> resourceUnits = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : partitions.entrySet()) {
      LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit = new LoadBalanceResourceUnit<>();

      // Find common services for all elements in this partition
      List<SpServiceRegistration> commonServices = null;
      
      for (String elementId : entry.getValue()) {
        InvocableStreamPipesEntity element = elementMap.get(elementId);
        resourceUnit.addElement(element);
        resourceUnit.setLabels(labels);

        List<SpServiceRegistration> elementServices = elementCompatibleServices.get(elementId);
        if (commonServices == null) {
          commonServices = new ArrayList<>(elementServices);
        } else {
          commonServices = findCommonServices(commonServices, elementServices);
        }
      }

      if (commonServices == null) {
        commonServices = new ArrayList<>();
      }

      resourceUnits.add(new ResourceUnitWithServices(resourceUnit, commonServices));
    }

    return resourceUnits;
  }

  /**
   * Find services compatible with the given tag
   */
  public static List<SpServiceRegistration> findCompatibleServices(
      String serviceTag,
      List<SpServiceRegistration> allServices) {

    return allServices.stream()
        .filter(service -> service.getStatus() != SpServiceStatus.UNHEALTHY)
        .filter(s-> filtersSupported(s, serviceTag))
        .sorted(Comparator.comparing(SpServiceRegistration::getSvcId))
        .collect(Collectors.toList());
  }

  private static boolean filtersSupported(SpServiceRegistration service,
                                          String tag) {
    return new HashSet<>(service.getTags())
      .stream()
      .anyMatch(t -> t.asString().equals(tag));
  }



  /**
   * Check if two service lists are equal
   * Since both lists are sorted by service ID, we can compare element by element
   */
  private static boolean servicesOverlap(
      List<SpServiceRegistration> services1,
      List<SpServiceRegistration> services2) {

    if (services1 == null || services2 == null) {
      return services1 == services2;
    }

    // Lists must have the same size to be equal
    if (services1.size() != services2.size()) {
      return false;
    }

    // Compare each element at the same index (both lists are sorted)
    for (int i = 0; i < services1.size(); i++) {
      if (!services1.get(i).getSvcId().equals(services2.get(i).getSvcId())) {
        return false;
      }
    }
    
    return true;
  }

  /**
   * Find common services between two lists
   * Since both lists are sorted by service ID, we can use two-pointer approach
   */
  private static List<SpServiceRegistration> findCommonServices(
      List<SpServiceRegistration> services1,
      List<SpServiceRegistration> services2) {

    if (services1 == null || services2 == null) {
      return new ArrayList<>();
    }

    List<SpServiceRegistration> commonServices = new ArrayList<>();
    
    // Two-pointer approach for sorted lists
    int i = 0, j = 0;
    while (i < services1.size() && j < services2.size()) {
      String id1 = services1.get(i).getSvcId();
      String id2 = services2.get(j).getSvcId();
      
      int comparison = id1.compareTo(id2);
      if (comparison == 0) {
        commonServices.add(services1.get(i)); // Found common service
        i++;
        j++;
      } else if (comparison < 0) {
        i++;
      } else {
        j++;
      }
    }
    
    return commonServices;
  }

  /**
   * Union-Find data structure for partitioning
   */
  private static class UnionFindPartitioner {
    private final Map<String, String> parent = new HashMap<>();

    public void makeSet(String element) {
      parent.put(element, element);
    }

    public String find(String element) {
      if (!parent.containsKey(element)) {
        return element;
      }
      if (parent.get(element).equals(element)) {
        return element;
      }
      // Path compression
      String root = find(parent.get(element));
      parent.put(element, root);
      return root;
    }

    public void union(String element1, String element2) {
      String root1 = find(element1);
      String root2 = find(element2);
      if (!root1.equals(root2)) {
        parent.put(root1, root2);
      }
    }
  }
}

