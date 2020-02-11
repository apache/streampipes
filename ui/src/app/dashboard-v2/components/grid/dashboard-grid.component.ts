import {AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges} from "@angular/core";
import {Dashboard, DashboardConfig} from "../../models/dashboard.model";
import {GridsterInfo} from "../../models/gridster-info.model";
import {ResizeService} from "../../services/resize.service";
import {GridType} from "angular-gridster2";

@Component({
    selector: 'dashboard-grid',
    templateUrl: './dashboard-grid.component.html',
    styleUrls: ['./dashboard-grid.component.css']
})
export class DashboardGridComponent implements OnInit, OnChanges {

    @Input() editMode: boolean;
    @Input() dashboard: Dashboard;
    options: DashboardConfig;

    constructor(private resizeService: ResizeService) {

    }

    ngOnInit(): void {
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: this.editMode },
            gridType: GridType.VerticalFixed,
            minCols: 8,
            maxCols: 8,
            minRows: 4,
            fixedRowHeight: 100,
            fixedColWidth: 100,
            resizable: { enabled: this.editMode },
            itemResizeCallback: ((item, itemComponent) => {
                this.resizeService.notify({gridsterItem: item, gridsterItemComponent: itemComponent} as GridsterInfo);
            }),
            itemInitCallback: ((item, itemComponent) => {
                this.resizeService.notify({gridsterItem: item, gridsterItemComponent: itemComponent} as GridsterInfo);
            })
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes["editMode"] && this.options) {
            this.options.draggable.enabled = this.editMode;
            this.options.resizable.enabled = this.editMode;
            this.options.displayGrid = this.editMode ? 'always' : 'none';
            this.options.api.optionsChanged();
        }
    }
}