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

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {StaticFileRestService} from './static-file-rest.service';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {AbstractStaticPropertyRenderer} from "../base/abstract-static-property";
import {FileStaticProperty} from "../../../core-model/gen/streampipes-model";
import {FilesService} from "../../../platform-services/apis/files.service";
import {FileMetadata} from "../../../core-model/gen/streampipes-model-client";


@Component({
    selector: 'app-static-file-input',
    templateUrl: './static-file-input.component.html',
    styleUrls: ['./static-file-input.component.css']
})
export class StaticFileInputComponent extends AbstractStaticPropertyRenderer<FileStaticProperty> implements OnInit {

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    chooseExistingFile: boolean = true;

    inputValue: String;
    fileName: String;

    selectedUploadFile: File;

    hasInput: Boolean;
    errorMessage = "Please enter a value";

    uploadStatus = 0;

    fileMetadata: FileMetadata[];
    selectedFile: FileMetadata;

    filesLoaded: boolean = false;

    constructor(public staticPropertyUtil: StaticPropertyUtilService,
                private staticFileRestService: StaticFileRestService,
                private filesService: FilesService){
        super();
    }

    ngOnInit() {
        this.fetchFileMetadata();
    }

    fetchFileMetadata() {
        this.filesService.getFileMetadata(this.staticProperty.requiredFiletypes).subscribe(fm => {
            this.fileMetadata = fm;
            if (this.staticProperty.locationPath) {
                this.selectedFile =
                    this.fileMetadata.find(fm => fm.internalFilename === this.staticProperty.locationPath);
            } else {
                if (this.fileMetadata.length > 0) {
                    this.selectedFile = this.fileMetadata[0];
                    this.staticProperty.locationPath = this.selectedFile.internalFilename;
                    this.emitUpdate(true);
                } else {
                    this.chooseExistingFile = false;
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
        this.uploadStatus = 0;
        if (this.selectedUploadFile !== undefined) {
            this.filesService.uploadFile(this.selectedUploadFile).subscribe(
                event => {
                    if (event.type == HttpEventType.UploadProgress) {
                        this.uploadStatus = Math.round(100 * event.loaded / event.total);
                    } else if (event instanceof HttpResponse) {
                        this.fetchFileMetadata();
                        (<FileStaticProperty> (this.staticProperty)).locationPath = event.body.notifications[0].title;
                               this.emitUpdate(true);
                    }
                },
                error => {
                },
            );
        }
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else{
            this.hasInput = true;
        }

        this.inputEmitter.emit(this.hasInput);
    }

    selectOption(fileMetadata: FileMetadata) {
        this.staticProperty.locationPath = fileMetadata.internalFilename;
    }

    displayFn(fileMetadata: FileMetadata) {
        return fileMetadata ? fileMetadata.originalFilename : "";
    }

}