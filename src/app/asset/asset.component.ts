import {Component, OnInit, ViewChild} from '@angular/core';
import {AssetRestService} from './service/asset-rest.service';
import {InfoResult} from './model/InfoResult';
import {Observable} from 'rxjs/Observable';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {MatSnackBar, MatTableDataSource} from '@angular/material';
import {HttpEventType} from '@angular/common/http';

@Component({
    selector: 'app-asset',
    templateUrl: './asset.component.html',
    styleUrls: ['./asset.component.css']
})
export class AssetComponent implements OnInit {

    myControl = new FormControl();
    infoResult: InfoResult[];
    filteredIndexInfos: Observable<InfoResult[]>;

    displayedColumns: string[] = [];
    dataSource = new MatTableDataSource();

    page: number = 0;
    pageSum: number = 0;
    itemsPerPage = 10;
    selectedIndex: string = '';

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;

    constructor(private restService: AssetRestService, private snackBar: MatSnackBar) {

    }

    ngOnInit(): void {
        this.restService.getAllInfos().subscribe(res => {
                this.infoResult = res;
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
        this.restService.getDataPage(this.selectedIndex, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    console.log(res);
                    this.page = res.page;
                    this.pageSum = res.pageSum;
                    this.dataSource.data = res.events as [];
                    this.displayedColumns = Object.keys(res.events[0]);
                } else {
                    this.openSnackBar('No data found on page ' + page);
                }
            });
    }

    loadData() {
        this.restService.getDataPageWithoutPage(this.selectedIndex,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                    console.log(res);
                    this.page = res.page;
                    this.pageSum = res.pageSum;
                    this.dataSource.data = res.events as [];
                    this.displayedColumns = Object.keys(res.events[0]);
                }
            }
        );
    }

    selectIndex(index: string) {
        this.selectedIndex = index;
        this.loadData()
    }

    selectItemsPerPage(num) {
        this.itemsPerPage = num;
        this.loadData()
    }

    downloadData() {
        this.isDownloading = true;
        this.restService.getFile(this.selectedIndex, this.downloadFormat).subscribe(event => {
            // progress
            if (event.type === HttpEventType.DownloadProgress) {
                console.log(event.loaded);
            }

            // finished
            if (event.type === HttpEventType.Response) {
                console.log(event);

                this.isDownloading = false;

                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/' + this.downloadFormat + ';charset=utf-8,' + encodeURIComponent(String(event.body)));

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);

            }
        })
    }

    private _filter(value: string): InfoResult[] {
        const filterValue = value.toLowerCase();

        return this.infoResult.filter(option => option.index.toLowerCase().includes(filterValue));
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }
}