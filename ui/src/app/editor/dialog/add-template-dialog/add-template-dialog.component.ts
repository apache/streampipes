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

import { Component, OnInit } from '@angular/core';
import {
    CompactPipelineTemplate,
    PipelineTemplateGenerationRequest,
    PipelineTemplateService,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-add-template-dialog',
    templateUrl: './add-template-dialog.component.html',
    styleUrls: ['./add-template-dialog.component.scss'],
})
export class AddTemplateDialogComponent implements OnInit {
    pipelineTemplates: CompactPipelineTemplate[] = [];

    constructor(
        private pipelineTemplateService: PipelineTemplateService,
        private dialogRef: DialogRef<AddTemplateDialogComponent>,
    ) {}

    ngOnInit() {
        this.pipelineTemplateService
            .findAll()
            .subscribe(t => (this.pipelineTemplates = t));
    }

    selectTemplate(template: CompactPipelineTemplate): void {
        const req: PipelineTemplateGenerationRequest = {
            streams: {},
            template,
            pipelineName: undefined,
            pipelineDescription: undefined,
        };
        this.pipelineTemplateService
            .getPipelineForTemplate(template.elementId, req)
            .subscribe(pipeline => {
                this.dialogRef.close(pipeline);
            });
    }

    close(): void {
        this.dialogRef.close();
    }
}
