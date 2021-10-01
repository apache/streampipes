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

import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ExistingNotification, NotificationItem } from './model/notifications.model';
import { ElementIconText } from '../services/get-element-icon-text.service';
import { NotificationsService } from './service/notifications.service';
import { Message } from '@stomp/stompjs';
import { Subscription } from 'rxjs';
import { RxStompService } from '@stomp/ng2-stompjs';
import { NotificationUtils } from './utils/notifications.utils';
import { NotificationCountService } from '../services/notification-count-service';
import { FreeTextStaticProperty, Pipeline } from '../core-model/gen/streampipes-model';
import { PipelineService } from '../platform-services/apis/pipeline.service';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {

    static readonly NOTIFICATIONS_APP_ID = 'org.apache.streampipes.sinks.internal.jvm.notification';
    static readonly NOTIFICATION_TOPIC_PREFIX = 'org.apache.streampipes.notifications.';
    static readonly NOTIFICATION_TITLE_KEY = 'title';

    @ViewChild('notificationPane') private notificationContainer: ElementRef;

    notifications: NotificationItem[] = [];
    unreadNotifications: any;
    existingNotifications: ExistingNotification[] = [];
    currentlySelectedNotification: ExistingNotification;
    currentlySelectedNotificationId: string;

    pipelinesWithNotificationsPresent = false;
    notificationsLoading = false;

    currentOffset = 0;
    liveOffset = 0;
    previousScrollHeight: number;

    subscription: Subscription;
    notificationTopic: string;

    newNotificationInfo: boolean[] = [];

    newEventArriving = false;

    constructor(private authService: AuthService,
                private pipelineService: PipelineService,
                public elementIconText: ElementIconText,
                private notificationService: NotificationsService,
                private rxStompService: RxStompService,
                private notificationCountService: NotificationCountService) {
        this.notifications = [];
        this.unreadNotifications = [];
        this.notificationTopic = NotificationsComponent.NOTIFICATION_TOPIC_PREFIX + this.authService.getCurrentUser().email;
    }

    ngOnInit() {
        this.getPipelinesWithNotifications();
    }

    createSubscription() {
        this.subscription = this.rxStompService.watch('/topic/' + this.notificationTopic).subscribe((message: Message) => {
            let scrollToBottom = false;
            if ((this.notificationContainer.nativeElement.scrollHeight - this.notificationContainer.nativeElement.scrollTop) <= (this.notificationContainer.nativeElement.clientHeight + 10) &&
                (this.notificationContainer.nativeElement.scrollHeight - this.notificationContainer.nativeElement.scrollTop) >= (this.notificationContainer.nativeElement.clientHeight - 10)) {
                scrollToBottom = true;
            }
            this.newEventArriving = true;
            const notification: NotificationItem = JSON.parse(message.body) as NotificationItem;
            const notificationId = NotificationUtils.makeNotificationId(notification.correspondingPipelineId, notification.title);
            if (this.currentlySelectedNotificationId === notificationId) {
                this.notifications.push(notification);
                this.liveOffset++;
                notification.read = true;
                setTimeout(() => {
                    this.notificationService.updateNotification(notification).subscribe();
                }, 500);

            } else {
                this.newNotificationInfo[notificationId] = true;
            }
            if (scrollToBottom) {
                setTimeout(() => {
                    this.notificationContainer.nativeElement.scrollTop = this.notificationContainer.nativeElement.scrollHeight + 100;
                });
            }

            this.newEventArriving = false;
        });
    }

    getPipelinesWithNotifications() {
        this.notificationsLoading = true;
        this.pipelineService.getOwnPipelines().subscribe(pipelines => {
            this.filterForNotifications(pipelines);
            this.notificationsLoading = false;
            if (this.existingNotifications.length > 0) {
                this.pipelinesWithNotificationsPresent = true;
                this.selectNotification(this.existingNotifications[0]);
                this.createSubscription();
            }
        });
    }

    filterForNotifications(pipelines: Pipeline[]) {
        pipelines.forEach(pipeline => {
           const notificationActions = pipeline.actions.filter(sink => sink.appId === NotificationsComponent.NOTIFICATIONS_APP_ID);
             notificationActions.forEach(notificationAction => {
                const notificationName = notificationAction
                    .staticProperties
                    .filter(sp => sp.internalName === NotificationsComponent.NOTIFICATION_TITLE_KEY)
                    .map(sp => (sp as FreeTextStaticProperty).value)[0];
                const pipelineName = pipeline.name;
                this.existingNotifications.push({notificationTitle: notificationName,
                    pipelineName, pipelineId: pipeline._id, notificationId: NotificationUtils.makeNotificationId(pipeline._id, notificationName)});
             });
        });
    }

    getNotifications(notification: ExistingNotification, offset: number, count: number, scrollToBottom: boolean) {
        this.notificationService.getNotifications(notification, offset, count).subscribe(notifications => {
            notifications.sort((a, b) => {
                return (a.createdAtTimestamp - b.createdAtTimestamp);
            });
            this.notifications.unshift(...notifications);
            if (scrollToBottom) {
                setTimeout(() => {
                    this.notificationContainer.nativeElement.scrollTop = this.notificationContainer.nativeElement.scrollHeight;
                });
            } else {
                setTimeout(() => {
                    this.notificationContainer.nativeElement.scrollTop = this.notificationContainer.nativeElement.scrollHeight - this.previousScrollHeight;
                });
            }
            notifications.forEach(n => {
                if (!n.read) {
                    n.read = true;
                    this.notificationCountService.decreaseNotificationCount();
                    this.notificationService.updateNotification(n).subscribe();
                }
            });
        });
    }

    selectNotification(notification: ExistingNotification) {
        this.notifications = [];
        this.currentOffset = 0;
        this.liveOffset = 0;
        this.currentlySelectedNotification = notification;
        this.currentlySelectedNotificationId = NotificationUtils.makeNotificationIdFromNotification(notification);
        this.notificationCountService.lockIncreaseUpdateForId(this.currentlySelectedNotificationId);
        this.getNotifications(notification, this.currentOffset, 10, true);
    }

    onScroll(event: any) {
        if (this.notificationContainer.nativeElement.scrollTop === 0) {
            this.currentOffset += 10;
            this.previousScrollHeight = this.notificationContainer.nativeElement.scrollHeight;
            this.getNotifications(this.currentlySelectedNotification, this.notifications.length + this.liveOffset, 10, false);
        }
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        this.notificationCountService.unlockIncreaseUpdate();
    }
}
