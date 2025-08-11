import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
    Dashboard,
    DashboardService,
    DataExplorerWidgetModel,
    TimeSettings,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { of, Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TimeSelectionService } from '@streampipes/shared-ui';

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

    dashboard: Dashboard;
    widgets: DataExplorerWidgetModel[] = [];
    refresh$: Subscription;

    ngOnInit() {
        const dashboardId = this.route.snapshot.params.dashboardId;
        this.dashboardService
            .getCompositeDashboard(dashboardId)
            .subscribe(cd => {
                this.dashboard = cd.dashboard;
                this.widgets = cd.widgets;
                if (this.dashboard.dashboardLiveSettings.refreshModeActive) {
                    this.createQuerySubscription();
                }
            });
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
