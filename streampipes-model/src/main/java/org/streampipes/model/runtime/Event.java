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
package org.streampipes.model.runtime;

import org.streampipes.model.constants.PropertySelectorConstants;
import org.streampipes.model.runtime.field.AbstractField;

import java.util.List;
import java.util.Map;

public class Event {

  private Map<String, Object> runtimeMap;
  private SourceInfo sourceInfo;
  private SchemaInfo schemaInfo;
  private Map<String, AbstractField> fieldMap;

  public Event(Map<String, Object> runtimeMap, Map<String, AbstractField> fieldMap, SourceInfo
          sourceInfo,
               SchemaInfo schemaInfo) {
    this.runtimeMap = runtimeMap;
    this.fieldMap = fieldMap;
    this.sourceInfo = sourceInfo;
    this.schemaInfo = schemaInfo;
  }

  public Map<String, AbstractField> getFields() {
    return fieldMap;
  }

  public Map<String, Object> getRaw() {
    return runtimeMap;
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public SchemaInfo getSchemaInfo() {
    return schemaInfo;
  }

  public AbstractField getFieldByRuntimeName(String runtimeName) {
    return fieldMap.get(addSelectorPrefix(runtimeName));
  }

  private String addSelectorPrefix(String runtimeName) {
    return sourceInfo.getSelectorPrefix()
            + PropertySelectorConstants.PROPERTY_DELIMITER
            + runtimeName;
  }

  public AbstractField getFieldBySelector(String fieldSelector) {
    return getFieldBySelector(fieldSelector, fieldMap);
  }

  private AbstractField getFieldBySelector(String fieldSelector, Map<String, AbstractField>
          currentFieldMap) {
    if (currentFieldMap.containsKey(fieldSelector)) {
      return currentFieldMap.get(fieldSelector);
    } else {
      return getFieldBySelector(fieldSelector, getNestedItem(fieldSelector, currentFieldMap));
    }
  }

  private Map<String, AbstractField> getNestedItem(String fieldSelector, Map<String,
          AbstractField> currentFieldMap) {
    String key = currentFieldMap.keySet().stream().filter(fieldSelector::startsWith)
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Key not found"));
    return currentFieldMap.get(key).getAsComposite().getRawValue();
  }

  private AbstractField getField(Integer position, String[] selectorParts, Map<String,
          AbstractField> map) {
    if (position == selectorParts.length - 1) {
      return map.get(selectorParts[position]);
    } else {
      return getField(position + 1, selectorParts, map.get(selectorParts[position])
              .getAsComposite().getRawValue());
    }
  }


  public void updateFieldByRuntimeName(String runtimeName, AbstractField field) {
    this.runtimeMap.put(runtimeName, field.getRawValue());
  }

  public void updateFieldBySelector(String selector, AbstractField field) {
    // TODO

  }

  public void addField(AbstractField field) {
    this.fieldMap.put(makeKey(field), field);
  }

  private String makeKey(AbstractField field) {
    return sourceInfo.getSelectorPrefix() + PropertySelectorConstants.PROPERTY_DELIMITER +field
            .getFieldNameIn();
  }

  public Event getSubset(List<String> fieldSelectors) {
    // TODO
    return null;
  }
}
