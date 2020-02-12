import {BaseStreamPipesWidget} from "./base-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {GridsterInfo} from "../../../models/gridster-info.model";

export abstract class BaseNgxChartsStreamPipesWidget extends BaseStreamPipesWidget {

    view: any[] = [];
    displayChart: boolean = false;

    constructor(rxStompService: RxStompService, protected resizeService: ResizeService) {
        super(rxStompService);
    }

    ngOnInit() {
        super.ngOnInit();
        this.view = [this.gridsterItem.width, this.gridsterItem.height];
        this.displayChart = true;
        this.resizeService.resizeSubject.subscribe(info => {
            this.onResize(info);
        });
    }

    onResize(info: GridsterInfo) {
        if (info.gridsterItem.id === this.gridsterItem.id) {
            setTimeout(() => {
                this.displayChart = false;
                this.view = [info.gridsterItemComponent.width, info.gridsterItemComponent.height];
                this.displayChart = true;
            }, 100);
        }
    }

}