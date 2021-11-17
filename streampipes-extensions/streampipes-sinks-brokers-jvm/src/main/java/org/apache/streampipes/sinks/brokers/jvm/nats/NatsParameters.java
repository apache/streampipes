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

package org.apache.streampipes.sinks.brokers.jvm.nats;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class NatsParameters extends EventSinkBindingParams {

    private String natsUrls;
    private String subject;
    private String username;
    private String password;
    private String properties;

    public NatsParameters(DataSinkInvocation graph, String natsUrls, String subject, String username, String password,
                          String properties) {
        super(graph);
        this.natsUrls = natsUrls;
        this.subject = subject;
        this.username = username;
        this.password = password;
        this.properties = properties;
    }

    public String getNatsUrls() {
        return natsUrls;
    }

    public String getSubject() {
        return subject;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProperties() {
        return properties;
    }
}
