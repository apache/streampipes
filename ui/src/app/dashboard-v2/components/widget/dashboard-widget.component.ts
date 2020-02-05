import {Component, Input, OnInit} from "@angular/core";
import {Dashboard, DashboardItem} from "../../models/dashboard.model";
import {DashboardService} from "../../services/dashboard.service";
import {DashboardImageComponent} from "../../../app-transport-monitoring/components/dashboard-image/dashboard-image.component";
import {DashboardWidget} from "../../../core-model/dashboard/DashboardWidget";

@Component({
    selector: 'dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css']
})
export class DashboardWidgetComponent implements OnInit {

    @Input() widget: DashboardItem;

    widgetLoaded: boolean = false;
    configuredWidget: DashboardWidget;

    constructor(private dashboardService: DashboardService) {
    }

    ngOnInit(): void {
        this.dashboardService.getWidget(this.widget.id).subscribe(response => {
            this.configuredWidget = response;
            this.widgetLoaded = true;
        });
    }
}