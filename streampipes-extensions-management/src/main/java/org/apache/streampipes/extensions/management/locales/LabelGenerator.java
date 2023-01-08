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

import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.apache.streampipes.extensions.management.util.LocalesUtil.makePath;

public class LabelGenerator {

  private static final String Delimiter = ".";
  private static final String Title = "title";
  private static final String Description = "description";

  private NamedStreamPipesEntity desc;

  public LabelGenerator(NamedStreamPipesEntity desc) {
    this.desc = desc;
  }

  public NamedStreamPipesEntity generateLabels() throws IOException {
    if (existsLocalesFile()) {
      Properties props = makeProperties();
      desc.setName(getTitle(props, desc.getAppId()));
      desc.setDescription(getDescription(props, desc.getAppId()));

      if (isAdapter() || isProtocol()) {
        List<StaticProperty> properties;
        if (isAdapter()) {
          properties = ((AdapterDescription) desc).getConfig();
        } else {
          properties = ((ProtocolDescription) desc).getConfig();
        }
        if (properties != null) {
          properties.forEach(sp -> {
            generateLabels(props, sp);
          });
        }
      }


      if (isConsumable()) {
        ((ConsumableStreamPipesEntity) desc).getStaticProperties().forEach(sp -> {
          generateLabels(props, sp);
        });
      }

      if (isDataProcessor()) {
        ((DataProcessorDescription) desc).getOutputStrategies().forEach(os -> {
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
    }

    return desc;
  }

  private StaticProperty generateLabels(Properties props, StaticProperty sp) {
    sp.setLabel(getTitle(props, sp.getInternalName()));
    sp.setDescription(getDescription(props, sp.getInternalName()));
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
        g.setLabel(getTitle(props, g.getInternalName()));
        g.setDescription(getDescription(props, g.getInternalName()));
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

  private Properties makeProperties() throws IOException {
    Properties props = new Properties();
    props.load(loadResource());

    return props;
  }

  public String getElementTitle() throws IOException {
    Properties props = makeProperties();
    return getTitle(props, desc.getAppId());
  }

  public String getElementDescription() throws IOException {
    Properties props = makeProperties();
    return getDescription(props, desc.getAppId());
  }

  private boolean existsLocalesFile() {
    return this.getClass().getClassLoader().getResourceAsStream(makePath(desc,
        this.desc.getIncludedLocales().get(0))) != null;
  }

  private boolean isConsumable() {
    return desc instanceof ConsumableStreamPipesEntity;
  }

  private boolean isDataProcessor() {
    return desc instanceof DataProcessorDescription;
  }

  private boolean isAdapter() {
    return desc instanceof AdapterDescription;
  }

  private boolean isProtocol() {
    return desc instanceof ProtocolDescription;
  }


  private InputStream loadResource() {
    if (desc.getIncludedLocales().size() > 0) {
      return getResourceFile(desc.getIncludedLocales().get(0));
    } else {
      throw new IllegalArgumentException("Could not find any language files");
    }
  }

  private String getTitle(Properties props, String id) {
    return getValue(props, Title, id);
  }

  private String getDescription(Properties props, String id) {
    return getValue(props, Description, id);
  }

  private String getValue(Properties props, String type, String id) {
    return props.getProperty(id + Delimiter + type, "");
  }


  private InputStream getResourceFile(String filename) {
    return this.getClass().getClassLoader().getResourceAsStream(makePath(desc, filename));
  }

}
