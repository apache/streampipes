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
import {
    DialogRef,
    FormFieldComponent,
    SpAlertBannerComponent,
} from '@streampipes/shared-ui';
import { ConnectScriptTemplatesService } from '@streampipes/platform-services';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { LayoutGapDirective } from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-create-adapter-transformation-template-dialog',
    templateUrl:
        './create-adapter-transformation-template-dialog.component.html',
    styleUrl: './create-adapter-transformation-template-dialog.component.scss',
    imports: [
        FormFieldComponent,
        SpAlertBannerComponent,
        MatFormField,
        MatInput,
        FormsModule,
        MatDivider,
        LayoutGapDirective,
        MatButton,
        MatIcon,
        TranslatePipe,
    ],
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
