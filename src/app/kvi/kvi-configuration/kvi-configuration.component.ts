import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Operator } from '../shared/operator.model';

@Component({
    selector: 'kvi-configuration',
    templateUrl: './kvi-configuration.component.html',
    styleUrls: ['./kvi-configuration.component.css']
})
export class KviConfigurationComponent {

    @Input() operators: Operator[];
    @Output() configuredOperators: EventEmitter<Operator[]> = new EventEmitter<Operator[]>();

    constructor() {
    }

}