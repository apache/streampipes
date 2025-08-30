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

import { inject, Injectable } from '@angular/core';
import {
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DataViewQueryGeneratorService,
    DateRange,
    TimeSettings,
} from '@streampipes/platform-services';
import {
    DataDownloadDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { ObjectPermissionDialogComponent } from '../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { ObservableGenerator } from '../models/dataview-dashboard.model';

@Injectable({ providedIn: 'root' })
export class DataExplorerSharedService {
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private dataViewQueryGeneratorService = inject(
        DataViewQueryGeneratorService,
    );

    openPermissionsDialog(
        elementId: string,
        headerTitle: string,
        anonymousReadSupported: boolean = false,
        publicLink: string = '',
    ) {
        return this.dialogService.open(ObjectPermissionDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage permissions'),
            width: '50vw',
            data: {
                objectInstanceId: elementId,
                headerTitle,
                anonymousReadSupported,
                publicLink,
            },
        });
    }

    downloadDataAsFile(
        timeSettings: TimeSettings,
        dataView: DataExplorerWidgetModel,
    ) {
        this.dialogService.open(DataDownloadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Download data'),
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

    defaultObservableGenerator(): ObservableGenerator {
        return {
            generateObservables: (
                startTime: number,
                endTime: number,
                dataConfig: DataExplorerDataConfig,
                widgetId: string,
                maxRowCountPerTag: number,
            ) => {
                return this.dataViewQueryGeneratorService.generateObservables(
                    startTime,
                    endTime,
                    dataConfig,
                    maxRowCountPerTag,
                );
            },
        };
    }

    kioskModeObservableGenerator(dashboardId: string): ObservableGenerator {
        return {
            generateObservables: (
                startTime: number,
                endTime: number,
                dataConfig: DataExplorerDataConfig,
                widgetId: string,
                maxRowCountPerTag: number,
            ) => {
                return this.dataViewQueryGeneratorService.generateObservablesForKioskMode(
                    startTime,
                    endTime,
                    dataConfig,
                    dashboardId,
                    widgetId,
                    maxRowCountPerTag,
                );
            },
        };
    }
}
