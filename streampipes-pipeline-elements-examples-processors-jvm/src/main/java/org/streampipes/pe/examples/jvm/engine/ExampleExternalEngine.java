/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.examples.jvm.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.runtime.ExternalEventProcessor;

public class ExampleExternalEngine
        implements ExternalEventProcessor<ExampleExternalEngineParameters> {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleExternalEngine.class);

  @Override
  public void onInvocation(ExampleExternalEngineParameters parameters,
                             EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG.info("I'm invoked!");
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    LOG.info("I'm detached!");
  }
}
