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

package org.apache.streampipes.sources;


import org.apache.streampipes.extensions.api.declarer.DataStreamDeclarer;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;

public abstract class AbstractAlreadyExistingStream implements DataStreamDeclarer {

  @Override
  public void executeStream() {

  }

  @Override
  public boolean isExecutable() {
    return false;
  }

  public ConfigExtractor configExtractor() {
    return ConfigExtractor.from(DeclarersSingleton.getInstance().getServiceDefinition().getServiceGroup());
  }
}
