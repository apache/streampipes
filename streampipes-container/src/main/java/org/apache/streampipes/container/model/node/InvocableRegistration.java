package org.apache.streampipes.container.model.node;/*
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

import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;

import java.util.List;

public class InvocableRegistration {

    private ConsulServiceRegistrationBody consulServiceRegistrationBody;
    private List<String> supportedPipelineElementAppIds;

    public InvocableRegistration() {
    }

    public InvocableRegistration(ConsulServiceRegistrationBody consulServiceRegistrationBody,
                                 List<String> supportedPipelineElementAppIds) {
        this.consulServiceRegistrationBody = consulServiceRegistrationBody;
        this.supportedPipelineElementAppIds = supportedPipelineElementAppIds;
    }

    public ConsulServiceRegistrationBody getConsulServiceRegistrationBody() {
        return consulServiceRegistrationBody;
    }

    public void setConsulServiceRegistrationBody(ConsulServiceRegistrationBody consulServiceRegistrationBody) {
        this.consulServiceRegistrationBody = consulServiceRegistrationBody;
    }

    public List<String> getSupportedPipelineElementAppIds() {
        return supportedPipelineElementAppIds;
    }

    public void setSupportedPipelineElementAppIds(List<String> supportedPipelineElementAppIds) {
        this.supportedPipelineElementAppIds = supportedPipelineElementAppIds;
    }
}
