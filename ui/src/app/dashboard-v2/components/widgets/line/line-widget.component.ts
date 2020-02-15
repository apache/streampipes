import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from "@angular/core";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {LineConfig} from "./line-config";
import {ResizeService} from "../../../services/resize.service";
import {BaseNgxChartsStreamPipesWidget} from "../base/base-ngx-charts-widget";
import {BaseNgxLineChartsStreamPipesWidget} from "../base/base-ngx-line-charts-widget";

@Component({
    selector: 'line-widget',
    templateUrl: './line-widget.component.html',
    styleUrls: ['./line-widget.component.css']
})
export class LineWidgetComponent extends BaseNgxLineChartsStreamPipesWidget implements OnInit, OnDestroy {

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