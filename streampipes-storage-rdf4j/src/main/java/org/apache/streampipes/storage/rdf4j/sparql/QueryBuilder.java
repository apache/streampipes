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

package org.apache.streampipes.storage.rdf4j.sparql;

public class QueryBuilder {

  public static String buildListDataStreamQuery() {
    return "where { ?result rdf:type sp:DataStream. }";
  }

  public static String buildListDataSetQuery() {
    return "where { ?result rdf:type sp:DataSet. }";
  }

  private static String getPrefix() {
    return "PREFIX sp:<https://streampipes.org/vocabulary/v1/>\n" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
  }

  public static String getMatching() {
    return getPrefix() +
            "select ?d where {?a rdf:type sp:EventProperty. ?b rdf:type sp:EventSchema. ?b " +
            "sp:hasEventProperty ?a. ?a sp:hasPropertyName ?d}";
  }

  public static String buildListSEPAQuery() {
    return "where { ?result rdf:type sp:DataProcessorDescription. }";
  }

  public static String buildListSECQuery() {
    return "where { ?result rdf:type sp:DataSinkDescription }";
  }

  public static String getClasses() {
    return getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/2000/01/rdf-schema#Class>}";
  }

  public static String getEventProperties() {
    return getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>}";
  }

}
