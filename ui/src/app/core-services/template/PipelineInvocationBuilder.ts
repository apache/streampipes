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
import {
    FreeTextStaticProperty,
    MappingPropertyUnary,
    PipelineTemplateInvocation,
} from '@streampipes/platform-services';

export class PipelineInvocationBuilder {
    private pipelineTemplateInvocation: PipelineTemplateInvocation;

    constructor(pipelineTemplateInvocation: PipelineTemplateInvocation) {
        this.pipelineTemplateInvocation = pipelineTemplateInvocation;
    }

    public static create(
        pipelineTemplateInvocation: PipelineTemplateInvocation,
    ) {
        return new PipelineInvocationBuilder(pipelineTemplateInvocation);
    }

    public setTemplateId(id: string) {
        this.pipelineTemplateInvocation.pipelineTemplateId = id;
        return this;
    }

    public setName(name: string) {
        this.pipelineTemplateInvocation.kviName = name;
        return this;
    }

    public setFreeTextStaticProperty(name: string, value: string) {
        this.pipelineTemplateInvocation.staticProperties.forEach(property => {
            if (
                property instanceof FreeTextStaticProperty &&
                'jsplumb_domId2' + name === property.internalName
            ) {
                property.value = value;
            }
        });

        return this;
    }

    public setMappingPropertyUnary(name: string, value: string) {
        this.pipelineTemplateInvocation.staticProperties.forEach(property => {
            if (
                property instanceof MappingPropertyUnary &&
                'jsplumb_domId2' + name === property.internalName
            ) {
                property.selectedProperty = value;
            }
        });

        return this;
    }

    public build() {
        return this.pipelineTemplateInvocation;
    }
}
