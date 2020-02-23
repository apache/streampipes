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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StaticProperty} from '../../model/StaticProperty';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {StaticFileRestService} from './static-file-rest.service';
import {FileStaticProperty} from '../../model/FileStaticProperty';
import {HttpEventType, HttpResponse} from '@angular/common/http';


@Component({
    selector: 'app-static-file-input',
    templateUrl: './static-file-input.component.html',
    styleUrls: ['./static-file-input.component.css']
})
export class StaticFileInputComponent implements OnInit {


    @Input() staticProperty: StaticProperty;
    @Input() adapterId: String;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    inputValue: String;
    fileName: String;

    selectedUploadFile: File;

    hasInput: Boolean;
    errorMessage = "Please enter a value";

    uploadStatus = 0;

    constructor(public staticPropertyUtil: StaticPropertyUtilService, private staticFileRestService: StaticFileRestService){

    }

    ngOnInit() {
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;
        this.uploadStatus = 0;
    }

    upload() {
        this.uploadStatus = 0;
        if (this.selectedUploadFile !== undefined) {
            this.staticFileRestService.uploadFile(this.adapterId, this.selectedUploadFile).subscribe(
                event => {
                    if (event.type == HttpEventType.UploadProgress) {
                        this.uploadStatus = Math.round(100 * event.loaded / event.total);
                    } else if (event instanceof HttpResponse) {
                        (<FileStaticProperty> (this.staticProperty)).locationPath = event.body.notifications[0].title;
                               this.valueChange(true);
                    }
                },
                error => {
                    this.valueChange(false);
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

}