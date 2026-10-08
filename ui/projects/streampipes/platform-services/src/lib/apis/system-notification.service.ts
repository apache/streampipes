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

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';
import { PlatformServicesCommons } from './commons.service';
import { SystemNotificationConfig } from '../model/config/general-config.model';

/**
 * Requests the system notification from the backend and lets the rest of the
 * application know when an administrator has changed it.
 */
@Injectable({
    providedIn: 'root',
})
export class SystemNotificationService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    private changedSubject = new Subject<void>();

    /**
     * Emits immediately after a notification was saved. This only reaches the
     * browser it was saved in, on all other clients the change becomes visible
     * when they request the notification.
     */
    readonly changed$ = this.changedSubject.asObservable();

    getActiveNotification(): Observable<SystemNotificationConfig> {
        const context = new HttpContext().set(NGX_LOADING_BAR_IGNORED, true);
        return this.http.get<SystemNotificationConfig>(this.notificationPath, {
            context,
        });
    }

    notifyChanged(): void {
        this.changedSubject.next();
    }

    private get notificationPath() {
        return (
            this.platformServicesCommons.apiBasePath + '/system-notification'
        );
    }
}
