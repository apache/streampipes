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

package org.apache.streampipes.smp.extractor;

import org.apache.streampipes.smp.constants.PeType;

public class PipelineElementTypeExtractor {

  private static final String ProcessorString = "ProcessingElementBuilder";
  private static final String SinkString = "DataSinkBuilder";

  private String typeString;

  public PipelineElementTypeExtractor(String typeString) {
    this.typeString = typeString;
  }

  public PeType extractType() throws IllegalArgumentException {
    if (typeString.equals(ProcessorString)) {
      return PeType.PROCESSOR;
    } else if (typeString.equals(SinkString)) {
      return PeType.SINK;
    } else {
      throw new IllegalArgumentException("Unknown pipeline element type " + typeString);
    }
  }
}
