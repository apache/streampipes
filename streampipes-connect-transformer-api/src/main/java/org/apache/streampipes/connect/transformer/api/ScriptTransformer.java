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

package org.apache.streampipes.connect.transformer.api;

import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;

import java.util.Map;

/**
 * Transforms an input event map into an output event map.
 * Implementations are language-specific but must adhere to this contract.
 */
public interface ScriptTransformer {

  /**
   * Apply the compiled template to the incoming data.
   *
   * @param input input event map keyed by runtime name
   * @param out output event
   * @param ctx reserved for later, currently null
   * @throws ScriptExecutionException Exception when execution fails or returns an invalid result
   */
  void transform(Map<String, Object> input,
                 OutputCollector<Map<String, Object>> out,
                 Context ctx) throws ScriptExecutionException;
}
