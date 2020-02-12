import {Component, OnDestroy, OnInit} from "@angular/core";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {MatTableDataSource} from "@angular/material/table";
import {TableConfig} from "./table-config";
import {SemanticTypeUtilsService} from "../../../../core-services/semantic-type/semantic-type-utils.service";

@Component({
    selector: 'table-widget',
    templateUrl: './table-widget.component.html',
    styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

    title: string;
    selectedProperties: Array<string>;

    displayedColumns: String[] = [];
    dataSource = new MatTableDataSource();
    semanticTypes: { [key: string]: string; } = {};

    constructor(rxStompService: RxStompService, private semanticTypeUtils: SemanticTypeUtilsService) {
        super(rxStompService);
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.widgetConfig.dashboardWidgetDataConfig.schema.eventProperties.forEach((key, index) => {
            this.semanticTypes[key.runtimeName] = key.domainProperty
        });
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.title = extractor.singleValueParameter(TableConfig.TITLE_KEY);
        this.selectedProperties = extractor.mappingPropertyValues(TableConfig.SELECTED_PROPERTIES_KEYS);
    }

    protected onEvent(event: any) {
        this.dataSource.data.unshift(this.createTableObject(event));
        if (this.dataSource.data.length > 10) {
            this.dataSource.data.pop();
        }
        this.dataSource.data = [...this.dataSource.data];
    }

    createTableObject(event: any) {
        let object = {};
        this.selectedProperties.forEach((key, index) => {
            event[key] = this.semanticTypeUtils.getValue(event[key], this.semanticTypes[key]);
            object[key] = event[key];
        });
        return object;
    }

}