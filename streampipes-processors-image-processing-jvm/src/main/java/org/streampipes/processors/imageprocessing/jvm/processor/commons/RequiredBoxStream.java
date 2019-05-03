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
package org.streampipes.processors.imageprocessing.jvm.processor.commons;

import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;

public class RequiredBoxStream {

  public static final String IMAGE_PROPERTY = "image-property";
  public static final String BOX_ARRAY_PROPERTY = "box-array-property";

  public static CollectedStreamRequirements getBoxStream() {
        return StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq("https://image.com"), Labels
                            .from(IMAGE_PROPERTY, "Image Classification", ""),
                    PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReqList("https://streampipes.org/boundingboxes"),
                    Labels.from(BOX_ARRAY_PROPERTY, "Array Width Bounding Boxes", "Contains an array with bounding boxes"),
                    PropertyScope.NONE)
            .build();
  }
}
