import {EventEmitter, Injectable, Input, Output} from '@angular/core';
import {EventSchema} from '../../connect/schema-editor/model/EventSchema';

@Injectable()
export abstract class BaseChartComponent {


    @Input() set datas(value: any[]) {
        if (value != undefined) {
            this.data = value;
            if (this.data !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
                this.transformedData = this.transformData(this.data, this.xKey);
                this.displayData(this.transformedData, this.yKeys);
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
                this.transformedData = this.transformData(this.data, this.xKey);
                this.displayData(this.transformedData, this.yKeys);
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
                    this.transformedData = this.transformData(this.data, this.xKey);
                this.displayData(this.transformedData, this.yKeys);
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

    xKey: String = undefined;
    yKeys: String[] = undefined;
    data: any[] = undefined;

    transformedData: any[] = undefined;


    //transform the input data to the schema of the chart
    abstract transformData(data: any[], xKey: String): any[];

    //display the data
    abstract displayData(transformedData: any[], yKeys: String[]);

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

}