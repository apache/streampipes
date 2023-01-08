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

package org.apache.streampipes.wrapper.flink;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.RuntimeContext;
import org.apache.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.apache.streampipes.wrapper.flink.consumer.JmsFlinkConsumer;
import org.apache.streampipes.wrapper.flink.consumer.MqttFlinkConsumer;
import org.apache.streampipes.wrapper.flink.converter.MapToEventConverter;
import org.apache.streampipes.wrapper.flink.logger.StatisticLogger;
import org.apache.streampipes.wrapper.flink.serializer.ByteArrayDeserializer;
import org.apache.streampipes.wrapper.params.binding.BindingParams;
import org.apache.streampipes.wrapper.params.runtime.RuntimeParams;

import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.MiniClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class FlinkRuntime<T extends RuntimeParams<V, W, X>, V extends BindingParams<W>, W
    extends
        InvocableStreamPipesEntity, X extends RuntimeContext> extends
    DistributedRuntime<T, V, W, X> implements Runnable, Serializable {

  private static final long serialVersionUID = 1L;

  protected TimeCharacteristic streamTimeCharacteristic;
  protected FlinkDeploymentConfig config;
  private StreamExecutionEnvironment env;

  public FlinkRuntime(V bindingParams,
                      ConfigExtractor configExtractor,
                      StreamPipesClient streamPipesClient) {
    super(bindingParams, configExtractor, streamPipesClient);
    this.config = getDeploymentConfig(configExtractor);
  }

  public void run() {
    try {
      if (!this.config.isMiniClusterMode()) {
        env.execute(bindingParams.getGraph().getElementId());
      } else {
        FlinkSpMiniCluster.INSTANCE.start();
        FlinkSpMiniCluster
            .INSTANCE
            .getClusterClient()
            .submitJob(env.getStreamGraph(bindingParams.getGraph().getElementId()).getJobGraph());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setStreamTimeCharacteristic(TimeCharacteristic streamTimeCharacteristic) {
    this.streamTimeCharacteristic = streamTimeCharacteristic;
  }

  private SourceFunction<Map<String, Object>> getStream1Source() {
    return getStreamSource(0);
  }

  private SourceFunction<Map<String, Object>> getStream2Source() {
    return getStreamSource(1);
  }

  /**
   * This method takes the i's input stream and creates a source for the flink graph
   * Currently just kafka is supported as a protocol
   * TODO Add also jms support
   *
   * @param i
   * @return
   */
  private SourceFunction<Map<String, Object>> getStreamSource(int i) {
    if (bindingParams.getGraph().getInputStreams().size() - 1 >= i) {

      SpDataStream stream = bindingParams.getGraph().getInputStreams().get(i);
      if (stream != null) {
        TransportProtocol protocol = stream.getEventGrounding().getTransportProtocol();
        TransportFormat format = stream.getEventGrounding().getTransportFormats().get(0);
        SpDataFormatDefinition dataFormatDefinition = getDataFormatDefinition(format);
        if (protocol instanceof KafkaTransportProtocol) {
          return getKafkaConsumer((KafkaTransportProtocol) protocol, dataFormatDefinition);
        } else if (protocol instanceof JmsTransportProtocol) {
          return getJmsConsumer((JmsTransportProtocol) protocol, dataFormatDefinition);
        } else if (protocol instanceof MqttTransportProtocol) {
          return getMqttConsumer((MqttTransportProtocol) protocol, dataFormatDefinition);
        } else {
          return null;
        }
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private SourceFunction<Map<String, Object>> getJmsConsumer(JmsTransportProtocol protocol,
                                                             SpDataFormatDefinition spDataFormatDefinition) {
    return new JmsFlinkConsumer(protocol, spDataFormatDefinition);
  }

  private SourceFunction<Map<String, Object>> getMqttConsumer(MqttTransportProtocol protocol,
                                                              SpDataFormatDefinition spDataFormatDefinition) {
    return new MqttFlinkConsumer(protocol, spDataFormatDefinition);
  }

  private SourceFunction<Map<String, Object>> getKafkaConsumer(KafkaTransportProtocol protocol,
                                                               SpDataFormatDefinition spDataFormatDefinition) {
    if (protocol.getTopicDefinition() instanceof SimpleTopicDefinition) {
      return new FlinkKafkaConsumer<>(protocol
          .getTopicDefinition()
          .getActualTopicName(), new ByteArrayDeserializer(spDataFormatDefinition), getProperties(protocol));
    } else {
      String patternTopic = replaceWildcardWithPatternFormat(protocol.getTopicDefinition().getActualTopicName());
      return new FlinkKafkaConsumer<>(Pattern.compile(patternTopic), new ByteArrayDeserializer(spDataFormatDefinition),
          getProperties(protocol));
    }
  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {
    if (config.isMiniClusterMode()) {
      this.env = StreamExecutionEnvironment.createLocalEnvironment();
    } else {
      this.env = StreamExecutionEnvironment
          .createRemoteEnvironment(config.getHost(), config.getPort(), config.getJarFile());
    }

    appendEnvironmentConfig(this.env);
    // Add the first source to the topology
    DataStream<Event> messageStream1;
    SourceFunction<Map<String, Object>> source1 = getStream1Source();
    if (source1 != null) {
      messageStream1 = addSource(source1, 0);
    } else {
      throw new SpRuntimeException("At least one source must be defined for a flink sepa");
    }

    SourceFunction<Map<String, Object>> source2 = getStream2Source();
    if (source2 != null) {
      DataStream<Event> messageStream2 = addSource(source2, 1);
      appendExecutionConfig(messageStream1, messageStream2);
    } else {
      appendExecutionConfig(messageStream1);
    }
  }

  private DataStream<Event> addSource(SourceFunction<Map<String, Object>> sourceFunction,
                                      Integer sourceIndex) {
    return env
        .addSource(sourceFunction)
        .flatMap(new MapToEventConverter<>(runtimeParams.getSourceInfo(sourceIndex).getSourceId(),
            runtimeParams))
        .flatMap(new StatisticLogger(getGraph()));
  }

  @Override
  public void postDiscard() throws SpRuntimeException {

  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    try {
      prepareRuntime();

      Thread thread = new Thread(this);
      thread.start();

      // TODO find a better solution
      // The loop waits until the job is deployed
      // When the deployment takes longer then 60 seconds it returns false
      // This check is not needed when the execution environment is st to local
      if (!config.isMiniClusterMode()) {
        boolean isDeployed = false;
        int count = 0;
        do {
          try {
            count++;
            Thread.sleep(1000);
            Optional<JobStatusMessage> statusMessageOpt =
                getJobStatus(bindingParams.getGraph().getElementId());
            if (statusMessageOpt.isPresent()
                && statusMessageOpt.get().getJobState().name().equals("RUNNING")) {
              isDeployed = true;
            }

          } catch (Exception e) {

          }
        } while (!isDeployed && count < 60);

        if (count == 60) {
          throw new SpRuntimeException("Error: Timeout reached when trying to connect to Flink Job Controller");
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new SpRuntimeException(e.getMessage());
    }
  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    try {
      ClusterClient<? extends Comparable<? extends Comparable<?>>> clusterClient = getClusterClient();
      Optional<JobStatusMessage> jobStatusMessage =
          getJobStatus(bindingParams.getGraph().getElementId());
      if (jobStatusMessage.isPresent()) {
        String jobStatusStr = jobStatusMessage.get().getJobState().name();
        // Cancel the job if running
        if (jobStatusStr.equals("RUNNING")) {
          clusterClient.cancel(jobStatusMessage.get().getJobId());
        }
        // else ignore, because job is already discarded
      } else {
        throw new SpRuntimeException("Could not stop Flink Job");
      }
    } catch (Exception e) {
      throw new SpRuntimeException("Could not find Flink Job Manager, is it running?");
    }
  }

  /**
   * This method can be called in case additional environment settings should be applied to the runtime.
   *
   * @param env The Stream Execution environment
   */
  public void appendEnvironmentConfig(StreamExecutionEnvironment env) {
    //This sets the stream time characteristics
    //The default value is TimeCharacteristic.ProcessingTime
    if (this.streamTimeCharacteristic != null) {
      env.setStreamTimeCharacteristic(this.streamTimeCharacteristic);
      env.setParallelism(1);
    }
  }

  private ClusterClient<? extends Comparable<? extends Comparable<?>>> getClusterClient() throws Exception {
    if (config.isMiniClusterMode()) {
      return getMiniClusterClient();
    } else {
      return getRestClusterClient();
    }
  }

  private MiniClusterClient getMiniClusterClient() throws Exception {
    return FlinkSpMiniCluster.INSTANCE.getClusterClient();
  }

  private RestClusterClient<String> getRestClusterClient() throws Exception {
    Configuration restConfig = new Configuration();
    restConfig.setString(JobManagerOptions.ADDRESS, config.getHost());
    restConfig.setInteger(JobManagerOptions.PORT, config.getPort());
    restConfig.setInteger(RestOptions.PORT, config.getPort());
    return new RestClusterClient<>(restConfig, "");
  }

  private Optional<JobStatusMessage> getJobStatus(String jobName) {
    try {
      ClusterClient<? extends Comparable<? extends Comparable<?>>> clusterClient = getClusterClient();
      CompletableFuture<Collection<JobStatusMessage>> jobs = clusterClient.listJobs();
      Collection<JobStatusMessage> jobsFound = jobs.get();

      // First, find a job with Running status
      Optional<JobStatusMessage> job = jobsFound.stream()
          .filter(j -> j.getJobName().equals(jobName) && j.getJobState().name().equals("RUNNING")).findFirst();
      if (job.isPresent()) {
        return job;
      }

      // Otherwise return job with any other status
      return jobs.get()
          .stream()
          .filter(j -> j.getJobName().equals(jobName)).findFirst();

    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  protected abstract FlinkDeploymentConfig getDeploymentConfig(ConfigExtractor configExtractor);

  protected abstract void appendExecutionConfig(DataStream<Event>... convertedStream);


}
