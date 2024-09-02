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

package org.apache.streampipes.processors.enricher.jvm.processor.expression;

import org.apache.streampipes.model.runtime.Event;

import org.apache.commons.jexl3.MapContext;

public class JexlContextGenerator {

  private final MathExpressionFieldExtractor extractor;
  private final MapContext mapContext;

  public JexlContextGenerator(MathExpressionFieldExtractor extractor) {
    this.extractor = extractor;
    this.mapContext = makeInitialContext();
  }

  private MapContext makeInitialContext() {
    var ctx = new MapContext();
    ctx.set("Math", Math.class);
    extractor.getInputProperties().forEach(ep ->
        ctx.set(ep.getRuntimeName(), 0)
    );
    return ctx;
  }

  public MapContext makeContext(Event event) {
    event.getRaw().forEach(mapContext::set);
    return mapContext;
  }
}
