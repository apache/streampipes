/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.wrapper.distributed.runtime;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

import java.util.Properties;
import java.util.UUID;

public abstract class DistributedRuntime<B extends BindingParams<I>, I extends InvocableStreamPipesEntity> extends
        PipelineElementRuntime {

  protected B bindingParams;
  protected B params; // backwards compatibility

  public DistributedRuntime(B bindingParams) {
    super();
    this.bindingParams = bindingParams;
    this.params = bindingParams;
  }

  protected I getGraph() {
    return bindingParams.getGraph();
  }

  protected Properties getProperties(KafkaTransportProtocol protocol) {
    Properties props = new Properties();

    String zookeeperHost = protocol.getZookeeperHost();
    int zookeeperPort = protocol.getZookeeperPort();

    String kafkaHost = protocol.getBrokerHostname();
    int kafkaPort = protocol.getKafkaPort();

    props.put("zookeeper.connect", zookeeperHost +":" +zookeeperPort);
    props.put("bootstrap.servers", kafkaHost +":" +kafkaPort);
    props.put("group.id", UUID.randomUUID().toString());
    props.put("zookeeper.session.timeout.ms", "60000");
    props.put("zookeeper.sync.time.ms", "20000");
    props.put("auto.commit.interval.ms", "10000");
    return props;
  }

  protected String getTopic(SpDataStream stream)
  {
    return protocol(stream)
            .getTopicName();
  }

  protected JmsTransportProtocol getJmsProtocol(SpDataStream stream)
  {
    return new JmsTransportProtocol((JmsTransportProtocol) protocol(stream));
  }

  protected boolean isKafkaProtocol(SpDataStream stream)
  {
    return protocol(stream) instanceof KafkaTransportProtocol;
  }

  private TransportProtocol protocol(SpDataStream stream) {
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

}
