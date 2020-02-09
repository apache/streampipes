import {Component, OnInit} from "@angular/core";
import {Dashboard} from "./models/dashboard.model";
import {MockDashboardService} from "./services/MockDashboard.service";
import {DashboardService} from "./services/dashboard.service";

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

    selectedDashboard: Dashboard;
    selectedIndex: number = 0;
    dashboardsLoaded: boolean = false;
    dashboardTabSelected: boolean = false;

    dashboards: Array<Dashboard>;

    constructor(private dashboardService: DashboardService) {}

    public ngOnInit() {
        this.getDashboards();

    }


    selectDashboard(index: number) {
        if (index == 0) {
            this.dashboardTabSelected = false;
        } else {
            this.dashboardTabSelected = true;
            this.selectedDashboard = this.dashboards[(index - 1)];
        }
    }

    protected getDashboards(reload?: boolean) {
        this.dashboardsLoaded = false;
        this.dashboardService.getDashboards().subscribe(data => {
            this.dashboards = data;
            this.selectedIndex = 0;
            this.dashboardsLoaded = true;
        });
    }
}
