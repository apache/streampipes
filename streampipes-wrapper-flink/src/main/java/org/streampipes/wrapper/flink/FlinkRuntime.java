/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.flink;

import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.streampipes.wrapper.flink.consumer.JmsConsumer;
import org.streampipes.wrapper.flink.converter.MapToEventConverter;
import org.streampipes.wrapper.flink.logger.StatisticLogger;
import org.streampipes.wrapper.flink.serializer.ByteArrayDeserializer;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.params.runtime.RuntimeParams;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class FlinkRuntime<RP extends RuntimeParams<B, I, RC>, B extends BindingParams<I>, I
        extends
        InvocableStreamPipesEntity, RC extends RuntimeContext> extends
        DistributedRuntime<RP, B, I, RC> implements Runnable, Serializable {

  private static final long serialVersionUID = 1L;

  protected TimeCharacteristic streamTimeCharacteristic;
  protected FlinkDeploymentConfig config;

  private boolean debug;
  private StreamExecutionEnvironment env;

  /**
   * @deprecated Use {@link #FlinkRuntime(BindingParams, boolean)} instead
   */
  @Deprecated
  public FlinkRuntime(B bindingParams) {
    this(bindingParams,true);
  }

  /**
   * @deprecated Use {@link #FlinkRuntime(BindingParams, boolean)} instead
   */
  @Deprecated
  public FlinkRuntime(B bindingParams, FlinkDeploymentConfig config) {
    this(bindingParams, config, false);
  }

  public FlinkRuntime(B bindingParams, boolean debug) {
    super(bindingParams);
    if (!debug) {
      this.config = getDeploymentConfig();
    } else {
      this.config = new FlinkDeploymentConfig("", "localhost", 6123);
    }
    this.debug = debug;
  }

  private FlinkRuntime(B bindingParams, FlinkDeploymentConfig config, boolean debug) {
    super(bindingParams);
    this.config = config;
    this.debug = debug;
  }

  public void run() {
    try {
      env.execute(bindingParams.getGraph().getElementId());

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
        } else {
          return getJmsConsumer((JmsTransportProtocol) protocol, dataFormatDefinition);
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
    return new JmsConsumer(protocol, spDataFormatDefinition);
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
    if (debug) {
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
      if (!debug) {
        boolean isDeployed = false;
        int count = 0;
        do {
          try {
            count++;
            Thread.sleep(1000);
            Optional<JobStatusMessage> statusMessageOpt =
                    getJobStatus(bindingParams.getGraph().getElementId());
            if (statusMessageOpt.isPresent()) {
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
      RestClusterClient<String> restClient = getRestClient();
      Optional<JobStatusMessage> jobStatusMessage =
              getJobStatus(bindingParams.getGraph().getElementId());
      if (jobStatusMessage.isPresent()) {
        restClient.cancel(jobStatusMessage.get().getJobId());
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

  private RestClusterClient<String> getRestClient() throws Exception {
    Configuration restConfig = new Configuration();
    restConfig.setString(JobManagerOptions.ADDRESS, config.getHost());
    restConfig.setInteger(JobManagerOptions.PORT, config.getPort());
    return new RestClusterClient<>(restConfig, "");
  }

  private Optional<JobStatusMessage> getJobStatus(String jobName) {
    try {
      RestClusterClient<String> restClient = getRestClient();
      CompletableFuture<Collection<JobStatusMessage>> jobs = restClient.listJobs();
      return jobs.get()
              .stream()
              .filter(j -> j.getJobName().equals(jobName) && j.getJobState().name().equals("RUNNING")).findFirst();

    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  protected abstract FlinkDeploymentConfig getDeploymentConfig();

  protected abstract void appendExecutionConfig(DataStream<Event>... convertedStream);


}
