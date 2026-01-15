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

import { Component, inject, OnInit } from '@angular/core';
import {
    ConnectScriptTemplatesService,
    ConnectTransformationScriptTemplate,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-select-adapter-transformation-template-dialog',
    templateUrl:
        './select-adapter-transformation-template-dialog.component.html',
    styleUrl: './select-adapter-transformation-template-dialog.component.scss',
    standalone: false,
})
export class SelectAdapterTransformationTemplateDialogComponent implements OnInit {
    allTemplates: ConnectTransformationScriptTemplate[] = [];
    selectedTemplate: ConnectTransformationScriptTemplate = undefined;

    private templateService = inject(ConnectScriptTemplatesService);
    private dialogRef = inject(
        DialogRef<SelectAdapterTransformationTemplateDialogComponent>,
    );

    ngOnInit() {
        this.templateService.getAll().subscribe(res => {
            this.allTemplates = res.sort((a, b) =>
                a.name.localeCompare(b.name),
            );
        });
    }

    deleteTemplate() {
        this.templateService
            .delete(this.selectedTemplate.elementId)
            .subscribe(() => {
                this.dialogRef.close();
            });
    }

    close(selectedTemplate = undefined): void {
        this.dialogRef.close(selectedTemplate);
    }
}
