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

package org.apache.streampipes.connect.transformer.js;

import org.apache.streampipes.connect.transformer.api.OutputCollector;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.model.shared.annotation.ExposedToScripts;

import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.Map;

public class ScriptOutputCollector {

  private final OutputCollector<Map<String, Object>> delegate;

  public ScriptOutputCollector(OutputCollector<Map<String, Object>> delegate) {
    this.delegate = delegate;
  }

  public static ProxyObject wrap(OutputCollector<Map<String, Object>> delegate) {
    ScriptOutputCollector collector = new ScriptOutputCollector(delegate);
    return ProxyObject.fromMap(Map.of(
        "collect",
        (ProxyExecutable) args -> {
          if (args.length != 1) {
            throw new IllegalArgumentException("collect expects exactly one event argument");
          }
          try {
            collector.collect(args[0]);
            return null;
          } catch (ScriptExecutionException e) {
            throw new IllegalStateException("Failed to collect script output event", e);
          }
        }
    ));
  }

  @ExposedToScripts
  public void collect(Object event) throws ScriptExecutionException {
    delegate.collect(PolyglotTypeConverter.toEventMap(event));
  }
}
