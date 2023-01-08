/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Injectable } from '@angular/core';
import { RestApi } from './rest-api.service';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class NotificationCountService {
    public unreadNotificationCount$ = new BehaviorSubject(0);
    lockNotificationId: string;
    lockActive = false;

    constructor(private restApi: RestApi) {}

    loadUnreadNotifications() {
        this.restApi.getUnreadNotificationsCount().subscribe(response => {
            this.unreadNotificationCount$.next(response.count);
        });
    }

    decreaseNotificationCount() {
        const newValue = Math.max(this.unreadNotificationCount$.value - 1, 0);
        this.unreadNotificationCount$.next(newValue);
    }

    lockIncreaseUpdateForId(notificationId: string) {
        this.lockNotificationId = notificationId;
        this.lockActive = true;
    }

    unlockIncreaseUpdate() {
        this.lockActive = false;
    }

    makeId(pipelineId: string, notificationTitle: string) {
        const vizName = notificationTitle.replace(/\\s/g, '-');
        return pipelineId + '-' + vizName;
    }
}
