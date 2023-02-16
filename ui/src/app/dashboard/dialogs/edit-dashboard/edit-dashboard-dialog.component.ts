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
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-edit-dashboard-dialog-component',
    templateUrl: './edit-dashboard-dialog.component.html',
    styleUrls: ['./edit-dashboard-dialog.component.scss'],
})
export class EditDashboardDialogComponent {
    @Input()
    createMode: boolean;

    @Input()
    dashboard: Dashboard;

    constructor(
        public dialogRef: DialogRef<EditDashboardDialogComponent>,
        private dashboardService: DashboardService,
    ) {}

    onCancel(): void {
        this.dialogRef.close();
    }

    onSave(): void {
        if (this.createMode) {
            this.dashboardService
                .saveDashboard(this.dashboard)
                .subscribe(result => this.onCancel());
        } else {
            this.dashboardService
                .updateDashboard(this.dashboard)
                .subscribe(result => this.onCancel());
        }
    }
}
