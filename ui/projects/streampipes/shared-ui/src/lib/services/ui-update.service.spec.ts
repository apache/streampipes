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
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { UI_UPDATE_RELOAD, UiUpdateService } from './ui-update.service';

describe('UiUpdateService', () => {
    let service: UiUpdateService;
    let action: Subject<void>;
    let dismissed: Subject<void>;
    const fetchMock = vi.fn();
    const reload = vi.fn();
    const open = vi.fn();
    const dismiss = vi.fn();

    beforeEach(() => {
        vi.useFakeTimers();
        vi.clearAllMocks();
        vi.stubGlobal('fetch', fetchMock);
        vi.spyOn(document, 'visibilityState', 'get').mockReturnValue('visible');
        document.head.innerHTML =
            '<base href="/test/"><meta name="ui-build-id" content="current">';
        action = new Subject<void>();
        dismissed = new Subject<void>();
        dismiss.mockImplementation(() => dismissed.next());
        open.mockReturnValue({
            onAction: () => action,
            afterDismissed: () => dismissed,
            dismiss,
        });
        fetchMock.mockResolvedValue({
            ok: true,
            json: async () => ({ buildId: 'new-build' }),
        });
        TestBed.configureTestingModule({
            providers: [
                UiUpdateService,
                { provide: MatSnackBar, useValue: { open } },
                {
                    provide: TranslateService,
                    useValue: { instant: (text: string) => text },
                },
                { provide: UI_UPDATE_RELOAD, useValue: reload },
            ],
        });
        service = TestBed.inject(UiUpdateService);
    });

    afterEach(() => {
        TestBed.resetTestingModule();
        vi.restoreAllMocks();
        vi.unstubAllGlobals();
        vi.useRealTimers();
        document.head.innerHTML = '';
    });

    it('polls without caching and reloads only after explicit action', async () => {
        service.start();
        await vi.advanceTimersByTimeAsync(15000);
        expect(fetchMock.mock.calls[0][0].pathname).toBe('/test/version.json');
        expect(fetchMock.mock.calls[0][1].cache).toBe('no-store');
        expect(open).toHaveBeenCalledTimes(1);
        expect(open.mock.calls[0][2].duration).toBe(0);
        expect(reload).not.toHaveBeenCalled();
        action.next();
        expect(reload).toHaveBeenCalledTimes(1);
    });

    it('does not notify for the current build or malformed manifests', async () => {
        service.start();
        for (const manifest of [
            { buildId: 'current' },
            {},
            null,
            { buildId: 42 },
            { buildId: '' },
        ]) {
            fetchMock.mockResolvedValueOnce({
                ok: true,
                json: async () => manifest,
            });
            await service.checkForUpdate();
        }
        expect(open).not.toHaveBeenCalled();
    });

    it('retries after a network failure and avoids duplicate notifications', async () => {
        service.start();
        fetchMock.mockRejectedValueOnce(new Error('offline'));
        await service.checkForUpdate();
        expect(open).not.toHaveBeenCalled();
        await service.checkForUpdate();
        await service.checkForUpdate();
        expect(open).toHaveBeenCalledTimes(1);
        dismissed.next();
        await service.checkForUpdate();
        expect(open).toHaveBeenCalledTimes(2);
    });

    it('ignores unsuccessful responses and dismisses stale offers on rollback', async () => {
        service.start();
        fetchMock.mockResolvedValueOnce({ ok: false });
        await service.checkForUpdate();
        expect(open).not.toHaveBeenCalled();
        await service.checkForUpdate();
        fetchMock.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ buildId: 'current' }),
        });
        await service.checkForUpdate();
        expect(dismiss).toHaveBeenCalledTimes(1);
    });

    it('skips hidden tabs and checks when visible again', async () => {
        service.start();
        vi.spyOn(document, 'visibilityState', 'get').mockReturnValue('hidden');
        await service.checkForUpdate();
        expect(fetchMock).not.toHaveBeenCalled();
        vi.spyOn(document, 'visibilityState', 'get').mockReturnValue('visible');
        document.dispatchEvent(new Event('visibilitychange'));
        await Promise.resolve();
        expect(fetchMock).toHaveBeenCalledTimes(1);
    });

    it('does not poll development builds without an embedded ID', async () => {
        document.querySelector('meta')?.remove();
        service.start();
        await vi.advanceTimersByTimeAsync(180000);
        expect(fetchMock).not.toHaveBeenCalled();
    });

    it('starts only once and stops timers and listeners when destroyed', async () => {
        service.start();
        service.start();
        await vi.advanceTimersByTimeAsync(15000);
        expect(fetchMock).toHaveBeenCalledTimes(1);
        TestBed.resetTestingModule();
        window.dispatchEvent(new Event('online'));
        await vi.advanceTimersByTimeAsync(180000);
        expect(fetchMock).toHaveBeenCalledTimes(1);
    });
    it('aborts stalled requests, prevents overlap and retries afterwards', async () => {
        service.start();
        fetchMock.mockImplementationOnce(
            (_url, options) =>
                new Promise((_resolve, reject) => {
                    options.signal.addEventListener('abort', () =>
                        reject(new Error('aborted')),
                    );
                }),
        );
        const pending = service.checkForUpdate();
        await service.checkForUpdate();
        expect(fetchMock).toHaveBeenCalledTimes(1);
        await vi.advanceTimersByTimeAsync(10000);
        await pending;
        expect(fetchMock.mock.calls[0][1].signal.aborted).toBe(true);
        await service.checkForUpdate();
        expect(open).toHaveBeenCalledTimes(1);
    });
});
