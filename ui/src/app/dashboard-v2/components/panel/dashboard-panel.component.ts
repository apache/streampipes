import {Component, Input, OnInit} from "@angular/core";
import {Dashboard, DashboardConfig, DashboardWidget} from "../../models/dashboard.model";
import {Subscription} from "rxjs";
import {MockDashboardService} from "../../services/MockDashboard.service";
import {GridType} from "angular-gridster2";
import {MatDialog} from "@angular/material/dialog";
import {AddVisualizationDialogComponent} from "../../dialogs/add-visualization-dialog.component";

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

    constructor(private dashboardService: MockDashboardService,
                public dialog: MatDialog) {}

    public ngOnInit() {
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
    }

    addWidget(): void {
        const dialogRef = this.dialog.open(AddVisualizationDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                // this.addNewVisulizationItem(result);
                // this.measurementPresent = true;
                // this.mainLayer.draw();
            }
        });
    }

}