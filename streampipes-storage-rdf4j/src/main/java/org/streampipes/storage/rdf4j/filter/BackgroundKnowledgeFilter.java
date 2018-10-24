/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.rdf4j.filter;

import org.streampipes.model.client.ontology.OntologyNode;
import org.streampipes.model.client.ontology.Property;
import org.streampipes.storage.rdf4j.util.BackgroundKnowledgeUtils;
import org.streampipes.vocabulary.StreamPipes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BackgroundKnowledgeFilter {

  public static List<String> omittedPropertyPrefixes = Arrays.asList(StreamPipes.NS,
          "http://schema.org/Thing", "http://purl.oclc.org/NET/ssnx/ssn#",
          "http://sepa.event-processing.org/sepa#",
          "http://www.w3.org/2000/01/rdf-schema#",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

  public static List<OntologyNode> propertiesFilter(List<OntologyNode> nodes, boolean filterDuplicates) {
    List<OntologyNode> filteredList = nodes
            .stream()
            .filter(n -> omittedPropertyPrefixes
                    .stream()
                    .noneMatch(op -> n.getId().startsWith(op)))
            .collect(Collectors.toList());
    if (filterDuplicates) {
      return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
    } else {
      return filteredList;
    }
  }

  public static List<Property> rdfsFilter(List<Property> properties, boolean filterDuplicates) {
    List<Property> filteredList = properties
            .stream()
            .filter(n -> omittedPropertyPrefixes
                    .stream()
                    .noneMatch(op -> n.getElementHeader()
                            .getId()
                            .startsWith(op)))
            .collect(Collectors.toList());
    if (filterDuplicates) {
      return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
    } else {
      return filteredList;
    }
  }


  public static List<OntologyNode> classFilter(List<OntologyNode> nodes, boolean filterDuplicates) {
    List<OntologyNode> filteredList = nodes
            .stream()
            .filter(n -> omittedPropertyPrefixes
                    .stream()
                    .noneMatch(op -> n
                            .getId()
                            .startsWith(op)))
            .collect(Collectors.toList());
    if (filterDuplicates) {
      return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
    } else {
      return filteredList;
    }
  }
}
