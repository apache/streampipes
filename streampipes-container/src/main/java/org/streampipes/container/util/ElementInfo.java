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

package org.streampipes.container.util;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.base.NamedStreamPipesEntity;

public class ElementInfo {
    private NamedStreamPipesEntity description;
    private InvocableDeclarer invocation;

    public ElementInfo(NamedStreamPipesEntity description, InvocableDeclarer invocation) {
        this.description = description;
        this.invocation = invocation;
    }

    public NamedStreamPipesEntity getDescription() {
        return description;
    }

    public void setDescription(NamedStreamPipesEntity description) {
        this.description = description;
    }

    public InvocableDeclarer getInvocation() {
        return invocation;
    }

    public void setInvocation(InvocableDeclarer invocation) {
        this.invocation = invocation;
    }
}
