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
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { AbstractChartViewDirective } from '../abstract-chart-view.directive';
import { GridStack, GridStackOptions } from 'gridstack';
import {
    GridstackComponent,
    GridstackItemComponent,
    nodesCB,
} from 'gridstack/dist/angular';
import { ChartContainerComponent } from '../../../../chart-shared/components/chart-container/chart-container.component';

@Component({
    selector: 'sp-dashboard-grid-view',
    templateUrl: './dashboard-grid-view.component.html',
    styleUrls: ['./dashboard-grid-view.component.scss'],
    imports: [
        GridstackComponent,
        GridstackItemComponent,
        ChartContainerComponent,
    ],
})
export class DashboardGridViewComponent
    extends AbstractChartViewDirective
    implements OnInit, AfterViewInit, OnChanges
{
    private readonly defaultGridCellHeightPx = 90;
    private readonly minGridCellHeightPx = 40;
    private readonly maxGridCellHeightPx = 200;

    @Input()
    kioskMode = false;

    loaded = false;

    @ViewChild('grid', { static: true })
    gridComp: GridstackComponent;

    grid: GridStack;

    gridOptions: GridStackOptions = {};

    ngAfterViewInit() {
        this.grid = this.gridComp.grid;
    }

    ngOnInit(): void {
        this.loadWidgetConfigs();
        this.gridOptions = {
            minRow: 5,
            column: this.dashboard.gridColumns,
            margin: 2,
            cellHeight: this.getGridCellHeight(),
            disableResize: !this.editMode,
            disableDrag: !this.editMode,
            float: true,
            resizable: {
                handles: 'w,e,se',
            },
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['editMode'] && this.grid) {
            this.gridOptions.disableResize = !this.editMode;
            this.gridOptions.disableDrag = !this.editMode;
            this.grid.updateOptions(this.gridOptions);
        }
    }

    onGridChange(data: nodesCB): void {
        data.nodes.forEach(changed => {
            const widget = this.dashboard.widgets.find(
                w => w.id === (changed as any).id,
            );
            if (widget) {
                widget.x = changed.x;
                widget.y = changed.y;
                widget.w = changed.w;
                widget.h = changed.h;
            }
        });
    }

    onWidgetsAvailable(): void {}

    private getGridCellHeight(): number {
        const configuredValue = Number(
            this.dashboard?.dashboardGeneralSettings?.gridRowHeightPx,
        );

        if (Number.isNaN(configuredValue)) {
            return this.defaultGridCellHeightPx;
        }

        return Math.min(
            this.maxGridCellHeightPx,
            Math.max(this.minGridCellHeightPx, configuredValue),
        );
    }

    isGridView(): boolean {
        return true;
    }

    selectNewWidget(_widgetId): void {}
}
