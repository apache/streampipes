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

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import {
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DataViewDataExplorerService,
    TimeSettings,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { TimeSelectionService } from '../../services/time-selection.service';
import { DataExplorerRoutingService } from '../../services/data-explorer-routing.service';
import { DataExplorerDashboardService } from '../../services/data-explorer-dashboard.service';

@Component({
    selector: 'sp-data-explorer-data-view',
    templateUrl: './data-explorer-data-view.component.html',
    styleUrls: ['./data-explorer-data-view.component.scss'],
})
export class DataExplorerDataViewComponent implements OnInit {
    dataViewLoaded = false;
    timeSettings: TimeSettings;

    editMode = true;
    dataView: DataExplorerWidgetModel;
    dataLakeMeasure: DataLakeMeasure;
    gridsterItemComponent: any;

    @ViewChild('panel', { static: false }) outerPanel: ElementRef;

    constructor(
        private dashboardService: DataExplorerDashboardService,
        private route: ActivatedRoute,
        private routingService: DataExplorerRoutingService,
        private dataViewService: DataViewDataExplorerService,
        private timeSelectionService: TimeSelectionService,
    ) {}

    ngOnInit() {
        const dataViewId = this.route.snapshot.params.id;
        this.editMode = this.route.snapshot.queryParams.editMode;

        if (dataViewId) {
            this.loadDataView(dataViewId);
        } else {
            this.createWidget();
            this.timeSettings = this.makeDefaultTimeSettings();
            this.afterDataViewLoaded();
        }
    }

    loadDataView(dataViewId: string): void {
        this.dataViewLoaded = false;
        this.dataViewService.getWidget(dataViewId).subscribe(res => {
            this.dataView = res;
            if (!this.dataView.timeSettings?.startTime) {
                this.timeSettings = this.makeDefaultTimeSettings();
            } else {
                this.timeSettings = this.dataView.timeSettings as TimeSettings;
            }
            this.afterDataViewLoaded();
        });
    }

    afterDataViewLoaded(): void {
        this.dataViewLoaded = true;
        setTimeout(() => {
            const width = this.outerPanel.nativeElement.offsetWidth;
            const height = this.outerPanel.nativeElement.offsetHeight;
            this.gridsterItemComponent = { width, height };
            this.timeSelectionService.notify(this.timeSettings);
        });
    }

    editDataView(): void {
        this.routingService.navigateToDataView(true, this.dataView.elementId);
    }

    makeDefaultTimeSettings(): TimeSettings {
        return this.timeSelectionService.getDefaultTimeSettings();
    }

    createWidget() {
        this.dataLakeMeasure = new DataLakeMeasure();
        this.dataView = new DataExplorerWidgetModel();
        this.dataView['@class'] =
            'org.apache.streampipes.model.datalake.DataExplorerWidgetModel';
        this.dataView.baseAppearanceConfig = {};
        this.dataView.baseAppearanceConfig.widgetTitle = 'New Widget';
        this.dataView.dataConfig = {};
        this.dataView.dataConfig.ignoreMissingValues = false;
        this.dataView.baseAppearanceConfig.backgroundColor = '#FFFFFF';
        this.dataView.baseAppearanceConfig.textColor = '#3e3e3e';
        this.dataView = { ...this.dataView };
    }

    saveDataView(): void {
        this.dataView.timeSettings = this.timeSettings;
        const observable =
            this.dataView.elementId !== undefined
                ? this.dataViewService.updateWidget(this.dataView)
                : this.dataViewService.saveWidget(this.dataView);
        observable.subscribe(() => {
            this.routingService.navigateToOverview();
        });
    }

    updateDateRange(timeSettings: TimeSettings) {
        this.timeSettings = timeSettings;
        this.timeSelectionService.notify(timeSettings);
    }

    downloadDataAsFile() {
        this.dashboardService.downloadDataAsFile(
            this.timeSettings,
            this.dataView,
        );
    }
}
