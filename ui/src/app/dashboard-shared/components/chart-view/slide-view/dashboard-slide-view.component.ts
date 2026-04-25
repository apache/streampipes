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
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { ChartContainerComponent } from '../../../../chart-shared/components/chart-container/chart-container.component';
import { DashboardLayoutItemComponent } from '../../layout-item/dashboard-layout-item.component';

@Component({
    selector: 'sp-dashboard-slide-view',
    templateUrl: './dashboard-slide-view.component.html',
    styleUrls: ['./dashboard-slide-view.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        NgClass,
        ClassDirective,
        ChartContainerComponent,
        DashboardLayoutItemComponent,
    ],
})
export class DashboardSlideViewComponent
    extends AbstractChartViewDirective
    implements OnInit
{
    selectedWidgetIndex = 0;
    currentWidget?: DataExplorerWidgetModel;
    currentMeasure?: DataLakeMeasure;
    currentDashboardItem?: ClientDashboardItem;

    displayWidget = false;

    @ViewChild('slideViewOuter') slideViewOuter: ElementRef;

    ngOnInit() {
        this.loadWidgetConfigs();
    }

    selectWidget(index: number): void {
        this.displayWidget = false;
        setTimeout(() => {
            this.selectedWidgetIndex = index;
            this.currentDashboardItem = this.dashboard.widgets[index];
            if (!this.isChartItem(this.currentDashboardItem)) {
                this.selectDashboardItem(this.currentDashboardItem.id);
            } else {
                this.selectDashboardItem(undefined);
            }
            if (this.isChartItem(this.currentDashboardItem)) {
                const dataViewElementId =
                    this.currentDashboardItem.dataViewElementId!;
                this.currentWidget =
                    this.configuredWidgets.get(dataViewElementId)!;
                this.currentMeasure =
                    this.dataLakeMeasures.get(dataViewElementId)!;
            } else {
                this.currentWidget = undefined;
                this.currentMeasure = undefined;
            }
            this.displayWidget = true;
        });
    }

    onWidgetsAvailable(): void {
        if (this.dashboard.widgets.length > 0) {
            this.selectWidget(0);
        }
    }

    getSlideItemLabel(item: ClientDashboardItem): string {
        if (this.isChartItem(item)) {
            return (
                this.configuredWidgets.get(item.dataViewElementId!)
                    ?.baseAppearanceConfig.widgetTitle ||
                this.getDashboardItemLabel(item)
            );
        }

        return this.getDashboardItemLabel(item);
    }

    isGridView(): boolean {
        return false;
    }

    selectNewWidget(widgetId: string): void {
        const itemIndex = this.dashboard.widgets.findIndex(
            item => item.id === widgetId,
        );
        if (itemIndex >= 0) {
            this.selectWidget(itemIndex);
        }
    }
}
