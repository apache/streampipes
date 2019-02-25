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

package org.streampipes.processors.statistics.flink.extensions;

import org.apache.flink.api.java.functions.KeySelector;
import org.streampipes.model.runtime.Event;

import java.io.Serializable;

public class MapKeySelector implements Serializable {

  private String groupBy;

  public MapKeySelector(String groupBy) {
    this.groupBy = groupBy;
  }

  public KeySelector<Event, String> getKeySelector() {
    return new KeySelector<Event, String>() {
      @Override
      public String getKey(Event in) throws Exception {
        return in.getFieldBySelector(groupBy).getAsPrimitive().getAsString();
      }
    };
  }
}
