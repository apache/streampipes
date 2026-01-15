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
import org.apache.streampipes.connect.transformer.api.ScriptTransformer;
import org.apache.streampipes.connect.transformer.api.TransformationEngine;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.model.connect.ScriptMetadata;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.Optional;

public class GraalJsScriptEngine implements TransformationEngine {

  @Override
  public ScriptMetadata metadata() {
    return new ScriptMetadata(
        "javascript",
        "JavaScript",
        """
            // returns the same event
            function transform(event, out, ctx) {
              out.collect(event);
            }
            """
    );
  }

  @Override
  public ScriptTransformer compile(String script) throws ScriptCompilationException {
    Context context = createContext();
    Value compiled;
    try {
      compiled = context.eval("js", script);
    } catch (PolyglotException e) {
      context.close();
      throw new ScriptCompilationException("Failed to compile Graal JS script", e);
    }

    Value transformFunction = resolveFunction(context, compiled);

    return (input, out, ctx) -> execute(transformFunction, input, out, ctx);
  }

  private Context createContext() {
    return Context.newBuilder("js")
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup(s -> false)
        .build();
  }

  private Value resolveFunction(Context context, Value compiled) throws ScriptCompilationException {
    if (compiled.canExecute()) {
      return compiled;
    }

    Optional<Value> transform = Optional.ofNullable(compiled.hasMember("transform")
        ? compiled.getMember("transform")
        : context.getBindings("js").getMember("transform"));

    if (transform.isPresent() && transform.get().canExecute()) {
      return transform.get();
    }

    throw new ScriptCompilationException(
        "Graal JS script must evaluate to an executable function or expose a callable 'transform' member",
        null);
  }

  private void execute(Value transformFunction,
                       Map<String, Object> input,
                       OutputCollector<Map<String, Object>> out,
                       org.apache.streampipes.connect.transformer.api.Context ctx)
      throws ScriptExecutionException {
    try {
      transformFunction.execute(input, PolyglotResultConverter.convertingCollector(out, metadata().language()), ctx);
      //return PolyglotResultConverter.ensureMap(result, metadata().language());
    } catch (PolyglotException e) {
      throw new ScriptExecutionException("Graal JS script execution failed", e);
    }
  }
}
