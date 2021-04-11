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
package org.apache.streampipes.commons.constants;

public enum PipelineElementUrl {

  DATA_PROCESSOR(PipelineElementPrefix.DATA_PROCESSOR),
  DATA_SINK(PipelineElementPrefix.DATA_SINK),
  DATA_STREAM(PipelineElementPrefix.DATA_STREAM),
  DATA_SET(PipelineElementPrefix.DATA_SET),
  ADAPTER(PipelineElementPrefix.ADAPTER);

  private final String HTTP = "http://";
  private final String SLASH = "/";
  private final String prefix;

  PipelineElementUrl(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public String getInvocationUrl(String host,
                                 Integer port,
                                 String appId) {
    return HTTP
            + host
            + ":"
            + port
            + SLASH
            + this.prefix
            + SLASH + appId;
  }

  public String getInvocationUrl(String baseUrl, String appId) {
    return baseUrl
            + SLASH
            + this.prefix
            + SLASH
            + appId;
  }

  public String getDetachUrl(String host,
                             Integer port,
                             String appId,
                             String invocationId) {
    return getInvocationUrl(host, port, appId)
            + SLASH
            + invocationId;
  }

  public String getDetachUrl(String baseUrl, String appId, String invocationId) {
    return getInvocationUrl(baseUrl, appId)
            + SLASH
            + invocationId;
  }


}
