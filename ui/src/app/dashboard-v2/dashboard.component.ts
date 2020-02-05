import {Component, Input, OnInit} from "@angular/core";
import {Subscription} from "rxjs";
import {GridType} from "angular-gridster2";
import {Dashboard, DashboardConfig} from "./models/dashboard.model";
import {MockDashboardService} from "./services/MockDashboard.service";

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

    selectedDashboard: Dashboard;
    selectedIndex: number = 0;
    dashboardsLoaded = false;

    dashboards: Array<Dashboard>;

    constructor(private dashboardService: MockDashboardService) {}

    public ngOnInit() {
        this.getDashboards();

    }


    selectDashboard(index: number) {
        this.selectedDashboard = this.dashboards[index];
    }

    protected getDashboards() {
        this.dashboardService.getDashboards().subscribe(data => {
            this.dashboards = data;
            this.selectedIndex = 0;
            this.selectDashboard(0);
            this.dashboardsLoaded = true;
        });
    }
}
