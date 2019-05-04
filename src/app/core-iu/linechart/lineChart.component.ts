import {Component, EventEmitter, Input, Output} from '@angular/core';
import {time} from '@ngtools/webpack/src/benchmark';

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
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKey !== undefined) {
                this.processData()
            }
        }
    }
    @Input() set xAxesKey(value: string) {
        if (value != undefined) {
            this._xAxesKey = value;
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKey !== undefined) {
                this.processData()
            }
        }
    }
    @Input() set yAxesKey(value: string) {
        if (value !== undefined) {
            this._yAxesKey = value;
            if (this._data !== undefined && this._xAxesKey !== undefined && this._yAxesKey !== undefined) {
                this.processData()
            }
        }
    }
    @Input() currentPage: number = undefined;
    @Input() maxPage: number = undefined;
    @Input() enablePaging: boolean = false;
    @Input() enableItemsPerPage: boolean = false;

    @Output() previousPage = new EventEmitter<boolean>();
    @Output() nextPage = new EventEmitter<boolean>();
    @Output() itemPerPageChange = new EventEmitter<number>();

    _xAxesKey: string = undefined;
    _yAxesKey: string = undefined;
    _data: any[] = undefined;

    displayData: any[] = undefined;
    itemsPerPage = 50;

    processData() {
        const serie = [];
        this._data.forEach(date => {
            serie.push({name: new Date(date[this._xAxesKey]), value: date[this._yAxesKey]})
        });
        this.displayData = [{name: this._yAxesKey, series: serie}]
        console.log(this.displayData)
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
}