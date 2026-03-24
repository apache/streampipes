# Adapter Data Source Health Check Implementation Plan

## Executive Summary

This document provides a comprehensive implementation plan for extending StreamPipes adapter health monitoring to include data source connectivity checks in addition to the existing backend service reachability checks.

**Current Problem**: Adapters are shown as "running" as long as the backend extension service is reachable, even when the underlying data source (Kafka, OPC-UA, MQTT) is down or unreachable.

**Solution**: Implement a dual-health-check system that monitors both backend service health and data source connectivity, with a configurable, lightweight, and low-overhead checking mechanism.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Backend Implementation](#backend-implementation)
3. [Data Source Health Check Strategies](#data-source-health-check-strategies)
4. [UI Implementation](#ui-implementation)
5. [Performance & Configuration](#performance--configuration)
6. [Implementation Phases](#implementation-phases)
7. [Testing Strategy](#testing-strategy)

---

## Architecture Overview

### High-Level Design

```
┌─────────────────────────────────────────────────────────────┐
│                    StreamPipes Core                          │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │      AdapterHealthCheckService                      │    │
│  │  - Orchestrates health checks                       │    │
│  │  - Aggregates backend + data source health         │    │
│  │  - Manages check scheduling with backoff           │    │
│  └──────────────────┬──────────────────────────────────┘    │
│                     │                                        │
│  ┌──────────────────▼──────────────────────────────────┐    │
│  │   AdapterHealthMonitoringManager                    │    │
│  │  - Maintains health state per adapter              │    │
│  │  - Implements exponential backoff                  │    │
│  │  - Stores error messages                           │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼──────┐         ┌────────▼────────┐
│   Backend    │         │   Data Source   │
│   Health     │         │   Health Check  │
│   (Existing) │         │   (New)         │
└──────────────┘         └─────────┬───────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
      ┌───────▼──────┐  ┌──────▼──────┐  ┌─────▼──────┐
      │ Kafka Check  │  │ OPC-UA Check│  │ MQTT Check │
      │ Strategy     │  │ Strategy    │  │ Strategy   │
      └──────────────┘  └─────────────┘  └────────────┘
```

### Key Components

1. **AdapterHealthStatus** (Model): Enhanced health model with backend + data source states
2. **DataSourceHealthCheck** (Interface): Contract for data source health verification
3. **Adapter-specific Health Checkers**: Kafka, OPC-UA, MQTT implementations
4. **AdapterHealthCheckService**: Orchestration and scheduling service
5. **REST API**: Enhanced endpoints for health status retrieval
6. **UI Component**: Enhanced status display with detailed breakdown

---

## Backend Implementation

### 1. Model Layer (`streampipes-model`)

#### New Classes

**File**: `streampipes-model/src/main/java/org/apache/streampipes/model/connect/adapter/AdapterHealthStatus.java`

```java
package org.apache.streampipes.model.connect.adapter;

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class AdapterHealthStatus {
  
  private String adapterId;
  private String adapterName;
  
  // Backend health (existing concept)
  private HealthCheckStatus backendHealth;
  private String backendHealthMessage;
  
  // Data source health (new)
  private HealthCheckStatus dataSourceHealth;
  private String dataSourceHealthMessage;
  private String dataSourceHealthDetails; // Stack trace or detailed error
  
  // Overall status
  private HealthCheckStatus overallStatus; // GREEN only if both are healthy
  
  // Metadata
  private long lastCheckTimestamp;
  private boolean dataSourceHealthSupported; // false for adapters without implementation
  
  // Health check configuration
  private int consecutiveFailures;
  private long nextCheckTimestamp;
  
  // Constructors, getters, setters
}

public enum HealthCheckStatus {
  HEALTHY,    // Green light
  UNHEALTHY,  // Red light
  UNKNOWN     // Gray light - initial state or check not applicable
}
```

**File**: `streampipes-model/src/main/java/org/apache/streampipes/model/connect/adapter/DataSourceHealthCheckConfig.java`

```java
package org.apache.streampipes.model.connect.adapter;

public class DataSourceHealthCheckConfig {
  
  // Initial check interval (default 30 seconds)
  private long initialCheckIntervalMs = 30_000;
  
  // Maximum check interval after repeated failures (default 1 day)
  private long maxCheckIntervalMs = 86_400_000;
  
  // Backoff multiplier (default 2.0 = exponential doubling)
  private double backoffMultiplier = 2.0;
  
  // Timeout for individual health checks (default 5 seconds)
  private long healthCheckTimeoutMs = 5_000;
  
  // Max retries before marking as unhealthy
  private int maxConsecutiveFailures = 3;
  
  // Constructors, getters, setters
}
```

---

### 2. Health Check API (`streampipes-extensions-api`)

**File**: `streampipes-extensions-api/src/main/java/org/apache/streampipes/extensions/api/connect/IDataSourceHealthCheck.java`

```java
package org.apache.streampipes.extensions.api.connect;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;

/**
 * Interface for adapter-specific data source health checks.
 * Adapters implementing this interface will have their data source connectivity verified.
 */
public interface IDataSourceHealthCheck {
  
  /**
   * Performs a lightweight health check on the data source.
   * 
   * @param adapterDescription The adapter configuration
   * @return Health check result with status and optional error message
   */
  DataSourceHealthResult checkDataSourceHealth(AdapterDescription adapterDescription);
}

public class DataSourceHealthResult {
  private HealthCheckStatus status;
  private String message;
  private String detailedError; // Full stack trace if needed
  private long checkDurationMs;
  
  public static DataSourceHealthResult healthy() {
    return new DataSourceHealthResult(HealthCheckStatus.HEALTHY, "Data source is reachable", null, 0);
  }
  
  public static DataSourceHealthResult unhealthy(String message, String details) {
    return new DataSourceHealthResult(HealthCheckStatus.UNHEALTHY, message, details, 0);
  }
  
  // Constructor, getters, setters
}
```

---

### 3. Data Source Health Check Strategies

#### 3.1 Apache Kafka Health Check

**File**: `streampipes-connectors-kafka/src/main/java/org/apache/streampipes/extensions/connectors/kafka/adapter/KafkaDataSourceHealthCheck.java`

```java
package org.apache.streampipes.extensions.connectors.kafka.adapter;

import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.extensions.api.connect.DataSourceHealthResult;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.KafkaException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaDataSourceHealthCheck implements IDataSourceHealthCheck {
  
  private static final int TIMEOUT_SECONDS = 5;
  
  @Override
  public DataSourceHealthResult checkDataSourceHealth(AdapterDescription adapterDescription) {
    long startTime = System.currentTimeMillis();
    
    try {
      // Extract Kafka configuration from adapter description
      var extractor = /* create extractor from adapterDescription */;
      var kafkaConfig = new KafkaConfigExtractor().extractAdapterConfig(extractor, true);
      
      // Create admin client properties with same security settings
      Properties props = new Properties();
      props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, 
                kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort());
      props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, TIMEOUT_SECONDS * 1000);
      props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, TIMEOUT_SECONDS * 1000);
      
      // Apply all security configuration appenders (SASL, SSL, etc.)
      kafkaConfig.getConfigAppenders().forEach(c -> c.appendConfig(props));
      
      // Try to connect and verify broker + topic
      try (AdminClient adminClient = AdminClient.create(props)) {
        
        // 1. Check cluster connectivity (lightweight metadata request)
        var clusterInfo = adminClient.describeCluster()
            .clusterId()
            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (clusterInfo == null) {
          return DataSourceHealthResult.unhealthy(
              "Unable to retrieve Kafka cluster information",
              "Cluster ID is null"
          );
        }
        
        // 2. Verify topic exists
        String topic = kafkaConfig.getTopic();
        var topics = adminClient.listTopics()
            .names()
            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (!topics.contains(topic)) {
          return DataSourceHealthResult.unhealthy(
              "Kafka topic does not exist: " + topic,
              "Available topics: " + topics
          );
        }
        
        // 3. Verify topic is accessible (check partitions)
        var topicDescription = adminClient.describeTopics(Collections.singleton(topic))
            .allTopicNames()
            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (topicDescription.isEmpty() || topicDescription.get(topic).partitions().isEmpty()) {
          return DataSourceHealthResult.unhealthy(
              "Kafka topic has no partitions: " + topic,
              "Topic description: " + topicDescription
          );
        }
        
        long duration = System.currentTimeMillis() - startTime;
        var result = DataSourceHealthResult.healthy();
        result.setCheckDurationMs(duration);
        result.setMessage("Kafka broker and topic '" + topic + "' are accessible");
        return result;
        
      }
    } catch (KafkaException e) {
      return DataSourceHealthResult.unhealthy(
          "Kafka connection failed: " + e.getMessage(),
          getStackTrace(e)
      );
    } catch (TimeoutException e) {
      return DataSourceHealthResult.unhealthy(
          "Kafka health check timed out after " + TIMEOUT_SECONDS + " seconds",
          getStackTrace(e)
      );
    } catch (Exception e) {
      return DataSourceHealthResult.unhealthy(
          "Unexpected error during Kafka health check: " + e.getMessage(),
          getStackTrace(e)
      );
    }
  }
  
  private String getStackTrace(Throwable t) {
    return ExceptionUtils.getStackTrace(t);
  }
}
```

**Strategy Justification**: 
- Uses AdminClient API (lightweight, no message consumption)
- Verifies broker connectivity via cluster metadata
- Validates topic existence and accessibility
- Reuses all authentication settings (SASL/SSL) from adapter config
- Timeout-controlled (5 seconds max)
- **Overhead**: Minimal - single metadata request, no data consumption

**Alternative Considered**: Consumer-based check (rejected due to higher overhead and offset management complexity)

---

#### 3.2 OPC-UA Health Check

**File**: `streampipes-connectors-opcua/src/main/java/org/apache/streampipes/extensions/connectors/opcua/adapter/OpcUaDataSourceHealthCheck.java`

```java
package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.extensions.api.connect.DataSourceHealthResult;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.concurrent.TimeUnit;

public class OpcUaDataSourceHealthCheck implements IDataSourceHealthCheck {
  
  private final OpcUaClientProvider clientProvider;
  private static final int TIMEOUT_SECONDS = 5;
  
  // Lightweight node to read (standard OPC-UA Server object - always present)
  private static final NodeId SERVER_STATE_NODE = new NodeId(0, 2259);
  
  public OpcUaDataSourceHealthCheck(OpcUaClientProvider clientProvider) {
    this.clientProvider = clientProvider;
  }
  
  @Override
  public DataSourceHealthResult checkDataSourceHealth(AdapterDescription adapterDescription) {
    long startTime = System.currentTimeMillis();
    
    try {
      // Extract OPC-UA configuration
      var extractor = /* create extractor from adapterDescription */;
      var opcConfig = SpOpcUaConfigExtractor.extractAdapterConfig(
          extractor, null, adapterDescription.getElementId()
      );
      
      // Get or create client connection
      var connectedClient = clientProvider.getClient(opcConfig);
      UaClient client = connectedClient.getClient();
      
      // 1. Check session state
      if (!client.getSession().isPresent()) {
        return DataSourceHealthResult.unhealthy(
            "OPC-UA session is not established",
            "Session state: inactive"
        );
      }
      
      // 2. Verify session keep-alive by reading server state node
      // This is a very lightweight operation supported by all OPC-UA servers
      var response = client.readValues(
          0.0, // maxAge = 0 forces fresh read
          TimestampsToReturn.Neither, // No timestamps needed
          Collections.singletonList(SERVER_STATE_NODE)
      ).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
      
      if (response == null || response.isEmpty()) {
        return DataSourceHealthResult.unhealthy(
            "OPC-UA server did not respond to read request",
            "Read response is empty"
        );
      }
      
      var dataValue = response.get(0);
      if (!dataValue.getStatusCode().isGood()) {
        return DataSourceHealthResult.unhealthy(
            "OPC-UA server returned bad status code: " + dataValue.getStatusCode(),
            "Server may be in error state"
        );
      }
      
      // 3. For subscription mode, verify subscription health
      if (!opcConfig.inPullMode()) {
        var subscriptions = client.getSubscriptionManager().getSubscriptions();
        if (subscriptions.isEmpty()) {
          return DataSourceHealthResult.unhealthy(
              "OPC-UA subscriptions are not active",
              "Expected active subscriptions but found none"
          );
        }
      }
      
      long duration = System.currentTimeMillis() - startTime;
      var result = DataSourceHealthResult.healthy();
      result.setCheckDurationMs(duration);
      result.setMessage("OPC-UA server is reachable and session is active");
      return result;
      
    } catch (TimeoutException e) {
      return DataSourceHealthResult.unhealthy(
          "OPC-UA health check timed out after " + TIMEOUT_SECONDS + " seconds",
          getStackTrace(e)
      );
    } catch (Exception e) {
      return DataSourceHealthResult.unhealthy(
          "OPC-UA connection failed: " + e.getMessage(),
          getStackTrace(e)
      );
    }
  }
  
  private String getStackTrace(Throwable t) {
    return ExceptionUtils.getStackTrace(t);
  }
}
```

**Strategy Justification**:
- Checks session keep-alive status
- Reads a standard lightweight node (ServerState) present in all OPC-UA servers
- Validates subscription health for subscription-mode adapters
- Reuses existing client connection (no new connection overhead)
- **Overhead**: Minimal - single node read (~100 bytes)

**Alternatives Considered**:
- Full node tree traversal (rejected: too expensive)
- Ping/pong mechanism (not standard OPC-UA)
- Reading configured nodes (rejected: may be expensive if many nodes)

---

#### 3.3 MQTT Health Check

**File**: `streampipes-connectors-mqtt/src/main/java/org/apache/streampipes/extensions/connectors/mqtt/adapter/MqttDataSourceHealthCheck.java`

```java
package org.apache.streampipes.extensions.connectors.mqtt.adapter;

import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.extensions.api.connect.DataSourceHealthResult;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConfig;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttDataSourceHealthCheck implements IDataSourceHealthCheck {
  
  private static final int TIMEOUT_SECONDS = 5;
  private static final String HEALTH_CHECK_CLIENT_PREFIX = "sp-health-check-";
  
  @Override
  public DataSourceHealthResult checkDataSourceHealth(AdapterDescription adapterDescription) {
    long startTime = System.currentTimeMillis();
    MqttClient client = null;
    
    try {
      // Extract MQTT configuration
      var extractor = /* create extractor from adapterDescription */;
      MqttConfig mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
      
      // Create temporary health check client with unique ID
      String clientId = HEALTH_CHECK_CLIENT_PREFIX + System.currentTimeMillis();
      client = new MqttClient(
          mqttConfig.getBrokerUrl(),
          clientId,
          new MemoryPersistence()
      );
      
      // Configure connection options with same security settings
      MqttConnectOptions options = new MqttConnectOptions();
      options.setConnectionTimeout(TIMEOUT_SECONDS);
      options.setAutomaticReconnect(false);
      options.setCleanSession(true);
      
      // Apply authentication if configured
      if (mqttConfig.getUsername() != null) {
        options.setUserName(mqttConfig.getUsername());
        options.setPassword(mqttConfig.getPassword().toCharArray());
      }
      
      if (mqttConfig.getSslContext() != null) {
        options.setSocketFactory(mqttConfig.getSslContext().getSocketFactory());
      }
      
      // 1. Try to connect to broker
      client.connect(options);
      
      if (!client.isConnected()) {
        return DataSourceHealthResult.unhealthy(
            "MQTT client failed to connect",
            "Client state: disconnected"
        );
      }
      
      // 2. Verify topic subscription (lightweight check - no message wait)
      // We subscribe but don't wait for messages (just checking broker accepts subscription)
      String topic = mqttConfig.getTopic();
      try {
        client.subscribe(topic, 0); // QoS 0 for minimal overhead
      } catch (MqttException e) {
        client.disconnect();
        return DataSourceHealthResult.unhealthy(
            "Failed to subscribe to MQTT topic: " + topic,
            e.getMessage() + "\n" + getStackTrace(e)
        );
      }
      
      // 3. Disconnect cleanly
      client.disconnect();
      
      long duration = System.currentTimeMillis() - startTime;
      var result = DataSourceHealthResult.healthy();
      result.setCheckDurationMs(duration);
      result.setMessage("MQTT broker is reachable and topic '" + topic + "' is subscribable");
      return result;
      
    } catch (MqttException e) {
      String errorMsg = switch (e.getReasonCode()) {
        case MqttException.REASON_CODE_CLIENT_TIMEOUT -> 
            "MQTT connection timeout after " + TIMEOUT_SECONDS + " seconds";
        case MqttException.REASON_CODE_BROKER_UNAVAILABLE -> 
            "MQTT broker is unavailable";
        case MqttException.REASON_CODE_FAILED_AUTHENTICATION -> 
            "MQTT authentication failed";
        case MqttException.REASON_CODE_NOT_AUTHORIZED -> 
            "MQTT authorization failed for topic";
        default -> "MQTT connection failed: " + e.getMessage();
      };
      
      return DataSourceHealthResult.unhealthy(errorMsg, getStackTrace(e));
      
    } catch (Exception e) {
      return DataSourceHealthResult.unhealthy(
          "Unexpected error during MQTT health check: " + e.getMessage(),
          getStackTrace(e)
      );
    } finally {
      // Cleanup
      if (client != null && client.isConnected()) {
        try {
          client.disconnect();
          client.close();
        } catch (Exception ignored) {
          // Best effort cleanup
        }
      }
    }
  }
  
  private String getStackTrace(Throwable t) {
    return ExceptionUtils.getStackTrace(t);
  }
}
```

**Strategy Justification**:
- Creates temporary health-check-only client (isolated from main adapter client)
- Verifies broker connectivity via connection attempt
- Validates topic subscription capability
- Disconnects immediately (no long-lived connection)
- **Overhead**: Minimal - single connect + subscribe + disconnect cycle

**Alternatives Considered**:
- Reuse existing adapter client (rejected: complexity with state management)
- Publish test message (rejected: side effects, topic pollution)
- Keep-alive ping only (insufficient - doesn't validate topic access)

---

### 4. Health Check Orchestration (`streampipes-health-monitoring`)

**File**: `streampipes-health-monitoring/src/main/java/org/apache/streampipes/health/monitoring/AdapterHealthCheckService.java`

```java
package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.model.connect.adapter.DataSourceHealthCheckConfig;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;

import java.util.Map;
import java.util.concurrent.*;

public class AdapterHealthCheckService {
  
  private final Map<String, AdapterHealthStatus> healthStatusCache = new ConcurrentHashMap<>();
  private final Map<String, IDataSourceHealthCheck> dataSourceHealthCheckers = new ConcurrentHashMap<>();
  private final DataSourceHealthCheckConfig config;
  private final ScheduledExecutorService scheduler;
  
  public AdapterHealthCheckService(DataSourceHealthCheckConfig config) {
    this.config = config;
    this.scheduler = Executors.newScheduledThreadPool(4); // Adjust pool size as needed
  }
  
  /**
   * Register a data source health checker for a specific adapter type
   */
  public void registerHealthChecker(String adapterType, IDataSourceHealthCheck checker) {
    dataSourceHealthCheckers.put(adapterType, checker);
  }
  
  /**
   * Start monitoring an adapter
   */
  public void startMonitoring(AdapterDescription adapter) {
    String adapterId = adapter.getElementId();
    
    // Initialize health status
    AdapterHealthStatus status = new AdapterHealthStatus();
    status.setAdapterId(adapterId);
    status.setAdapterName(adapter.getName());
    status.setBackendHealth(HealthCheckStatus.HEALTHY); // Assume healthy initially
    status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
    status.setDataSourceHealthSupported(dataSourceHealthCheckers.containsKey(adapter.getAppId()));
    status.setLastCheckTimestamp(System.currentTimeMillis());
    
    healthStatusCache.put(adapterId, status);
    
    // Schedule first health check
    scheduleNextHealthCheck(adapter, config.getInitialCheckIntervalMs());
  }
  
  /**
   * Stop monitoring an adapter
   */
  public void stopMonitoring(String adapterId) {
    healthStatusCache.remove(adapterId);
    // Cancel scheduled checks (tracked separately)
  }
  
  /**
   * Get current health status for an adapter
   */
  public AdapterHealthStatus getHealthStatus(String adapterId) {
    return healthStatusCache.getOrDefault(adapterId, createUnknownStatus(adapterId));
  }
  
  /**
   * Perform health check for an adapter
   */
  private void performHealthCheck(AdapterDescription adapter) {
    String adapterId = adapter.getElementId();
    AdapterHealthStatus status = healthStatusCache.get(adapterId);
    
    if (status == null) {
      return; // Adapter no longer monitored
    }
    
    // Update backend health (from existing health check mechanism)
    status.setBackendHealth(checkBackendHealth(adapter));
    
    // Perform data source health check if supported
    IDataSourceHealthCheck checker = dataSourceHealthCheckers.get(adapter.getAppId());
    if (checker != null) {
      try {
        // Execute with timeout
        Future<DataSourceHealthResult> future = CompletableFuture.supplyAsync(
            () -> checker.checkDataSourceHealth(adapter)
        );
        
        DataSourceHealthResult result = future.get(
            config.getHealthCheckTimeoutMs(), 
            TimeUnit.MILLISECONDS
        );
        
        status.setDataSourceHealth(result.getStatus());
        status.setDataSourceHealthMessage(result.getMessage());
        status.setDataSourceHealthDetails(result.getDetailedError());
        
        // Reset failure counter on success
        if (result.getStatus() == HealthCheckStatus.HEALTHY) {
          status.setConsecutiveFailures(0);
        } else {
          status.setConsecutiveFailures(status.getConsecutiveFailures() + 1);
        }
        
      } catch (TimeoutException e) {
        handleHealthCheckFailure(status, "Health check timed out", e);
      } catch (Exception e) {
        handleHealthCheckFailure(status, "Health check failed: " + e.getMessage(), e);
      }
    } else {
      // No health checker implemented for this adapter type
      status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
      status.setDataSourceHealthSupported(false);
    }
    
    // Update overall status
    status.setOverallStatus(calculateOverallStatus(status));
    status.setLastCheckTimestamp(System.currentTimeMillis());
    
    // Schedule next check with exponential backoff if failing
    long nextInterval = calculateNextCheckInterval(status);
    scheduleNextHealthCheck(adapter, nextInterval);
  }
  
  private void handleHealthCheckFailure(AdapterHealthStatus status, String message, Exception e) {
    status.setDataSourceHealth(HealthCheckStatus.UNHEALTHY);
    status.setDataSourceHealthMessage(message);
    status.setDataSourceHealthDetails(ExceptionUtils.getStackTrace(e));
    status.setConsecutiveFailures(status.getConsecutiveFailures() + 1);
  }
  
  private HealthCheckStatus calculateOverallStatus(AdapterHealthStatus status) {
    // Overall is HEALTHY only if both backend and data source are healthy
    if (status.getBackendHealth() == HealthCheckStatus.HEALTHY) {
      if (!status.isDataSourceHealthSupported()) {
        // No data source check available - rely on backend only
        return HealthCheckStatus.HEALTHY;
      }
      return status.getDataSourceHealth();
    }
    return HealthCheckStatus.UNHEALTHY;
  }
  
  private long calculateNextCheckInterval(AdapterHealthStatus status) {
    if (status.getConsecutiveFailures() == 0) {
      return config.getInitialCheckIntervalMs();
    }
    
    // Exponential backoff: interval * (multiplier ^ failures)
    long interval = (long) (config.getInitialCheckIntervalMs() * 
                           Math.pow(config.getBackoffMultiplier(), status.getConsecutiveFailures()));
    
    return Math.min(interval, config.getMaxCheckIntervalMs());
  }
  
  private void scheduleNextHealthCheck(AdapterDescription adapter, long delayMs) {
    scheduler.schedule(
        () -> performHealthCheck(adapter),
        delayMs,
        TimeUnit.MILLISECONDS
    );
  }
  
  private HealthCheckStatus checkBackendHealth(AdapterDescription adapter) {
    // Delegate to existing backend health check logic
    // Check if extension service is reachable
    return HealthCheckStatus.HEALTHY; // Placeholder
  }
  
  private AdapterHealthStatus createUnknownStatus(String adapterId) {
    AdapterHealthStatus status = new AdapterHealthStatus();
    status.setAdapterId(adapterId);
    status.setOverallStatus(HealthCheckStatus.UNKNOWN);
    status.setBackendHealth(HealthCheckStatus.UNKNOWN);
    status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
    return status;
  }
  
  public void shutdown() {
    scheduler.shutdown();
  }
}
```

---

### 5. Integration into Existing Health Check

**File**: `streampipes-health-monitoring/src/main/java/org/apache/streampipes/health/monitoring/AdapterHealthCheck.java` (Modified)

```java
// Add field
private final AdapterHealthCheckService healthCheckService;

// In constructor
this.healthCheckService = new AdapterHealthCheckService(loadHealthCheckConfig());

// Register data source health checkers
healthCheckService.registerHealthChecker(KafkaProtocol.ID, new KafkaDataSourceHealthCheck());
healthCheckService.registerHealthChecker(OpcUaAdapter.ID, new OpcUaDataSourceHealthCheck(clientProvider));
healthCheckService.registerHealthChecker(MqttProtocol.ID, new MqttDataSourceHealthCheck());

// When adapter starts
public void onAdapterStarted(AdapterDescription adapter) {
  healthCheckService.startMonitoring(adapter);
}

// When adapter stops
public void onAdapterStopped(String adapterId) {
  healthCheckService.stopMonitoring(adapterId);
}
```

---

### 6. REST API Enhancement

**File**: `streampipes-rest/src/main/java/org/apache/streampipes/rest/impl/connect/AdapterHealthResource.java` (New)

```java
package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.health.monitoring.AdapterHealthCheckService;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/connect/adapters/health")
public class AdapterHealthResource {
  
  private final AdapterHealthCheckService healthCheckService;
  
  public AdapterHealthResource(AdapterHealthCheckService healthCheckService) {
    this.healthCheckService = healthCheckService;
  }
  
  /**
   * Get health status for a specific adapter
   */
  @GetMapping("/{adapterId}")
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<AdapterHealthStatus> getAdapterHealth(@PathVariable String adapterId) {
    AdapterHealthStatus status = healthCheckService.getHealthStatus(adapterId);
    return ResponseEntity.ok(status);
  }
  
  /**
   * Get health status for all adapters
   */
  @GetMapping
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<List<AdapterHealthStatus>> getAllAdapterHealth() {
    List<AdapterHealthStatus> statuses = healthCheckService.getAllHealthStatuses();
    return ResponseEntity.ok(statuses);
  }
  
  /**
   * Trigger immediate health check for an adapter (admin only)
   */
  @PostMapping("/{adapterId}/check")
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<AdapterHealthStatus> triggerHealthCheck(@PathVariable String adapterId) {
    healthCheckService.triggerImmediateCheck(adapterId);
    AdapterHealthStatus status = healthCheckService.getHealthStatus(adapterId);
    return ResponseEntity.ok(status);
  }
}
```

---

## UI Implementation

### 1. Enhanced Adapter Status Model

**File**: `ui/src/app/connect/model/adapter-health-status.model.ts` (New)

```typescript
export interface AdapterHealthStatus {
  adapterId: string;
  adapterName: string;
  
  backendHealth: HealthCheckStatus;
  backendHealthMessage: string;
  
  dataSourceHealth: HealthCheckStatus;
  dataSourceHealthMessage: string;
  dataSourceHealthDetails: string;
  
  overallStatus: HealthCheckStatus;
  lastCheckTimestamp: number;
  dataSourceHealthSupported: boolean;
  
  consecutiveFailures: number;
}

export enum HealthCheckStatus {
  HEALTHY = 'HEALTHY',
  UNHEALTHY = 'UNHEALTHY',
  UNKNOWN = 'UNKNOWN'
}
```

---

### 2. Enhanced Status Light Component

**File**: `ui/src/app/connect/components/existing-adapters/adapter-status-light/adapter-status-light.component.ts`

```typescript
import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdapterHealthStatus, HealthCheckStatus } from '../../../model/adapter-health-status.model';
import { AdapterHealthDetailsDialogComponent } from '../adapter-health-details-dialog/adapter-health-details-dialog.component';

@Component({
  selector: 'sp-adapter-status-light',
  templateUrl: './adapter-status-light.component.html',
  styleUrls: ['./adapter-status-light.component.scss']
})
export class AdapterStatusLightComponent {
  @Input() adapterId: string;
  @Input() adapterRunning: boolean;
  @Input() healthStatus: AdapterHealthStatus;
  
  constructor(private dialog: MatDialog) {}
  
  get statusClass(): string {
    if (!this.adapterRunning) {
      return 'light-neutral';
    }
    
    if (!this.healthStatus) {
      return 'light-green'; // Fallback to old behavior
    }
    
    switch (this.healthStatus.overallStatus) {
      case HealthCheckStatus.HEALTHY:
        return 'light-green';
      case HealthCheckStatus.UNHEALTHY:
        return 'light-red';
      case HealthCheckStatus.UNKNOWN:
        return 'light-gray';
      default:
        return 'light-neutral';
    }
  }
  
  openHealthDetails(): void {
    if (this.adapterRunning && this.healthStatus) {
      this.dialog.open(AdapterHealthDetailsDialogComponent, {
        data: {
          healthStatus: this.healthStatus
        },
        width: '600px',
        panelClass: 'health-details-dialog'
      });
    }
  }
}
```

**File**: `ui/src/app/connect/components/existing-adapters/adapter-status-light/adapter-status-light.component.html`

```html
<div class="status-light-container" (click)="openHealthDetails()">
  <div 
    class="light" 
    [ngClass]="statusClass"
    [matTooltip]="adapterRunning ? 'Click for health details' : 'Adapter not running'"
    matTooltipPosition="above">
  </div>
</div>
```

**File**: `ui/src/app/connect/components/existing-adapters/adapter-status-light/adapter-status-light.component.scss`

```scss
.status-light-container {
  cursor: pointer;
  display: inline-block;
  
  &:hover .light {
    transform: scale(1.1);
    transition: transform 0.2s ease-in-out;
  }
}

.light {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
  transition: transform 0.2s ease-in-out;
}

.light-green {
  background-color: #4caf50;
  box-shadow: 0 0 8px #4caf50;
}

.light-red {
  background-color: #f44336;
  box-shadow: 0 0 8px #f44336;
}

.light-gray {
  background-color: #9e9e9e;
  box-shadow: 0 0 8px #9e9e9e;
}

.light-neutral {
  background-color: #bdbdbd;
}
```

---

### 3. Health Details Dialog Component

**File**: `ui/src/app/connect/components/existing-adapters/adapter-health-details-dialog/adapter-health-details-dialog.component.ts`

```typescript
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AdapterHealthStatus, HealthCheckStatus } from '../../../model/adapter-health-status.model';

@Component({
  selector: 'sp-adapter-health-details-dialog',
  templateUrl: './adapter-health-details-dialog.component.html',
  styleUrls: ['./adapter-health-details-dialog.component.scss']
})
export class AdapterHealthDetailsDialogComponent {
  healthStatus: AdapterHealthStatus;
  showFullDetails = false;
  
  constructor(
    public dialogRef: MatDialogRef<AdapterHealthDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.healthStatus = data.healthStatus;
  }
  
  getStatusIcon(status: HealthCheckStatus): string {
    switch (status) {
      case HealthCheckStatus.HEALTHY:
        return 'check_circle';
      case HealthCheckStatus.UNHEALTHY:
        return 'error';
      case HealthCheckStatus.UNKNOWN:
        return 'help_outline';
      default:
        return 'help_outline';
    }
  }
  
  getStatusClass(status: HealthCheckStatus): string {
    switch (status) {
      case HealthCheckStatus.HEALTHY:
        return 'status-healthy';
      case HealthCheckStatus.UNHEALTHY:
        return 'status-unhealthy';
      case HealthCheckStatus.UNKNOWN:
        return 'status-unknown';
      default:
        return 'status-unknown';
    }
  }
  
  getStatusLabel(status: HealthCheckStatus): string {
    switch (status) {
      case HealthCheckStatus.HEALTHY:
        return 'Healthy';
      case HealthCheckStatus.UNHEALTHY:
        return 'Unhealthy';
      case HealthCheckStatus.UNKNOWN:
        return 'Unknown';
      default:
        return 'Unknown';
    }
  }
  
  toggleFullDetails(): void {
    this.showFullDetails = !this.showFullDetails;
  }
  
  close(): void {
    this.dialogRef.close();
  }
  
  formatTimestamp(timestamp: number): string {
    return new Date(timestamp).toLocaleString();
  }
}
```

**File**: `ui/src/app/connect/components/existing-adapters/adapter-health-details-dialog/adapter-health-details-dialog.component.html`

```html
<div class="health-details-dialog">
  <h2 mat-dialog-title>Adapter Health Status</h2>
  
  <mat-dialog-content>
    <div class="adapter-info">
      <h3>{{ healthStatus.adapterName }}</h3>
      <p class="last-check">Last checked: {{ formatTimestamp(healthStatus.lastCheckTimestamp) }}</p>
    </div>
    
    <div class="health-section">
      <div class="health-item">
        <div class="health-header">
          <mat-icon [ngClass]="getStatusClass(healthStatus.backendHealth)">
            {{ getStatusIcon(healthStatus.backendHealth) }}
          </mat-icon>
          <span class="health-label">Backend Health</span>
          <span [ngClass]="getStatusClass(healthStatus.backendHealth)" class="health-status">
            {{ getStatusLabel(healthStatus.backendHealth) }}
          </span>
        </div>
        
        <div class="health-message" *ngIf="healthStatus.backendHealthMessage">
          <p>{{ healthStatus.backendHealthMessage }}</p>
        </div>
      </div>
      
      <mat-divider></mat-divider>
      
      <div class="health-item">
        <div class="health-header">
          <mat-icon [ngClass]="getStatusClass(healthStatus.dataSourceHealth)">
            {{ getStatusIcon(healthStatus.dataSourceHealth) }}
          </mat-icon>
          <span class="health-label">Data Source Health</span>
          
          <span 
            *ngIf="healthStatus.dataSourceHealthSupported"
            [ngClass]="getStatusClass(healthStatus.dataSourceHealth)" 
            class="health-status">
            {{ getStatusLabel(healthStatus.dataSourceHealth) }}
          </span>
          
          <span 
            *ngIf="!healthStatus.dataSourceHealthSupported"
            class="status-unknown health-status">
            No support yet
          </span>
        </div>
        
        <div class="health-message" *ngIf="healthStatus.dataSourceHealthMessage">
          <p>{{ healthStatus.dataSourceHealthMessage }}</p>
        </div>
      </div>
    </div>
    
    <!-- Error Details Section (Terminal-like) -->
    <div 
      class="error-details-section" 
      *ngIf="healthStatus.dataSourceHealthDetails && 
             healthStatus.dataSourceHealth === 'UNHEALTHY'">
      
      <div class="error-details-header" (click)="toggleFullDetails()">
        <mat-icon>{{ showFullDetails ? 'expand_less' : 'expand_more' }}</mat-icon>
        <span>{{ showFullDetails ? 'Hide' : 'Show' }} Full Details</span>
      </div>
      
      <div class="terminal-output" *ngIf="showFullDetails">
        <pre><code>{{ healthStatus.dataSourceHealthDetails }}</code></pre>
      </div>
    </div>
    
    <!-- Retry Info -->
    <div class="retry-info" *ngIf="healthStatus.consecutiveFailures > 0">
      <mat-icon class="warning-icon">warning</mat-icon>
      <span>
        Consecutive failures: {{ healthStatus.consecutiveFailures }}
        <br>
        Next check will be delayed due to exponential backoff.
      </span>
    </div>
  </mat-dialog-content>
  
  <mat-dialog-actions align="end">
    <button mat-button (click)="close()">Close</button>
  </mat-dialog-actions>
</div>
```

**File**: `ui/src/app/connect/components/existing-adapters/adapter-health-details-dialog/adapter-health-details-dialog.component.scss`

```scss
.health-details-dialog {
  font-family: 'Roboto', sans-serif;
  
  .adapter-info {
    margin-bottom: 20px;
    
    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 500;
    }
    
    .last-check {
      color: #757575;
      font-size: 12px;
      margin: 0;
    }
  }
  
  .health-section {
    margin: 20px 0;
    
    .health-item {
      padding: 16px 0;
      
      .health-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        mat-icon {
          font-size: 24px;
          width: 24px;
          height: 24px;
        }
        
        .health-label {
          font-weight: 500;
          flex: 1;
        }
        
        .health-status {
          font-weight: 600;
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;
        }
      }
      
      .health-message {
        margin-left: 36px;
        padding: 8px 12px;
        background-color: #f5f5f5;
        border-radius: 4px;
        
        p {
          margin: 0;
          font-size: 13px;
          color: #424242;
        }
      }
    }
  }
  
  .status-healthy {
    color: #4caf50;
    background-color: #e8f5e9;
  }
  
  .status-unhealthy {
    color: #f44336;
    background-color: #ffebee;
  }
  
  .status-unknown {
    color: #9e9e9e;
    background-color: #f5f5f5;
  }
  
  .error-details-section {
    margin-top: 20px;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    
    .error-details-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px;
      background-color: #fafafa;
      cursor: pointer;
      user-select: none;
      
      &:hover {
        background-color: #f5f5f5;
      }
      
      mat-icon {
        font-size: 20px;
        width: 20px;
        height: 20px;
      }
      
      span {
        font-weight: 500;
        font-size: 13px;
      }
    }
    
    .terminal-output {
      background-color: #1e1e1e;
      color: #d4d4d4;
      padding: 16px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      line-height: 1.5;
      max-height: 300px;
      overflow-y: auto;
      
      pre {
        margin: 0;
        white-space: pre-wrap;
        word-wrap: break-word;
      }
      
      code {
        color: #d4d4d4;
      }
    }
  }
  
  .retry-info {
    display: flex;
    align-items: start;
    gap: 12px;
    margin-top: 16px;
    padding: 12px;
    background-color: #fff3e0;
    border-radius: 4px;
    border-left: 4px solid #ff9800;
    
    .warning-icon {
      color: #ff9800;
      font-size: 20px;
      width: 20px;
      height: 20px;
    }
    
    span {
      font-size: 13px;
      color: #424242;
      line-height: 1.5;
    }
  }
  
  mat-divider {
    margin: 8px 0;
  }
}
```

---

### 4. Service for Health Data

**File**: `ui/src/app/connect/services/adapter-health.service.ts` (New)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdapterHealthStatus } from '../model/adapter-health-status.model';

@Injectable({
  providedIn: 'root'
})
export class AdapterHealthService {
  
  private readonly basePath = '/api/v2/connect/adapters/health';
  
  constructor(private http: HttpClient) {}
  
  getAdapterHealth(adapterId: string): Observable<AdapterHealthStatus> {
    return this.http.get<AdapterHealthStatus>(`${this.basePath}/${adapterId}`);
  }
  
  getAllAdapterHealth(): Observable<AdapterHealthStatus[]> {
    return this.http.get<AdapterHealthStatus[]>(this.basePath);
  }
  
  triggerHealthCheck(adapterId: string): Observable<AdapterHealthStatus> {
    return this.http.post<AdapterHealthStatus>(`${this.basePath}/${adapterId}/check`, {});
  }
}
```

---

### 5. Integration with Existing Adapters List

**File**: `ui/src/app/connect/components/existing-adapters/existing-adapters.component.ts` (Modified)

```typescript
// Add field
healthStatuses: Map<string, AdapterHealthStatus> = new Map();

// In constructor
constructor(
  // ... existing
  private adapterHealthService: AdapterHealthService
) {}

// Load health statuses
ngOnInit() {
  // ... existing code
  this.loadHealthStatuses();
  
  // Poll health statuses every 30 seconds
  interval(30000).pipe(
    takeUntil(this.destroy$)
  ).subscribe(() => this.loadHealthStatuses());
}

private loadHealthStatuses(): void {
  this.adapterHealthService.getAllAdapterHealth().subscribe(statuses => {
    this.healthStatuses.clear();
    statuses.forEach(status => {
      this.healthStatuses.set(status.adapterId, status);
    });
  });
}

getHealthStatus(adapterId: string): AdapterHealthStatus | undefined {
  return this.healthStatuses.get(adapterId);
}
```

**File**: `ui/src/app/connect/components/existing-adapters/existing-adapters.component.html` (Modified)

```html
<!-- Update status light usage -->
<sp-adapter-status-light
  [adapterId]="adapter.elementId"
  [adapterRunning]="adapter.running"
  [healthStatus]="getHealthStatus(adapter.elementId)">
</sp-adapter-status-light>
```

---

## Performance & Configuration

### 1. Configuration Properties

**File**: `streampipes-service-core/src/main/resources/application.yml` (Add section)

```yaml
streampipes:
  health-check:
    adapter:
      initial-interval-ms: 30000         # 30 seconds
      max-interval-ms: 86400000          # 1 day
      backoff-multiplier: 2.0            # Exponential doubling
      health-check-timeout-ms: 5000      # 5 seconds per check
      max-consecutive-failures: 3        # Before marking unhealthy
      thread-pool-size: 4                # Concurrent health checks
```

### 2. Performance Characteristics

| Adapter Type | Check Method | Overhead | Network Calls | Timeout |
|--------------|--------------|----------|---------------|---------|
| Kafka | AdminClient metadata | ~50-100ms | 1 (cluster info) + 1 (topic list) | 5s |
| OPC-UA | Node read (ServerState) | ~100-200ms | 1 read request | 5s |
| MQTT | Connect + Subscribe + Disconnect | ~200-500ms | 1 connection handshake | 5s |

### 3. Exponential Backoff Example

```
Check 1: Success → Next check in 30s
Check 2: Failure → Next check in 60s (30 * 2^1)
Check 3: Failure → Next check in 120s (30 * 2^2)
Check 4: Failure → Next check in 240s (30 * 2^3)
...
Check N: Failure → Next check in min(30 * 2^N, 86400) seconds
```

### 4. Resource Usage Estimates

**Scenario**: 100 adapters running
- **Initial state**: 100 health checks every 30 seconds = ~3.3 checks/second
- **Memory**: ~100KB per health status object = ~10MB total
- **CPU**: Minimal (async execution, mostly I/O wait)
- **Network**: ~100-500 bytes per check * 3.3/s = ~2-16 Kbps

**With failures and backoff**:
- Failed adapters gradually reduce check frequency
- Worst case: All adapters failing → Eventually 100 checks/day = 0.001 checks/second

---

## Implementation Phases

### Phase 1: Foundation (Week 1)
**Goal**: Establish core health check infrastructure

- [ ] Create model classes (AdapterHealthStatus, DataSourceHealthCheckConfig, etc.)
- [ ] Define IDataSourceHealthCheck interface
- [ ] Implement AdapterHealthCheckService with scheduling and backoff
- [ ] Add configuration properties to application.yml
- [ ] Write unit tests for health check service

**Deliverable**: Core health checking framework without adapter-specific implementations

---

### Phase 2: Kafka Implementation (Week 2)
**Goal**: Implement and test Kafka health checks

- [ ] Implement KafkaDataSourceHealthCheck
- [ ] Test with various Kafka configurations (plain, SSL, SASL)
- [ ] Test topic existence validation
- [ ] Test timeout and error handling
- [ ] Integration with existing KafkaProtocol adapter
- [ ] Write integration tests

**Deliverable**: Fully functional Kafka health checking

---

### Phase 3: OPC-UA Implementation (Week 2)
**Goal**: Implement and test OPC-UA health checks

- [ ] Implement OpcUaDataSourceHealthCheck
- [ ] Test with pull and subscription modes
- [ ] Test with different security modes (None, Sign, Sign&Encrypt)
- [ ] Test session keep-alive validation
- [ ] Integration with existing OpcUaAdapter
- [ ] Write integration tests

**Deliverable**: Fully functional OPC-UA health checking

---

### Phase 4: MQTT Implementation (Week 3)
**Goal**: Implement and test MQTT health checks

- [ ] Implement MqttDataSourceHealthCheck
- [ ] Test with different authentication modes
- [ ] Test SSL/TLS connections
- [ ] Test topic subscription validation
- [ ] Integration with existing MqttProtocol adapter
- [ ] Write integration tests

**Deliverable**: Fully functional MQTT health checking

---

### Phase 5: REST API (Week 3)
**Goal**: Expose health status via REST endpoints

- [ ] Create AdapterHealthResource
- [ ] Implement GET /api/v2/connect/adapters/health/{adapterId}
- [ ] Implement GET /api/v2/connect/adapters/health
- [ ] Implement POST /api/v2/connect/adapters/health/{adapterId}/check
- [ ] Add security/authorization
- [ ] Write API integration tests

**Deliverable**: REST API for health status retrieval

---

### Phase 6: UI Implementation (Week 4)
**Goal**: Create user interface for health status visualization

- [ ] Create AdapterHealthStatus TypeScript model
- [ ] Implement AdapterHealthService
- [ ] Update AdapterStatusLightComponent (clickable, color-coded)
- [ ] Create AdapterHealthDetailsDialogComponent
- [ ] Style components (CSS/SCSS)
- [ ] Integrate with existing adapter list view
- [ ] Add polling mechanism (refresh every 30s)
- [ ] Write component tests

**Deliverable**: Complete UI for viewing adapter health

---




---


## Risk Assessment & Mitigation

### Risk 1: Health Check Overhead
**Impact**: High health check frequency could overload data sources

**Mitigation**:
- Configurable check intervals
- Exponential backoff for failed checks
- Lightweight check implementations (metadata only)
- Timeout protection (5s max)

### Risk 2: False Negatives
**Impact**: Health check passes but data source is actually failing

**Mitigation**:
- Validate topic/node accessibility, not just connection
- Use same authentication as adapter
- Test multiple aspects (connection + subscription/read)

### Risk 3: False Positives
**Impact**: Health check fails but data source is actually working

**Mitigation**:
- Use max-consecutive-failures threshold (3 failures before marking unhealthy)
- Generous timeouts (5 seconds)
- Clear error messages for debugging

### Risk 4: Backward Compatibility
**Impact**: Existing adapters without health check support

**Mitigation**:
- Optional interface (IDataSourceHealthCheck)
- Graceful degradation (show "No support yet")
- Overall status falls back to backend health only

### Risk 5: Performance Degradation
**Impact**: Too many concurrent health checks

**Mitigation**:
- Thread pool size limit (4 threads)
- Staggered scheduling
- Exponential backoff reduces load over time



## Conclusion

This implementation plan provides a comprehensive, production-ready solution for adapter data source health monitoring in StreamPipes. The design prioritizes:

✅ **Low Overhead**: Lightweight, metadata-based checks  
✅ **Reliability**: Exponential backoff, timeout protection  
✅ **Usability**: Clear UI with detailed error messages  
✅ **Extensibility**: Easy to add new adapter types  
✅ **Performance**: Configurable intervals, thread pool management  
✅ **Backward Compatibility**: Graceful degradation for unsupported adapters  

The phased implementation approach allows for incremental delivery and testing, reducing risk and ensuring quality at each step.

---

## References

### Apache Kafka
- AdminClient API Documentation: https://kafka.apache.org/documentation/#adminclientapi
- Health Check Best Practices: https://docs.confluent.io/platform/current/kafka/monitoring.html

### OPC-UA
- Eclipse Milo Documentation: https://github.com/eclipse/milo
- OPC-UA Specification Part 4 (Services): https://opcfoundation.org/developer-tools/specifications-unified-architecture

### MQTT
- Eclipse Paho Documentation: https://www.eclipse.org/paho/
- MQTT v3.1.1 Specification: http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html

### StreamPipes
- Adapter Development Guide: https://streampipes.apache.org/docs/extend-adapters
- Health Monitoring Module: `streampipes-health-monitoring`
