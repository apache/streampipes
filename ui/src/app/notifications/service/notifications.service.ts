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

import {HttpClient} from "@angular/common/http";
import {AuthStatusService} from "../../services/auth-status.service";
import {Observable} from "rxjs";
import {ExistingNotification, NotificationCount, NotificationItem} from "../model/notifications.model";
import {Injectable} from "@angular/core";
import {NotificationUtils} from "../utils/notifications.utils";

@Injectable()
export class NotificationsService {

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService) {
    }

    getUnreadNotificationsCount(): Observable<NotificationCount> {
        return this.http.get(this.notificationUrl + "/count").map(data => {
            return data as NotificationCount;
        })
    }

    getNotifications(existingNotification: ExistingNotification, offset: number, limit: number): Observable<NotificationItem[]> {
        return this.http
            .get(this.notificationUrl
                + "?"
                + "notificationType=" + NotificationUtils.makeNotificationIdFromNotification(existingNotification)
                + "&"
                + "offset=" + offset
                + "&"
                + "count=" + limit)
            .map(data => {
                return data as NotificationItem[];
            });
    }

    updateNotification(notificationItem: NotificationItem): Observable<any> {
        return this.http.put(this.notificationUrl + "/" + notificationItem._id, notificationItem, {
            headers: { ignoreLoadingBar: '' }
        });
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get notificationUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/notifications'
    }
}