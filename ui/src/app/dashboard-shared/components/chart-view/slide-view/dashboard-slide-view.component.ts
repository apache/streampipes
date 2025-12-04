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
import { AbstractChartViewDirective } from '../abstract-chart-view.directive';
import {
    ClientDashboardItem,
    DataExplorerWidgetModel,
    DataLakeMeasure,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-dashboard-slide-view',
    templateUrl: './dashboard-slide-view.component.html',
    styleUrls: ['./dashboard-slide-view.component.scss'],
    standalone: false,
})
export class DashboardSlideViewComponent
    extends AbstractChartViewDirective
    implements OnInit
{
    selectedWidgetIndex = 0;
    currentWidget: DataExplorerWidgetModel;
    currentMeasure: DataLakeMeasure;
    currentDashboardItem: ClientDashboardItem;

    displayWidget = false;

    @ViewChild('slideViewOuter') slideViewOuter: ElementRef;

    ngOnInit() {
        this.loadWidgetConfigs();
    }

    selectWidget(index: number, widgetId: string): void {
        this.displayWidget = false;
        setTimeout(() => {
            this.selectedWidgetIndex = index;
            this.currentWidget = this.configuredWidgets.get(widgetId);
            this.currentMeasure = this.dataLakeMeasures.get(widgetId);
            this.currentDashboardItem = this.dashboard.widgets[index];
            this.displayWidget = true;
        });
    }

    onWidgetsAvailable(): void {
        this.selectWidget(0, this.dashboard.widgets[0].id);
    }

    isGridView(): boolean {
        return false;
    }

    selectNewWidget(widgetId: string): void {
        this.selectWidget(this.dashboard.widgets.length - 1, widgetId);
    }
}
