import {Component, EventEmitter, Output} from '@angular/core';
import {DashboardConfiguration} from "./model/dashboard-configuration.model";

@Component({
    selector: 'app-asset-monitoring',
    templateUrl: './app-asset-monitoring.component.html',
    styleUrls: ['./app-asset-monitoring.component.css']
})
export class AppAssetMonitoringComponent {


    selectedIndex: number = 0;
    dashboardSelected: boolean = false;
    selectedDashboard: DashboardConfiguration;
    @Output() appOpened = new EventEmitter<boolean>();

    constructor() {

    }

    ngOnInit() {
        this.appOpened.emit(true);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    openDashboard(dashboardConfig: DashboardConfiguration) {
        this.selectedDashboard = dashboardConfig;
        this.dashboardSelected = true;
    }

    closeDashboard(dashboardClosed: boolean) {
        this.dashboardSelected = false;
    }

}