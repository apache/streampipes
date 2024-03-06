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

import {
    Component,
    EventEmitter,
    NgModule,
    OnInit,
    Output,
} from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import {
    FilesService,
    FileStaticProperty,
    FileMetadata,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';
import { UntypedFormControl, ValidatorFn, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FileRenameDialogComponent } from '../../../files/dialog/file-rename/file-rename-dialog.component';

@Component({
    selector: 'sp-static-file-input',
    templateUrl: './static-file-input.component.html',
    styleUrls: ['./static-file-input.component.css'],
})
export class StaticFileInputComponent
    extends AbstractValidatedStaticPropertyRenderer<FileStaticProperty>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    public chooseExistingFileControl = new UntypedFormControl();

    dialogRef: MatDialogRef<FileRenameDialogComponent>;

    fileName: string;

    selectedUploadFile: File;

    hasInput: boolean;
    errorMessage = 'Please enter a value';

    uploadStatus = 0;

    fileMetadata: FileMetadata[];
    selectedFile: FileMetadata;

    filesLoaded = false;

    constructor(
        private filesService: FilesService,
        public dialog: MatDialog,
    ) {
        super();
    }

    ngOnInit() {
        this.fetchFileMetadata();
        this.addValidator(
            this.staticProperty.locationPath,
            this.collectValidators(),
        );
        this.enableValidators();

        this.chooseExistingFileControl.setValue(true);
    }

    collectValidators() {
        const validators: ValidatorFn[] = [];
        validators.push(Validators.required);

        return validators;
    }

    fetchFileMetadata(filenameToSelect?: any) {
        this.filesService
            .getFileMetadata(this.staticProperty.requiredFiletypes)
            .subscribe(fm => {
                this.fileMetadata = fm;
                if (filenameToSelect) {
                    this.selectedFile = this.fileMetadata.find(
                        fmi => fmi.filename === filenameToSelect,
                    );
                    this.selectOption(this.selectedFile);
                    this.emitUpdate(true);
                    this.parentForm.controls[this.fieldName].setValue(
                        this.selectedFile,
                    );

                    this.chooseExistingFileControl.setValue(true);
                } else if (this.staticProperty.locationPath) {
                    this.selectedFile = this.fileMetadata.find(
                        fmi =>
                            fmi.filename === this.staticProperty.locationPath,
                    );
                } else {
                    if (this.fileMetadata.length > 0) {
                        this.selectedFile = this.fileMetadata[0];
                        this.selectOption(this.selectedFile);
                        this.emitUpdate(true);
                        this.parentForm.controls[this.fieldName].setValue(
                            this.selectedFile,
                        );
                    } else {
                        this.chooseExistingFileControl.setValue(false);
                    }
                }
                this.filesLoaded = true;
            });
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;
        this.uploadStatus = 0;
    }

    upload() {
        if (this.selectedUploadFile !== undefined) {
            this.filesService.getAllFilenames().subscribe(allFileNames => {
                if (
                    !allFileNames.includes(
                        this.selectedUploadFile.name.toLowerCase(),
                    )
                ) {
                    this.uploadStatus = 0;
                    this.filesService
                        .uploadFile(this.selectedUploadFile)
                        .subscribe(
                            event => {
                                if (
                                    event.type === HttpEventType.UploadProgress
                                ) {
                                    this.uploadStatus = Math.round(
                                        (100 * event.loaded) / event.total,
                                    );
                                } else if (event instanceof HttpResponse) {
                                    const filename = event.body.filename;
                                    this.parentForm.controls[
                                        this.fieldName
                                    ].setValue(filename);
                                    this.fetchFileMetadata(filename);
                                }
                            },
                            error => {},
                        );
                } else {
                    this.openRenameDialog();
                }
            });
        }
    }

    selectOption(fileMetadata: FileMetadata) {
        this.staticProperty.locationPath = fileMetadata.filename;
        const valid: boolean =
            fileMetadata.filename !== '' || fileMetadata.filename !== undefined;
        this.updateEmitter.emit(
            new ConfigurationInfo(this.staticProperty.internalName, valid),
        );
    }

    displayFn(fileMetadata: FileMetadata) {
        return fileMetadata ? fileMetadata.filename : '';
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {
        this.staticProperty.locationPath = value.filename;
        this.parentForm.updateValueAndValidity();
    }

    openRenameDialog() {
        this.dialogRef = this.dialog.open(FileRenameDialogComponent);
        this.dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.fileName = data;
                this.selectedUploadFile = new File(
                    [this.selectedUploadFile],
                    this.fileName,
                    {
                        type: this.selectedUploadFile.type,
                        lastModified: this.selectedUploadFile.lastModified,
                    },
                );
            }
        });
    }
}
