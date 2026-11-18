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

import { HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';
import { describe, expect, it } from 'vitest';
import { LivePreviewService } from './live-preview.service';

describe('LivePreviewService', () => {
    const service = new LivePreviewService();
    const progress = (partialText: string): HttpDownloadProgressEvent => ({
        type: HttpEventType.DownloadProgress,
        loaded: partialText.length,
        partialText,
    });

    it('waits for a complete first record', () => {
        expect(service.convert(progress('{"adapter":'))).toBeUndefined();
        expect(service.convert(progress('{"adapter":{"value":1}}\n'))).toEqual({
            adapter: { value: 1 },
        });
    });

    it('reads the latest complete record before an incomplete tail', () => {
        expect(
            service.convert(progress('{"value":1}\n{"value":2}\n{"value":')),
        ).toEqual({ value: 2 });
    });

    it('handles multiple complete records in one progress event', () => {
        expect(service.convert(progress('{"value":1}\n{"value":2}\n'))).toEqual(
            {
                value: 2,
            },
        );
    });

    it('ignores empty input and blank records', () => {
        expect(service.convert(progress(''))).toBeUndefined();
        expect(service.convert(progress('\n'))).toBeUndefined();
        expect(service.convert(progress(' \n'))).toBeUndefined();
    });

    it('does not mix simultaneous previews', () => {
        expect(service.convert(progress('{"a":1}\n'))).toEqual({ a: 1 });
        expect(service.convert(progress('{"b":'))).toBeUndefined();
        expect(service.convert(progress('{"b":2}\n'))).toEqual({ b: 2 });
    });
});
