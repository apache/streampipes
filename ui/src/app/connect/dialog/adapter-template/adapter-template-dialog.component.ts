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

import { Component, Input, OnInit, inject } from '@angular/core';
import {
    PipelineElementTemplate,
    PipelineElementTemplateService,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { PipelineElementTemplateConfigComponent } from '../../../core-ui/pipeline-element-template-config/pipeline-element-template-config.component';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';

@Component({
    selector: 'sp-adapter-template-dialog',
    templateUrl: './adapter-template-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        PipelineElementTemplateConfigComponent,
        MatDivider,
        MatButton,
    ],
})
export class SpAdapterTemplateDialogComponent implements OnInit {
    dialogRef = inject<DialogRef<SpAdapterTemplateDialogComponent>>(DialogRef);
    private pipelineElementTemplateService = inject(
        PipelineElementTemplateService,
    );

    @Input()
    configs: StaticPropertyUnion[];

    @Input()
    appId: string;

    template: PipelineElementTemplate;
    templateConfigs: Map<string, any>[] = [];

    ngOnInit(): void {
        this.template = new PipelineElementTemplate();
    }

    saveTemplate() {
        this.template.templateConfigs = this.convert(this.templateConfigs);
        this.pipelineElementTemplateService
            .storePipelineElementTemplate(this.template)
            .subscribe(() => {
                this.dialogRef.close(true);
            });
    }

    convert(templateConfigs: Map<string, any>[]): Record<string, any>[] {
        return templateConfigs.map(map => {
            const obj: Record<string, any> = {};
            map.forEach((value, key) => {
                obj[key] = value;
            });
            return obj;
        });
    }
}
