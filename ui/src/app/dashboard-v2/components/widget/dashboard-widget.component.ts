import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Dashboard, DashboardItem} from "../../models/dashboard.model";
import {DashboardService} from "../../services/dashboard.service";
import {DashboardImageComponent} from "../../../app-transport-monitoring/components/dashboard-image/dashboard-image.component";
import {DashboardWidget} from "../../../core-model/dashboard/DashboardWidget";
import {Subject} from "rxjs";
import {GridsterItem, GridsterItemComponent} from "angular-gridster2";
import {GridsterInfo} from "../../models/gridster-info.model";
import {ResizeService} from "../../services/resize.service";

@Component({
    selector: 'dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css']
})
export class DashboardWidgetComponent implements OnInit {

    @Input() widget: DashboardItem;
    @Input() editMode: boolean;
    @Input() item: GridsterItem;
    @Input() gridsterItemComponent: GridsterItemComponent;

    @Output() deleteCallback: EventEmitter<DashboardItem> = new EventEmitter<DashboardItem>();

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

    removeWidget() {
        this.deleteCallback.emit(this.widget);
    }
}