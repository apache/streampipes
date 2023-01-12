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

import { XsService } from './xs.service';

describe('XSService', () => {
    const service: XsService = new XsService();

    it('should get string XS String', () => {
        expect(service.XS_STRING).toBe('xs:string');
    });

    it('should check for numbers', () => {
        expect(service.isNumber(service.XS_DOUBLE)).toBeTrue();
        expect(service.isNumber(service.XS_INTEGER)).toBeTrue();
        expect(service.isNumber(service.XS_NUMBER)).toBeTrue();
        expect(service.isNumber(service.XS_STRING)).toBeFalse();
    });
});
