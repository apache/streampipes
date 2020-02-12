import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {LineConfig} from "./line-config";
import {ResizeService} from "../../../services/resize.service";
import {GridsterInfo} from "../../../models/gridster-info.model";
import {NumberCardComponent} from "@swimlane/ngx-charts";

@Component({
    selector: 'line-widget',
    templateUrl: './line-widget.component.html',
    styleUrls: ['./line-widget.component.css']
})
export class LineWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

    @ViewChild('pbgChartContainer') pbgChartContainer: ElementRef;
    @ViewChild('chart') chart: NumberCardComponent;

    multi:any = [];
    view: any[] = [];

    selectedNumberProperty: string;
    selectedTimestampProperty: string;
    title: string;
    minYAxisRange: number;
    maxYAxisRange: number;
    displayChart: boolean = false;

    constructor(rxStompService: RxStompService, private resizeService: ResizeService) {
        super(rxStompService);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.view = [this.gridsterItem.width, this.gridsterItem.height];
        this.displayChart = true;
        this.resizeService.resizeSubject.subscribe(info => {
            this.onResize(info);
        });
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