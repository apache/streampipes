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
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ShepherdService} from '../../../services/tour/shepherd.service';
import {RestService} from "../../rest.service";
import {TsonLdSerializerService} from '../../../platform-services/tsonld-serializer.service';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-export-dialog.html',
    styleUrls: ['./adapter-export-dialog.component.css'],
})
export class AdapterExportDialog {

    adapterJsonLd;

    constructor(
        public dialogRef: MatDialogRef<AdapterExportDialog>,
        private restService: RestService,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private tsonLdSerializerService: TsonLdSerializerService,
        private ShepherdService: ShepherdService) {

    }

    ngOnInit() {
        delete this.data.adapter['userName'];
        this.tsonLdSerializerService.toJsonLd(this.data.adapter).subscribe(res => {
            this.adapterJsonLd = res;
        });

    }

    download() {
        this.tsonLdSerializerService.toJsonLd(this.data.adapter).subscribe(res => {
            var data = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(res, null, 2));
            var downloader = document.createElement('a');

            downloader.setAttribute('href', data);
            downloader.setAttribute('download', this.data.adapter.label + '-adapter-template.json');
            downloader.click();

        });


    }


    onCloseConfirm() {
        this.dialogRef.close('Confirm');
    }

}