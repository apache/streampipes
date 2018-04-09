import { AfterViewInit, Component, Input, OnChanges, ViewChild } from '@angular/core';
import { MatSort, MatTableDataSource } from '@angular/material';

@Component({
    selector: 'kvi-table',
    templateUrl: './kvi-table.component.html',
    styleUrls: ['./kvi-table.component.css']
})
export class KviTableComponent implements OnChanges, AfterViewInit {

    @Input() kviData = [];
    kviDataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;

    displayedColumns = ['rowindex', 'name', 'kvi'];

    constructor() {

    }

    ngOnChanges() {
        this.kviDataSource = new MatTableDataSource(this.kviData);
        this.kviDataSource.sort = this.sort;
    }

    ngAfterViewInit() {
        this.kviDataSource.sort = this.sort;
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim();
        filterValue = filterValue.toLowerCase();
        this.kviDataSource.filter = filterValue;
    }

}