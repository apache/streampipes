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

package org.apache.streampipes.model.quality;

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;

import java.net.URI;

public class MeasurementObject extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = 4391097898611686930L;

  private URI measuresObject;

  public MeasurementObject() {
    super();
  }

  public MeasurementObject(MeasurementObject other) {
    super(other);
    this.measuresObject = other.getMeasuresObject();
  }

  public MeasurementObject(URI measurementObject) {
    super();
    this.measuresObject = measurementObject;
  }

  public URI getMeasuresObject() {
    return measuresObject;
  }

  public void setMeasuresObject(URI measurementObject) {
    this.measuresObject = measurementObject;
  }


}
