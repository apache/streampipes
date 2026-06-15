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
import { HttpEventType, HttpResponse } from '@angular/common/http';
import {
    FileMetadata,
    FilesService,
    FileStaticProperty,
} from '@streampipes/platform-services';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormControl,
    ValidatorFn,
    Validators,
} from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FileRenameDialogComponent } from '../../../configuration/dialog/file-rename/file-rename-dialog.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
    MatError,
    MatFormField,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
    MatAutocomplete,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatOption } from '@angular/material/select';
import { MatProgressBar } from '@angular/material/progress-bar';

@Component({
    selector: 'sp-static-file-input',
    templateUrl: './static-file-input.component.html',
    styleUrls: ['./static-file-input.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        MatRadioGroup,
        MatRadioButton,
        MatFormField,
        MatInput,
        MatAutocompleteTrigger,
        MatIconButton,
        MatSuffix,
        MatIcon,
        MatAutocomplete,
        MatOption,
        MatProgressBar,
        MatButton,
        MatError,
        TranslatePipe,
    ],
})
export class StaticFileInputComponent
    extends AbstractValidatedStaticPropertyRenderer<FileStaticProperty>
    implements OnInit
{
    private filesService = inject(FilesService);
    dialog = inject(MatDialog);

    public chooseExistingFileControl = new UntypedFormControl();

    translateService = inject(TranslateService);

    dialogRef: MatDialogRef<FileRenameDialogComponent>;

    fileName: string;

    selectedUploadFile: File;

    hasInput: boolean;
    errorMessage = this.translateService.instant('Please enter a value');

    uploadStatus = 0;

    fileMetadata: FileMetadata[];
    selectedFile: FileMetadata;

    filesLoaded = false;

    ngOnInit() {
        this.fetchFileMetadata(this.staticProperty.locationPath);
        this.addValidator(
            this.staticProperty.locationPath,
            this.collectValidators(),
        );
        this.enableValidators();

        this.chooseExistingFileControl.setValue(true);

        if (this.staticProperty.label) {
            this.parentForm.controls[this.fieldName].setValue(
                this.staticProperty.label,
            );
        }
    }

    collectValidators() {
        const validators: ValidatorFn[] = [];
        if (!this.staticProperty.optional) {
            validators.push(Validators.required);
        }

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
                    this.applyCompletedConfiguration(true);
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
                        this.applyCompletedConfiguration(true);
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
                            _error => {},
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
        this.applyCompletedConfiguration(valid);
    }

    displayFn(fileMetadata: FileMetadata) {
        return fileMetadata ? fileMetadata.filename : '';
    }

    onStatusChange(_status: any) {}

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
