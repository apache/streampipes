import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from '@angular/material';

@Component({
    selector: 'sp-all-adapters',
    templateUrl: './all.component.html',
    styleUrls: ['./all.component.css']
})
export class AllAdaptersComponent implements OnInit {
    private ELEMENT_DATA: Element[] = [
        {position: 1, name: 'Hydrogen', symbol: 'H'},
        {position: 2, name: 'Helium',  symbol: 'He'},
        {position: 3, name: 'Lithium',  symbol: 'Li'},
    ];

    displayedColumns = ['position', 'name','symbol'];
    dataSource = new MatTableDataSource(this.ELEMENT_DATA);

    public AllAdaptersComponent() {}

    ngOnInit() {}

}

interface Element {
    name: string;
    position: number;
    symbol: string;
}
