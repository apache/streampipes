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

package org.apache.streampipes.model.connect.rules.schema;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;

@Deprecated(since = "0.99.0", forRemoval = true)
public class MoveRuleDescription extends SchemaTransformationRuleDescription {

  private String oldRuntimeKey;

  private String newRuntimeKey;

  public MoveRuleDescription() {
    super();
  }

  public MoveRuleDescription(String oldRuntimeKey, String newRuntimeKey) {
    super();
    this.oldRuntimeKey = oldRuntimeKey;
    this.newRuntimeKey = newRuntimeKey;
  }

  public MoveRuleDescription(MoveRuleDescription other) {
    super(other);
    this.oldRuntimeKey = other.getOldRuntimeKey();
    this.newRuntimeKey = other.getNewRuntimeKey();
  }

  public String getOldRuntimeKey() {
    return oldRuntimeKey;
  }

  public void setOldRuntimeKey(String oldRuntimeKey) {
    this.oldRuntimeKey = oldRuntimeKey;
  }

  public String getNewRuntimeKey() {
    return newRuntimeKey;
  }

  public void setNewRuntimeKey(String newRuntimeKey) {
    this.newRuntimeKey = newRuntimeKey;
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.MOVE.getCode();
  }

  /**
   * Accepts a visitor to apply this transformation rule with validation.
   * Validates that both oldRuntimeKey and newRuntimeKey are set before processing.
   *
   * @param visitor The transformation rule visitor
   * @throws IllegalArgumentException if required properties are not set
   */
  public void accept(ITransformationRuleVisitor visitor) {
    // Validate that required properties are set
    if (this.oldRuntimeKey == null || this.oldRuntimeKey.isEmpty()) {
      throw new IllegalArgumentException(
          "MoveRuleDescription requires oldRuntimeKey to be set"
      );
    }
    if (this.newRuntimeKey == null || this.newRuntimeKey.isEmpty()) {
      throw new IllegalArgumentException(
          "MoveRuleDescription requires newRuntimeKey to be set"
      );
    }
    // Call the visitor to process this rule
    visitor.visit(this);
  }

  /**
   * Updates property paths when nested structures change.
   * This is useful when intermediate structures are moved or renamed.
   *
   * @param oldPrefix The old path prefix to search for
   * @param newPrefix The new path prefix to replace with
   */
  public void updatePathsAfterMove(String oldPrefix, String newPrefix) {
    if (oldPrefix != null && newPrefix != null) {
      if (this.oldRuntimeKey != null && this.oldRuntimeKey.startsWith(oldPrefix + ".")) {
        this.oldRuntimeKey = this.oldRuntimeKey.replace(oldPrefix + ".", newPrefix + ".");
      }
      if (this.newRuntimeKey != null && this.newRuntimeKey.startsWith(oldPrefix + ".")) {
        this.newRuntimeKey = this.newRuntimeKey.replace(oldPrefix + ".", newPrefix + ".");
      }
    }
  }
}

