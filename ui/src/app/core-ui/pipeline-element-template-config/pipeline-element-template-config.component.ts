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

import { Component, Input, OnInit } from '@angular/core';
import {
    PipelineElementTemplate,
    PipelineElementTemplateService,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { PipelineElementTemplateGenerator } from './pipeline-element-template-generator';

@Component({
    selector: 'sp-pipeline-element-template-config',
    templateUrl: './pipeline-element-template-config.component.html',
    styleUrls: ['./pipeline-element-template-config.component.scss'],
})
export class PipelineElementTemplateConfigComponent implements OnInit {
    @Input()
    template: PipelineElementTemplate;

    @Input()
    templateConfigs: Map<string, any>[] = [];

    @Input()
    appId: string;

    @Input()
    staticProperties: StaticPropertyUnion[];

    existingTemplates: PipelineElementTemplate[] = [];

    constructor(
        private pipelineElementTemplateService: PipelineElementTemplateService,
    ) {}

    ngOnInit(): void {
        this.loadTemplates();
        this.template.basePipelineElementAppId = this.appId;
        this.staticProperties.forEach(sp => {
            this.templateConfigs.push(
                new PipelineElementTemplateGenerator(sp).toTemplateValue(),
            );
        });
    }

    loadTemplates() {
        this.pipelineElementTemplateService
            .getPipelineElementTemplates(this.appId)
            .subscribe(templates => {
                this.existingTemplates = templates;
            });
    }

    deleteTemplate(templateId: string) {
        this.pipelineElementTemplateService
            .deletePipelineElementTemplate(templateId)
            .subscribe(result => this.loadTemplates());
    }
}
