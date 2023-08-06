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
package org.apache.streampipes.svcdiscovery.api.model;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

public class DefaultSpServiceTags {

  public static final SpServiceTag CORE = SpServiceTag.create(SpServiceTagPrefix.SYSTEM, "core");
  public static final SpServiceTag PE = SpServiceTag.create(SpServiceTagPrefix.SYSTEM, "pe");
  public static final SpServiceTag CONNECT_WORKER = SpServiceTag
      .create(SpServiceTagPrefix.SYSTEM, "connect-worker");
}
