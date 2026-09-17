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

package org.apache.streampipes.messaging;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.model.grounding.BrokerConfiguration;
import org.apache.streampipes.model.grounding.InternalTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class InternalBrokerSettings {
  private final String protocolId;
  private final String url;
  private final String token;

  public InternalBrokerSettings(String protocolId, String url, String token) {
    this.protocolId = Objects.requireNonNull(protocolId, "Missing broker protocolId");
    if (!java.util.Set.of("nats", "kafka", "mqtt", "pulsar").contains(protocolId)) {
      throw new IllegalArgumentException("Unsupported internal broker protocol");
    }
    URI uri = parse(url);
    if (uri.getHost() == null || uri.getPort() < 1 || uri.getPort() > 65535 || uri.getUserInfo() != null) {
      throw new IllegalArgumentException("Internal broker URL requires a host and port without embedded credentials");
    }
    String scheme = switch (protocolId) {
      case "nats" -> "nats";
      case "kafka" -> "kafka";
      case "mqtt" -> "tcp";
      default -> "pulsar";
    };
    boolean supportedScheme = scheme.equals(uri.getScheme())
        || ("pulsar".equals(protocolId) && "pulsar+ssl".equals(uri.getScheme()));
    if (!supportedScheme || (uri.getPath() != null && !uri.getPath().isEmpty() && !"/".equals(uri.getPath()))
        || uri.getQuery() != null || uri.getFragment() != null) {
      throw new IllegalArgumentException("Unsupported internal broker URL scheme or components");
    }
    this.url = url;
    this.token = token == null || token.isBlank() ? null : token;
  }

  public static InternalBrokerSettings fromEnvironment(Environment env) {
    String id = env.getPrioritizedProtocol().getValueOrDefault();
    String url = switch (id) {
      case "nats" -> "nats://" + env.getNatsHost().getValueOrDefault() + ":" + env.getNatsPort().getValueOrDefault();
      case "kafka" -> "kafka://" + env.getKafkaHost().getValueOrDefault() + ":" + env.getKafkaPort().getValueOrDefault();
      case "mqtt" -> "tcp://" + env.getMqttHost().getValueOrDefault() + ":" + env.getMqttPort().getValueOrDefault();
      case "pulsar" -> env.getPulsarUrl().getValueOrDefault();
      default -> throw new IllegalArgumentException("Unsupported internal broker protocol");
    };
    return new InternalBrokerSettings(id, url, "nats".equals(id) ? env.getNatsToken().getValueOrDefault() : null);
  }

  public static InternalBrokerSettings resolve(BrokerConfiguration defaults, Environment env) {
    Objects.requireNonNull(defaults, "Missing registration broker configuration");
    new InternalBrokerSettings(defaults.getProtocolId(), defaults.getUrl(), defaults.getToken());
    String id = defaults.getProtocolId();
    URI uri = parse(defaults.getUrl());
    String host = uri.getHost();
    int port = uri.getPort();
    String token = defaults.getToken();
    String url = defaults.getUrl();
    switch (id) {
      case "nats" -> {
        host = env.getNatsHost().getValueOrReturn(host);
        port = env.getNatsPort().getValueOrReturn(port);
        token = env.getNatsToken().getValueOrReturn(token);
        if (defaults.getToken() != null && !defaults.getToken().isBlank() && (token == null || token.isBlank())) {
          throw new IllegalArgumentException("A blank local token cannot clear registration authentication");
        }
      }
      case "kafka" -> {
        host = env.getKafkaHost().getValueOrReturn(host);
        port = env.getKafkaPort().getValueOrReturn(port);
      }
      case "mqtt" -> {
        host = env.getMqttHost().getValueOrReturn(host);
        port = env.getMqttPort().getValueOrReturn(port);
      }
      case "pulsar" -> url = env.getPulsarUrl().getValueOrReturn(url);
      default -> throw new IllegalArgumentException("Unsupported internal broker protocol");
    }
    if (!"pulsar".equals(id)) {
      try {
        url = new URI(uri.getScheme(), null, host, port, null, null, null).toString();
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException("Invalid internal broker endpoint override");
      }
    }
    return new InternalBrokerSettings(id, url, token);
  }

  public BrokerConfiguration configuration() {
    var result = new BrokerConfiguration();
    result.setProtocolId(protocolId);
    result.setUrl(url);
    result.setToken(token);
    return result;
  }

  public String protocolId() {
    return protocolId;
  }

  public TransportProtocol bind(InternalTransportProtocol channel) {
    URI endpoint = parse(url);
    TransportProtocol protocol;
    switch (protocolId) {
      case "nats" -> {
        var nats = new NatsTransportProtocol();
        nats.setPort(endpoint.getPort());
        nats.setToken(token);
        protocol = nats;
      }
      case "kafka" -> {
        var kafka = new KafkaTransportProtocol();
        kafka.setKafkaPort(endpoint.getPort());
        var options = channel.getOptions();
        kafka.setGroupId(options.get("groupId"));
        kafka.setOffset(options.get("offset"));
        kafka.setAcks(options.get("acks"));
        kafka.setBatchSize(options.get("batchSize"));
        kafka.setMessageMaxBytes(options.get("messageMaxBytes"));
        kafka.setMaxRequestSize(options.get("maxRequestSize"));
        if (options.get("lingerMs") != null) {
          kafka.setLingerMs(Integer.valueOf(options.get("lingerMs")));
        }
        protocol = kafka;
      }
      case "mqtt" -> {
        var mqtt = new MqttTransportProtocol();
        mqtt.setPort(endpoint.getPort());
        protocol = mqtt;
      }
      case "pulsar" -> protocol = new PulsarTransportProtocol();
      default -> throw new IllegalStateException("Unsupported internal broker protocol");
    }
    protocol.setBrokerHostname("pulsar".equals(protocolId) ? url : endpoint.getHost());
    protocol.setTopicDefinition(channel.getTopicDefinition());
    return protocol;
  }

  private static URI parse(String value) {
    try {
      return new URI(Objects.requireNonNull(value));
    } catch (URISyntaxException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid internal broker URL");
    }
  }
}
