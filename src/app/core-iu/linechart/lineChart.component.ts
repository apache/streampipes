import {Component, EventEmitter, Input, Output} from '@angular/core';


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


    updatemenus=[
        {
            buttons: [
                {
                    args: ['mode', 'lines'],
                    label:'Line',
                    method:'restyle'
                },
                {
                    args: ['mode', 'markers'],
                    label: 'Dots',
                    method: 'restyle'
                },

                {
                    args: ['mode', 'lines+markers'],
                    label:'Dots + Lines',
                    method:'restyle'
                }
            ],
            direction: 'left',
            pad: {'r': 10, 't': 10},
            showactive: true,
            type: 'buttons',
            x: 0.0,
            xanchor: 'left',
            y: 1.3,
            yanchor: 'top',
            font: {color: '#000'},
            bgcolor: '#fafafa',
            bordercolor: '#000'
        }
    ];

    graph = {
        layout: {
            plot_bgcolor:"#fafafa",
            paper_bgcolor:"#fafafa",
            xaxis: {
                type: 'date',
            },
            yaxis: {
                fixedrange: true
            },
            updatemenus: this.updatemenus,
        }
    };


    processData() {
        const tmp = [];

        let dataKeys = [];
        for (let event of this._data) {
            for (let key in event) {
                if (typeof event[key] == 'number') {
                    if (!dataKeys.includes(key)) {
                        dataKeys.push(key)
                    }
                }
            }
        }

        dataKeys.forEach(key => {
            tmp.push({
                type: 'scatter', mode: 'lines', name: key, connectgaps: false, x: [], y: []})
        });
        for (let event of this._data) {
            let i = 0;
            for (let dataKey of dataKeys) {
                tmp[i].x.push(new Date(event[this._xAxesKey]));
                if ((event[dataKey]) !== undefined) {
                    tmp[i].y.push(event[dataKey])
                } else {
                    tmp[i].y.push(null)
                }
                i++;
            }
        }

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
            });
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