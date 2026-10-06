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

import '@angular/compiler';
import { describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { DeleteDatasetDialogComponent } from './delete-dataset-dialog.component';

describe('Dataset bulk operations', () => {
    function setup() {
        const component = Object.create(DeleteDatasetDialogComponent.prototype);
        const drop = vi.fn((name: string) =>
            name === 'failed' ? throwError(() => new Error('Failed')) : of({}),
        );
        const remove = vi.fn(() => of({}));
        const close = vi.fn();
        Object.assign(component, {
            datasetNames: ['success', 'failed'],
            isInProgress: false,
            hasChanges: false,
            datalakeRestService: {
                dropSingleMeasurementSeries: drop,
                removeData: remove,
            },
            translateService: { instant: (key: string) => key },
            dialogRef: { close },
        });
        return { component, drop, remove, close };
    }

    it('keeps failures open and retries only failed datasets', () => {
        const { component, drop, close } = setup();
        component.deleteData();
        expect(component.datasetNames).toEqual(['failed']);
        expect(component.hasChanges).toBe(true);
        expect(component.isInProgress).toBe(false);
        expect(close).not.toHaveBeenCalled();
        drop.mockImplementation(() => of({}));
        component.deleteData();
        expect(drop.mock.calls.map(call => call[0])).toEqual([
            'success',
            'failed',
            'failed',
        ]);
        expect(close).toHaveBeenCalledWith(true);
    });

    it('truncates all selected datasets without dropping them', () => {
        const { component, drop, remove, close } = setup();
        component.truncateData();
        expect(remove).toHaveBeenCalledTimes(2);
        expect(drop).not.toHaveBeenCalled();
        expect(close).toHaveBeenCalledWith(true);
    });

    it('does not issue requests for an empty selection or an active operation', () => {
        const { component, drop } = setup();
        component.isInProgress = true;
        component.deleteData();
        component.isInProgress = false;
        component.datasetNames = [];
        component.deleteData();
        expect(drop).not.toHaveBeenCalled();
    });
});
