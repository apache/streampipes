import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { map, startWith } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { DataSetDescription } from '../../connect/model/DataSetDescription';

@Component({
    selector: 'select-dataset',
    templateUrl: './select-dataset.component.html',
    styleUrls: ['./select-dataset.component.css']
})
export class SelectDatasetComponent implements OnChanges {

    @Input() dataSets: DataSetDescription[];
    @Output() selectDataSet: EventEmitter<DataSetDescription> = new EventEmitter<DataSetDescription>();

    filteredDataSets: Observable<DataSetDescription[]>;
    myControl: FormControl = new FormControl();

    constructor() {
    }

    ngOnChanges() {
        this.filteredDataSets = this.myControl.valueChanges
            .pipe(
                startWith<string | DataSetDescription>(''),
                map(value => typeof value === 'string' ? value : value.label),
                map(label => label ? this.filter(label) : this.dataSets.slice())
            );
        this.myControl.valueChanges
            .subscribe(res => {
                if(res.id !== undefined) {
                    this.selectDataSet.emit(res);
                } else {
                    this.selectDataSet.emit(undefined);
                }
            });
    }

    filter(val: string): DataSetDescription[] {
        return this.dataSets.filter(dataSet =>
            dataSet.label.toLowerCase().indexOf(val.toLowerCase()) === 0);
    }

    getDataSetLabel(dataSet?: DataSetDescription): string | undefined {
        return dataSet ? dataSet.label : undefined;
    }

}