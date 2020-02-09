import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from "@angular/core";
import {Dashboard, DashboardConfig, DashboardItem} from "../../models/dashboard.model";
import {Subscription} from "rxjs";
import {GridType} from "angular-gridster2";
import {MatDialog} from "@angular/material/dialog";
import {AddVisualizationDialogComponent} from "../../dialogs/add-widget/add-visualization-dialog.component";
import {DashboardWidget} from "../../../core-model/dashboard/DashboardWidget";
import {DashboardService} from "../../services/dashboard.service";

@Component({
    selector: 'dashboard-panel',
    templateUrl: './dashboard-panel.component.html',
    styleUrls: ['./dashboard-panel.component.css']
})
export class DashboardPanelComponent implements OnInit, OnChanges {

    @Input() dashboard: Dashboard;
    @Input("editMode") editMode: boolean;
    @Output("editModeChange") editModeChange: EventEmitter<boolean> = new EventEmitter();

    public options: DashboardConfig;
    public items: DashboardItem[];

    protected subscription: Subscription;

    constructor(private dashboardService: DashboardService,
                public dialog: MatDialog) {}

    public ngOnInit() {
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: this.editMode },
            gridType: GridType.VerticalFixed,
            minCols: 8,
            maxCols: 8,
            minRows: 4,
            fixedRowHeight: 100,
            fixedColWidth: 100,
            resizable: { enabled: this.editMode }
        };
    }

    addWidget(): void {
        const dialogRef = this.dialog.open(AddVisualizationDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container'
        });

        dialogRef.afterClosed().subscribe(widget => {
            if (widget) {
                this.addWidgetToDashboard(widget);
                this.updateDashboard();
            }
        });
    }

    addWidgetToDashboard(widget: DashboardWidget) {
        let dashboardItem = {} as DashboardItem;
        dashboardItem.widgetId = widget._id;
        dashboardItem.id = widget._id;
        // TODO there should be a widget type DashboardWidget
        dashboardItem.widgetType = widget.dashboardWidgetSettings.widgetName;
        dashboardItem.cols = 2;
        dashboardItem.rows = 2;
        dashboardItem.x = 0;
        dashboardItem.y = 0;
        this.dashboard.widgets.push(dashboardItem);
    }

    updateDashboard() {
        this.dashboardService.updateDashboard(this.dashboard).subscribe(result => {
            this.dashboard._rev = result._rev;
        })
    }

    updateDashboardAndCloseEditMode() {
        this.updateDashboard();
        this.editModeChange.emit(!(this.editMode));
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes["editMode"] && this.options) {
            // if (this.editMode) {
            //     this.updateDashboard();
            // }
            // this.editMode = !(this.editMode);
            this.options.draggable.enabled = this.editMode;
            this.options.resizable.enabled = this.editMode;
            this.options.displayGrid = this.editMode ? 'always' : 'none';
            this.options.api.optionsChanged();
        }
    }

}