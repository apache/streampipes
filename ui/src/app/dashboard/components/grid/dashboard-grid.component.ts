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
    AfterContentInit,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    QueryList,
    SimpleChanges,
    ViewChildren,
} from '@angular/core';
import {
    Dashboard,
    DashboardConfig,
    DashboardItem,
    DashboardWidgetModel,
    DataLakeMeasure,
    DatalakeRestService,
    SpQueryResult,
} from '@streampipes/platform-services';
import { ResizeService } from '../../services/resize.service';
import { GridsterItemComponent, GridType } from 'angular-gridster2';
import { GridsterInfo } from '../../models/gridster-info.model';
import { DashboardWidgetComponent } from '../widget/dashboard-widget.component';
import { exhaustMap } from 'rxjs/operators';
import { Observable, Subscription, timer } from 'rxjs';

@Component({
    selector: 'sp-dashboard-grid',
    templateUrl: './dashboard-grid.component.html',
    styleUrls: ['./dashboard-grid.component.css'],
})
export class DashboardGridComponent
    implements OnInit, OnChanges, AfterContentInit, OnDestroy
{
    @Input() editMode: boolean;
    @Input() headerVisible: boolean;
    @Input() dashboard: Dashboard;
    @Input() allMeasurements: DataLakeMeasure[];

    @Output() deleteCallback: EventEmitter<DashboardItem> =
        new EventEmitter<DashboardItem>();
    @Output() updateCallback: EventEmitter<DashboardWidgetModel> =
        new EventEmitter<DashboardWidgetModel>();

    options: DashboardConfig;
    loaded = false;

    subscription: Subscription;

    @ViewChildren(GridsterItemComponent)
    gridsterItemComponents: QueryList<GridsterItemComponent>;
    @ViewChildren(DashboardWidgetComponent)
    dashboardWidgetComponents: QueryList<DashboardWidgetComponent>;

    constructor(
        private resizeService: ResizeService,
        private datalakeRestService: DatalakeRestService,
    ) {}

    ngOnInit(): void {
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: this.editMode },
            gridType: GridType.VerticalFixed,
            minCols: 12,
            maxCols: 12,
            minRows: 4,
            fixedRowHeight: 50,
            fixedColWidth: 50,
            margin: 5,
            resizable: { enabled: this.editMode },
            displayGrid: this.editMode ? 'always' : 'none',
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
            },
        };
    }

    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['editMode'] && this.options) {
            this.options.draggable.enabled = this.editMode;
            this.options.resizable.enabled = this.editMode;
            this.options.displayGrid = this.editMode ? 'always' : 'none';
            this.options.api.optionsChanged();
        }
    }

    propagateItemRemoval(widget: DashboardItem) {
        this.deleteCallback.emit(widget);
    }

    propagateItemUpdate(dashboardWidget: DashboardWidgetModel) {
        this.updateCallback.emit(dashboardWidget);
    }

    ngAfterContentInit(): void {
        if (this.dashboard.dashboardGeneralSettings.globalRefresh) {
            this.checkWidgetsReady();
        }
    }

    checkWidgetsReady() {
        if (this.dashboardWidgetComponents) {
            this.createQuerySubscription();
        } else {
            setTimeout(() => this.checkWidgetsReady(), 1000);
        }
    }

    createQuerySubscription() {
        this.subscription = timer(
            0,
            this.dashboard.dashboardGeneralSettings.refreshIntervalInSeconds *
                1000,
        )
            .pipe(exhaustMap(() => this.makeQueryObservable()))
            .subscribe(res => {
                if (res.length > 0) {
                    this.dashboardWidgetComponents.forEach((widget, index) => {
                        const widgetId = widget.getWidgetId();
                        const queryResult = res.find(r => r.forId === widgetId);
                        if (queryResult) {
                            widget.processQueryResponse(queryResult);
                        }
                    });
                }
            });
    }

    makeQueryObservable(): Observable<SpQueryResult[]> {
        const queries = this.dashboardWidgetComponents
            .map(dw => dw.getWidgetQuery())
            .filter(query => query !== undefined);
        return this.datalakeRestService.performMultiQuery(queries);
    }
}
