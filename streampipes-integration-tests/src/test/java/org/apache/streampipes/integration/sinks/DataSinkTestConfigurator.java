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

package org.apache.streampipes.integration.sinks;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DataSinkTestConfigurator {

  public List<StaticProperty> prepareStaticProperties(IStreamPipesDataSink sink,
                                                      List<SpDataStream> inputStreams)
      throws SpConfigurationException {
    List<StaticProperty> staticProperties = new Cloner().staticProperties(sink.declareConfig()
        .getDescription()
        .getStaticProperties());

    if (!(sink instanceof SupportsRuntimeConfig supportsRuntimeConfig)) {
      return staticProperties;
    }

    List<StaticProperty> resolvedProperties = new ArrayList<>(staticProperties);
    for (int i = 0; i < resolvedProperties.size(); i++) {
      StaticProperty staticProperty = resolvedProperties.get(i);
      if (isRuntimeResolvable(staticProperty)) {
        resolvedProperties.set(
            i,
            supportsRuntimeConfig.resolveConfiguration(
                staticProperty.getInternalName(),
                StaticPropertyExtractor.from(
                    resolvedProperties,
                    inputStreams,
                    sink.declareConfig().getDescription().getAppId()
                )
            )
        );
      }
    }

    return resolvedProperties;
  }

  public <T extends StaticProperty> T requireProperty(List<StaticProperty> staticProperties,
                                                      String internalName,
                                                      Class<T> propertyType) {
    return requireProperty(staticProperties, property -> internalName.equals(property.getInternalName()), propertyType);
  }

  public <T extends StaticProperty> T requireProperty(StaticPropertyGroup group,
                                                      String internalName,
                                                      Class<T> propertyType) {
    return requireProperty(group.getStaticProperties(), internalName, propertyType);
  }

  public <T extends StaticProperty> T requirePropertyMatching(StaticPropertyGroup group,
                                                              Predicate<StaticProperty> predicate,
                                                              Class<T> propertyType) {
    return requireProperty(group.getStaticProperties(), predicate, propertyType);
  }

  public void selectAlternative(StaticPropertyAlternatives alternatives,
                                String internalName) {
    boolean found = false;
    for (StaticPropertyAlternative alternative : alternatives.getAlternatives()) {
      boolean selected = internalName.equals(alternative.getInternalName());
      alternative.setSelected(selected);
      if (selected) {
        found = true;
      }
    }

    if (!found) {
      throw new IllegalArgumentException("Could not find alternative " + internalName);
    }
  }

  public StaticPropertyGroup addCollectionMember(CollectionStaticProperty collectionStaticProperty) {
    StaticPropertyGroup member =
        (StaticPropertyGroup) new Cloner().staticProperty(collectionStaticProperty.getStaticPropertyTemplate());
    if (collectionStaticProperty.getMembers() == null) {
      collectionStaticProperty.setMembers(new ArrayList<>());
    }
    collectionStaticProperty.getMembers().add(member);
    return member;
  }

  private <T extends StaticProperty> T requireProperty(List<StaticProperty> staticProperties,
                                                       Predicate<StaticProperty> predicate,
                                                       Class<T> propertyType) {
    return staticProperties.stream()
        .filter(predicate)
        .filter(propertyType::isInstance)
        .map(propertyType::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find static property"));
  }

  private boolean isRuntimeResolvable(StaticProperty staticProperty) {
    return staticProperty instanceof RuntimeResolvableGroupStaticProperty
        || staticProperty instanceof RuntimeResolvableOneOfStaticProperty
        || staticProperty instanceof RuntimeResolvableAnyStaticProperty
        || staticProperty instanceof RuntimeResolvableTreeInputStaticProperty;
  }
}
