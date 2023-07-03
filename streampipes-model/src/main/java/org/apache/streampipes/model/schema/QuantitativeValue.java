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

package org.apache.streampipes.model.schema;

import java.util.Objects;

public class QuantitativeValue extends ValueSpecification {

  private Float minValue;

  private Float maxValue;

  private Float step;

  public QuantitativeValue() {
    super();
  }

  public QuantitativeValue(Float minValue, Float maxValue, Float step) {
    super();
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.step = step;
  }

  public QuantitativeValue(QuantitativeValue other) {
    this.minValue = other.getMinValue();
    this.maxValue = other.getMaxValue();
    this.step = other.getStep();
  }

  public Float getMinValue() {
    return minValue;
  }

  public void setMinValue(Float minValue) {
    this.minValue = minValue;
  }

  public Float getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(Float maxValue) {
    this.maxValue = maxValue;
  }

  public Float getStep() {
    return step;
  }

  public void setStep(Float step) {
    this.step = step;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuantitativeValue that = (QuantitativeValue) o;
    return Objects.equals(minValue, that.minValue) && Objects.equals(maxValue, that.maxValue)
           && Objects.equals(step, that.step);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minValue, maxValue, step);
  }
}
