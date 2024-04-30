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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomServiceTagResolver {

  private final Environment env;

  public CustomServiceTagResolver(Environment env) {
    this.env = env;
  }

  public Set<SpServiceTag> getCustomServiceTags() {
    if (env.getCustomServiceTags().exists()) {
      var serviceTags = env.getCustomServiceTags().getValue();
      return Arrays.stream(
              serviceTags.split(",")
          ).map(serviceTagString -> SpServiceTag.create(SpServiceTagPrefix.CUSTOM, serviceTagString))
          .collect(Collectors.toSet());
    } else {
      return Collections.emptySet();
    }
  }
}
