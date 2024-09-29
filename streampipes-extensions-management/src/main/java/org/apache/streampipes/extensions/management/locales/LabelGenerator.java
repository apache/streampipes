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
package org.apache.streampipes.extensions.management.locales;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.assets.DefaultAssetResolver;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class LabelGenerator<T extends NamedStreamPipesEntity> {

  protected static final String DELIMITER = ".";
  protected static final String TITLE = "title";
  protected static final String DESCRIPTION = "description";

  private final T desc;

  private final boolean replaceTitles;

  private final AssetResolver assetResolver;

  public LabelGenerator(T desc, boolean replaceTitles) {
    this.desc = desc;
    this.replaceTitles = replaceTitles;
    this.assetResolver = new DefaultAssetResolver(desc.getAppId());
  }

  public LabelGenerator(T desc, boolean replaceTitles, AssetResolver assetResolver) {
    this.desc = desc;
    this.replaceTitles = replaceTitles;
    this.assetResolver = assetResolver;
  }

  public LabelGenerator(T desc) {
    this(desc, true);
  }

  public T generateLabels() throws IOException {
    if (desc.isIncludesLocales()) {
      Properties props = loadResourceAndMakeProperties();
      if (replaceTitles) {
        desc.setName(getTitle(props, desc.getAppId()));
        desc.setDescription(getDescription(props, desc.getAppId()));
      }

      if (isAdapter()) {
        ((AdapterDescription) desc).getConfig().forEach(sp -> generateLabels(props, sp));
      }

      if (isConsumable()) {
        ((ConsumableStreamPipesEntity) desc).getStaticProperties().forEach(sp -> {
          generateLabels(props, sp);
        });
      }

      if (isInvocable()) {
        ((InvocableStreamPipesEntity) desc).getStaticProperties().forEach(sp -> generateLabels(props, sp));
        if (isDataProcessorInvocation()) {
          applyOutputStrategies(((DataProcessorInvocation) desc).getOutputStrategies(), props);
        }
      }

      if (isDataProcessorDescription()) {
        applyOutputStrategies(((DataProcessorDescription) desc).getOutputStrategies(), props);
      }
    }
    return desc;
  }

  private void applyOutputStrategies(List<OutputStrategy> outputStrategies, Properties props) {
    outputStrategies.forEach(os -> {
      if (os instanceof AppendOutputStrategy) {
        ((AppendOutputStrategy) os).getEventProperties().forEach(ep -> {
          ep.setLabel(getTitle(props, ep.getRuntimeId()));
          ep.setDescription(getDescription(props, ep.getRuntimeId()));
        });
      } else if (os instanceof FixedOutputStrategy) {
        ((FixedOutputStrategy) os).getEventProperties().forEach(ep -> {
          ep.setLabel(getTitle(props, ep.getRuntimeId()));
          ep.setDescription(getDescription(props, ep.getRuntimeId()));
        });
      }
    });
  }

  /**
   * Returns the tile of the element description based on the data of the resource files
   */
  public String getElementTitle() throws IOException {
    var props = loadResourceAndMakeProperties();
    return getTitle(props, desc.getAppId());
  }

  /**
   * Returns the description of the element description based on the data of the resource files
   */
  public String getElementDescription() throws IOException {
    var props = loadResourceAndMakeProperties();
    return getDescription(props, desc.getAppId());
  }

  private StaticProperty generateLabels(Properties props, StaticProperty sp) {
    sp.setLabel(getTitle(props, sp.getInternalName(), sp.getLabel()));
    sp.setDescription(getDescription(props, sp.getInternalName(), sp.getDescription()));
    if (sp instanceof CollectionStaticProperty) {

      if (((CollectionStaticProperty) sp).getMembers() != null) {
        ((CollectionStaticProperty) sp).getMembers().forEach(a -> {
          generateLabels(props, a);
        });
      } else {
        ((StaticPropertyGroup) ((CollectionStaticProperty) sp).getStaticPropertyTemplate()).getStaticProperties()
                .forEach(a -> {
                  generateLabels(props, a);
                });
      }

    } else if (sp instanceof StaticPropertyGroup) {
      ((StaticPropertyGroup) sp).getStaticProperties().forEach(g -> {
        g.setLabel(getTitle(props, g.getInternalName(), g.getLabel()));
        g.setDescription(getDescription(props, g.getInternalName(), g.getDescription()));
      });
    } else if (sp instanceof StaticPropertyAlternatives) {
      ((StaticPropertyAlternatives) sp).getAlternatives().forEach(a -> {
        generateLabels(props, a);
      });
    } else if (sp instanceof StaticPropertyAlternative) {
      if (((StaticPropertyAlternative) sp).getStaticProperty() != null) {
        generateLabels(props, ((StaticPropertyAlternative) sp).getStaticProperty());
      }
    }

    return sp;
  }

  protected Properties loadResourceAndMakeProperties() throws IOException {
    if (!desc.getIncludedLocales().isEmpty()) {
      return assetResolver.getLocale(desc.getIncludedLocales().get(0));
    } else {
      throw new IOException("No locales file defined");
    }
  }

  private boolean isConsumable() {
    return desc instanceof ConsumableStreamPipesEntity;
  }

  private boolean isDataProcessorDescription() {
    return desc instanceof DataProcessorDescription;
  }

  private boolean isDataProcessorInvocation() {
    return desc instanceof DataProcessorInvocation;
  }

  private boolean isInvocable() {
    return desc instanceof InvocableStreamPipesEntity;
  }

  private boolean isAdapter() {
    return desc instanceof AdapterDescription;
  }

  private String getTitle(Properties props, String id, String defaultValue) {
    return getValue(props, TITLE, id, defaultValue);
  }

  private String getTitle(Properties props, String id) {
    return getValue(props, TITLE, id, "");
  }

  private String getDescription(Properties props, String id) {
    return getValue(props, DESCRIPTION, id, "");
  }

  private String getDescription(Properties props, String id, String defaultValue) {
    return getValue(props, DESCRIPTION, id, defaultValue);
  }

  private String getValue(Properties props, String type, String id, String defaultValue) {
    return props.getProperty(id + DELIMITER + type, defaultValue);
  }

}
