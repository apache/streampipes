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

package org.streampipes.sources.random;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.ArrayList;
import java.util.List;

public class RandomDataProducer implements SemanticEventProducerDeclarer {

  @Override
  public DataSourceDescription declareModel() {
    DataSourceDescription sep = new DataSourceDescription("org.streampipes.sources.random", "Random", "Random Event Producer");
    return sep;
  }


  @Override
  public List<DataStreamDeclarer> getEventStreams() {

    List<DataStreamDeclarer> streams = new ArrayList<>();

    //streams.add(new RandomTextStream());
    streams.add(new RandomNumberStreamJson());
    streams.add(new RandomNumberStreamList());
//		streams.add(new ComplexRandomStream());
    streams.add(new RandomNumberStreamWildcard());
    streams.add(new RandomNumberDataSet());


    return streams;
  }
}
