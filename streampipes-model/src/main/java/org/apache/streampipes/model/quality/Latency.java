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

public class Latency extends EventStreamQualityDefinition {

  private static final long serialVersionUID = -9211064635743833555L;

  private float quantityValue;

  public Latency() {
    super();
  }

  public Latency(Latency other) {
    super(other);
    this.quantityValue = other.getQuantityValue();
  }

  public Latency(float quantityValue) {
    this.quantityValue = quantityValue;
  }

  public float getQuantityValue() {
    return quantityValue;
  }

  public void setQuantityValue(float quantityValue) {
    this.quantityValue = quantityValue;
  }


  //@Override
  public int compareTo(EventStreamQualityDefinition o) {
    Latency other = (Latency) o;
    if (other.getQuantityValue() == this.getQuantityValue()) {
      return 0;

    } else if ((other).getQuantityValue() > this.getQuantityValue()) {
      return -1;
    } else {
      return 1;
    }
  }


}
