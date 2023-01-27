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

package org.apache.streampipes.commons.environment;

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.environment.variable.BooleanEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

public class DefaultEnvironment implements Environment {

  @Override
  public StringEnvironmentVariable getConsulHost() {
    return new StringEnvironmentVariable(Envs.SP_CONSUL_HOST);
  }

  @Override
  public IntEnvironmentVariable getConsulPort() {
    return new IntEnvironmentVariable(Envs.SP_CONSUL_PORT);
  }

  @Override
  public BooleanEnvironmentVariable getSpDebug() {
    return new BooleanEnvironmentVariable(Envs.SP_DEBUG);
  }

  @Override
  public StringEnvironmentVariable getConsulLocation() {
    return new StringEnvironmentVariable(Envs.SP_CONSUL_LOCATION);
  }
}
