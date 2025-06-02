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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;

public interface IStreamPipesSwitchProcessor extends IStreamPipesDataProcessor {

  String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";
  String SWITCH_FILTER_KEY = "switch-filter-key";
  String SWITCH_CASE_VALUE_OPERATOR = "switch-case-value-operator";
  String SWITCH_CASE_VALUE = "switch-case-value";
  String SWITCH_CASE_VALUE_OUTPUT = "switch-case-value-output";
  String SWITCH_CASE_GROUP = "switch-case-group";
  String OUTPUT_TYPE_KEY = "output-type";
  String SWITCH_CASE_VALUE_DEFAULT_OUTPUT = "switch-case-value-default-output";

}
