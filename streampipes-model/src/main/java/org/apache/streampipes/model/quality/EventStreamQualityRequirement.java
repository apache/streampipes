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

package org.apache.streampipes.model.quality;

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;

public class EventStreamQualityRequirement extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = 1484115035721357275L;

  private transient EventStreamQualityDefinition minimumStreamQuality;

  private transient EventStreamQualityDefinition maximumStreamQuality;

  public EventStreamQualityRequirement(EventStreamQualityDefinition minimumStreamQuality,
                                       EventStreamQualityDefinition maximumStreamQuality) {
    super();
    //TODO check that minimum and maximum have the same type
    this.minimumStreamQuality = minimumStreamQuality;
    this.maximumStreamQuality = maximumStreamQuality;
  }

  public EventStreamQualityRequirement(EventStreamQualityRequirement other) {
    super(other);
    //this.minimumStreamQuality = other.getMinimumStreamQuality();
    //this.maximumStreamQuality = other.getMaximumStreamQuality();
  }

  public EventStreamQualityRequirement() {
    super();
  }

  public EventStreamQualityDefinition getMinimumStreamQuality() {
    return minimumStreamQuality;
  }

  public void setMinimumStreamQuality(EventStreamQualityDefinition minimumStreamQuality) {
    this.minimumStreamQuality = minimumStreamQuality;
  }

  public EventStreamQualityDefinition getMaximumStreamQuality() {
    return maximumStreamQuality;
  }

  public void setMaximumStreamQuality(EventStreamQualityDefinition maximumStreamQuality) {
    this.maximumStreamQuality = maximumStreamQuality;
  }
}
