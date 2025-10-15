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

import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SpAssetTreeNode } from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-dialog',
    templateUrl: './asset-dialog.component.html',
    standalone: false,
})
export class AssetDialogComponent {
    addToAssets: boolean = false;
    @Input()
    selectedAssets: SpAssetTreeNode[];
    @Input()
    deselectedAssets: SpAssetTreeNode[];
    @Input()
    originalAssets: SpAssetTreeNode[];

    @Output() selectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() deselectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() originalAssetsChange = new EventEmitter<SpAssetTreeNode[]>();

    constructor(
        public dialogRef: MatDialogRef<AssetDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
    ) {}

    onSelectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.selectedAssets = updatedAssets;
        this.selectedAssetsChange.emit(this.selectedAssets);
    }

    onDeselectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.deselectedAssets = updatedAssets;
        this.deselectedAssetsChange.emit(this.deselectedAssets);
    }

    onOriginalAssetsEmitted(updatedAssets: SpAssetTreeNode[]): void {
        this.originalAssets = updatedAssets;
        this.originalAssetsChange.emit(this.originalAssets);
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onOk(): void {
        this.dialogRef.close(true);
    }
}
