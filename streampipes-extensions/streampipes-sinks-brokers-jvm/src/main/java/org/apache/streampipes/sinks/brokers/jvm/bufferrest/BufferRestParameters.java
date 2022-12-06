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

package org.apache.streampipes.sinks.brokers.jvm.bufferrest;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.List;

public class BufferRestParameters extends EventSinkBindingParams {

  private String restEndpointURI;
  private List<String> fieldsToSend;
  private int bufferSize;

  public BufferRestParameters(DataSinkInvocation graph, List<String> fieldsToSend, String restEndpointURI,
                              int bufferSize) {
    super(graph);
    this.fieldsToSend = fieldsToSend;
    this.restEndpointURI = restEndpointURI;
    this.bufferSize = bufferSize;
  }

  public List<String> getFieldsToSend() {
    return fieldsToSend;
  }

  public String getRestEndpointURI() {
    return restEndpointURI;
  }

  public int getBufferSize() {
    return bufferSize;
  }
}
