import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {BaseChartComponent} from '../chart/baseChart.component';

@Component({
    selector: 'sp-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
})
export class TableComponent extends BaseChartComponent {

    displayedColumns: String[] = [];
    dataSource = new MatTableDataSource();

    constructor() {
        super();
    }

    transformData(data: any[], xKey: String): any[] {
        return data;
    }

    displayData(transformedData: any[], yKeys: String[]) {
        this.displayedColumns = Object.assign([], yKeys);
        this.displayedColumns.unshift(this.xKey);

        this.dataSource.data = transformedData;
    }

    stopDisplayData() {
        this.dataSource.data = []
    }

}