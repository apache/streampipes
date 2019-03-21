import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-status',
    templateUrl: './dashboard-status.component.html',
    styleUrls: ['./dashboard-status.component.css']
})
export class DashboardStatusComponent {

    @Input() statusValue: string;

    constructor() {

    }

    ngOnInit() {

    }


}