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

import { Component, inject } from '@angular/core';
import { DialogRef, SpAlertBannerComponent } from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-asset-image-upload-dialog',
    templateUrl: './asset-image-upload-dialog.component.html',
    imports: [
        LayoutDirective,
        SpAlertBannerComponent,
        MatFormField,
        MatSuffix,
        MatInput,
        MatButton,
        MatDivider,
        MatIcon,
        TranslatePipe,
    ],
})
export class AssetImageUploadDialogComponent {
    private dialogRef =
        inject<DialogRef<AssetImageUploadDialogComponent>>(DialogRef);
    private translateService = inject(TranslateService);

    selectedUploadFile: File | undefined;
    uploadErrorMessage = '';

    handleFileInput(files: FileList | null): void {
        this.clearSelectedFile();
        const file = files?.item(0);
        if (!file) return;
        if (!['image/jpeg', 'image/png'].includes(file.type)) {
            this.uploadErrorMessage = this.translateService.instant(
                'Only JPEG, JPG and PNG images are supported',
            );
            return;
        }
        this.selectedUploadFile = file;
    }

    clearSelectedFile(): void {
        this.selectedUploadFile = undefined;
        this.uploadErrorMessage = '';
    }

    store(): void {
        if (this.selectedUploadFile)
            this.dialogRef.close(this.selectedUploadFile);
    }

    cancel(): void {
        this.dialogRef.close();
    }
}
