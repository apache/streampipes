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

public class ElementInfo<G, D> {
    private G description;
    private D invocation;

    public ElementInfo(G description, D invocation) {
        this.description = description;
        this.invocation = invocation;
    }

    public G getDescription() {
        return description;
    }

    public void setDescription(G description) {
        this.description = description;
    }

    public D getInvocation() {
        return invocation;
    }

    public void setInvocation(D invocation) {
        this.invocation = invocation;
    }
}
