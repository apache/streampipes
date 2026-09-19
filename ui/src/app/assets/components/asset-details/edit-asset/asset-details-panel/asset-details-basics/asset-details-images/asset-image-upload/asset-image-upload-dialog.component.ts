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
import { FilesService } from '@streampipes/platform-services';
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

    private filesService = inject(FilesService);
    private translateService = inject(TranslateService);

    fileNames: string[] = [];
    duplicateFileNames: string[] = [];
    renamedFileNames: string[] = [];

    selectedUploadFiles: FileList;

    get hasInput(): boolean {
        return !!this.selectedUploadFiles?.length;
    }

    uploadStatus = 0;

    uploadError = false;
    uploadErrorMessage = '';

    errorMessage = this.translateService.instant('Please enter a value');

    private readonly allowedFileTypes = ['image/jpeg'];

    handleFileInput(files: FileList): void {
        this.uploadError = false;
        this.uploadErrorMessage = '';
        this.fileNames = [];

        const dataTransfer = new DataTransfer();

        for (let i = 0; i < files.length; i++) {
            const file = files.item(i);

            if (!file) {
                continue;
            }

            if (!this.allowedFileTypes.includes(file.type)) {
                this.uploadError = true;
                this.uploadErrorMessage = this.translateService.instant(
                    'Only JPEG, JPG images are supported',
                );
                continue;
            }

            dataTransfer.items.add(file);
            this.fileNames.push(file.name);
        }

        this.selectedUploadFiles = dataTransfer.files;
        this.uploadStatus = 0;
    }

    removeFilesFromUpload(): void {
        this.selectedUploadFiles = undefined;
        this.fileNames = [];
    }

    store(): void {
        if (!this.selectedUploadFiles?.length) {
            return;
        }

        this.filesService.getAllFilenames().subscribe(data => {
            const allFileNames = new Set(data);

            this.duplicateFileNames = this.fileNames.filter(fileName =>
                allFileNames.has(fileName.toLowerCase()),
            );

            if (this.duplicateFileNames.length === 0) {
                this.uploadStatus = 0;
                this.uploadFile(0);
            }
        });
    }

    uploadFile(index: number): void {
        this.uploadError = false;

        const file = this.selectedUploadFiles.item(index);

        if (!file) {
            return;
        }

        this.filesService.uploadFile(file).subscribe(
            event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.uploadStatus = Math.round(
                        (100 * event.loaded) / event.total,
                    );
                } else if (event instanceof HttpResponse) {
                    index++;

                    if (index === this.selectedUploadFiles.length) {
                        this.resolveUploadedFile(file.name);
                    } else {
                        this.uploadFile(index);
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

    private resolveUploadedFile(filename: string): void {
        this.filesService.getFileMetadata().subscribe(files => {
            const uploadedFile = files.find(file => file.filename === filename);

            if (uploadedFile) {
                this.dialogRef.close(uploadedFile);
            } else {
                this.uploadError = true;
                this.uploadErrorMessage = this.translateService.instant(
                    'Uploaded image could not be found',
                );
            }
        });
    }

    cancel(): void {
        this.dialogRef.close();
    }

    renameDuplicateFiles(): void {
        const dataTransfer = new DataTransfer();

        for (let i = 0; i < this.fileNames.length; i++) {
            let fileName = this.fileNames[i];

            const index = this.duplicateFileNames.indexOf(fileName);

            if (index !== -1) {
                this.fileNames[i] = this.renamedFileNames[index];
                fileName = this.renamedFileNames[index];
            }

            const selectedUploadFile = this.selectedUploadFiles[i];

            const renamedFile = new File([selectedUploadFile], fileName, {
                type: selectedUploadFile.type,
                lastModified: selectedUploadFile.lastModified,
            });

            dataTransfer.items.add(renamedFile);
        }

        this.selectedUploadFiles = dataTransfer.files;
        this.duplicateFileNames = [];
        this.renamedFileNames = [];
    }
}
