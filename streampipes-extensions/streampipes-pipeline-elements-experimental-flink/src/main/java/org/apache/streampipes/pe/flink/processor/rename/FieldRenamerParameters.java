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

package org.apache.streampipes.pe.flink.processor.rename;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

public class FieldRenamerParameters extends ProcessorParams {

  private String oldPropertyName;
  private String newPropertyName;

  public FieldRenamerParameters(DataProcessorInvocation graph, String oldPropertyName, String newPropertyName) {
    super(graph);
    this.oldPropertyName = oldPropertyName;
    this.newPropertyName = newPropertyName;
  }

  public String getOldPropertyName() {
    return oldPropertyName;
  }

  public void setOldPropertyName(String oldPropertyName) {
    this.oldPropertyName = oldPropertyName;
  }

  public String getNewPropertyName() {
    return newPropertyName;
  }

  public void setNewPropertyName(String newPropertyName) {
    this.newPropertyName = newPropertyName;
  }


}
