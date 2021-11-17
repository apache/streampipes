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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.commons;

import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

public class RequiredBoxStream {

  public static final String IMAGE_PROPERTY = "image-property";
  public static final String BOX_ARRAY_PROPERTY = "box-array-property";

  public static CollectedStreamRequirements getBoxStream() {
        return StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq("https://image.com"), Labels
                            .withId(IMAGE_PROPERTY),
                    PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReqList("https://streampipes.org/boundingboxes"),
                    Labels.withId(BOX_ARRAY_PROPERTY),
                    PropertyScope.NONE)
            .build();
  }
}
