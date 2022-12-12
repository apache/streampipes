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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.output.ListOutputStrategy;
import org.apache.streampipes.model.output.TransformOperation;
import org.apache.streampipes.model.output.TransformOutputStrategy;
import org.apache.streampipes.model.output.UserDefinedOutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.Arrays;
import java.util.List;

public class OutputStrategies {

  /**
   * Creates a {@link org.apache.streampipes.model.output.CustomOutputStrategy}. Custom output strategies let pipeline
   * developers decide which events are produced by the corresponding pipeline element.
   *
   * @return CustomOutputStrategy
   */
  public static CustomOutputStrategy custom() {
    return new CustomOutputStrategy();
  }

  /**
   * Creates a {@link org.apache.streampipes.model.output.CustomOutputStrategy}.
   *
   * @param outputBoth If two input streams are expected by a pipeline element, you can use outputBoth to indicate
   *                   whether the properties of both input streams should be available to the pipeline developer for
   *                   selection.
   * @return CustomOutputStrategy
   */
  public static CustomOutputStrategy custom(boolean outputBoth) {
    return new CustomOutputStrategy(outputBoth);
  }


  /**
   * Creates a {@link org.apache.streampipes.model.output.AppendOutputStrategy}. Append output strategies add additional
   * properties to an input event stream.
   *
   * @param appendProperties An arbitrary number of event properties that are appended to any input stream.
   * @return AppendOutputStrategy
   */
  public static AppendOutputStrategy append(EventProperty... appendProperties) {
    return new AppendOutputStrategy(Arrays.asList(appendProperties));
  }

  public static AppendOutputStrategy append(List<EventProperty> appendProperties) {
    return new AppendOutputStrategy(appendProperties);
  }

  /**
   * Creates a {@link org.apache.streampipes.model.output.FixedOutputStrategy}.
   * Fixed output strategies always output the schema defined by the pipeline element itself.
   *
   * @param fixedProperties An arbitrary number of event properties that form the output event schema
   * @return FixedOutputStrategy
   */
  public static FixedOutputStrategy fixed(EventProperty... fixedProperties) {
    return new FixedOutputStrategy(Arrays.asList(fixedProperties));
  }

  public static FixedOutputStrategy fixed(List<EventProperty> appendProperties) {
    return new FixedOutputStrategy(appendProperties);
  }

  /**
   * Creates a {@link org.apache.streampipes.model.output.KeepOutputStrategy}. Keep output strategies do not change the
   * schema of an input event, i.e., the output schema matches the input schema.
   *
   * @return KeepOutputStrategy
   */
  public static KeepOutputStrategy keep() {
    return new KeepOutputStrategy();
  }

  /**
   * Creates a {@link org.apache.streampipes.model.output.UserDefinedOutputStrategy}. User-defined output strategies are
   * fully flexible output strategies which are created by users at pipeline development time.
   *
   * @return UserDefinedOutputStrategy
   */
  public static UserDefinedOutputStrategy userDefined() {
    return new UserDefinedOutputStrategy();
  }

  public static KeepOutputStrategy keep(boolean mergeInputStreams) {
    return new KeepOutputStrategy("Rename", mergeInputStreams);
  }

  public static ListOutputStrategy list(String propertyRuntimeName) {
    return new ListOutputStrategy(propertyRuntimeName);
  }

  public static TransformOutputStrategy transform(TransformOperation... transformOperations) {
    TransformOutputStrategy tos = new TransformOutputStrategy();
    tos.setTransformOperations(Arrays.asList(transformOperations));
    return tos;
  }

  public static CustomTransformOutputStrategy customTransformation() {
    return new CustomTransformOutputStrategy();
  }
}
