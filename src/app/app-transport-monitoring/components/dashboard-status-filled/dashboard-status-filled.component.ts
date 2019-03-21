import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-status-filled',
    templateUrl: './dashboard-status-filled.component.html',
    styleUrls: ['./dashboard-status-filled.component.css']
})
export class DashboardStatusFilledComponent {

    @Input() statusValue: string;
    @Input() label: string;
    @Input() color: string;

    constructor() {

    }

    ngOnInit() {

    }

    getBackground() {
        return {'background': this.color};
    }


}