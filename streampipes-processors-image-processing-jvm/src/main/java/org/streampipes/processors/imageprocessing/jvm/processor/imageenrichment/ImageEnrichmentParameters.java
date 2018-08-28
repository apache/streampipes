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
package org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ImageEnrichmentParameters extends EventProcessorBindingParams {

  private String imageProperty;
  private String boxArray;

  private String boxWidth;
  private String boxHeight;
  private String boxX;
  private String boxY;

  public ImageEnrichmentParameters(DataProcessorInvocation graph, String imageProperty, String boxArray, String boxWidth, String boxHeight, String boxX, String boxY) {
    super(graph);
    this.imageProperty = imageProperty;
    this.boxArray = boxArray;
    this.boxWidth = boxWidth;
    this.boxHeight = boxHeight;
    this.boxX = boxX;
    this.boxY = boxY;
  }


  public ImageEnrichmentParameters(DataProcessorInvocation graph, String imageProperty, String boxWidth, String boxHeight, String boxX, String boxY) {
    super(graph);
    this.imageProperty = imageProperty;
    this.boxWidth = boxWidth;
    this.boxHeight = boxHeight;
    this.boxX = boxX;
    this.boxY = boxY;
  }

  public String getImageProperty() {
    return imageProperty;
  }

  public String getBoxArray() {
    return boxArray;
  }

  public String getBoxWidth() {
    return boxWidth;
  }

  public String getBoxHeight() {
    return boxHeight;
  }

  public String getBoxX() {
    return boxX;
  }

  public String getBoxY() {
    return boxY;
  }

}
