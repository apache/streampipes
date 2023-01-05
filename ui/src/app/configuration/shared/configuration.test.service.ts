/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { StreampipesPeContainer } from './streampipes-pe-container.model';
import { StreampipesPeContainerConifgs } from './streampipes-pe-container-configs';

@Injectable()
export class ConfigurationMockService {
    constructor() {}

    getServerUrl() {
        return '/streampipes-backend';
    }

    getConsulServices(): Observable<StreampipesPeContainer[]> {
        const config: StreampipesPeContainerConifgs[] = [];
        config[0] = new StreampipesPeContainerConifgs();
        config[0].description = 'test int';
        config[0].key = 'testint';
        config[0].value = '80';
        config[0].valueType = 'xs:integer';
        config[0].isPassword = false;

        return of([
            {
                mainKey: 'sp/test/1',
                meta: {
                    status: 'passing',
                },
                name: 'test1',
                configs: [
                    config[0],
                    {
                        key: 'teststring',
                        value: '765',
                        valueType: 'xs:string',
                        isPassword: false,
                        description: 'test string',
                    },
                ],
            },
            {
                mainKey: 'sp/test/2',
                meta: {
                    status: 'critical',
                },
                name: 'test2',
                configs: [
                    {
                        key: 'testbool',
                        value: 'false',
                        valueType: 'xs:boolean',
                        isPassword: false,
                        description: 'test bool',
                    },
                ],
            },
        ] as StreampipesPeContainer[]);
    }

    updateConsulService(
        consulService: StreampipesPeContainer,
    ): Observable<Object> {
        return of({});
    }
}
