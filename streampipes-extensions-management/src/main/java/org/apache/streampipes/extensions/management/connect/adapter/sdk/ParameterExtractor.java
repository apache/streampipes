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

package org.apache.streampipes.extensions.management.connect.adapter.sdk;

import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SelectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import com.github.drapostolos.typeparser.TypeParser;

import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class ParameterExtractor {
  private List<StaticProperty> list;
  private TypeParser typeParser;

  public ParameterExtractor(List<StaticProperty> list) {
    this.list = list;
    this.typeParser = TypeParser.newBuilder().build();
  }

  public String singleValue(String internalName) {
    return (((FreeTextStaticProperty) getStaticPropertyByName(internalName))
        .getValue());
  }

  public String secretValue(String internalName) {
    return (((SecretStaticProperty) getStaticPropertyByName(internalName))
        .getValue());
  }

  public <V> V singleValue(String internalName, Class<V> targetClass) {
    return typeParser.parse(singleValue(internalName), targetClass);
  }

  public String selectedSingleValueInternalName(String internalName) {
    return selectedSingleValueInternalName(((SelectionStaticProperty) getStaticPropertyByName(internalName)));
  }

  private String selectedSingleValueInternalName(SelectionStaticProperty sp) {
    return sp.getOptions()
        .stream()
        .filter(Option::isSelected)
        .findFirst()
        .get()
        .getInternalName();
  }


  public List<String> selectedMultiValues(String internalName) {
    return selectedMultiValues((SelectionStaticProperty) getStaticPropertyByName(internalName));
  }

  public List<String> selectedMultiValues(SelectionStaticProperty sp) {
    return sp.getOptions()
        .stream()
        .filter(Option::isSelected)
        .map(Option::getName)
        .collect(Collectors.toList());
  }


  public String selectedSingleValueOption(String internalName) {
    return selectedMultiValues(internalName).get(0);
  }

  public StaticProperty getStaticPropertyByName(String name) {
    for (StaticProperty p : list) {
      if (p.getInternalName().equals(name)) {
        return p;
      }
    }
    return null;
  }

  // Collection

  public List<String> collectionSingleValue(String internalName) {
    return ((CollectionStaticProperty) getStaticPropertyByName(internalName))
        .getMembers()
        .stream()
        .map(sp -> ((FreeTextStaticProperty) sp).getValue())
        .collect(Collectors.toList());
  }

  public <V> List<V> collectionSingleValue(String internalName, Class<V> targetClass) {
    return ((CollectionStaticProperty) getStaticPropertyByName(internalName))
        .getMembers()
        .stream()
        .map(sp -> typeParser.parse(((FreeTextStaticProperty) sp).getValue(), targetClass))
        .collect(Collectors.toList());
  }

  public List<String> collectionSelectedSingleValueInternalName(String internalName) {
    return ((CollectionStaticProperty) getStaticPropertyByName(internalName))
        .getMembers()
        .stream()
        .map(sp -> selectedSingleValueInternalName(((SelectionStaticProperty) sp)))
        .collect(Collectors.toList());

  }

  public List<List<String>> collectionSelectedMultiValues(String internalName) {
    return ((CollectionStaticProperty) getStaticPropertyByName(internalName))
        .getMembers()
        .stream()
        .map(sp -> selectedMultiValues(((SelectionStaticProperty) sp)))
        .collect(Collectors.toList());
  }

  public List<String> collectionSelectedSingleValueOption(String internalName) {
    return collectionSelectedMultiValues(internalName)
        .stream()
        .map(list -> list.get(0))
        .collect(Collectors.toList());
  }

  public List<ParameterExtractor> collectionGroup(String internalName) {
    return ((CollectionStaticProperty) getStaticPropertyByName(internalName))
        .getMembers()
        .stream()
        .map(sp -> new ParameterExtractor(((StaticPropertyGroup) sp).getStaticProperties()))
        .collect(Collectors.toList());
  }
}
