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

import { describe, expect, it } from 'vitest';
import { getPipelineStatus } from './pipeline-status';

describe('getPipelineStatus', () => {
    it('reports a stopped pipeline regardless of its last health', () => {
        expect(
            getPipelineStatus({ running: false, healthStatus: 'FAILURE' }),
        ).toEqual({ tone: 'neutral', label: 'Stopped' });
    });

    it.each([
        ['OK', 'success', 'Running'],
        ['REQUIRES_ATTENTION', 'warning', 'Warning'],
        ['HANDLE_MEASUREMENT_UPDATE', 'warning', 'Warning'],
        ['FAILURE', 'error', 'Failed'],
    ] as const)(
        'maps the running health %s to %s',
        (healthStatus, tone, label) => {
            expect(getPipelineStatus({ running: true, healthStatus })).toEqual({
                tone,
                label,
            });
        },
    );
});
