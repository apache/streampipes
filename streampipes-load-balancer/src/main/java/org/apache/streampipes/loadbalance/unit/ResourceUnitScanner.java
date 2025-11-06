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
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resource unit scanner for on-demand discovery of pipeline elements Does not cache data in memory,
 * instead scans pipelines and adapters when needed
 */
public class ResourceUnitScanner {

  private static final Logger logger = LoggerFactory.getLogger(ResourceUnitScanner.class);

  /**
   * Result of scanning a service for resource units
   */
  public static class ServiceResourceUnits {
    private final List<PipelineElementPartitioner.PartitionResult> pipelineUnits;
    private final List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterUnits;

    public ServiceResourceUnits(List<PipelineElementPartitioner.PartitionResult> pipelineUnits,
                                List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterUnits) {
      this.pipelineUnits = pipelineUnits;
      this.adapterUnits = adapterUnits;
    }

    public List<PipelineElementPartitioner.PartitionResult> getPipelineUnits() {
      return pipelineUnits;
    }

    public List<PipelineElementPartitioner.AdapterResourceUnitWithServices> getAdapterUnits() {
      return adapterUnits;
    }

    public int getTotalUnits() {
      return pipelineUnits.size() + adapterUnits.size();
    }

    public boolean isEmpty() {
      return pipelineUnits.isEmpty() && adapterUnits.isEmpty();
    }
  }

  /**
   * Scan service and generate partitioned resource units This is the main method that combines
   * scanning with PipelineElementPartitioner logic
   * 
   * @param service Service registration
   * @return Service resource units containing partitioned pipeline and adapter units
   */
  public static ServiceResourceUnits scanAndPartitionService(SpServiceRegistration service) {
    logger.info("Scanning and partitioning service {}", service.getSvcId());

    // Scan and partition pipeline elements
    List<PipelineElementPartitioner.PartitionResult> pipelineUnits =
        scanAndPartitionPipeline(service);

    // Scan and create adapter units
    List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterUnits =
        scanAndCreateAdapter(service);

    logger.info("Service {} has {} pipeline units and {} adapter units", service.getSvcId(),
                pipelineUnits.size(), adapterUnits.size());

    return new ServiceResourceUnits(pipelineUnits, adapterUnits);
  }

  /**
   * Scan service and partition pipeline elements according to PipelineElementPartitioner rules
   * 
   * @param service Service registration
   * @return List of partitioned resource units
   */
  private static List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> scanAndPartitionPipelineElements(SpServiceRegistration service) {

    String serviceUrl = service.getServiceUrl();
    List<Pipeline> allPipelines = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().findAll();

    List<Pipeline> relevantPipelines = allPipelines.stream()
        .filter(pipeline -> pipeline.isRunning() && pipelineUsesService(pipeline, serviceUrl))
        .toList();

    logger.debug("Found {} pipelines using service {}", relevantPipelines.size(),
                 service.getSvcId());

    List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> resourceUnits = new ArrayList<>();

    // Process each pipeline
    for (Pipeline pipeline : relevantPipelines) {
      // Extract elements running on this service
      List<DataSinkInvocation> serviceSinks = extractServiceSinks(pipeline, serviceUrl);
      List<DataProcessorInvocation> serviceProcessors =
          extractServiceProcessors(pipeline, serviceUrl);

      if (serviceSinks.isEmpty() && serviceProcessors.isEmpty()) {
        continue;
      }

      // Use PipelineElementPartitioner to partition these elements
      PipelineElementPartitioner.PartitionResult partitionResult = PipelineElementPartitioner
          .partitionElements(serviceSinks, serviceProcessors, pipeline.getLabels());

      // Add partitioned units with service ID set
      for (PipelineElementPartitioner.ResourceUnitWithServices unitWithServices : partitionResult
          .getResourceUnits()) {
        LoadBalanceResourceUnit<InvocableStreamPipesEntity> unit =
            unitWithServices.getResourceUnit();
        unit.setServiceId(service.getSvcId());
        unit.setPipelineId(pipeline.getPipelineId());
        resourceUnits.add(unit);
      }
    }

    return resourceUnits;
  }

