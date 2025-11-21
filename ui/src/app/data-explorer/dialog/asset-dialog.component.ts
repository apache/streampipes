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

import { Component, inject, Input, OnInit } from '@angular/core';
import { SpAssetTreeNode } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-asset-dialog',
    templateUrl: './asset-dialog.component.html',
    standalone: false,
})
export class AssetDialogComponent implements OnInit {
    @Input() selectedAssets: SpAssetTreeNode[];
    @Input() deselectedAssets: SpAssetTreeNode[];
    @Input() originalAssets: SpAssetTreeNode[];
    @Input() dataViewId: string;
    // TODO why is this a string and not a boolean
    @Input() editMode: string;
    @Input() cancelTitle: string;
    @Input() okTitle: string;

    private dialogRef = inject<DialogRef<AssetDialogComponent>>(DialogRef);

    addToAssets = false;

    ngOnInit(): void {
        if (this.editMode) {
            this.addToAssets = true;
        }
    }

    onSelectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.selectedAssets = updatedAssets;
    }

    onDeselectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.deselectedAssets = updatedAssets;
    }

    onOriginalAssetsEmitted(updatedAssets: SpAssetTreeNode[]): void {
        this.originalAssets = updatedAssets;
    }

    onCancel(): void {
        this.dialogRef.close({});
    }

    onAddAsset(): void {
        this.dialogRef.close({
            selectedAssets: this.selectedAssets,
            deselectedAssets: this.deselectedAssets,
            originalAssets: this.originalAssets,
        });
    }
}
