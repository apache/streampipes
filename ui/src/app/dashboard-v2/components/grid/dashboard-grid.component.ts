import {Component, Input, OnInit} from "@angular/core";
import {Dashboard, DashboardConfig} from "../../models/dashboard.model";

@Component({
    selector: 'dashboard-grid',
    templateUrl: './dashboard-grid.component.html',
    styleUrls: ['./dashboard-grid.component.css']
})
export class DashboardGridComponent implements OnInit {

    @Input() editMode: boolean;
    @Input() dashboard: Dashboard;
    @Input() options: DashboardConfig;

    ngOnInit(): void {
    }

}