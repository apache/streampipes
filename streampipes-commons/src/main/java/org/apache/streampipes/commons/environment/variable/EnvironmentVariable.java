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

package org.apache.streampipes.commons.environment.variable;

import org.apache.streampipes.commons.constants.CustomEnvs;
import org.apache.streampipes.commons.constants.Envs;

public abstract class EnvironmentVariable<T> {

  private final String unparsedDefaultValue;
  private final String envVariableName;
  private boolean devModeActive;

  public EnvironmentVariable(Envs envVariable) {
    this.envVariableName = envVariable.getEnvVariableName();
    this.devModeActive = isDevModeActive();
    this.unparsedDefaultValue = devModeActive ? envVariable.getDevDefaultValue() : envVariable.getDefaultValue();
  }

  public T getValue() {
    return parse(CustomEnvs.getEnv(envVariableName));
  }

  public boolean exists() {
    return CustomEnvs.exists(envVariableName);
  }

  public T getValueOrDefault() {
    return exists() ? getValue() : parse(unparsedDefaultValue);
  }

  public T getValueOrReturn(T defaultValue) {
    return exists() ? getValue() : defaultValue;
  }

  public T getValueOrResolve(EnvResolver<T> resolver) {
    return resolver.resolve();
  }

  public T getDefault() {
    return parse(unparsedDefaultValue);
  }

  public String getEnvVariableName() {
    return this.envVariableName;
  }

  private boolean isDevModeActive() {
    return CustomEnvs.getEnvAsBoolean(Envs.SP_DEBUG.getEnvVariableName());
  }

  public abstract T parse(String value);

}
