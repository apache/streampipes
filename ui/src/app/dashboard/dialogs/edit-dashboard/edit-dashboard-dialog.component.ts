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

import {
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    Dashboard,
    DashboardService,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-edit-dashboard-dialog-component',
    templateUrl: './edit-dashboard-dialog.component.html',
    styleUrls: ['./edit-dashboard-dialog.component.scss'],
    standalone: false,
})
export class EditDashboardDialogComponent implements OnInit {
    @Input() createMode: boolean;
    @Input() dashboard: Dashboard;
    @Input() selectedAssets: SpAssetTreeNode[];
    @Input() deselectedAssets: SpAssetTreeNode[];
    @Input() originalAssets: SpAssetTreeNode[];

    @Output() selectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() deselectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() originalAssetsChange = new EventEmitter<SpAssetTreeNode[]>();

    private dialogRef = inject(DialogRef<EditDashboardDialogComponent>);
    private dashboardService = inject(DashboardService);

    addToAssets: boolean = false;

    ngOnInit() {
        if (!this.dashboard.dashboardGeneralSettings.defaultViewMode) {
            this.dashboard.dashboardGeneralSettings.defaultViewMode = 'grid';
        }
        if (
            this.dashboard.dashboardGeneralSettings.globalTimeEnabled ===
            undefined
        ) {
            this.dashboard.dashboardGeneralSettings.globalTimeEnabled = true;
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }

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

    onSave(): void {
        this.dashboard.metadata.lastModifiedEpochMs = Date.now();
        if (this.createMode) {
            this.dashboardService
                .saveDashboard(this.dashboard)
                .subscribe(() => {
                    this.dialogRef.close();
                });
        } else {
            this.dashboardService
                .updateDashboard(this.dashboard)
                .subscribe(() => {
                    this.dialogRef.close();
                });
        }
    }
}
