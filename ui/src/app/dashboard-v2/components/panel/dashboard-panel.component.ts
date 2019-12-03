import {Component, Input, OnInit} from "@angular/core";
import {Dashboard, DashboardConfig, DashboardWidget} from "../../models/dashboard.model";
import {Subscription} from "rxjs";
import {MockDashboardService} from "../../services/MockDashboard.service";
import {GridType} from "angular-gridster2";

@Component({
    selector: 'dashboard-panel',
    templateUrl: './dashboard-panel.component.html',
    styleUrls: ['./dashboard-panel.component.css']
})
export class DashboardPanelComponent implements OnInit {

    @Input() dashboard: Dashboard;

    public options: DashboardConfig;
    public items: DashboardWidget[];

    protected subscription: Subscription;

    constructor(private dashboardService: MockDashboardService) {}

    public ngOnInit() {
console.log(this.dashboard);
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: true },
            gridType: GridType.Fit,
            minCols: 8,
            maxCols: 8,
            minRows: 2,
            resizable: { enabled: true }
        };
        this.items = this.dashboard.widgets;
        console.log("items");
        console.log(this.items);
    }

}