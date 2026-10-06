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

import { describe, expect, it, vi } from 'vitest';
import { PipelineEditorService } from './pipeline-editor.service';

describe('Pipeline palette drop coordinates', () => {
    const service = new PipelineEditorService();

    it.each([0.5, 0.75, 1, 1.5, 2])(
        'accounts for pan and zoom at scale %s',
        zoom => {
            const canvas = document.createElement('div');
            Object.defineProperty(canvas, 'offsetWidth', { value: 4000 });
            vi.spyOn(canvas, 'getBoundingClientRect').mockReturnValue(
                new DOMRect(-120, 75, 4000 * zoom, 3000 * zoom),
            );
            const preview = new DOMRect(
                -120 + 300 * zoom,
                75 + 150 * zoom,
                70,
                70,
            );
            expect(service.getCoordinates(preview, canvas)).toEqual({
                x: 300,
                y: 150,
            });
        },
    );

    it('accepts a preview exactly inside the visible canvas', () => {
        expect(
            service.fitsInside(
                new DOMRect(100, 200, 70, 70),
                new DOMRect(100, 200, 70, 70),
            ),
        ).toBe(true);
    });

    it.each([
        [99, 200],
        [100, 199],
        [431, 200],
        [100, 431],
    ])('rejects a preview extending outside the canvas at %s, %s', (x, y) => {
        expect(
            service.fitsInside(
                new DOMRect(x, y, 70, 70),
                new DOMRect(100, 200, 400, 300),
            ),
        ).toBe(false);
    });
});
