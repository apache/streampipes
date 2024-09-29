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
package org.apache.streampipes.service.core;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI openApiDocsConfiguration(@Value("${app.version}") String appVersion) {
    return new OpenAPI()
            .components(new Components()
                    .addSecuritySchemes("bearerAuth",
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer"))
                    .parameters(makeAuthParams()))
            .info(new Info().title("Apache StreamPipes API")
                    .description("This is the documentation of the Apache StreamPipes developer API.")
                    .version(appVersion).contact(new Contact().email("dev@streampipes.apache.org"))
                    .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")));
  }

  private Map<String, Parameter> makeAuthParams() {
    var map = new HashMap<String, Parameter>();
    map.put("usernameParam",
            new Parameter().in("path").name("username").required(true)
                    .schema(new Schema<String>().type("string").minimum(BigDecimal.valueOf(0))
                            .description("The username (currently the email address of the logged in user")));

    return map;
  }
}
