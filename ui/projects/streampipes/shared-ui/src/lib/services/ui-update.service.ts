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

import { DOCUMENT } from '@angular/common';
import {
    DestroyRef,
    Injectable,
    InjectionToken,
    NgZone,
    inject,
} from '@angular/core';
import {
    MatSnackBar,
    MatSnackBarRef,
    SimpleSnackBar,
} from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export const UI_UPDATE_RELOAD = new InjectionToken<() => void>(
    'UI_UPDATE_RELOAD',
    {
        providedIn: 'root',
        factory: () => () => window.location.reload(),
    },
);

/** Polls the UI image's manifest; reloading always requires an explicit user action. */
@Injectable({ providedIn: 'root' })
export class UiUpdateService {
    private readonly document = inject(DOCUMENT);
    private readonly zone = inject(NgZone);
    private readonly destroyRef = inject(DestroyRef);
    private readonly snackBar = inject(MatSnackBar);
    private readonly translate = inject(TranslateService);
    private readonly reload = inject(UI_UPDATE_RELOAD);
    private buildId?: string;
    private timer?: ReturnType<typeof setTimeout>;
    private request?: AbortController;
    private notification?: MatSnackBarRef<SimpleSnackBar>;
    private stopped = false;
    private started = false;

    start(): void {
        if (this.started) {
            return;
        }
        this.started = true;
        this.buildId = this.document.querySelector<HTMLMetaElement>(
            'meta[name="ui-build-id"]',
        )?.content;
        const window = this.document.defaultView;
        // ng serve has no build manifest or embedded ID.
        if (!this.buildId || !window) {
            return;
        }
        const resume = () => void this.checkForUpdate();
        this.zone.runOutsideAngular(() => {
            this.document.addEventListener('visibilitychange', resume);
            window.addEventListener('online', resume);
            this.schedule(5000 + Math.random() * 10000);
        });
        this.destroyRef.onDestroy(() => {
            this.stopped = true;
            clearTimeout(this.timer);
            this.request?.abort();
            this.notification?.dismiss();
            this.document.removeEventListener('visibilitychange', resume);
            window.removeEventListener('online', resume);
        });
    }

    async checkForUpdate(): Promise<void> {
        if (
            !this.buildId ||
            this.stopped ||
            this.request ||
            this.document.visibilityState === 'hidden' ||
            this.document.defaultView?.navigator.onLine === false
        ) {
            return;
        }
        const controller = new AbortController();
        this.request = controller;
        const timeout = setTimeout(() => controller.abort(), 10000);
        try {
            const response = await fetch(
                new URL('version.json', this.document.baseURI),
                {
                    cache: 'no-store',
                    signal: controller.signal,
                    credentials: 'same-origin',
                },
            );
            if (!response.ok) {
                return;
            }
            const manifest: unknown = await response.json();
            if (
                this.stopped ||
                !manifest ||
                typeof manifest !== 'object' ||
                !('buildId' in manifest) ||
                typeof manifest.buildId !== 'string' ||
                !/^[a-zA-Z0-9_-]{1,128}$/.test(manifest.buildId)
            ) {
                return;
            }
            if (manifest.buildId === this.buildId) {
                this.notification?.dismiss();
                return;
            }
            if (!this.notification) {
                this.zone.run(() => this.showUpdate());
            }
        } catch {
            // Offline, restarting servers and invalid manifests are retried next poll.
        } finally {
            clearTimeout(timeout);
            this.request = undefined;
        }
    }

    private schedule(delay: number): void {
        this.timer = setTimeout(async () => {
            await this.checkForUpdate();
            if (!this.stopped) {
                this.schedule(60000 + Math.random() * 30000);
            }
        }, delay);
    }

    private showUpdate(): void {
        const notification = this.snackBar.open(
            this.translate.instant(
                'A new version is available. Update when you have finished your work.',
            ),
            this.translate.instant('Update now'),
            { duration: 0, politeness: 'polite' },
        );
        this.notification = notification;
        notification
            .onAction()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => this.reload());
        notification
            .afterDismissed()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
                if (this.notification === notification) {
                    this.notification = undefined;
                }
            });
    }
}
