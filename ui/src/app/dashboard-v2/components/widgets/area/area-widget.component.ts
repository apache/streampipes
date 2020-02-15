import {Component, OnDestroy, OnInit} from "@angular/core";
import {BaseNgxLineChartsStreamPipesWidget} from "../base/base-ngx-line-charts-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";

@Component({
    selector: 'area-widget',
    templateUrl: './area-widget.component.html',
    styleUrls: ['./area-widget.component.css']
})
export class AreaWidgetComponent extends BaseNgxLineChartsStreamPipesWidget implements OnInit, OnDestroy {

    constructor(rxStompService: RxStompService, resizeService: ResizeService) {
        super(rxStompService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

}