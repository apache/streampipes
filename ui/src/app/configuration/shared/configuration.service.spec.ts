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

import { TestBed } from '@angular/core/testing';
import {
    HttpTestingController,
    provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ConfigurationService } from './configuration.service';
import { SpServiceConfiguration } from '@streampipes/platform-services';
import {
    provideHttpClient,
    withInterceptorsFromDi,
} from '@angular/common/http';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

describe('ConfigurationService', () => {
    let service: ConfigurationService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [],
            providers: [
                ConfigurationService,
                provideHttpClient(withInterceptorsFromDi()),
                provideHttpClientTesting(),
            ],
        });
        service = TestBed.inject(ConfigurationService);
        httpMock = TestBed.inject(HttpTestingController);
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
            .updateExtensionsServiceConfigs(
                Object.assign(new SpServiceConfiguration(), {
                    serviceGroup: 'abc',
                }),
            )
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
