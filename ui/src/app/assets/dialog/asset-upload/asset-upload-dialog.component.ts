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
import { GenericStorageService } from '@streampipes/platform-services';
import { AssetConstants } from '../../constants/asset.constants';

@Component({
    selector: 'sp-file-upload-dialog-component',
    templateUrl: './asset-upload-dialog.component.html',
    styleUrls: ['./asset-upload-dialog.component.scss'],
})
export class AssetUploadDialogComponent {
    inputValue: string;
    fileName: string;

    jsonModel: string;

    hasInput: boolean;
    errorMessage = 'Please enter a value';

    uploadStatus = 0;

    constructor(
        private dialogRef: DialogRef<AssetUploadDialogComponent>,
        private genericStorageService: GenericStorageService,
    ) {}

    handleFileInput(files: any) {
        this.uploadStatus = 0;

        const fr = new FileReader();

        fr.onload = ev => {
            const jsonObject = JSON.parse(ev.target.result as string);
            this.jsonModel = JSON.stringify(jsonObject, null, 2);
        };

        fr.readAsText(files.item(0));
    }

    store() {
        this.uploadStatus = 0;
        if (this.jsonModel !== undefined) {
            const jsonObject = JSON.parse(this.jsonModel);
            jsonObject._rev = undefined;
            this.genericStorageService
                .createDocument(AssetConstants.ASSET_APP_DOC_NAME, jsonObject)
                .subscribe(result => {
                    this.dialogRef.close(true);
                });
        }
    }

    cancel() {
        this.dialogRef.close();
    }
}
