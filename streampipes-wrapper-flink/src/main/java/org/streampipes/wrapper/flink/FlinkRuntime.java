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

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.distributed.runtime.DistributedRuntime;
import org.streampipes.wrapper.flink.consumer.JmsConsumer;
import org.streampipes.wrapper.flink.converter.JsonToMapFormat;
import org.streampipes.wrapper.flink.logger.StatisticLogger;
import org.streampipes.wrapper.params.binding.BindingParams;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class FlinkRuntime<B extends BindingParams<I>, I extends InvocableStreamPipesEntity> extends
        DistributedRuntime<B, I> implements Runnable, Serializable {

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

  protected abstract FlinkDeploymentConfig getDeploymentConfig();

  protected abstract void appendExecutionConfig(DataStream<Map<String, Object>>... convertedStream);

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

  private SourceFunction<String> getStream1Source() {
    return getStreamSource(0);
  }

  private SourceFunction<String> getStream2Source() {
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
  private SourceFunction<String> getStreamSource(int i) {
    if (bindingParams.getGraph().getInputStreams().size() - 1 >= i) {

      SpDataStream stream = bindingParams.getGraph().getInputStreams().get(i);
      if (stream != null) {
        TransportProtocol protocol = stream.getEventGrounding().getTransportProtocol();

        if (protocol instanceof KafkaTransportProtocol) {
          return getKafkaConsumer((KafkaTransportProtocol) protocol);
        } else {
          return getJmsConsumer((JmsTransportProtocol) protocol);
        }
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private SourceFunction<String> getJmsConsumer(JmsTransportProtocol protocol) {
    return new JmsConsumer(protocol);
  }

  private SourceFunction<String> getKafkaConsumer(KafkaTransportProtocol protocol) {
    if (protocol.getTopicDefinition() instanceof SimpleTopicDefinition) {
      return new FlinkKafkaConsumer010<>(protocol
              .getTopicDefinition()
              .getActualTopicName(), new SimpleStringSchema
              (), getProperties(protocol));
    } else {
      String patternTopic = replaceWildcardWithPatternFormat(protocol.getTopicDefinition().getActualTopicName());
      return new FlinkKafkaConsumer010<>(Pattern.compile(patternTopic), new SimpleStringSchema
              (), getProperties(protocol));
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
    DataStream<Map<String, Object>> messageStream1;
    SourceFunction<String> source1 = getStream1Source();
    if (source1 != null) {
      messageStream1 = env
              .addSource(source1).flatMap(new JsonToMapFormat()).flatMap(new StatisticLogger(getGraph()));
    } else {
      throw new SpRuntimeException("At least one source must be defined for a flink sepa");
    }

    DataStream<Map<String, Object>> messageStream2;
    SourceFunction<String> source2 = getStream2Source();
    if (source2 != null) {
      messageStream2 = env
              .addSource(source2).flatMap(new JsonToMapFormat()).flatMap(new StatisticLogger(getGraph()));

      appendExecutionConfig(messageStream1, messageStream2);
    } else {
      appendExecutionConfig(messageStream1);
    }

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
        FlinkJobController ctrl = new FlinkJobController(config.getHost(), config.getPort());
        boolean isDeployed = false;
        int count = 0;
        do {
          try {
            count++;
            Thread.sleep(1000);
            ctrl.findJobId(ctrl.getJobManagerGateway(), bindingParams.getGraph().getElementId());
            isDeployed = true;

          } catch (Exception e) {

          }
        } while (!isDeployed && count < 60);

        if (count == 60) {
          throw new SpRuntimeException("Error: Timeout reached when trying to connect to Flink Job Controller");
        }
      }

    } catch (Exception e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    FlinkJobController ctrl = new FlinkJobController(config.getHost(), config.getPort());

    try {
      if (!ctrl.deleteJob(ctrl.findJobId(ctrl.getJobManagerGateway(), bindingParams.getGraph().getElementId()))) {
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
    }
  }

}
