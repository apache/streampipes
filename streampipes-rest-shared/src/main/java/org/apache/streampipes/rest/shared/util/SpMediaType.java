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
package org.apache.streampipes.rest.shared.util;

import jakarta.ws.rs.core.MediaType;

public class SpMediaType {

  public static final String JSONLD = "application/ld+json";
  public static final MediaType JSONLD_TYPE = new MediaType("application", "ld+json");

  public static final String APPLICATION_ZIP = "application/zip";
  public static final MediaType APPLICATION_ZIP_TYPE = new MediaType("application", "zip");

}
