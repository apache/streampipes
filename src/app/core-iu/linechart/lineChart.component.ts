import {Component, EventEmitter, Input, Output} from '@angular/core';
import {keyframes} from '@angular/animations';

@Component({
    selector: 'sp-lineChart',
    templateUrl: './lineChart.component.html',
    styleUrls: ['./lineChart.component.css']
})
export class LineChartComponent {

    constructor() {
    }

    @Input() set data(value: any[]) {
        if (value != undefined) {
            this._data = value;
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKeys !== undefined) {
                this.processData();
                this.selectDataToDisplay();
            }
        } else {
            this.displayData = undefined;
            this._data = undefined;
        }
    }
    @Input() set xAxesKey(value: string) {
        if (value != undefined) {
            this._xAxesKey = value;
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKeys !== undefined) {
                this.processData();
                this.selectDataToDisplay();
            }
        }
    }
    @Input() set yAxesKeys(value: string[]) {
        if (value !== undefined) {
            this._yAxesKeys = value;
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKeys !== undefined) {
                if (this.processedData === undefined)
                    this.processData();
                this.selectDataToDisplay();
            }
        }
    }
    @Input() currentPage: number = undefined;
    @Input() maxPage: number = undefined;
    @Input() enablePaging: boolean = false;
    @Input() enableItemsPerPage: boolean = false;
    @Input() isLoadingData: boolean = false;

    @Output() previousPage = new EventEmitter<boolean>();
    @Output() nextPage = new EventEmitter<boolean>();
    @Output() firstPage = new EventEmitter<boolean>();
    @Output() lastPage = new EventEmitter<boolean>();
    @Output() itemPerPageChange = new EventEmitter<number>();


    _xAxesKey: string = undefined;
    _yAxesKeys: string[] = undefined;
    _data: any[] = undefined;

    processedData: any[] = undefined;

    displayData: any[] = undefined;
    itemsPerPage = 50;

    processData() {
        const tmp = [];
        Object.keys(this._data[0]).forEach(key => {
            const serie = [];
            this._data.forEach(date => {
                serie.push({name: new Date(date[this._xAxesKey]), value: date[key]})
            });
            tmp.push({name: key, series: serie});
        })
        this.processedData = tmp;


        this.displayData = tmp;
    }

    selectDataToDisplay() {
        if (this._yAxesKeys.length > 0) {
            const tmp = [];
            this._yAxesKeys.forEach(key => {
                this.processedData.forEach(serie => {
                    if (serie.name === key)
                        tmp.push(serie)
                })
            })
            this.displayData = tmp;
        } else {
            this.displayData = undefined;
        }


    }



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

    selectItemsPerPage(num) {
        this.itemsPerPage = num;
        this.itemPerPageChange.emit(this.itemsPerPage);
    }
}