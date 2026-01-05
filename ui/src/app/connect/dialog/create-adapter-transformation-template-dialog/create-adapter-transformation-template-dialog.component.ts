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

import { Component, inject, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { ConnectScriptTemplatesService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-create-adapter-transformation-template-dialog',
    templateUrl:
        './create-adapter-transformation-template-dialog.component.html',
    styleUrl: './create-adapter-transformation-template-dialog.component.scss',
    standalone: false,
})
export class CreateAdapterTransformationTemplateDialogComponent {
    @Input()
    script: string;

    @Input()
    language: string;

    templateName = 'Name';
    templateDescription = 'Description';

    private templateService = inject(ConnectScriptTemplatesService);
    private dialogRef = inject(
        DialogRef<CreateAdapterTransformationTemplateDialogComponent>,
    );

    save(): void {
        this.templateService
            .create({
                appDocType: 'transformation-script-template',
                elementId: undefined,
                rev: undefined,
                language: this.language,
                code: this.script,
                name: this.templateName,
                description: this.templateDescription,
            })
            .subscribe(() => this.close(true));
    }

    close(reloadTemplate = false): void {
        this.dialogRef.close(reloadTemplate);
    }
}
