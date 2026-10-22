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
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { ImagesService } from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatError,
    MatFormField,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'sp-asset-image-upload-dialog',
    templateUrl: './asset-image-upload-dialog.component.html',
    imports: [
        LayoutDirective,
        SpAlertBannerComponent,
        FlexDirective,
        MatFormField,
        MatInput,
        MatProgressBar,
        MatButton,
        MatSuffix,
        MatIcon,
        MatError,
        LayoutAlignDirective,
        FormsModule,
        MatDivider,
        TranslatePipe,
    ],
})
export class AssetImageUploadDialogComponent {
    private dialogRef =
        inject<DialogRef<AssetImageUploadDialogComponent>>(DialogRef);

    private imagesService = inject(ImagesService);
    private translateService = inject(TranslateService);

    selectedUploadFile: File | undefined;
    duplicateFilename = '';
    renamedFilename = '';

    get hasInput(): boolean {
        return !!this.selectedUploadFile;
    }

    uploadStatus = 0;

    uploadError = false;
    uploadErrorMessage = '';

    errorMessage = this.translateService.instant('Please enter a value');

    private readonly allowedFileTypes = ['image/jpeg', 'image/png'];

    handleFileInput(files: FileList | null): void {
        this.clearSelectedFile();
        const file = files?.item(0);
        if (!file) {
            return;
        }

        if (!this.allowedFileTypes.includes(file.type)) {
            this.uploadError = true;
            this.uploadErrorMessage = this.translateService.instant(
                'Only JPEG, JPG and PNG images are supported',
            );
            return;
        }

        this.selectedUploadFile = file;
    }

    clearSelectedFile(): void {
        this.selectedUploadFile = undefined;
        this.duplicateFilename = '';
        this.renamedFilename = '';
        this.uploadStatus = 0;
        this.uploadError = false;
        this.uploadErrorMessage = '';
    }

    store(): void {
        const file = this.selectedUploadFile;
        if (!file) {
            return;
        }

        this.imagesService.getImageMetadata().subscribe(images => {
            if (file !== this.selectedUploadFile) return;
            const duplicate = images.some(
                image =>
                    image.filename.toLowerCase() === file.name.toLowerCase(),
            );

            this.duplicateFilename = duplicate ? file.name : '';

            if (!duplicate) {
                this.uploadStatus = 0;
                this.uploadImage(file);
            }
        });
    }

    private uploadImage(file: File): void {
        this.uploadError = false;

        this.imagesService.uploadImage(file).subscribe(
            event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.uploadStatus = Math.round(
                        (100 * event.loaded) / event.total,
                    );
                } else if (event instanceof HttpResponse) {
                    if (event.body?.fileId) {
                        this.dialogRef.close(event.body);
                    } else {
                        this.uploadError = true;
                        this.uploadErrorMessage = this.translateService.instant(
                            'Uploaded image could not be found',
                        );
                    }
                }
            },
            error => {
                this.uploadError = true;

                if (error.error?.notifications?.length > 0) {
                    this.uploadErrorMessage =
                        error.error.notifications[0].title;
                } else {
                    this.uploadErrorMessage = error.message;
                }
            },
        );
    }

    cancel(): void {
        this.dialogRef.close();
    }

    renameDuplicateFile(): void {
        const file = this.selectedUploadFile;
        const filename = this.renamedFilename.trim();
        if (!file || !filename) {
            return;
        }

        this.selectedUploadFile = new File([file], filename, {
            type: file.type,
            lastModified: file.lastModified,
        });
        this.duplicateFilename = '';
        this.renamedFilename = '';
    }
}
