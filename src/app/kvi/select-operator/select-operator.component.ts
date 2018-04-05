import { Component, EventEmitter, Input, Output, ViewChildren } from '@angular/core';

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

    selectedOperatorChange(operatorsList) {
        if(operatorsList.selectedOptions.selected.length > 0) {
            this.selectOperator.emit(operatorsList.selectedOptions.selected[0].value);
        } else {
            this.selectOperator.emit(undefined);
        }
    }

}