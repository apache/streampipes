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

package org.apache.streampipes.model.export;

public class AdapterOverviewEntry extends OverviewEntry {

  private String adapterType;
  private boolean running;
  private String securityMode;
  private String securityPolicy;

  public AdapterOverviewEntry() {
    super();
  }

  public String getAdapterType() {
    return adapterType;
  }

  public void setAdapterType(String adapterType) {
    this.adapterType = adapterType;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public String getSecurityMode() {
    return securityMode;
  }

  public void setSecurityMode(String securityMode) {
    this.securityMode = securityMode;
  }

  public String getSecurityPolicy() {
    return securityPolicy;
  }

  public void setSecurityPolicy(String securityPolicy) {
    this.securityPolicy = securityPolicy;
  }
}
