import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {BaseChartComponent} from '../chart/baseChart.component';


@Component({
    selector: 'sp-lineChart',
    templateUrl: './lineChart.component.html',
    styleUrls: ['./lineChart.component.css']
})
export class LineChartComponent extends BaseChartComponent implements OnChanges {

    constructor() {
        super();
    }

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

    ngOnChanges(changes: SimpleChanges) {
        //TODO: is needed because bindings are not working correct
        if (changes.endDateData !== undefined) {
            this.endDateData = changes.endDateData.currentValue;
        }
        if (changes.startDateData !== undefined) {
            this.startDateData = changes.startDateData.currentValue;
        }
        //TODO should be done in displaydata
        if (this.startDateData !== undefined && this.endDateData !== undefined) {
            this.graph.layout.xaxis['range'] = [this.startDateData.getTime(), this.endDateData.getTime()];
        }
    }



    displayData(transformedData: any[], yKeys: String[]) {
        if (this.yKeys.length > 0) {
            const tmp = [];
            this.yKeys.forEach(key => {
                transformedData.forEach(serie => {
                    if (serie.name === key)
                        tmp.push(serie)
                })
            });
            this.dataToDisplay = tmp;

        //    this.graph.layout.xaxis['range'] = [this.startDateData.getTime(), this.endDateData.getTime()];
        } else {
            this.dataToDisplay = undefined;
       //     this.graph.layout.xaxis['range'] = undefined;

        }
    }

    transformData(data: any[], xKey: string): any[] {
        const tmp = [];

        let dataKeys = [];
        for (let event of this.data) {
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
        for (let event of this.data) {
            let i = 0;
            for (let dataKey of dataKeys) {
                tmp[i].x.push(new Date(event[xKey]));
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


}