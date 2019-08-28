import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseChartComponent} from '../chart/baseChart.component';


@Component({
    selector: 'sp-lineChart',
    templateUrl: './lineChart.component.html',
    styleUrls: ['./lineChart.component.css']
})
export class LineChartComponent extends BaseChartComponent {

    constructor() {
        super();
    }

    @Output() itemPerPageChange = new EventEmitter<number>();


    _xAxesKey: string = undefined;
    _yAxesKeys: string[] = undefined;
    _data: any[] = undefined;

    dataToDisplay: any[] = undefined;
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

    displayData(transformedData: any[], yKeys: String[]) {
        if (this._yAxesKeys.length > 0) {
            const tmp = [];
            this._yAxesKeys.forEach(key => {
                this.transformedData.forEach(serie => {
                    if (serie.name === key)
                        tmp.push(serie)
                })
            });
            this.dataToDisplay = tmp;
        } else {
            this.dataToDisplay = undefined;
        }
    }

    transformData(data: any[], xKey: String): any[] {
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

        return tmp;
    }

    stopDisplayData() {
    }

    selectItemsPerPage(num) {
        this.itemsPerPage = num;
        this.itemPerPageChange.emit(this.itemsPerPage);
    }




}