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

package org.apache.streampipes.vocabulary;


import java.util.Arrays;
import java.util.List;

public class Geonames {

  public static String alternateName = "http://www.geonames.org/ontology#alternateName";
  public static String countryCode = "http://www.geonames.org/ontology#countryCode";
  public static String name = "http://www.geonames.org/ontology#name";
  public static String officialName = "http://www.geonames.org/ontology#officialName";
  public static String population = "http://www.geonames.org/ontology#population";
  public static String postalCode = "http://www.geonames.org/ontology#postalCode";
  public static String shortName = "http://www.geonames.org/ontology#shortName";

  public static List<String> getAll() {
    return Arrays.asList(alternateName,
        countryCode,
        name,
        officialName,
        population,
        postalCode,
        shortName);
  }

}
