import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-status-filled',
    templateUrl: './dashboard-status-filled.component.html',
    styleUrls: ['./dashboard-status-filled.component.css']
})
export class DashboardStatusFilledComponent {

    @Input() color: string = "rgb(156, 156, 156)";
    _label: string;
    _statusValue: string;

    chartData: any;

    constructor() {

    }

    ngOnInit() {

    }

    @Input()
    set statusValue(statusValue: string) {
        this._statusValue = statusValue;
        this.updateChartData();
    }

    @Input()
    set label(label: string) {
        this._label = label;
        this.updateChartData();
    }

    updateChartData() {
        this.chartData = [];
        this.chartData = [{"name": this._label, "value": this._statusValue}];
    }

    getBackground() {
        return {'background': this.color};
    }


}