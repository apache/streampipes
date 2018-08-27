/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.imageprocessing.jvm.processor.imagerectification;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.util.Map;

public class ImageRectifier extends StandaloneEventProcessorEngine<ImageRectificationParameters> {

  private ImageRectificationParameters params;

  public ImageRectifier(ImageRectificationParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(ImageRectificationParameters imageRectificationParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.params = imageRectificationParameters;
  }

  @Override
  public void onEvent(Map<String, Object> map, String s, SpOutputCollector spOutputCollector) {
    // TODO add logic here
  }

  @Override
  public void onDetach() {

  }
}
