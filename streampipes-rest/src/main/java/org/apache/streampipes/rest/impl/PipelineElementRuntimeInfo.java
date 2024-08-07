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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.runtime.DataStreamRuntimeInfoProvider;
import org.apache.streampipes.manager.runtime.RateLimitedRuntimeInfoProvider;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/pipeline-element/runtime")
public class PipelineElementRuntimeInfo extends AbstractRestResource {

  @PostMapping(
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public StreamingResponseBody getRuntimeInfo(HttpServletResponse response,
                                              @RequestBody SpDataStream spDataStream) {
    // deactivate nginx proxy buffering for better performance of streaming output
    response.addHeader("X-Accel-Buffering", "no");
    var runtimeInfoFetcher = new DataStreamRuntimeInfoProvider(Map.of("adapter", spDataStream));
    var runtimeInfoProvider = new RateLimitedRuntimeInfoProvider(runtimeInfoFetcher);
    return runtimeInfoProvider::streamOutput;
  }
}
