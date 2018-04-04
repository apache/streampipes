import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Operator } from '../shared/operator.model';

@Component({
    selector: 'select-operator',
    templateUrl: './select-operator.component.html',
    styleUrls: ['./select-operator.component.css']
})
export class SelectOperatorComponent {

    @Input() operators: Operator[];
    @Output() selectOperator: EventEmitter<Operator> = new EventEmitter<Operator>();

    constructor() {
    }

}