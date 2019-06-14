import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material';

@Component({
    selector: 'sp-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
})
export class TableComponent {

    @Input() set data(value: any[]) {
        if (value != undefined) {
            this.dataSource.data = value;
            this.displayedColumns = Object.keys(value[0]);
        }
    }
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
    }


    clickPreviousPage(){
        this.previousPage.emit()
    }

    clickNextPage() {
        this.nextPage.emit()
    }

    selectItemsPerPage(num) {
        this.itemsPerPage = num;
        this.itemPerPageChange.emit(this.itemsPerPage);
    }

    clickFirstPage(){
        this.firstPage.emit()
    }

    clickLastPage() {
        this.lastPage.emit()
    }

}