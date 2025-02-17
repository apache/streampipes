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

package org.apache.streampipes.rest.core.base.impl.converter;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class YamlConfiguration implements WebMvcConfigurer {
  private static final MediaType MEDIA_TYPE_YAML = MediaType.valueOf("application/yaml");
  private static final MediaType MEDIA_TYPE_YML = MediaType.valueOf("application/yml");

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .mediaType(MEDIA_TYPE_YML.getSubtype(), MEDIA_TYPE_YML)
        .mediaType(MEDIA_TYPE_YAML.getSubtype(), MEDIA_TYPE_YAML);
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    YAMLMapper yamlMapper = new YAMLMapper();

    MappingJackson2HttpMessageConverter yamlConverter = new MappingJackson2HttpMessageConverter(yamlMapper);
    yamlConverter.setSupportedMediaTypes(
        List.of(
            MEDIA_TYPE_YML,
            MEDIA_TYPE_YAML
        )
    );
    converters.add(yamlConverter);
  }
}
