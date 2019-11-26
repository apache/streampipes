import {EventEmitter, Injectable, Input, Output} from '@angular/core';
import {EventSchema} from '../../connect/schema-editor/model/EventSchema';
import {DataResult} from '../../core-model/datalake/DataResult';
import {GroupedDataResult} from '../../core-model/datalake/GroupedDataResult';

@Injectable()
export abstract class BaseChartComponent {


    @Input() set datas(value: DataResult | GroupedDataResult) {
        if (value != undefined) {
            this.data = this.clone(value);
            if (this.data !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
                this.transform();
                this.display();
            }
        } else {
            this.stopDisplayData();
            this.data = undefined;
        }
    }
    @Input() set xAxesKey(value: string) {
        if (value != undefined) {
            this.xKey = value;
            if (this.data !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
                this.transform();
                this.display();
            }
        } else {
            this.stopDisplayData();
            this.xKey = undefined;
        }
    }
    @Input() set yAxesKeys(value: string[]) {
        if (value !== undefined) {
            this.yKeys = value;
            if (this.data !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
                if (this.transformedData === undefined)
                    this.transform();
                this.display();
            }
        } else {
            this.stopDisplayData();
            this.yKeys = undefined;
        }
    }

    @Input() eventschema: EventSchema = undefined;

    @Input() startDateData:Date = undefined;
    @Input() endDateData:Date = undefined;


    @Input() currentPage: number = undefined;
    @Input() maxPage: number = undefined;
    @Input() enablePaging: boolean = false;

    @Output() previousPage = new EventEmitter<boolean>();
    @Output() nextPage = new EventEmitter<boolean>();
    @Output() firstPage = new EventEmitter<boolean>();
    @Output() lastPage = new EventEmitter<boolean>();

    xKey: string = undefined;
    yKeys: string[] = undefined;

    data: DataResult | GroupedDataResult = undefined;
    transformedData: DataResult | GroupedDataResult = undefined;


    dataMode: string = '';


    transform() {
        if (this.data["headers"] !== undefined) {
            this.transformedData = this.transformData(this.data as DataResult, this.xKey);
            this.dataMode = 'single';
        } else {
            this.transformedData = this.transformGroupedData(this.data as GroupedDataResult, this.xKey);
            this.dataMode = 'group';
        }
    }

    display() {
        if (this.data["headers"] !== undefined) {
            this.displayData(this.transformedData as DataResult, this.yKeys);
        } else {
            this.displayGroupedData(this.transformedData as GroupedDataResult, this.yKeys);
        }
    }

    //transform the input data to the schema of the chart
    abstract transformData(data: DataResult, xKey: String): DataResult;

    //transform the grouped input data to the schema of the chart
    abstract transformGroupedData(data: GroupedDataResult, xKey: string): GroupedDataResult;

    //display the data
    abstract displayData(transformedData: DataResult, yKeys: string[]);

    //display the grouped data
    abstract displayGroupedData(transformedData: GroupedDataResult, yKeys: string[]);

    //
    abstract stopDisplayData()

    clickPreviousPage(){
        this.previousPage.emit()
    }

    clickNextPage() {
        this.nextPage.emit()
    }

    clickFirstPage(){
        this.firstPage.emit()
    }

    clickLastPage() {
        this.lastPage.emit()
    }

    clone(value): DataResult {
        return (JSON.parse(JSON.stringify(value)));
    }

}