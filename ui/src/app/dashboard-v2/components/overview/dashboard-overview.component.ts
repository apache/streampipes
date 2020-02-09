import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Dashboard} from "../../models/dashboard.model";
import {MatTableDataSource} from "@angular/material/table";
import {MatDialog} from "@angular/material/dialog";
import {DashboardService} from "../../services/dashboard.service";
import {AddVisualizationDialogComponent} from "../../dialogs/add-widget/add-visualization-dialog.component";
import {EditDashboardDialogComponent} from "../../dialogs/edit-dashboard/edit-dashboard-dialog.component";

@Component({
    selector: 'dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.css']
})
export class DashboardOverviewComponent implements OnInit {

    @Input() dashboards: Array<Dashboard>;
    @Output() reloadDashboardsEmitter = new EventEmitter<boolean>();
    @Output() selectDashboardEmitter = new EventEmitter<Dashboard>();

    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = ['name', 'open', 'openWindow', 'edit', 'delete'];

    constructor(private dashboardService: DashboardService,
                public dialog: MatDialog) {

    }

    ngOnInit(): void {
        this.dataSource.data = this.dashboards;
    }

    openNewDashboardDialog() {
        let dashboard = {} as Dashboard;
        dashboard.widgets = [];

        this.openDashboardModificationDialog(true, dashboard);
    }

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        const dialogRef = this.dialog.open(EditDashboardDialogComponent, {
            width: '70%',
            panelClass: 'custom-dialog-container'
        });
        dialogRef.componentInstance.createMode = createMode;
        dialogRef.componentInstance.dashboard = dashboard;

        dialogRef.afterClosed().subscribe(result => {
            this.reloadDashboardsEmitter.emit(true);
        });
    }

    openEditDashboardDialog(dashboard: Dashboard) {
        this.openDashboardModificationDialog(false, dashboard);
    }

    openDeleteDashboardDialog(dashboard: Dashboard) {
        // TODO add confirm dialog
        this.dashboardService.deleteDashboard(dashboard).subscribe(result => {
            this.reloadDashboardsEmitter.emit(true);
        });
    }

    showDashboard(dashboard: Dashboard) {
        this.selectDashboardEmitter.emit(dashboard);
    }

    openExternalDashboard(dashboard: Dashboard) {

    }

}