/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {RestService} from "../../rest.service";
import {TsonLdSerializerService} from '../../../platform-services/tsonld-serializer.service';
import {AdapterDescriptionList} from '../../model/connect/AdapterDescriptionList';
import {AdapterDescription} from '../../model/connect/AdapterDescription';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-upload-dialog.html',
    styleUrls: ['./adapter-upload-dialog.component.css'],
})
export class AdapterUploadDialog {

    private selectedUploadFile: File;
    private uploaded: boolean;

    constructor(
        public dialogRef: MatDialogRef<AdapterUploadDialog>,
        private restService: RestService,
        private tsonLdSerializerService: TsonLdSerializerService,
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

            if (jsonString.indexOf('AdapterDescriptionList') != -1) {
                let allTemplates: AdapterDescriptionList  = this.tsonLdSerializerService.fromJsonLd(json, 'sp:AdapterDescriptionList');
                let self = this;

                allTemplates.list.forEach(function (adapterTemplate) {
                    self.restService.addAdapterTemplate(adapterTemplate).subscribe(x => {
                    });
                });

            } else {

                let adapterTemplate: AdapterDescription;

                if (jsonString.indexOf('GenericAdapterSetDescription') != -1) {
                    adapterTemplate = this.tsonLdSerializerService.fromJsonLd(json, 'sp:GenericAdapterSetDescription');
                } else if (jsonString.indexOf('SpecificAdapterSetDescription') != -1) {
                    adapterTemplate = this.tsonLdSerializerService.fromJsonLd(json, 'sp:SpecificAdapterSetDescription');
                } else if (jsonString.indexOf('GenericAdapterStreamDescription') != -1) {
                    adapterTemplate = this.tsonLdSerializerService.fromJsonLd(json, 'sp:GenericAdapterStreamDescription');
                } else if (jsonString.indexOf('SpecificAdapterStreamDescription') != -1) {
                    adapterTemplate = this.tsonLdSerializerService.fromJsonLd(json, 'sp:SpecificAdapterStreamDescription');
                }

                this.restService.addAdapterTemplate(adapterTemplate).subscribe(x => {
                });
            }

        }
        fileReader.readAsText(this.selectedUploadFile);

    }

    storeAdapter

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
    }

}