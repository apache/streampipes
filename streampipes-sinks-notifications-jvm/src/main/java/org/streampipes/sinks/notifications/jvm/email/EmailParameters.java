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

package org.streampipes.sinks.notifications.jvm.email;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class EmailParameters extends EventSinkBindingParams {

    private String toEmailAddress;
    private String subject;
    private String content;

    public EmailParameters(DataSinkInvocation graph, String toEmailAddress, String subject, String content) {
        super(graph);
        this.toEmailAddress = toEmailAddress;
        this.subject = subject;
        this.content = content;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
