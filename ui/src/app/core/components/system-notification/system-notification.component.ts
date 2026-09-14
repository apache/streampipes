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

import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    SystemNotificationConfig,
    SystemNotificationService,
} from '@streampipes/platform-services';
import { SpLabelComponent } from '@streampipes/shared-ui';
import { MatTooltip } from '@angular/material/tooltip';
import { EMPTY, Subscription, merge, timer } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import {
    NotificationTone,
    toNotificationTone,
} from './system-notification-appearance';

/**
 * Determines how long it takes for users to see a new notification or a change
 * to it.
 */
const NOTIFICATION_POLL_INTERVAL_MS = 30000;

/**
 * Holds the longest delay a browser timer supports (32-bit, about 24.8 days).
 */
const MAX_TIMER_DELAY_MS = 2 ** 31 - 1;

/**
 * Displays the system notification an administrator has configured.
 */
@Component({
    selector: 'sp-system-notification',
    templateUrl: './system-notification.component.html',
    styleUrls: ['./system-notification.component.scss'],
    imports: [SpLabelComponent, MatTooltip],
})
export class SpSystemNotificationComponent implements OnInit {
    private notificationService = inject(SystemNotificationService);
    private destroyRef = inject(DestroyRef);

    notification: SystemNotificationConfig;

    private expiryTimer: Subscription;

    ngOnInit(): void {
        // Reloads on a fixed interval and immediately after an administrator
        // has saved in this tab
        merge(
            timer(0, NOTIFICATION_POLL_INTERVAL_MS),
            this.notificationService.changed$,
        )
            .pipe(
                switchMap(() =>
                    this.notificationService
                        .getActiveNotification()
                        // Ignores a failed request, so that one error does not
                        // end further updates
                        .pipe(catchError(() => EMPTY)),
                ),
                takeUntilDestroyed(this.destroyRef),
            )
            .subscribe(notification => this.applyNotification(notification));
    }

    get tone(): NotificationTone {
        return toNotificationTone(this.notification?.type);
    }

    private applyNotification(notification: SystemNotificationConfig): void {
        this.notification = notification;
        this.expiryTimer?.unsubscribe();

        if (notification.enabled && notification.expiresAtMillis) {
            this.scheduleHideOnExpiry(notification.expiresAtMillis);
        }
    }

    /**
     * Hides the notification exactly when it expires, not at the next request.
     *
     * @param expiresAtMillis when the notification should disappear.
     */
    private scheduleHideOnExpiry(expiresAtMillis: number): void {
        const delay = expiresAtMillis - Date.now();

        if (delay <= 0) {
            this.notification = undefined;
            return;
        }
        if (delay > MAX_TIMER_DELAY_MS) {
            // Too far ahead for a timer, so none is started yet. Each response
            // calls this method again and starts it once the delay fits.
            return;
        }

        this.expiryTimer = timer(delay)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => (this.notification = undefined));
    }
}
