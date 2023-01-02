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

import { Component, Input } from '@angular/core';
import {
    AssetManagementService,
    DataViewDataExplorerService,
    SpAssetModel,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-create-asset-dialog-component',
    templateUrl: './create-asset-dialog.component.html',
    styleUrls: ['./create-asset-dialog.component.scss'],
})
export class SpCreateAssetDialogComponent {
    @Input() createMode: boolean;
    @Input() assetModel: SpAssetModel;

    constructor(
        private dialogRef: DialogRef<SpCreateAssetDialogComponent>,
        private assetManagementService: AssetManagementService,
    ) {}

    onCancel(): void {
        this.dialogRef.close();
    }

    onSave(): void {
        if (this.createMode) {
            this.assetManagementService
                .createAsset(this.assetModel)
                .subscribe(() => {
                    this.dialogRef.close();
                });
        } else {
            this.assetManagementService
                .updateAsset(this.assetModel)
                .subscribe(() => {
                    this.dialogRef.close();
                });
        }
    }
}
