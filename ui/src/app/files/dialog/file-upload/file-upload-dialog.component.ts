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

import { Component } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { FilesService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-file-upload-dialog-component',
    templateUrl: './file-upload-dialog.component.html',
    styleUrls: ['./file-upload-dialog.component.scss'],
})
export class FileUploadDialogComponent {
    inputValue: string;
    fileNames: string[] = [];
    duplicateFileNames: string[] = [];
    renamedFileNames: string[] = [];

    selectedUploadFiles: FileList;

    hasInput: boolean;
    errorMessage = 'Please enter a value';

    uploadStatus = 0;

    constructor(
        private dialogRef: DialogRef<FileUploadDialogComponent>,
        private filesService: FilesService,
    ) {}

    handleFileInput(files: FileList) {
        this.selectedUploadFiles = files;
        for (let i = 0; i < files.length; i++) {
            this.fileNames.push(files.item(i).name);
        }
        this.uploadStatus = 0;
    }

    removeFilesFromUpload(): void {
        this.selectedUploadFiles = undefined;
        this.fileNames = [];
    }

    store() {
        this.filesService.getAllFilenames().subscribe(data => {
            const allFileNames = new Set(data);
            this.duplicateFileNames = this.fileNames.filter(fileName =>
                allFileNames.has(fileName.toLowerCase()),
            );
            if (this.duplicateFileNames.length === 0) {
                this.uploadStatus = 0;
                if (this.selectedUploadFiles.length > 0) {
                    this.uploadFile(0);
                }
            }
        });
    }

    uploadFile(index: number): void {
        this.filesService
            .uploadFile(this.selectedUploadFiles.item(index))
            .subscribe(
                event => {
                    if (event.type === HttpEventType.UploadProgress) {
                        this.uploadStatus = Math.round(
                            (100 * event.loaded) / event.total,
                        );
                    } else if (event instanceof HttpResponse) {
                        index++;
                        if (index === this.selectedUploadFiles.length) {
                            this.dialogRef.close();
                        } else {
                            this.uploadFile(index);
                        }
                    }
                },
                error => {},
            );
    }

    cancel() {
        this.dialogRef.close();
    }

    renameDuplicateFiles() {
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
