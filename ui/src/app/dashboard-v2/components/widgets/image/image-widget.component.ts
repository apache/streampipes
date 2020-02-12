import {Component, OnDestroy, OnInit} from "@angular/core";
import {BaseNgxChartsStreamPipesWidget} from "../base/base-ngx-charts-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {GaugeConfig} from "../gauge/gauge-config";

@Component({
    selector: 'image-widget',
    templateUrl: './image-widget.component.html',
    styleUrls: ['./image-widget.component.css']
})
export class ImageWidgetComponent extends BaseNgxChartsStreamPipesWidget implements OnInit, OnDestroy {

    item: any;
    title: string;
    selectedProperty: string;

    constructor(rxStompService: RxStompService, resizeService: ResizeService) {
        super(rxStompService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.title = extractor.singleValueParameter(GaugeConfig.TITLE_KEY);
        this.selectedProperty = extractor.mappingPropertyValue(GaugeConfig.NUMBER_MAPPING_KEY);
    }

    isNumber(item: any): boolean {
        return false;
    }

    protected onEvent(event: any) {
        this.item = event[this.selectedProperty];
    }

}