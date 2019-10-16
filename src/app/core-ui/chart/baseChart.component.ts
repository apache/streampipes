import {EventEmitter, Injectable, Input, Output} from '@angular/core';
import {EventSchema} from '../../connect/schema-editor/model/EventSchema';

@Injectable()
export abstract class BaseChartComponent {


    @Input() set datas(value: any[] | Map<string, any[]>) {
        if (value != undefined) {
            this.rowData = value;
            if (this.rowData !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
                this.transform();
                this.display();
            }
        } else {
            this.stopDisplayData();
            this.rowData = undefined;
        }
    }
    @Input() set xAxesKey(value: string) {
        if (value != undefined) {
            this.xKey = value;
            if (this.rowData !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
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
            if (this.rowData !== undefined && this.xKey !== undefined && this.yKeys !== undefined) {
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

    rowData: any[] | Map<string, any[]> = undefined;
    transformedData: any[] | Map<string, any[]> = undefined;


    dataMode: string = '';


    transform() {
        if (Array.isArray(this.rowData)) {
            this.transformedData = this.transformData(this.rowData, this.xKey);
            this.dataMode = 'single';
        } else {
            this.transformedData = this.transformGroupedDate(this.rowData, this.xKey);
            this.dataMode = 'group';
        }
    }

    display() {
        if (this.dataMode === 'single') {
            this.displayData(this.transformedData as any[], this.yKeys);
        } else {
            this.displayGroupedData(this.transformedData as Map<string, any[]>, this.yKeys);
        }
    }

    //transform the input data to the schema of the chart
    abstract transformData(data: any[], xKey: String): any[];

    //transform the grouped input data to the schema of the chart
    abstract transformGroupedDate(data: Map<string, any[]>, xKey: string): Map<string, any[]>;

    //display the data
    abstract displayData(transformedData: any[], yKeys: string[]);

    //display the grouped data
    abstract displayGroupedData(transformedData: Map<string, any[]>, yKeys: string[]);

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