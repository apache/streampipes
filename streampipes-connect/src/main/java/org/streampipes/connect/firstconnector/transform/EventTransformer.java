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

package org.streampipes.connect.firstconnector.transform;

import java.util.List;
import java.util.Map;

public class EventTransformer implements TransformationRule {

    private List<RenameTransformationRule> renameTransformationRules;
    private List<CreateNestedTransformationRule> createNestedTransformationRules;
    private List<MoveTransformationRule> moveTransformationRules;
    private List<DeleteTransformationRule> deleteTransformationRules;

    public EventTransformer(List<RenameTransformationRule> renameTransformationRules, List<CreateNestedTransformationRule> createNestedTransformationRules, List<MoveTransformationRule> moveTransformationRules, List<DeleteTransformationRule> deleteTransformationRules) {
        this.renameTransformationRules = renameTransformationRules;
        this.createNestedTransformationRules = createNestedTransformationRules;
        this.moveTransformationRules = moveTransformationRules;
        this.deleteTransformationRules = deleteTransformationRules;
    }


    @Override
    public Map<String, Object> transform(Map<String, Object> event) {

        for (RenameTransformationRule renameRule :  renameTransformationRules) {
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
