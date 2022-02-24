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
package org.apache.streampipes.messaging.kafka.config;

import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

public abstract class AbstractConfigFactory {

  private static final String COLON = ":";
  private static final String SASL_MECHANISM = "PLAIN";

  protected KafkaTransportProtocol protocol;

  public AbstractConfigFactory(KafkaTransportProtocol protocol) {
    this.protocol = protocol;
  }

  protected abstract Properties makeDefaultProperties();

  protected <T> T getConfigOrDefault(Supplier<T> function,
                                      T defaultValue) {
    return function.get() != null ? function.get() : defaultValue;
  }

  protected String getBrokerUrl() {
    return protocol.getBrokerHostname() + COLON + protocol.getKafkaPort();
  }

  public Properties buildProperties(List<KafkaConfigAppender> appenders) {
    Properties props = makeDefaultProperties();
    appenders.forEach(appender -> appender.appendConfig(props));

    return  props;
    // TODO check Kafka Security
  }

//  public Properties makePropertiesSaslPlain(String username,
//                                            String password) {
//    Properties props = makeProperties();
//    props.put(SaslConfigs.SASL_MECHANISM,SASL_MECHANISM);
//    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.toString());
//
////    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_SSL.toString());
//
//    String SASL_JAAS_CONFIG = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";";
//    props.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS_CONFIG);
//    return props;
//  }
}
