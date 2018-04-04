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
    selectedDataSet: DataSetDescription = null;
    isValidDataSet: boolean = true;

    operators: Operator[];
    selectedOperators: Operator[];
    isValidOperator: boolean = true;

    configuration: KviConfiguration[];
    isValidConfiguration: boolean = true;

    constructor(private kviService: KviService) {
        this.dataSets = this.kviService.getDataSets();
        this.operators = this.kviService.getOperators(this.selectedDataSet);
    }

}