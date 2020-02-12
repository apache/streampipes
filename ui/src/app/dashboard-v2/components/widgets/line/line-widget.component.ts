import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {LineConfig} from "./line-config";
import {ResizeService} from "../../../services/resize.service";
import {GridsterInfo} from "../../../models/gridster-info.model";
import {NumberCardComponent} from "@swimlane/ngx-charts";
import {BaseNgxChartsStreamPipesWidget} from "../base/base-ngx-charts-widget";

@Component({
    selector: 'line-widget',
    templateUrl: './line-widget.component.html',
    styleUrls: ['./line-widget.component.css']
})
export class LineWidgetComponent extends BaseNgxChartsStreamPipesWidget implements OnInit, OnDestroy {

    multi:any = [];

    selectedNumberProperty: string;
    selectedTimestampProperty: string;
    title: string;
    minYAxisRange: number;
    maxYAxisRange: number;

    constructor(rxStompService: RxStompService, resizeService: ResizeService) {
        super(rxStompService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.multi = [
            {
                "name": this.selectedNumberProperty,
                "series": [
                ]
            }];
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    protected extractConfig(extractor: StaticPropertyExtractor) {
        this.title = extractor.singleValueParameter(LineConfig.TITLE_KEY);
        this.selectedNumberProperty = extractor.mappingPropertyValue(LineConfig.NUMBER_MAPPING_KEY);
        this.selectedTimestampProperty = extractor.mappingPropertyValue(LineConfig.TIMESTAMP_MAPPING_KEY);
        //this.minYAxisRange = extractor.integerParameter(LineConfig.MIN_Y_AXIS_KEY);
        //this.maxYAxisRange = extractor.integerParameter(LineConfig.MAX_Y_AXIS_KEY);
    }

    protected onEvent(event: any) {
        let time = event[this.selectedTimestampProperty];
        let value = event[this.selectedNumberProperty];
        this.makeEvent(time, value);
    }

    makeEvent(time: any, value: any): void {
        this.multi[0].series.push({"name": time, "value": value});
        if (this.multi[0].series.length > 10) {
            this.multi[0].series.shift();
        }
        this.multi = [...this.multi];
    }

    timestampTickFormatting(timestamp: any): string {
        var date = new Date(timestamp);
        let timeString = date.getHours() + ':' + date.getMinutes().toString().substr(-2) + ':' + date.getSeconds().toString().substr(-2);
        return timeString;
    }

}