import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { DataSetDescription } from '../../connect/model/DataSetDescription';
import { Operator } from './operator.model';

@Injectable()
export class KviService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getDataSets(): DataSetDescription[] {//Observable<DataSetDescription[]> {
        // /datasets
        return [{
            id: 'a',
            label: 'Kindergarten',
            description: 'Test',
            uri: 'xxx',
            eventSchema: null
        }, {
            id: 'b',
            label: 'Kirche',
            description: 'Test',
            uri: 'xxx',
            eventSchema: null
        }];
    }

    getOperators(dataSet?: DataSetDescription): Operator[] {//Observable<Operator[]>
        // /pipelineTemplates?datasetId=datasetId
        return [
            {
                name: 'Test 1',
                config: null
            },
            {
                name: 'Test 2',
                config: null
            },
            {
                name: 'Test 3',
                config: null
            },
            {
                name: 'Test 4',
                config: null
            }
        ]
    }

}