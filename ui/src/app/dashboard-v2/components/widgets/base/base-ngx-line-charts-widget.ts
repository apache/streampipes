import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {BaseNgxChartsStreamPipesWidget} from "./base-ngx-charts-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {LineConfig} from "../line/line-config";

export abstract class BaseNgxLineChartsStreamPipesWidget extends BaseNgxChartsStreamPipesWidget {

    multi:any = [];

    selectedNumberProperty: string;
    selectedTimestampProperty: string;
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

    protected extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedNumberProperty = extractor.mappingPropertyValue(LineConfig.NUMBER_MAPPING_KEY);
        this.selectedTimestampProperty = extractor.mappingPropertyValue(LineConfig.TIMESTAMP_MAPPING_KEY);
        this.minYAxisRange = extractor.integerParameter(LineConfig.MIN_Y_AXIS_KEY);
        this.maxYAxisRange = extractor.integerParameter(LineConfig.MAX_Y_AXIS_KEY);
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