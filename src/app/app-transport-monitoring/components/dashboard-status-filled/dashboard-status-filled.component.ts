import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-status-filled',
    templateUrl: './dashboard-status-filled.component.html',
    styleUrls: ['./dashboard-status-filled.component.css']
})
export class DashboardStatusFilledComponent {

    @Input() statusValue: string;
    @Input() label: string;
    @Input() color: string = "rgb(156, 156, 156)";

    chartData: any;

    constructor() {

    }

    ngOnInit() {
        this.chartData = [{"name": this.label, "value": this.statusValue}]
    }

    getBackground() {
        return {'background': this.color};
    }


}