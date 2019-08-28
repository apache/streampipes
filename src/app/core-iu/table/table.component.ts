import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {BaseChartComponent} from '../chart/baseChart.component';

@Component({
    selector: 'sp-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
})
export class TableComponent extends BaseChartComponent {

    @Input() enableItemsPerPage: boolean = false;


    @Output() itemPerPageChange = new EventEmitter<number>();


    displayedColumns: string[] = [];
    dataSource = new MatTableDataSource();

    itemsPerPage = 10;

    constructor() {
        super();
    }

    selectItemsPerPage(num) {
        this.itemsPerPage = num;
        this.itemPerPageChange.emit(this.itemsPerPage);
    }

    transformData(data: any[], xKey: String): any[] {
        console.log("TRANSFORM DATA");
        console.log(data);
        console.log(xKey);
        return data;
    }

    displayData(transformedData: any[], yKeys: String[]) {
        console.log("DISPLAY");
        console.log(transformedData);
        console.log(yKeys);
    }

    stopDisplayData() {
    }






}