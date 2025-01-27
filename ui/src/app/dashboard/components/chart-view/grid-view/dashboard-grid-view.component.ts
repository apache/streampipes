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
    Component,
    OnChanges,
    OnInit,
    QueryList,
    SimpleChanges,
    ViewChildren,
} from '@angular/core';
import { GridsterItemComponent, GridType } from 'angular-gridster2';
import { GridsterInfo } from '../../../../data-explorer-shared/models/gridster-info.model';
import { IDataViewDashboardConfig } from '../../../../data-explorer-shared/models/dataview-dashboard.model';
import { ResizeService } from '../../../../data-explorer-shared/services/resize.service';
import { DataViewDataExplorerService } from '@streampipes/platform-services';
import { AbstractChartViewDirective } from '../abstract-chart-view.directive';
import { DataExplorerChartRegistry } from '../../../../data-explorer-shared/registry/data-explorer-chart-registry';

@Component({
    selector: 'sp-dashboard-grid-view',
    templateUrl: './dashboard-grid-view.component.html',
    styleUrls: ['./dashboard-grid-view.component.scss'],
})
export class DashboardGridViewComponent
    extends AbstractChartViewDirective
    implements OnInit, OnChanges
{
    options: IDataViewDashboardConfig;
    loaded = false;

    @ViewChildren(GridsterItemComponent)
    gridsterItemComponents: QueryList<GridsterItemComponent>;

    constructor(
        protected resizeService: ResizeService,
        protected dataViewDataExplorerService: DataViewDataExplorerService,
        protected widgetRegistryService: DataExplorerChartRegistry,
    ) {
        super(
            resizeService,
            dataViewDataExplorerService,
            widgetRegistryService,
        );
    }

    ngOnInit(): void {
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: this.editMode },
            gridType: GridType.VerticalFixed,
            minCols: 8,
            maxCols: 8,
            minRows: 4,
            fixedRowHeight: 100,
            fixedColWidth: 100,
            margin: 5,
            displayGrid: this.editMode ? 'always' : 'none',
            resizable: { enabled: this.editMode },
            itemResizeCallback: (item, itemComponent) => {
                this.resizeService.notify({
                    gridsterItem: item,
                    gridsterItemComponent: itemComponent,
                } as GridsterInfo);
            },
            itemInitCallback: (item, itemComponent) => {
                this.resizeService.notify({
                    gridsterItem: item,
                    gridsterItemComponent: itemComponent,
                } as GridsterInfo);
                window.dispatchEvent(new Event('resize'));
            },
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['editMode'] && this.options) {
            this.options.draggable.enabled = this.editMode;
            this.options.resizable.enabled = this.editMode;
            this.options.displayGrid = this.editMode ? 'always' : 'none';
            this.options.api.optionsChanged();
        }
    }

    onOptionsChanged() {
        this.options.api.optionsChanged();
    }

    onWidgetsAvailable(): void {}

    isGridView(): boolean {
        return true;
    }

    selectNewWidget(widgetId): void {}
}
