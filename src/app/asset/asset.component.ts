import {Component, OnInit, ViewChild} from '@angular/core';
import {AssetRestService} from './service/asset-rest.service';
import {InfoResult} from './model/InfoResult';
import {IndexInfo} from './model/IndexInfo';
import {Observable} from 'rxjs/Observable';
import {FormControl, FormGroup} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {MatPaginator, MatSnackBar, MatSort, MatTableDataSource} from '@angular/material';

@Component({
    selector: 'app-asset',
    templateUrl: './asset.component.html',
    styleUrls: ['./asset.component.css']
})
export class AssetComponent implements OnInit {

    myControl = new FormControl();
    indexInfos: IndexInfo[];
    filteredIndexInfos: Observable<IndexInfo[]>;

    displayedColumns: string[] = [];
    dataSource = new MatTableDataSource();

    page: number;
    itemsPerPage = 10;
    selectedIndex: string;

    constructor(private restService: AssetRestService, private snackBar: MatSnackBar) {

    }

    ngOnInit(): void {
        this.restService.getAllIndices().subscribe(res => {
                this.indexInfos = res;
                this.filteredIndexInfos = this.myControl.valueChanges
                    .pipe(
                        startWith(''),
                        map(value => this._filter(value))
                    );
            }
        );
    }

    previousPage(){
        if(this.page >= 0)
            this.paging(this.page - 1);
    }

    nextPage() {
        this.paging(this.page + 1);
    }

    paging(page) {
        this.restService.getDataPage(this, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    console.log(res);
                    this.page = res.page;
                    this.dataSource.data = res.events as [];
                    this.displayedColumns = Object.keys(res.events[0]);
                } else {
                    this.openSnackBar('No data found on page ' + page);
                }
            });
    }




    selectIndex(index: string) {
        /*this.restService.getDataPage(index, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    console.log(res)
                    this.page = res.page;
                    this.dataSource.data = res.events as [];
                    this.displayedColumns = Object.keys(res.events[0]);
                }
            }
        );*/
        this.selectedIndex = index;
        this.restService.getDataPageWithoutPage(index,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                    console.log(res);
                    this.page = res.page;
                    this.dataSource.data = res.events as [];
                    this.displayedColumns = Object.keys(res.events[0]);
                }
            }
        );
    }

    private _filter(value: string): IndexInfo[] {
        const filterValue = value.toLowerCase();

        return this.indexInfos.filter(option => option.indexName.toLowerCase().includes(filterValue));
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }
}