import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'transport-summary',
    templateUrl: './transport-summary.component.html',
    styleUrls: ['./transport-summary.component.css']
})
export class TransportSummaryComponent {

    @Input() statusValue: string;
    @Input() label: string;
    @Input() color: string;

    constructor() {

    }

    ngOnInit() {

    }


}