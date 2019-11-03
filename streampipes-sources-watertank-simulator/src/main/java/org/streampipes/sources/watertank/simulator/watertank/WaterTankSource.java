/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sources.watertank.simulator.watertank;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataSourceBuilder;
import org.streampipes.sources.watertank.simulator.watertank.streams.*;

import java.util.Arrays;
import java.util.List;

public class WaterTankSource  implements SemanticEventProducerDeclarer {

  public DataSourceDescription declareModel() {
    return DataSourceBuilder.create("water-tank", "Water Tank Source", "A data source that " +
            "holds event streams produces by a water tank system.")
            .build();
  }

  public List<DataStreamDeclarer> getEventStreams() {
    return Arrays.asList(new WaterLevel1Stream(),
            new WaterLevel2Stream(),
            new FlowRate1Stream(),
            new PressureTankStream(),
            new FlowRate2Stream());
  }
}
