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

package org.streampipes.wrapper.runtime;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.commons.exceptions.SpRuntimeException;

public abstract class PipelineElementRuntime {

  protected String instanceId;

  public PipelineElementRuntime() {
    this.instanceId = RandomStringUtils.randomAlphabetic(8);
  }

  public abstract void prepareRuntime() throws SpRuntimeException;

  public abstract void postDiscard() throws SpRuntimeException;

  public abstract void bindRuntime() throws SpRuntimeException;

  public abstract void discardRuntime() throws SpRuntimeException;
}