  /**
   * Scan service and partition pipeline elements according to PipelineElementPartitioner rules
   * 
   * @param service Service registration
   * @return List of partitioned resource units
   */
  private static List<PipelineElementPartitioner.PartitionResult> scanAndPartitionPipeline(SpServiceRegistration service) {

    String serviceUrl = service.getServiceUrl();
    List<Pipeline> allPipelines = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().findAll();

    List<Pipeline> relevantPipelines = allPipelines.stream()
        .filter(pipeline -> pipeline.isRunning() && pipelineUsesService(pipeline, serviceUrl))
        .toList();

    logger.debug("Found {} pipelines using service {}", relevantPipelines.size(),
                 service.getSvcId());

    List<PipelineElementPartitioner.PartitionResult> resourceUnits = new ArrayList<>();

    // Process each pipeline
    for (Pipeline pipeline : relevantPipelines) {
      // Extract elements running on this service
      List<DataSinkInvocation> serviceSinks = extractServiceSinks(pipeline, serviceUrl);
      List<DataProcessorInvocation> serviceProcessors =
          extractServiceProcessors(pipeline, serviceUrl);

      if (serviceSinks.isEmpty() && serviceProcessors.isEmpty()) {
        continue;
      }

      // Use PipelineElementPartitioner to partition these elements
      PipelineElementPartitioner.PartitionResult partitionResult = PipelineElementPartitioner
          .partitionElements(serviceSinks, serviceProcessors, pipeline.getLabels());

      resourceUnits.add(partitionResult);
    }

    return resourceUnits;
  }

  /**
   * Scan service and create adapter resource units
   * 
   * @param service Service registration
   * @return List of adapter resource units
   */
  private static List<LoadBalanceResourceUnit<AdapterDescription>> scanAndCreateAdapterUnits(SpServiceRegistration service) {

    String serviceUrl = service.getServiceUrl();
    var adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
    List<AdapterDescription> allAdapters = adapterStorage.findAll();

    // Find adapters running on this service
    List<AdapterDescription> serviceAdapters =
        allAdapters.stream().filter(adapter -> adapter.isRunning() && serviceUrl != null
            && adapter.getSelectedEndpointUrl() != null
            && adapter.getSelectedEndpointUrl().startsWith(serviceUrl)).toList();

    logger.debug("Found {} adapters running on service {}", serviceAdapters.size(),
                 service.getSvcId());

    // Create resource unit for each adapter (adapters don't need partitioning)
    List<LoadBalanceResourceUnit<AdapterDescription>> adapterUnits = new ArrayList<>();
    for (AdapterDescription adapter : serviceAdapters) {
      LoadBalanceResourceUnit<AdapterDescription> unit = new LoadBalanceResourceUnit<>();
      unit.setPipelineId(adapter.getElementId());
      unit.setServiceId(service.getSvcId());
      unit.setLabels(Collections.EMPTY_LIST);
      unit.addElement(adapter);
      adapterUnits.add(unit);
    }

    return adapterUnits;
  }


  /**
   * Scan service and create adapter resource units
   * 
   * @param service Service registration
   * @return List of adapter resource units
   */
  private static List<PipelineElementPartitioner.AdapterResourceUnitWithServices> scanAndCreateAdapter(SpServiceRegistration service) {

    String serviceUrl = service.getServiceUrl();
    var adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
    List<AdapterDescription> allAdapters = adapterStorage.findAll();

    // Find adapters running on this service
    List<AdapterDescription> serviceAdapters =
        allAdapters.stream().filter(adapter -> adapter.isRunning() && serviceUrl != null
            && adapter.getSelectedEndpointUrl() != null
            && adapter.getSelectedEndpointUrl().startsWith(serviceUrl)).toList();

    logger.debug("Found {} adapters running on service {}", serviceAdapters.size(),
                 service.getSvcId());

    // Create resource unit for each adapter (adapters don't need partitioning)
    List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterUnits =
        new ArrayList<>();
    for (AdapterDescription adapter : serviceAdapters) {
      adapterUnits.add(PipelineElementPartitioner.createAdapterResourceUnit(adapter));
    }

    return adapterUnits;
  }

