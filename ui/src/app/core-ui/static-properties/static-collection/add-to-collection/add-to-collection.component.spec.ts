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

describe('AddToCollectionComponent', () => {
    const component: AddToCollectionComponent = new AddToCollectionComponent(
        undefined,
    );

    it('parse csv string', () => {
        const csvString = ['a,b', 'a1,b1', 'a2,b2'].join('\n');

        const result = component.parseCsv(csvString);

        result.subscribe(res => {
            expect(res.length).toBe(2);
            expect(res[0]).toEqual({ a: 'a1', b: 'b1' });
            expect(res[1]).toEqual({ a: 'a2', b: 'b2' });
        });
    });
});
