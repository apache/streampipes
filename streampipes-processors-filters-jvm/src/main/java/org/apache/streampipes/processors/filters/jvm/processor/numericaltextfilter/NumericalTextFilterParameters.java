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

package org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class NumericalTextFilterParameters extends EventProcessorBindingParams {

  private double numberThreshold;
  private NumericalOperator numericalOperator;
  private String numberProperty;
  private String textKeyword;
  private StringOperator textOperator;
  private String textProperty;

  public NumericalTextFilterParameters(DataProcessorInvocation graph, Double numberThreshold, NumericalOperator
          NumericalOperator, String numberProperty, String textKeyword, StringOperator textOperator, String textProperty) {
    super(graph);
    this.numberThreshold = numberThreshold;
    this.numericalOperator = NumericalOperator;
    this.numberProperty = numberProperty;
    this.textKeyword = textKeyword;
    this.textOperator = textOperator;
    this.textProperty = textProperty;
  }

  public double getNumberThreshold() {
    return numberThreshold;
  }

  public NumericalOperator getNumericalOperator() {
    return numericalOperator;
  }

  public String getNumberProperty() {
    return numberProperty;
  }

  public String getTextKeyword() {
    return textKeyword;
  }

  public StringOperator getTextOperator() {
    return textOperator;
  }

  public String getTextProperty() {
    return textProperty;
  }
}
