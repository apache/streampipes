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


package org.apache.streampipes.manager.recommender;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllElementsProvider {

  private final List<NamedStreamPipesEntity> allElements;

  public AllElementsProvider(Pipeline pipeline) {
    this.allElements = Stream.of(
            pipeline.getStreams(),
            pipeline.getSepas(),
            pipeline.getActions())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  public List<NamedStreamPipesEntity> getAllElements() {
    return allElements;
  }

  public NamedStreamPipesEntity findElement(String domId) {
    return this.allElements
        .stream()
        .filter(p -> p.getDom().equals(domId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
