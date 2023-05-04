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

package org.apache.streampipes.model.connect.adapter;


import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;

import java.util.List;

/**
 * Represents an adapter configuration consisting of parsers and an AdapterDescription
 */
public class AdapterConfiguration extends NamedStreamPipesEntity {

  public AdapterConfiguration() {
  }

  public AdapterConfiguration(AdapterDescription adapterDescription, List<Parser> supportedParsers) {
    this.adapterDescription = adapterDescription;
    this.supportedParsers = supportedParsers;
  }

  private List<Parser> supportedParsers;

  private AdapterDescription adapterDescription;

  public List<Parser> getSupportedParsers() {
    return supportedParsers;
  }

  public void setSupportedParsers(List<Parser> supportedParsers) {
    this.supportedParsers = supportedParsers;
  }

  public AdapterDescription getAdapterDescription() {
    // Add parser configuration to adapter description
    if (supportedParsers != null) {
      var alternatives = supportedParsers.stream()
          .map(parser -> {
            var result = new StaticPropertyAlternative(
                parser.declareDescription().getName(),
                parser.declareDescription().getName(),
                parser.declareDescription().getDescription());

            result.setStaticProperty(parser.declareDescription().getConfig());

            return result;
          })
          .toList();

      if (alternatives.size() > 0) {
        adapterDescription.addConfig(getFormatAlternatives(alternatives));
      }
    }

    return adapterDescription;
  }

  public void setAdapterDescription(AdapterDescription adapterDescription) {
    this.adapterDescription = adapterDescription;
  }

  private StaticPropertyAlternatives getFormatAlternatives(List<StaticPropertyAlternative> alternatives) {
    var alternativesContainer =
        new StaticPropertyAlternatives("format", "Format", "Select the format that is used to parse the events");

    for (int i = 0; i < alternatives.size(); i++) {
      alternatives.get(i).setIndex(i);
    }

    alternativesContainer.setAlternatives(alternatives);

    return alternativesContainer;
  }

}