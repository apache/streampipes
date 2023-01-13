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
    PipelineElementTemplateConfig,
    PipelineElementTemplateService,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-adapter-template-dialog',
    templateUrl: './adapter-template-dialog.component.html',
    styleUrls: ['./adapter-template-dialog.component.scss'],
})
export class SpAdapterTemplateDialogComponent implements OnInit {
    @Input()
    configs: StaticPropertyUnion[];

    @Input()
    appId: string;

    template: PipelineElementTemplate;
    templateConfigs: Map<string, any> = new Map();

    constructor(
        public dialogRef: DialogRef<SpAdapterTemplateDialogComponent>,
        private pipelineElementTemplateService: PipelineElementTemplateService,
    ) {}

    ngOnInit(): void {
        this.template = new PipelineElementTemplate();
    }

    saveTemplate() {
        this.template.templateConfigs = this.convert(this.templateConfigs);
        this.pipelineElementTemplateService
            .storePipelineElementTemplate(this.template)
            .subscribe(result => {
                this.dialogRef.close(true);
            });
    }

    convert(templateConfigs: Map<string, any>): any {
        const configs: { [index: string]: PipelineElementTemplateConfig } = {};
        templateConfigs.forEach((value, key) => {
            configs[key] = new PipelineElementTemplateConfig();
            configs[key].editable = value.editable;
            configs[key].displayed = value.displayed;
            configs[key].value = value.value;
        });
        return configs;
    }
}