  /**
   * Extract sinks from pipeline that are running on the specified service
   * 
   * @param pipeline Pipeline
   * @param serviceUrl Service URL
   * @return List of sinks running on this service
   */
  private static List<DataSinkInvocation> extractServiceSinks(Pipeline pipeline,
                                                              String serviceUrl) {
    if (pipeline.getActions() == null) {
      return new ArrayList<>();
    }

    return pipeline.getActions().stream().filter(sink -> matchesSelectedEndpoint(sink, serviceUrl))
        .toList();
  }

  /**
   * Extract processors from pipeline that are running on the specified service
   * 
   * @param pipeline Pipeline
   * @param serviceUrl Service URL
   * @return List of processors running on this service
   */
  private static List<DataProcessorInvocation> extractServiceProcessors(Pipeline pipeline,
                                                                        String serviceUrl) {
    if (pipeline.getSepas() == null) {
      return new ArrayList<>();
    }

    return pipeline.getSepas().stream()
        .filter(processor -> matchesSelectedEndpoint(processor, serviceUrl)).toList();
  }

  /**
   * Find all resource units (sinks and processors) running on a specific service
   * 
   * @param service Service registration
   * @return List of resource units for this service
   */
  public static List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> findResourceUnitsForService(SpServiceRegistration service) {
    return scanAndPartitionPipelineElements(service);
  }

  /**
   * Find all adapter resource units running on a specific service
   * 
   * @param service Service registration
   * @return List of adapter resource units for this service
   */
  public static List<LoadBalanceResourceUnit<AdapterDescription>> findAdapterUnitsForService(SpServiceRegistration service) {
    return scanAndCreateAdapterUnits(service);
  }

  /**
   * Find all resource units for migration from a list of services
   * 
   * @param sourceServices Services to scan for resource units
   * @return Map of service ID to list of resource units
   */
  public static Map<String, List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>>> findAllResourceUnitsForMigration(List<SpServiceRegistration> sourceServices) {

    logger.info("Scanning resource units for {} services", sourceServices.size());

    return sourceServices.stream().collect(Collectors
        .toMap(SpServiceRegistration::getSvcId, ResourceUnitScanner::findResourceUnitsForService));
  }

  /**
   * Find all adapter units for migration from a list of services
   * 
   * @param sourceServices Services to scan for adapter units
   * @return Map of service ID to list of adapter resource units
   */
  public static Map<String, List<LoadBalanceResourceUnit<AdapterDescription>>> findAllAdapterUnitsForMigration(List<SpServiceRegistration> sourceServices) {

    logger.info("Scanning adapter units for {} services", sourceServices.size());

    return sourceServices.stream().collect(Collectors
        .toMap(SpServiceRegistration::getSvcId, ResourceUnitScanner::findAdapterUnitsForService));
  }

  /**
   * Check if a pipeline uses a specific service
   * 
   * @param pipeline Pipeline to check
   * @param serviceUrl Service URL
   * @return true if pipeline uses this service
   */
  private static boolean pipelineUsesService(Pipeline pipeline, String serviceUrl) {
    boolean sepaUsesService = pipeline.getSepas() != null
        && pipeline.getSepas().stream().anyMatch(sepa -> matchesSelectedEndpoint(sepa, serviceUrl));

    boolean actionUsesService = pipeline.getActions() != null && pipeline.getActions().stream()
        .anyMatch(action -> matchesSelectedEndpoint(action, serviceUrl));

    return sepaUsesService || actionUsesService;
  }

  /**
   * Check if an entity's selected endpoint matches the service URL
   * 
   * @param entity Entity to check
   * @param serviceUrl Service URL to match
   * @return true if endpoints match
   */
  private static boolean matchesSelectedEndpoint(InvocableStreamPipesEntity entity,
                                                 String serviceUrl) {
    if (entity == null || entity.getSelectedEndpointUrl() == null || serviceUrl == null) {
      return false;
    }
    return entity.getSelectedEndpointUrl().startsWith(serviceUrl);
  }
}
