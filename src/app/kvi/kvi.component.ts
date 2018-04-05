import { Component } from '@angular/core';

import { KviService } from './shared/kvi.service';
import { DataSetDescription } from '../connect/model/DataSetDescription';
import { Operator } from './shared/operator.model';
import { KviConfiguration } from './shared/kvi-configuration.model';

@Component({
    templateUrl: './kvi.component.html',
    styleUrls: ['./kvi.component.css']
})
export class KviComponent {

    dataSets: DataSetDescription[];
    selectedDataSet: DataSetDescription;
    isValidDataSet: boolean = false;

    operators: Operator[];
    selectedOperator: Operator;
    isValidOperator: boolean = false;

    configuration: KviConfiguration[];
    isValidConfiguration: boolean = true;

    constructor(private kviService: KviService) {
        this.dataSets = this.kviService.getDataSets();
    }

    selectDataSet(dataSet: DataSetDescription) {
        this.isValidDataSet = !!dataSet;
        if (this.isValidDataSet) {
            this.selectedDataSet = dataSet;
            this.operators = this.kviService.getOperators(dataSet);
        }
    }

    selectOperator(operator: Operator) {
        this.isValidOperator = !!operator;
        if (this.isValidOperator) {
            this.selectedOperator = operator;
        }
    }

    calculateKvi() {
        console.log('Los gehts')
    }

}