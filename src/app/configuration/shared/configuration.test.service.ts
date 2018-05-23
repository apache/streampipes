import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

import { ConsulService } from './consul-service.model';

@Injectable()
export class ConfigurationMockService {

    constructor() {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getConsulServices(): Observable<ConsulService[]> {
        return of([
            {
                mainKey: 'sp/test/1',
                meta: {
                    status: 'passing'
                },
                name: 'test1',
                configs: [
                    {
                        key: 'testint',
                        value: '80',
                        valueType: 'xs:integer',
                        isPassword: false,
                        description: 'test int'
                    },
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
        ] as ConsulService[]);
    }

    updateConsulService(consulService: ConsulService): Observable<Object> {
        return of({});
    }

}