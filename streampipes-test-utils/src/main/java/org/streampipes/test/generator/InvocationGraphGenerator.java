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
package org.streampipes.test.generator;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;

import java.util.Arrays;

public class InvocationGraphGenerator {

  public static DataProcessorInvocation makeEmptyInvocation(DataProcessorDescription description) {
    DataProcessorInvocation invocation = new DataProcessorInvocation(description);

    invocation.setOutputStream(EventStreamGenerator.makeEmptyStream());
    invocation.setInputStreams(Arrays.asList(EventStreamGenerator.makeEmptyStream()));

    return invocation;
  }

}
