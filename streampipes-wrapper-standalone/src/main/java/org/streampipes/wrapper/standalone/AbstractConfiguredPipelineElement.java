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
package org.streampipes.wrapper.standalone;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElement;

import java.util.function.Supplier;

public class AbstractConfiguredPipelineElement<I extends InvocableStreamPipesEntity,
        B extends BindingParams<I>,
        T extends PipelineElement<B, I>> {

  private B bindingParams;
  private Supplier<T> engineSupplier;

  public AbstractConfiguredPipelineElement(B bindingParams,
                                          Supplier<T> engineSupplier) {
    this.bindingParams = bindingParams;
    this.engineSupplier = engineSupplier;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public Supplier<T> getEngineSupplier() {
    return engineSupplier;
  }
}
