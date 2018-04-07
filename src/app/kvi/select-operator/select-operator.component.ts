import { Component, EventEmitter, Input, Output, ViewChildren } from '@angular/core';

import { PipelineTemplateDescription } from '../../connect/model/PipelineTemplateDescription';

@Component({
    selector: 'select-operator',
    templateUrl: './select-operator.component.html',
    styleUrls: ['./select-operator.component.css']
})
export class SelectOperatorComponent {

    @Input() operators: PipelineTemplateDescription[] = [];
    @Output() selectOperator: EventEmitter<PipelineTemplateDescription> = new EventEmitter<PipelineTemplateDescription>();

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