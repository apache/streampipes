import {Component} from '@angular/core';
import {DashboardConfiguration} from "./model/dashboard-configuration.model";

@Component({
    templateUrl: './app-asset-monitoring.component.html',
    styleUrls: ['./app-asset-monitoring.component.css']
})
export class AppAssetMonitoringComponent {


    selectedIndex: number = 0;
    dashboardSelected: boolean = false;
    selectedDashboard: DashboardConfiguration;

    constructor() {

    }

    ngOnInit() {

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