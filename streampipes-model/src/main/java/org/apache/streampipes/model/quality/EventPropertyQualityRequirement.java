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

import java.util.Objects;

public class EventPropertyQualityRequirement extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = -8173312776233284351L;

  private transient EventPropertyQualityDefinition minimumPropertyQuality;

  private transient EventPropertyQualityDefinition maximumPropertyQuality;

  public EventPropertyQualityRequirement() {
    super();
  }

  public EventPropertyQualityRequirement(
      EventPropertyQualityDefinition minimumPropertyQuality,
      EventPropertyQualityDefinition maximumPropertyQuality) {

    super();
    this.minimumPropertyQuality = minimumPropertyQuality;
    this.maximumPropertyQuality = maximumPropertyQuality;
  }

  public EventPropertyQualityRequirement(EventPropertyQualityRequirement other) {
    super(other);
    //this.minimumPropertyQuality = other.getMinimumPropertyQuality();
    //this.maximumPropertyQuality = other.getMaximumPropertyQuality();
  }

  public EventPropertyQualityDefinition getMinimumPropertyQuality() {
    return minimumPropertyQuality;
  }

  public void setMinimumPropertyQuality(
      EventPropertyQualityDefinition minimumPropertyQuality) {
    this.minimumPropertyQuality = minimumPropertyQuality;
  }

  public EventPropertyQualityDefinition getMaximumPropertyQuality() {
    return maximumPropertyQuality;
  }

  public void setMaximumPropertyQuality(
      EventPropertyQualityDefinition maximumPropertyQuality) {
    this.maximumPropertyQuality = maximumPropertyQuality;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventPropertyQualityRequirement that = (EventPropertyQualityRequirement) o;
    return Objects.equals(minimumPropertyQuality, that.minimumPropertyQuality)
        && Objects.equals(maximumPropertyQuality, that.maximumPropertyQuality);
  }
}
