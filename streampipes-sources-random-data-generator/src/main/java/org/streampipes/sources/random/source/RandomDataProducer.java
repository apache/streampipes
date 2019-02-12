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

package org.streampipes.sources.random.source;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sources.random.set.RandomNumberDataSet;
import org.streampipes.sources.random.stream.ComplexRandomStream;
import org.streampipes.sources.random.stream.RandomNestedListStream;
import org.streampipes.sources.random.stream.RandomNumberStreamJson;
import org.streampipes.sources.random.stream.RandomNumberStreamList;
import org.streampipes.sources.random.stream.RandomNumberStreamWildcard;

import java.util.Arrays;
import java.util.List;

public class RandomDataProducer implements SemanticEventProducerDeclarer {

  @Override
  public DataSourceDescription declareModel() {
    DataSourceDescription sep = new DataSourceDescription("org.streampipes.sources.random", "Random", "Random Event Producer");
    return sep;
  }

  @Override
  public List<DataStreamDeclarer> getEventStreams() {
    return Arrays.asList(new RandomNumberStreamJson(),
            new RandomNumberStreamList(),
            new RandomNumberStreamWildcard(),
            new RandomNumberDataSet(),
            new ComplexRandomStream(),
            new RandomNestedListStream());
  }
}
