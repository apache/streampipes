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
import { GridstackComponent, nodesCB } from 'gridstack/dist/angular';

@Component({
    selector: 'sp-dashboard-grid-view',
    templateUrl: './dashboard-grid-view.component.html',
    styleUrls: ['./dashboard-grid-view.component.scss'],
    standalone: false,
})
export class DashboardGridViewComponent
    extends AbstractChartViewDirective
    implements OnInit, AfterViewInit, OnChanges
{
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
            cellHeight: 'initial',
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

    isGridView(): boolean {
        return true;
    }

    selectNewWidget(widgetId): void {}
}
