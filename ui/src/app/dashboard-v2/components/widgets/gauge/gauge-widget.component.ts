import {Component, OnDestroy, OnInit} from "@angular/core";
import {BaseNgxChartsStreamPipesWidget} from "../base/base-ngx-charts-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {GaugeConfig} from "./gauge-config";


@Component({
    selector: 'gauge-widget',
    templateUrl: './gauge-widget.component.html',
    styleUrls: ['./gauge-widget.component.css']
})
export class GaugeWidgetComponent extends BaseNgxChartsStreamPipesWidget implements OnInit, OnDestroy {

    data: any = [];
    title: string;
    color: string = "green";
    min: number;
    max: number;

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
        this.color = extractor.selectedColor(GaugeConfig.COLOR_KEY);
        this.min = extractor.integerParameter(GaugeConfig.MIN_KEY);
        this.max = extractor.integerParameter(GaugeConfig.MAX_KEY);
        this.title = extractor.singleValueParameter(GaugeConfig.TITLE_KEY);
        this.selectedProperty = extractor.mappingPropertyValue(GaugeConfig.NUMBER_MAPPING_KEY);
    }

    isNumber(item: any): boolean {
        return false;
    }

    protected onEvent(event: any) {
        this.data[0] = ({"name": "value", "value": event[this.selectedProperty]});
        this.data = [...this.data];
    }

}