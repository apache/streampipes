import {Component, OnInit, ViewChild} from '@angular/core';
import {DatalakeRestService} from '../core-services/datalake/datalake-rest.service';
import {InfoResult} from '../core-model/datalake/InfoResult';
import {Observable} from 'rxjs/Observable';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {MatSnackBar} from '@angular/material';

@Component({
    selector: 'sp-data-explorer',
    templateUrl: './data-explorer.component.html',
    styleUrls: ['./data-explorer.css']
})
export class DataExplorerComponent implements OnInit {

    myControl = new FormControl();
    infoResult: InfoResult[];
    filteredIndexInfos: Observable<InfoResult[]>;

    page: number = 0;
    selectedIndex: string = '';

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

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

    selectIndex(index: string) {
        this.selectedIndex = index;
    }

    _filter(value: string): InfoResult[] {
        const filterValue = value.toLowerCase();

        return this.infoResult.filter(option => option.measureName.toLowerCase().includes(filterValue));
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }
}