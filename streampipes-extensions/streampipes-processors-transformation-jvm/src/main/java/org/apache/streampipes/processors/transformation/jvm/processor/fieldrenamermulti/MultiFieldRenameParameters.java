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

package org.apache.streampipes.processors.transformation.jvm.processor.fieldrenamermulti;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MultiFieldRenameParameters {

  private final List<Pair<String, String>> mappings;

  public MultiFieldRenameParameters(IDataProcessorParameters params) {
    var extractor = params.extractor();
    List<String> oldFields = extractor.singleValueParameterFromCollection(MultiFieldRenameDeclarer.OLD_FIELD, String.class);
    List<String> newFields = extractor.singleValueParameterFromCollection(MultiFieldRenameDeclarer.NEW_FIELD, String.class);
    var temp = new java.util.ArrayList<Pair<String, String>>();
    int size = Math.min(oldFields.size(), newFields.size());
    for (int i = 0; i < size; i++) {
      temp.add(Pair.of(oldFields.get(i), newFields.get(i)));
    }
    this.mappings = java.util.Collections.unmodifiableList(temp);
  }

  public List<Pair<String, String>> getMappings() {
    return mappings;
  }
}




