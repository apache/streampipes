import {Component, Input, OnInit} from "@angular/core";
import {Dashboard, DashboardWidget} from "../../models/dashboard.model";

@Component({
    selector: 'dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css']
})
export class DashboardWidgetComponent implements OnInit {

    @Input() widget: DashboardWidget;

    constructor() {
    }

    ngOnInit(): void {
    }


}