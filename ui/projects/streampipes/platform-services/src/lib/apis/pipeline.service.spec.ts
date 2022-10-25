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

import { TestBed } from '@angular/core/testing';
import { PipelineService } from './pipeline.service';
import {
    HttpClientTestingModule,
    HttpTestingController,
} from '@angular/common/http/testing';
import { Pipeline } from '../model/gen/streampipes-model';

describe('PipelineService', () => {
    const mockPath = 'mock';
    let pipelineService: PipelineService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        httpMock = TestBed.inject(HttpTestingController);
        pipelineService = TestBed.inject(PipelineService);
        spyOnProperty(pipelineService, 'apiBasePath', 'get').and.returnValue(
            mockPath,
        );
    });

    it('Get pipelines containing element', () => {
        const elementId = 'elementId';
        const expectedPipeline = new Pipeline();
        expectedPipeline.name = 'Test Pipeline';
        const expectedPipelines = [expectedPipeline];

        pipelineService
            .getPipelinesContainingElementId(elementId)
            .subscribe(pipelines => {
                expect(pipelines.length).toBe(1);
                expect(pipelines[0]).toEqual(
                    jasmine.objectContaining(expectedPipeline),
                );
            });

        const req = httpMock.expectOne({
            method: 'GET',
            url: `${mockPath}/pipelines/contains/${elementId}`,
        });

        req.flush(expectedPipelines);
    });
});
