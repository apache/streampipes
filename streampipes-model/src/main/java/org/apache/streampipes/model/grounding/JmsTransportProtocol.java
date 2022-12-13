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

package org.apache.streampipes.model.grounding;

public class JmsTransportProtocol extends TransportProtocol {

  private static final long serialVersionUID = -5650426611208789835L;

  private int port;

  public JmsTransportProtocol(String uri, int port, String topicName) {
    super(uri, new SimpleTopicDefinition(topicName));
    this.port = port;
  }

  public JmsTransportProtocol(JmsTransportProtocol other) {
    super(other);
    this.port = other.getPort();
  }

  public JmsTransportProtocol() {
    super();
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return getBrokerHostname() + ":" + getPort();
  }
}
