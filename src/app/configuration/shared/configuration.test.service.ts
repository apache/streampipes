import { Injectable } from '@angular/core';
import { Observable ,  of } from 'rxjs';

import { StreampipesPeContainer } from './streampipes-pe-container.model';
import { StreampipesPeContainerConifgs } from './streampipes-pe-container-configs';

@Injectable()
export class ConfigurationMockService {

    constructor() {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getConsulServices(): Observable<StreampipesPeContainer[]> {

        let config: StreampipesPeContainerConifgs []
        config[0] = new StreampipesPeContainerConifgs
        config[0].description = "test int"
        config[0].key = 'testint'
        config[0].value = '80'
        config[0].valueType = 'xs:integer'
        config[0].isPassword = false

        
        return of([
            {
                mainKey: 'sp/test/1',
                meta: {
                    status: 'passing'
                },
                name: 'test1',
                configs: [
                    config[0],
                    {
                        key: 'teststring',
                        value: '765',
                        valueType: 'xs:string',
                        isPassword: false,
                        description: 'test string'
                    }
                ]
            },
            {
                mainKey: 'sp/test/2',
                meta: {
                    status: 'critical'
                },
                name: 'test2',
                configs: [
                    {
                        key: 'testbool',
                        value: 'false',
                        valueType: 'xs:boolean',
                        isPassword: false,
                        description: 'test bool'
                    }
                ]
            }
        ] as StreampipesPeContainer[]);
    }

    updateConsulService(consulService: StreampipesPeContainer): Observable<Object> {
        return of({});
    }

}