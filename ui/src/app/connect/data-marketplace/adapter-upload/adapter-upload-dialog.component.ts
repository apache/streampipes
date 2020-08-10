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

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RestService} from "../../rest.service";
import {
    AdapterDescription,
    AdapterDescriptionList
} from "../../../core-model/gen/streampipes-model";

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-upload-dialog.html',
    styleUrls: ['./adapter-upload-dialog.component.css'],
})
export class AdapterUploadDialog {

    private selectedUploadFile: File;
    uploaded: boolean;

    constructor(
        public dialogRef: MatDialogRef<AdapterUploadDialog>,
        private restService: RestService,
        @Inject(MAT_DIALOG_DATA) public data: any) {

    }

    ngOnInit() {
        this.uploaded = false;
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];


        let fileReader = new FileReader();
        fileReader.onload = (e) => {
            this.uploaded = true;

            var jsonString: any = fileReader.result;
            var json = JSON.parse(jsonString);

            if (jsonString["@class"].contains('AdapterDescriptionList') != -1) {
                let allTemplates: AdapterDescriptionList  = AdapterDescriptionList.fromData(json as AdapterDescriptionList);
                let self = this;

                allTemplates.list.forEach(function (adapterTemplate) {
                    self.restService.addAdapterTemplate(adapterTemplate).subscribe(x => {
                    });
                });

            } else {

                let adapterTemplate: AdapterDescription;
                adapterTemplate = AdapterDescription.fromDataUnion(json);

                this.restService.addAdapterTemplate(adapterTemplate).subscribe(x => {
                });
            }

        }
        fileReader.readAsText(this.selectedUploadFile);

    }

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
    }

}