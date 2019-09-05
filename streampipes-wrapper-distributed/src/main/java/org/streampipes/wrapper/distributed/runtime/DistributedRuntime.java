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
package org.streampipes.wrapper.distributed.runtime;

import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.kafka.config.ConsumerConfigFactory;
import org.streampipes.messaging.kafka.config.ProducerConfigFactory;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.params.runtime.RuntimeParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

import java.util.Properties;

public abstract class DistributedRuntime<RP extends RuntimeParams<B, I, RC>, B extends
        BindingParams<I>, I extends InvocableStreamPipesEntity, RC extends RuntimeContext> extends
        PipelineElementRuntime {

  protected RP runtimeParams;
  protected B bindingParams;

  @Deprecated
  protected B params;

  public DistributedRuntime(RP runtimeParams) {
    super();
    this.runtimeParams = runtimeParams;
    this.bindingParams = runtimeParams.getBindingParams();
    this.params = runtimeParams.getBindingParams();
  }

  public DistributedRuntime(B bindingParams) {
    super();
    this.bindingParams = bindingParams;
    this.params = bindingParams;
    this.runtimeParams = makeRuntimeParams();
  }

  protected I getGraph() {
    return runtimeParams.getBindingParams().getGraph();
  }

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    return new ConsumerConfigFactory(protocol).makeProperties();
  }

  protected Properties getProducerProperties(KafkaTransportProtocol protocol) {
    return new ProducerConfigFactory(protocol).makeProperties();
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

  protected boolean isKafkaProtocol(SpDataStream stream)
  {
    return protocol(stream) instanceof KafkaTransportProtocol;
  }

  protected TransportProtocol protocol(SpDataStream stream) {
    return stream
            .getEventGrounding()
            .getTransportProtocol();
  }

  protected String getKafkaUrl(SpDataStream stream) {
    // TODO add also jms support
    return protocol(stream).getBrokerHostname() +
            ":" +
            ((KafkaTransportProtocol) protocol(stream)).getKafkaPort();
  }

  protected String replaceWildcardWithPatternFormat(String topic) {
    topic = topic.replaceAll("\\.", "\\\\.");
    return topic.replaceAll("\\*", ".*");
  }

  protected abstract RP makeRuntimeParams();

}
