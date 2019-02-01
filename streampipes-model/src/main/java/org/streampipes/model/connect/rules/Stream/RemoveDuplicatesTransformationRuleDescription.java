/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.model.connect.rules.Stream;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.REMOVE_DUPLICATES_RULE_DESCRIPTION)
@Entity
public class RemoveDuplicatesTransformationRuleDescription extends StreamTransformationRuleDescription {

    @RdfProperty(StreamPipes.FILTER_TIME_WINDOW)
    private String filterTimeWindow;


    public RemoveDuplicatesTransformationRuleDescription() {
        super();
    }

    public RemoveDuplicatesTransformationRuleDescription(RemoveDuplicatesTransformationRuleDescription other) {
        super(other);
        this.filterTimeWindow = other.getFilterTimeWindow();
    }

    public String getFilterTimeWindow() {
        return filterTimeWindow;
    }

    public void setFilterTimeWindow(String filterTimeWindow) {
        this.filterTimeWindow = filterTimeWindow;
    }
}
