/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { PipelineService } from './pipeline.service';
import { HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PlatformServicesCommons } from './commons.service';
import { PlatformServicesModule } from '../platform-services.module';

describe('PipelineService', () => {
    let pipelineService: PipelineService;
    let httpController: HttpTestingController;
    let platformServicesCommons: PlatformServicesCommons;
    const mockedPlatformServicesCommons = jasmine.createSpyObj('PlatformServicesCommons', ['apiBasePath']);
    mockedPlatformServicesCommons.apiBasePath.and.returnValue('base/')

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpTestingController],
            // providers: [{
                // platformServicesCommons: mockedPlatformServicesCommons
            // }]
        });
        pipelineService = TestBed.inject(PipelineService);
        httpController = TestBed.inject(HttpTestingController);
        platformServicesCommons = TestBed.inject(PlatformServicesCommons);


    });

    it('Get pipelines containing element', () => {
        const elementId = 'elementId';

        pipelineService.getPipelinesContainingElementId('id').subscribe(pipelines => {
            // TODO think about how to check this
            expect(pipelines).toBe([]);
        });

        const req = httpController.expectOne({
            method: 'GET',
            url: `localhost:80/api/v2/pipelines/contains/${elementId}`
        });
    });
});
