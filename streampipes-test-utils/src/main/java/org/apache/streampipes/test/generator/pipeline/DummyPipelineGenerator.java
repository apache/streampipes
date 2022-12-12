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

package org.apache.streampipes.test.generator.pipeline;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.test.generator.pipelineelement.DummyProcessorGenerator;
import org.apache.streampipes.test.generator.pipelineelement.DummySinkGenerator;
import org.apache.streampipes.test.generator.pipelineelement.DummyStreamGenerator;

import java.util.Collections;

public class DummyPipelineGenerator {
  public static final String PIPELINE_NAME = "Test Pipeline";

  public static Pipeline makePipelineWithPipelineName() {
    Pipeline pipeline = new Pipeline();
    pipeline.setName(PIPELINE_NAME);

    return pipeline;
  }

  public static Pipeline makePipelineWithProcessorAndSink() {
    Pipeline pipeline = makePipelineWithPipelineName();

    pipeline.setStreams(Collections.singletonList(DummyStreamGenerator.makeDummyStream()));
    pipeline.setSepas(Collections.singletonList(DummyProcessorGenerator.makeDummyProcessor()));
    pipeline.setActions(Collections.singletonList(DummySinkGenerator.makeDummySink()));

    return pipeline;
  }
}
