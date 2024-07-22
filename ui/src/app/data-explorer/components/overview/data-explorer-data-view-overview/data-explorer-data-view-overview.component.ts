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
import { SpDataExplorerOverviewDirective } from '../data-explorer-overview.directive';
import { MatTableDataSource } from '@angular/material/table';
import {
    Dashboard,
    DataExplorerWidgetModel,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import { CurrentUserService, DialogService } from '@streampipes/shared-ui';
import { AuthService } from '../../../../services/auth.service';
import { DataExplorerRoutingService } from '../../../services/data-explorer-routing.service';

@Component({
    selector: 'sp-data-explorer-data-view-overview',
    templateUrl: './data-explorer-data-view-overview.component.html',
    styleUrls: ['../data-explorer-overview.component.scss'],
})
export class SpDataExplorerDataViewOverviewComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<DataExplorerWidgetModel>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];

    constructor(
        private dataViewService: DataViewDataExplorerService,
        public dialogService: DialogService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        routingService: DataExplorerRoutingService,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.displayedColumns = ['name', 'actions'];
        this.getCharts();
    }

    getCharts(): void {
        this.dataViewService.getAllWidgets().subscribe(widgets => {
            widgets = widgets.sort((a, b) =>
                a.baseAppearanceConfig.widgetTitle.localeCompare(
                    b.baseAppearanceConfig.widgetTitle,
                ),
            );
            this.dataSource.data = widgets;
        });
    }

    openDataView(dataView: DataExplorerWidgetModel, editMode: boolean): void {
        this.routingService.navigateToDataView(editMode, dataView.elementId);
    }

    deleteDataView(dataView: DataExplorerWidgetModel) {
        this.dataViewService.deleteWidget(dataView.elementId);
    }
}
