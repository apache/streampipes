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
    AfterViewInit,
    Component,
    ElementRef,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { AbstractWidgetViewDirective } from '../abstract-widget-view.directive';
import { ResizeService } from '../../../services/resize.service';
import {
    DashboardItem,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-dashboard-slide-view',
    templateUrl: './data-explorer-dashboard-slide-view.component.html',
    styleUrls: ['./data-explorer-dashboard-slide-view.component.scss'],
})
export class DataExplorerDashboardSlideViewComponent
    extends AbstractWidgetViewDirective
    implements AfterViewInit
{
    selectedWidgetIndex = 0;

    gridsterItemComponent: any = { width: 100, height: 100 };

    currentWidget: DataExplorerWidgetModel;
    currentMeasure: DataLakeMeasure;
    currentDashboardItem: DashboardItem;

    displayWidget = false;

    @ViewChild('slideViewOuter') slideViewOuter: ElementRef;

    constructor(
        protected resizeService: ResizeService,
        protected dataViewDataExplorerService: DataViewDataExplorerService,
    ) {
        super(resizeService, dataViewDataExplorerService);
    }

    selectWidget(index: number, widgetId: string): void {
        this.displayWidget = false;
        setTimeout(() => {
            this.selectedWidgetIndex = index;
            this.currentWidget = this.configuredWidgets.get(widgetId);
            this.currentMeasure = this.dataLakeMeasures.get(widgetId);
            this.currentDashboardItem = this.dashboard.widgets[
                index
            ] as unknown as DashboardItem;
            this.currentlyConfiguredWidgetId = widgetId;

            // Opens the design panel for the current widget when in edit mode
            if (this.editMode) {
                this.startEditModeEmitter.emit(this.currentWidget);
            }

            this.displayWidget = true;
        });
    }

    ngAfterViewInit(): void {
        const obs = new ResizeObserver(entries => {
            entries.forEach(entry => {
                const cr = entry.contentRect;
                this.gridsterItemComponent.width = cr.width;
                this.gridsterItemComponent.height = cr.height;
                this.resizeService.notify({
                    gridsterItem:
                        this.dashboard.widgets[this.selectedWidgetIndex],
                    gridsterItemComponent: this.gridsterItemComponent,
                });
            });
        });
        obs.observe(document.getElementById('slideViewOuter'));
    }

    onOptionsChanged() {}

    onWidgetsAvailable(): void {
        this.selectWidget(0, this.dashboard.widgets[0].id);
    }

    isGridView(): boolean {
        return false;
    }

    selectNewWidget(widgetId): void {
        this.selectWidget(this.dashboard.widgets.length - 1, widgetId);
    }
}
