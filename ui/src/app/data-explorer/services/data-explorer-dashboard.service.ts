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

import { Injectable } from '@angular/core';
import {
    Dashboard,
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DateRange,
    TimeSettings,
} from '@streampipes/platform-services';
import { DataExplorerEditDashboardDialogComponent } from '../dialogs/edit-dashboard/data-explorer-edit-dashboard-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { DataDownloadDialogComponent } from '../../core-ui/data-download-dialog/data-download-dialog.component';
import { ObjectPermissionDialogComponent } from '../../core-ui/object-permission-dialog/object-permission-dialog.component';

@Injectable({ providedIn: 'root' })
export class DataExplorerDashboardService {
    constructor(private dialogService: DialogService) {}

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        return this.dialogService.open(
            DataExplorerEditDashboardDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: createMode ? 'New Dashboard' : 'Edit Dashboard',
                width: '60vw',
                data: {
                    createMode: createMode,
                    dashboard: dashboard,
                },
            },
        );
    }

    openPermissionsDialog(elementId: string, headerTitle: string) {
        return this.dialogService.open(ObjectPermissionDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Manage permissions',
            width: '50vw',
            data: {
                objectInstanceId: elementId,
                headerTitle,
            },
        });
    }

    downloadDataAsFile(
        timeSettings: TimeSettings,
        dataView: DataExplorerWidgetModel,
    ) {
        this.dialogService.open(DataDownloadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Download data',
            width: '50vw',
            data: {
                dataDownloadDialogModel: {
                    dataExplorerDateRange:
                        DateRange.fromTimeSettings(timeSettings),
                    dataExplorerDataConfig:
                        dataView.dataConfig as DataExplorerDataConfig,
                },
            },
        });
    }
}
