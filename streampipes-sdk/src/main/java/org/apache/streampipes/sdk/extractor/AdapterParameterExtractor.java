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

package org.apache.streampipes.sdk.extractor;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.Parser;
import org.apache.streampipes.sdk.builder.adapter.JsonParser;

// TODO only provisional implementation
public class AdapterParameterExtractor {
  private StaticPropertyExtractor staticPropertyExtractor;

  public AdapterParameterExtractor() {
    super();
  }

  public Parser selectedParser() {
    // TODO implement
    return new JsonParser();
  }

  public static AdapterParameterExtractor from(AdapterDescription adapterDescription) {
    var result = new AdapterParameterExtractor();
    result.setStaticPropertyExtractor(StaticPropertyExtractor.from(adapterDescription.getConfig()));
    return result;
  }

  public void setStaticPropertyExtractor(StaticPropertyExtractor staticPropertyExtractor) {
    this.staticPropertyExtractor = staticPropertyExtractor;
  }

  public StaticPropertyExtractor getStaticPropertyExtractor() {
    return staticPropertyExtractor;
  }
}
