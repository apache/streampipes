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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SPSensor;

import java.util.List;

public class LabelerUtils {

  public static EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement, String labelName,
                                                  List<String> labelStrings) throws SpRuntimeException {

    List<EventProperty> properties = processingElement
        .getInputStreams()
        .get(0)
        .getEventSchema()
        .getEventProperties();

    properties.add(PrimitivePropertyBuilder
        .create(Datatypes.String, labelName)
        .valueSpecification(labelName, "possible label values", labelStrings)
        .domainProperty(SPSensor.STATE)
        .scope(PropertyScope.DIMENSION_PROPERTY)
        .build());

    return new EventSchema(properties);
  }
}
