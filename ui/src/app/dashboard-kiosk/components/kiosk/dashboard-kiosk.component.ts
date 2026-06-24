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
    DestroyRef,
    inject,
    OnDestroy,
    OnInit,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    CompositeDashboard,
    Dashboard,
    DashboardService,
    DataExplorerWidgetModel,
    TimeSettings,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, Subscription, timer } from 'rxjs';
import { catchError, exhaustMap, tap } from 'rxjs/operators';
import { SpLabelComponent, TimeSelectionService } from '@streampipes/shared-ui';
import { DataExplorerDashboardService } from '../../../dashboard-shared/services/dashboard.service';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { ObservableGenerator } from '../../../chart-shared/models/dataview-dashboard.model';
import { MatToolbar } from '@angular/material/toolbar';
import { DashboardGridViewComponent } from '../../../dashboard-shared/components/chart-view/grid-view/dashboard-grid-view.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-dashboard-kiosk',
    templateUrl: './dashboard-kiosk.component.html',
    styleUrl: './dashboard-kiosk.component.scss',
    imports: [
        LayoutDirective,
        MatToolbar,
        FlexDirective,
        LayoutAlignDirective,
        DashboardGridViewComponent,
        SpLabelComponent,
        TranslatePipe,
    ],
})
export class DashboardKioskComponent implements OnInit, OnDestroy {
    private route = inject(ActivatedRoute);
    private destroyRef = inject(DestroyRef);
    private dashboardService = inject(DashboardService);
    private timeSelectionService = inject(TimeSelectionService);
    private translateService = inject(TranslateService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(ChartSharedService);

    observableGenerator: ObservableGenerator;
    dashboard: Dashboard;
    widgets: DataExplorerWidgetModel[] = [];
    refresh$: Subscription;
    dashboardRefresh$: Subscription;
    lastUpdatedClock$: Subscription;
    eTag: string;
    lastUpdatedAt: number;
    currentTime: number;

    ngOnInit() {
        const dashboardId = this.route.snapshot.params.dashboardId;
        this.observableGenerator =
            this.dataExplorerSharedService.kioskModeObservableGenerator(
                dashboardId,
            );
        this.dashboardService
            .getCompositeDashboard(dashboardId)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(res => {
                if (res.ok) {
                    const eTag = res.headers.get('ETag');
                    this.initDashboard(res.body, eTag);
                }
            });
    }

    initDashboard(cd: CompositeDashboard, eTag: string): void {
        cd.dashboard.widgets.forEach(w => {
            w.id ??= this.dataExplorerDashboardService.makeUniqueWidgetId();
        });
        this.dashboard = cd.dashboard;
        this.widgets = cd.widgets;
        this.eTag = eTag;
        this.refresh$?.unsubscribe();
        if (this.dashboard.dashboardLiveSettings.refreshModeActive) {
            this.createQuerySubscription();
            this.createDashboardRefreshSubscription();
        }
    }

    createQuerySubscription() {
        this.refresh$ = timer(
            0,
            this.dashboard.dashboardLiveSettings.refreshIntervalInSeconds *
                1000,
        )
            .pipe(
                tap(() => {
                    this.timeSelectionService.updateTimeSettings(
                        this.timeSelectionService.defaultQuickTimeSelections,
                        this.dashboard.dashboardTimeSettings,
                        new Date(),
                    );
                    this.updateDateRange(this.dashboard.dashboardTimeSettings);
                }),
                takeUntilDestroyed(this.destroyRef),
            )
            .subscribe();
    }

    createDashboardRefreshSubscription(): void {
        if (this.dashboardRefresh$) {
            return;
        }

        this.dashboardRefresh$ = timer(5000, 5000)
            .pipe(
                exhaustMap(() =>
                    this.dashboardService
                        .getCompositeDashboard(
                            this.dashboard.elementId,
                            this.eTag,
                        )
                        .pipe(catchError(() => EMPTY)),
                ),
                takeUntilDestroyed(this.destroyRef),
            )
            .subscribe(res => {
                if (res.status === 200) {
                    const newEtag = res.headers.get('ETag');
                    if (newEtag) {
                        this.eTag = newEtag;
                    }
                    this.initDashboard(res.body, newEtag);
                }
            });
    }

    updateDateRange(timeSettings: TimeSettings) {
        let ts = undefined;
        if (this.dashboard.dashboardGeneralSettings.globalTimeEnabled) {
            this.dashboard.dashboardTimeSettings = timeSettings;
            ts = timeSettings;
        }
        this.timeSelectionService.notify(ts);
        this.markDataUpdated();
    }

    markDataUpdated(): void {
        this.lastUpdatedAt = Date.now();
        this.currentTime = this.lastUpdatedAt;
        this.startLastUpdatedClock();
    }

    startLastUpdatedClock(): void {
        if (this.lastUpdatedClock$) {
            return;
        }

        this.lastUpdatedClock$ = timer(1000, 1000)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
                this.currentTime = Date.now();
            });
    }

    formatLastUpdatedAt(): string {
        const exactTime = this.formatExactLastUpdatedAt();
        const relativeTime = this.formatRelativeLastUpdatedAt();
        return relativeTime ? `${relativeTime} (${exactTime})` : exactTime;
    }

    private formatExactLastUpdatedAt(): string {
        return new Intl.DateTimeFormat(this.currentLocale, {
            dateStyle: 'medium',
            timeStyle: 'medium',
        }).format(new Date(this.lastUpdatedAt));
    }

    private formatRelativeLastUpdatedAt(): string | undefined {
        const ageInSeconds = Math.max(
            0,
            Math.round((this.currentTime - this.lastUpdatedAt) / 1000),
        );

        if (ageInSeconds < 60) {
            return this.relativeTimeFormatter.format(-ageInSeconds, 'second');
        } else if (ageInSeconds < 3600) {
            return this.relativeTimeFormatter.format(
                -Math.floor(ageInSeconds / 60),
                'minute',
            );
        } else if (ageInSeconds < 86400) {
            return this.relativeTimeFormatter.format(
                -Math.floor(ageInSeconds / 3600),
                'hour',
            );
        }

        return undefined;
    }

    private get currentLocale(): string {
        return (
            this.translateService.currentLang ||
            this.translateService.defaultLang ||
            'en'
        );
    }

    private get relativeTimeFormatter(): Intl.RelativeTimeFormat {
        return new Intl.RelativeTimeFormat(this.currentLocale, {
            numeric: 'auto',
        });
    }

    ngOnDestroy() {
        this.refresh$?.unsubscribe();
        this.dashboardRefresh$?.unsubscribe();
        this.lastUpdatedClock$?.unsubscribe();
    }
}
