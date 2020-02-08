import {Component, Input, OnInit} from "@angular/core";
import {RxStompService} from "@stomp/ng2-stompjs";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {NumberConfig} from "./number-config.component";

@Component({
    selector: 'number-viz',
    templateUrl: './number-viz.component.html',
    styleUrls: ['./number-viz.component.css']
})
export class NumberVizComponent extends BaseStreamPipesWidget implements OnInit {

    item: any;
    title: string;
    color: string = "green";

    selectedProperty: string;

    constructor(rxStompService: RxStompService) {
        super(rxStompService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.title = extractor.singleValueParameter(NumberConfig.TITLE_KEY);
        this.selectedProperty = extractor.mappingPropertyValue(NumberConfig.NUMBER_MAPPING_KEY);
    }

    isNumber(item: any): boolean {
        return false;
    }

    protected onEvent(event: any) {
        console.log(event);
        console.log(this.selectedProperty);
        this.item = event[this.selectedProperty];
    }

}