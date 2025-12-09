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

import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;

/**
 * Compiles code templates into reusable transformers.
 */
public interface TransformationEngine {

  /**
   * Identifier of the underlying scripting language.
   */
  String language();

  /**
   * Compile the user-provided script into an executable transformer.
   *
   * @param script code template that reads from {@code input} and returns a map
   * @return compiled transformer ready for repeated execution
   * @throws ScriptCompilationException when the script cannot be compiled
   */
  ScriptTransformer compile(String script) throws ScriptCompilationException;
}
