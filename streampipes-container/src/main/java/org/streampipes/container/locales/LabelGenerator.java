/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.container.locales;

import static org.streampipes.container.util.LocalesUtil.makePath;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

      if (isConsumable()) {
        ((ConsumableStreamPipesEntity) desc).getStaticProperties().forEach(sp -> {
          sp.setLabel(getTitle(props, sp.getInternalName()));
          sp.setDescription(getDescription(props, sp.getInternalName()));
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
