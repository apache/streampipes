import { Component, OnInit } from '@angular/core';

import { KviService } from './shared/kvi.service';
import { DataSetDescription } from '../connect/model/DataSetDescription';
import { KviConfiguration } from './shared/kvi-configuration.model';
import { PipelineTemplateDescription } from '../connect/model/PipelineTemplateDescription';
import { StaticProperty } from '../connect/model/StaticProperty';
import { PipelineTemplateInvocation } from '../connect/model/PipelineTemplateInvocation';
import { FormControl } from '@angular/forms';

@Component({
    templateUrl: './kvi.component.html',
    styleUrls: ['./kvi.component.css']
})
export class KviComponent implements OnInit {

    dataSets: DataSetDescription[] = [];
    selectedDataSet: DataSetDescription;
    isValidDataSet: boolean = false;

    operators: PipelineTemplateDescription[];
    selectedOperator: PipelineTemplateDescription;
    isValidOperator: boolean = false;

    configurations: StaticProperty[];
    isValidConfiguration: boolean = false;

    invocationGraph: PipelineTemplateInvocation;
    isValidName: boolean = false;
    nameControl: FormControl = new FormControl();

    constructor(private kviService: KviService) {
        this.kviService.getDataSets().subscribe(res => {
            this.dataSets = res;
        });
    }

    ngOnInit() {
        this.nameControl.valueChanges
            .subscribe(res => {
                this.invocationGraph.name = res;
                this.isValidName = !!res;
            });
    }

    selectDataSet(dataSet: DataSetDescription) {
        this.isValidDataSet = !!dataSet;
        if (this.isValidDataSet) {
            this.selectedDataSet = dataSet;
            this.kviService.getOperators(dataSet).subscribe(res => {
                this.operators = res;
            });
        }
    }

    selectOperator(operator: PipelineTemplateDescription) {
        this.isValidOperator = !!operator;
        if (this.isValidOperator) {
            this.selectedOperator = operator;
            this.kviService.getStaticProperties(this.selectedDataSet, operator).subscribe(res => {
                this.invocationGraph = res;
                this.configurations = res.list;
            });
        }
    }

    selectConfiguration(configuration: any) {
        this.isValidConfiguration = !!configuration;
        if (this.isValidConfiguration) {
            this.invocationGraph.list = configuration;
        }
    }

    calculateKvi() {
        this.kviService.createPipelineTemplateInvocation(this.invocationGraph).subscribe(res => console.log(res));
    }

}