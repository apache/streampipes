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

package org.apache.streampipes.connect.shared.preprocessing.transform.schema;

import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchemaEventTransformer implements SchemaTransformationRule {

  private List<RenameTransformationRule> renameTransformationRules;
  private List<CreateNestedTransformationRule> createNestedTransformationRules;
  private List<MoveTransformationRule> moveTransformationRules;
  private List<DeleteTransformationRule> deleteTransformationRules;

  public SchemaEventTransformer(List<TransformationRule> rules) {
    this.renameTransformationRules = new ArrayList<>();
    this.createNestedTransformationRules = new ArrayList<>();
    this.moveTransformationRules = new ArrayList<>();
    this.deleteTransformationRules = new ArrayList<>();

    for (TransformationRule rule : rules) {
      if (rule instanceof RenameTransformationRule) {
        this.renameTransformationRules.add((RenameTransformationRule) rule);
      } else if (rule instanceof CreateNestedTransformationRule) {
        this.createNestedTransformationRules.add((CreateNestedTransformationRule) rule);
      } else if (rule instanceof MoveTransformationRule) {
        this.moveTransformationRules.add((MoveTransformationRule) rule);
      } else if (rule instanceof DeleteTransformationRule) {
        this.deleteTransformationRules.add((DeleteTransformationRule) rule);
      }
    }
  }


  public SchemaEventTransformer(List<RenameTransformationRule> renameTransformationRules,
                                List<CreateNestedTransformationRule> createNestedTransformationRules,
                                List<MoveTransformationRule> moveTransformationRules,
                                List<DeleteTransformationRule> deleteTransformationRules) {
    this.renameTransformationRules = renameTransformationRules;
    this.createNestedTransformationRules = createNestedTransformationRules;
    this.moveTransformationRules = moveTransformationRules;
    this.deleteTransformationRules = deleteTransformationRules;
  }


  @Override
  public Map<String, Object> transform(Map<String, Object> event) {

    for (RenameTransformationRule renameRule : renameTransformationRules) {
      event = renameRule.transform(event);
    }

    for (CreateNestedTransformationRule createRule : createNestedTransformationRules) {
      event = createRule.transform(event);
    }

    for (MoveTransformationRule moveRule : moveTransformationRules) {
      event = moveRule.transform(event);
    }

    for (DeleteTransformationRule deleteRule : deleteTransformationRules) {
      event = deleteRule.transform(event);
    }

    return event;
  }


  public List<RenameTransformationRule> getRenameTransformationRules() {
    return renameTransformationRules;
  }

  public void setRenameTransformationRules(List<RenameTransformationRule> renameTransformationRules) {
    this.renameTransformationRules = renameTransformationRules;
  }

  public List<CreateNestedTransformationRule> getCreateNestedTransformationRules() {
    return createNestedTransformationRules;
  }

  public void setCreateNestedTransformationRules(List<CreateNestedTransformationRule> createNestedTransformationRules) {
    this.createNestedTransformationRules = createNestedTransformationRules;
  }

  public List<MoveTransformationRule> getMoveTransformationRules() {
    return moveTransformationRules;
  }

  public void setMoveTransformationRules(List<MoveTransformationRule> moveTransformationRules) {
    this.moveTransformationRules = moveTransformationRules;
  }

  public List<DeleteTransformationRule> getDeleteTransformationRules() {
    return deleteTransformationRules;
  }

  public void setDeleteTransformationRules(List<DeleteTransformationRule> deleteTransformationRules) {
    this.deleteTransformationRules = deleteTransformationRules;
  }
}
