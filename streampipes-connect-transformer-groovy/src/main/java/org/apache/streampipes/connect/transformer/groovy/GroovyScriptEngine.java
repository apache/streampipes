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

package org.apache.streampipes.connect.transformer.groovy;

import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.connect.transformer.api.OutputCollector;
import org.apache.streampipes.connect.transformer.api.ScriptTransformer;
import org.apache.streampipes.connect.transformer.api.TransformationEngine;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.connect.transformer.api.utils.TransformationEngineConversionUtils;
import org.apache.streampipes.model.connect.ScriptMetadata;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.Map;

public class GroovyScriptEngine implements TransformationEngine {

  @Override
  public ScriptMetadata metadata() {
    return new ScriptMetadata(
        "groovy",
    "Groovy",
    "out.collect(input)"
    );
  }

  @Override
  public ScriptTransformer compile(String code) throws ScriptCompilationException {
    GroovyShell shell = new GroovyShell();
    Script script;
    try {
      script = shell.parse(code);
    } catch (CompilationFailedException e) {
      throw new ScriptCompilationException("Failed to compile Groovy template", e);
    }

    Class<? extends Script> scriptClass = script.getClass();

    return (input, out, ctx) -> execute(scriptClass, input, out, ctx);
  }

  private void execute(Class<? extends Script> scriptClass,
                       Map<String, Object> input,
                       OutputCollector<Map<String, Object>> out,
                       Context ctx)
      throws ScriptExecutionException {
    try {
      Binding binding = new Binding();
      binding.setVariable("input", input);
      binding.setVariable("out", TransformationEngineConversionUtils.convertingCollector(out, metadata().language()));
      binding.setVariable("ctx", ctx);
      Script scriptInstance = InvokerHelper.createScript(scriptClass, binding);
      scriptInstance.run();
    } catch (Exception e) {
      throw new ScriptExecutionException("Groovy template execution failed", e);
    }
  }
}
