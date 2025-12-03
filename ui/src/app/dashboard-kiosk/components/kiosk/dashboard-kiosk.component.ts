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

import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
    CompositeDashboard,
    Dashboard,
    DashboardService,
    DataExplorerWidgetModel,
    TimeSettings,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { of, Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TimeSelectionService } from '@streampipes/shared-ui';
import { DataExplorerDashboardService } from '../../../dashboard-shared/services/dashboard.service';
import { ChartSharedService } from '../../../data-explorer-shared/services/chart-shared.service';
import { ObservableGenerator } from '../../../data-explorer-shared/models/dataview-dashboard.model';

@Component({
    selector: 'sp-dashboard-kiosk',
    standalone: false,
    templateUrl: './dashboard-kiosk.component.html',
    styleUrl: './dashboard-kiosk.component.scss',
})
export class DashboardKioskComponent implements OnInit, OnDestroy {
    private route = inject(ActivatedRoute);
    private dashboardService = inject(DashboardService);
    private timeSelectionService = inject(TimeSelectionService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(ChartSharedService);

    observableGenerator: ObservableGenerator;
    dashboard: Dashboard;
    widgets: DataExplorerWidgetModel[] = [];
    refresh$: Subscription;
    eTag: string;

    ngOnInit() {
        const dashboardId = this.route.snapshot.params.dashboardId;
        this.observableGenerator =
            this.dataExplorerSharedService.kioskModeObservableGenerator(
                dashboardId,
            );
        this.dashboardService
            .getCompositeDashboard(dashboardId)
            .subscribe(res => {
                if (res.ok) {
                    const cd = res.body;
                    cd.dashboard.widgets.forEach(w => {
                        w.widgetId ??=
                            this.dataExplorerDashboardService.makeUniqueWidgetId();
                    });
                    const eTag = res.headers.get('ETag');
                    this.initDashboard(cd, eTag);
                }
            });
    }

    initDashboard(cd: CompositeDashboard, eTag: string): void {
        this.dashboard = cd.dashboard;
        this.widgets = cd.widgets;
        this.eTag = eTag;
        if (this.dashboard.dashboardLiveSettings.refreshModeActive) {
            this.createQuerySubscription();
            this.createRefreshListener();
        }
    }

    createQuerySubscription() {
        this.refresh$ = timer(
            0,
            this.dashboard.dashboardLiveSettings.refreshIntervalInSeconds *
                1000,
        )
            .pipe(
                switchMap(() => {
                    this.timeSelectionService.updateTimeSettings(
                        this.timeSelectionService.defaultQuickTimeSelections,
                        this.dashboard.dashboardTimeSettings,
                        new Date(),
                    );
                    this.updateDateRange(this.dashboard.dashboardTimeSettings);
                    return of(null);
                }),
            )
            .subscribe();
    }

    createRefreshListener(): void {
        this.dashboardService
            .getCompositeDashboard(this.dashboard.elementId, this.eTag) // this should send If-None-Match
            .subscribe({
                next: res => {
                    if (res.status === 200) {
                        const newEtag = res.headers.get('ETag');
                        if (newEtag) {
                            this.eTag = newEtag;
                        }
                        this.dashboard = undefined;
                        this.refresh$?.unsubscribe();
                        setTimeout(() => {
                            this.initDashboard(res.body, newEtag);
                        });
                    }
                    setTimeout(() => this.createRefreshListener(), 5000);
                },
                error: err => {
                    setTimeout(() => this.createRefreshListener(), 5000);
                },
            });
    }

    updateDateRange(timeSettings: TimeSettings) {
        let ts = undefined;
        if (this.dashboard.dashboardGeneralSettings.globalTimeEnabled) {
            this.dashboard.dashboardTimeSettings = timeSettings;
            ts = timeSettings;
        }
        this.timeSelectionService.notify(ts);
    }

    ngOnDestroy() {
        this.refresh$?.unsubscribe();
    }
}
