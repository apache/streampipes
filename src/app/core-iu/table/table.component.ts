import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {BaseChartComponent} from '../chart/baseChart.component';

@Component({
    selector: 'sp-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
})
export class TableComponent extends BaseChartComponent {

    @Input() currentPage: number = undefined;
    @Input() maxPage: number = undefined;
    @Input() enablePaging: boolean = false;
    @Input() enableItemsPerPage: boolean = false;

    @Output() previousPage = new EventEmitter<boolean>();
    @Output() nextPage = new EventEmitter<boolean>();
    @Output() itemPerPageChange = new EventEmitter<number>();
    @Output() firstPage = new EventEmitter<boolean>();
    @Output() lastPage = new EventEmitter<boolean>();

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
        return data;
    }

    displayData(transformedData: any[], yKeys: String[]) {
        //   this.dataSource.data = value;
       // this.displayedColumns = Object.keys(value[0]);
        return [];
    }

    stopDisplayData() {
    }






}