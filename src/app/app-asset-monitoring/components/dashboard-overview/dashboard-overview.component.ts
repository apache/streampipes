import {Component, EventEmitter, Input, Output} from "@angular/core";
import {RestService} from "../../services/rest.service";
import {DashboardConfiguration} from "../../model/dashboard-configuration.model";

@Component({
    selector: 'asset-dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.css']
})
export class AssetDashboardOverviewComponent {

    @Output() selectedDashboard = new EventEmitter<DashboardConfiguration>();

    dashboardConfigs: DashboardConfiguration[] = [];

    constructor(private restService: RestService) {

    }

    ngOnInit() {
        this.getDashboards();
    }

    getImageUrl(dashboardConfig: DashboardConfiguration) {
        return this.restService.getImageUrl(dashboardConfig.imageInfo.imageName);
    }

    getDashboards() {
        this.restService.getDashboards().subscribe(response => {
            this.dashboardConfigs = response;
        })
    }

    openDashboard(dashboardConfig: DashboardConfiguration) {
        this.selectedDashboard.emit(dashboardConfig);
    }

    deleteDashboard(dashboardId: string) {
        this.restService.deleteDashboard(dashboardId).subscribe(response => {
            this.getDashboards();
        })
    }
}