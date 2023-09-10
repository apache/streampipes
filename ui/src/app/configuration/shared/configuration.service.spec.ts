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

import { getTestBed, TestBed } from '@angular/core/testing';
import {
    HttpClientTestingModule,
    HttpTestingController,
} from '@angular/common/http/testing';
import { ConfigurationService } from './configuration.service';
import { SpServiceConfiguration } from '../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

describe('ConfigurationService', () => {
    let injector: TestBed;
    let service: ConfigurationService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ConfigurationService],
        });
        injector = getTestBed();
        service = injector.get(ConfigurationService);
        httpMock = injector.get(HttpTestingController);
    });
    afterEach(() => {
        httpMock.verify();
    });

    it('should create Get to /api/v2/extensions-services-configurations', () => {
        service.getExtensionsServiceConfigs().subscribe(res => res);
        const req = httpMock.expectOne(
            '/streampipes-backend/api/v2/extensions-services-configurations',
        );
        expect(req.request.method).toBe('GET');
    });

    it('should create Put to /api/v2/extensions-services-configurations/abc', () => {
        service
            .updateExtensionsServiceConfigs({} as SpServiceConfiguration)
            .subscribe(res => res);
        const req = httpMock.expectOne(
            '/streampipes-backend/api/v2/extensions-services-configurations/abc',
        );
        expect(req.request.method).toBe('PUT');
    });

    it('should get Server URL', () => {
        expect(service.getServerUrl()).toBe('/streampipes-backend');
    });

    it('should modify key', () => {
        expect(service.adjustConfigurationKey('SP_A_TEST')).toBe('A TEST');
    });
});
