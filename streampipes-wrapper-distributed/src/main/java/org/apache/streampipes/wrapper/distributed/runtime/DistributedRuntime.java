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
package org.apache.streampipes.wrapper.distributed.runtime;

import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.context.IContextGenerator;
import org.apache.streampipes.extensions.api.pe.context.RuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IParameterGenerator;
import org.apache.streampipes.extensions.api.pe.param.IPipelineElementParameters;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.messaging.kafka.config.ConsumerConfigFactory;
import org.apache.streampipes.messaging.kafka.config.ProducerConfigFactory;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;

import java.util.Properties;

public abstract class DistributedRuntime<
    PeT extends IStreamPipesPipelineElement<?>,
    IvT extends InvocableStreamPipesEntity,
    RcT extends RuntimeContext,
    ExT extends IParameterExtractor<IvT>,
    PepT extends IPipelineElementParameters<IvT, ExT>>
    extends PipelineElementRuntime<PeT, IvT, RcT, ExT, PepT>
    implements IStreamPipesRuntime<PeT, IvT> {

  public DistributedRuntime(IContextGenerator<RcT, IvT> contextGenerator,
                             IParameterGenerator<IvT, ExT, PepT> parameterGenerator) {
    super(contextGenerator, parameterGenerator);
  }

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    return new ConsumerConfigFactory(protocol).makeDefaultProperties();
  }

  protected Properties getProducerProperties(KafkaTransportProtocol protocol) {
    return new ProducerConfigFactory(protocol).makeDefaultProperties();
  }

  protected SpDataFormatDefinition getDataFormatDefinition(TransportFormat transportFormat) {
    return SpDataFormatManager.INSTANCE.findDefinition(transportFormat).get();
  }

  protected String getTopic(SpDataStream stream) {
    return protocol(stream)
        .getTopicDefinition()
        .getActualTopicName();
  }

  protected JmsTransportProtocol getJmsProtocol(SpDataStream stream) {
    return new JmsTransportProtocol((JmsTransportProtocol) protocol(stream));
  }

  protected MqttTransportProtocol getMqttProtocol(SpDataStream stream) {
    return new MqttTransportProtocol((MqttTransportProtocol) protocol(stream));
  }

  protected boolean isKafkaProtocol(SpDataStream stream) {
    return protocol(stream) instanceof KafkaTransportProtocol;
  }

  protected boolean isJmsProtocol(SpDataStream stream) {
    return protocol(stream) instanceof JmsTransportProtocol;
  }

  protected boolean isMqttProtocol(SpDataStream stream) {
    return protocol(stream) instanceof MqttTransportProtocol;
  }

  protected TransportProtocol protocol(SpDataStream stream) {
    return stream
        .getEventGrounding()
        .getTransportProtocol();
  }

  protected String getKafkaUrl(SpDataStream stream) {
    // TODO add also jms support
    return protocol(stream).getBrokerHostname()
        + ":"
        + ((KafkaTransportProtocol) protocol(stream)).getKafkaPort();
  }

  protected String replaceWildcardWithPatternFormat(String topic) {
    topic = topic.replaceAll("\\.", "\\\\.");
    return topic.replaceAll("\\*", ".*");
  }

}
