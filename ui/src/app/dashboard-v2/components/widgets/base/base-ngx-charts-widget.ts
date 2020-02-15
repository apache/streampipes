import {BaseStreamPipesWidget} from "./base-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {GridsterInfo} from "../../../models/gridster-info.model";
import {GridsterItemComponent} from "angular-gridster2";

export abstract class BaseNgxChartsStreamPipesWidget extends BaseStreamPipesWidget {

    view: any[] = [];
    displayChart: boolean = false;

    colorScheme: any;

    constructor(rxStompService: RxStompService, protected resizeService: ResizeService) {
        super(rxStompService);
    }

    ngOnInit() {
        super.ngOnInit();
        this.colorScheme = {domain: [this.selectedSecondaryTextColor, this.selectedPrimaryTextColor]};
        this.view = [this.computeCurrentWidth(this.gridsterItemComponent),
            this.computeCurrentHeight(this.gridsterItemComponent)];
        this.displayChart = true;
        this.resizeService.resizeSubject.subscribe(info => {
            this.onResize(info);
        });
    }

    onResize(info: GridsterInfo) {
        if (info.gridsterItem.id === this.gridsterItem.id) {
            setTimeout(() => {
                this.displayChart = false;
                this.view = [this.computeCurrentWidth(info.gridsterItemComponent),
                    this.computeCurrentHeight(info.gridsterItemComponent)];
                this.displayChart = true;
            }, 100);
        }
    }

    computeCurrentWidth(gridsterItemComponent: GridsterItemComponent): number {
        return (gridsterItemComponent.width - (BaseNgxChartsStreamPipesWidget.PADDING * 2));
    }

    computeCurrentHeight(gridsterItemComponent: GridsterItemComponent): number {
        return (gridsterItemComponent.height - (BaseNgxChartsStreamPipesWidget.PADDING * 2) - this.editModeOffset());
    }

    editModeOffset(): number {
        return this.editMode ? BaseNgxChartsStreamPipesWidget.EDIT_HEADER_HEIGHT : 0;
    }

}