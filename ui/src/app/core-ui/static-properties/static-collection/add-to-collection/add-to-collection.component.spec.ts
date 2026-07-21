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
import { AddToCollectionComponent } from './add-to-collection.component';
import { firstValueFrom } from 'rxjs';
import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { StaticPropertyUtilService } from '../../static-property-util.service';
import { TranslateService } from '@ngx-translate/core';

describe('AddToCollectionComponent', () => {
    let component: AddToCollectionComponent;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: StaticPropertyUtilService, useValue: {} },
                {
                    provide: TranslateService,
                    useValue: { instant: () => '' },
                },
            ],
        });
        component = TestBed.runInInjectionContext(
            () => new AddToCollectionComponent(),
        );
    });

    it('parse csv string', async () => {
        const csvString = ['a,b', 'a1,b1', 'a2,b2'].join('\n');

        const result = await firstValueFrom(component.parseCsv(csvString));

        expect(result.length).toBe(2);
        expect(result[0]).toEqual({ a: 'a1', b: 'b1' });
        expect(result[1]).toEqual({ a: 'a2', b: 'b2' });
    });
});
