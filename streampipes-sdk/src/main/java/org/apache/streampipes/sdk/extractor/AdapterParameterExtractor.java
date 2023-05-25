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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.List;

public class AdapterParameterExtractor implements IAdapterParameterExtractor {

  private List<IParser> parsers;

  private final StaticPropertyExtractor staticPropertyExtractor;

  private final AdapterDescription adapterDescription;

  public AdapterParameterExtractor(StaticPropertyExtractor extractor,
                                   AdapterDescription adapterDescription) {
    super();
    this.staticPropertyExtractor = extractor;
    this.adapterDescription = adapterDescription;
  }

  @Override
  public IParser selectedParser() throws AdapterException {
    var parserStaticProperties =
        staticPropertyExtractor.getStaticPropertyByName("format");

    if (parserStaticProperties instanceof StaticPropertyAlternatives) {
      var selectedFormat = ((StaticPropertyAlternatives) parserStaticProperties).getAlternatives()
          .stream()
          .filter(StaticPropertyAlternative::getSelected)
          .findFirst()
          .orElseThrow(() -> new AdapterException("No format was selected in adapter configuration"));

      var selectedParser = parsers
          .stream()
          .filter(parser ->
              parser.declareDescription().getName().equals(selectedFormat.getInternalName())
          ).findFirst()
          .orElseThrow(
              () -> new AdapterException("Selected parser is not supported")
          );

      var parserConfigs = ((StaticPropertyGroup) selectedFormat.getStaticProperty()).getStaticProperties();
      return selectedParser.fromDescription(parserConfigs);
    } else {
      throw new AdapterException("Parser configuration is not found in adapter configuration");
    }
  }

  public static IAdapterParameterExtractor from(AdapterDescription adapterDescription, List<IParser> parsers) {
    var extractor = StaticPropertyExtractor.from(adapterDescription.getConfig());
    var result = new AdapterParameterExtractor(extractor, adapterDescription);
    result.setParsers(parsers);
    return result;
  }

  @Override
  public StaticPropertyExtractor getStaticPropertyExtractor() {
    return staticPropertyExtractor;
  }

  @Override
  public AdapterDescription getAdapterDescription() {
    return adapterDescription;
  }

  public void setParsers(List<IParser> parsers) {
    this.parsers = parsers;
  }
}
