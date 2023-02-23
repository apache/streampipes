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

import {
    Component,
    ElementRef,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    ExistingNotification,
    NotificationItem,
} from './model/notifications.model';
import { ElementIconText } from '../services/get-element-icon-text.service';
import { NotificationsService } from './service/notifications.service';
import { Subscription, timer } from 'rxjs';
import { NotificationUtils } from './utils/notifications.utils';
import { NotificationCountService } from '../services/notification-count-service';
import {
    FreeTextStaticProperty,
    Pipeline,
    PipelineService,
} from '@streampipes/platform-services';
import { AuthService } from '../services/auth.service';
import { filter, switchMap } from 'rxjs/operators';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent implements OnInit, OnDestroy {
    static readonly NOTIFICATIONS_APP_ID =
        'org.apache.streampipes.sinks.internal.jvm.notification';
    static readonly NOTIFICATION_TITLE_KEY = 'title';

    @ViewChild('notificationPane') private notificationContainer: ElementRef;

    allNotifications: Map<string, NotificationItem[]> = new Map();
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
    lastFetchTime = new Date().getTime();

    constructor(
        private authService: AuthService,
        private pipelineService: PipelineService,
        public elementIconText: ElementIconText,
        private notificationService: NotificationsService,
        private notificationCountService: NotificationCountService,
        private breadcrumbService: SpBreadcrumbService,
    ) {
        this.unreadNotifications = [];
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb([{ label: 'Notifications' }]);
        this.getPipelinesWithNotifications();
        this.subscription = timer(0, 5000)
            .pipe(
                filter(
                    () =>
                        this.currentlySelectedNotification !== undefined &&
                        this.allNotifications.size > 0,
                ),
                switchMap(() =>
                    this.notificationService.getNotificationsFromTime(
                        this.lastFetchTime,
                    ),
                ),
            )
            .subscribe(notifications => {
                let scrollToBottom = false;
                if (notifications.length > 0) {
                    if (
                        this.notificationContainer.nativeElement.scrollHeight -
                            this.notificationContainer.nativeElement
                                .scrollTop <=
                            this.notificationContainer.nativeElement
                                .clientHeight +
                                10 &&
                        this.notificationContainer.nativeElement.scrollHeight -
                            this.notificationContainer.nativeElement
                                .scrollTop >=
                            this.notificationContainer.nativeElement
                                .clientHeight -
                                10
                    ) {
                        scrollToBottom = true;
                    }
                    this.newEventArriving = true;
                    notifications.forEach(notification => {
                        const notificationId =
                            NotificationUtils.makeNotificationId(
                                notification.correspondingPipelineId,
                                notification.title,
                            );
                        const existingNots =
                            this.allNotifications.get(notificationId);
                        existingNots.push(notification);
                        this.allNotifications.set(notificationId, existingNots);
                        if (
                            this.currentlySelectedNotificationId ===
                            notificationId
                        ) {
                            this.liveOffset++;
                            notification.read = true;
                            setTimeout(() => {
                                this.notificationService
                                    .updateNotification(notification)
                                    .subscribe();
                            }, 500);
                        } else {
                            this.newNotificationInfo[notificationId] = true;
                        }
                    });

                    if (scrollToBottom) {
                        setTimeout(() => {
                            this.scrollToBottom();
                        });
                    }
                }
                this.lastFetchTime = new Date().getTime();
                this.newEventArriving = false;
            });
    }

    scrollToBottom() {
        setTimeout(() => {
            this.notificationContainer.nativeElement.scrollTop =
                this.notificationContainer.nativeElement.scrollHeight + 100;
        }, 200);
    }

    getPipelinesWithNotifications() {
        this.notificationsLoading = true;
        this.pipelineService.getPipelines().subscribe(pipelines => {
            this.existingNotifications =
                this.getAllExistingNotifications(pipelines);
            this.notificationsLoading = false;
            if (this.existingNotifications.length > 0) {
                this.pipelinesWithNotificationsPresent = true;
                this.selectNotification(this.existingNotifications[0]);
                this.existingNotifications.forEach(notification => {
                    this.getNotifications(
                        notification,
                        this.currentOffset,
                        10,
                        true,
                    );
                });
            }
        });
    }

    getAllExistingNotifications(pipelines: Pipeline[]): ExistingNotification[] {
        const existingNotifications = [];
        pipelines.forEach(pipeline => {
            const notificationActions = pipeline.actions.filter(
                sink =>
                    sink.appId === NotificationsComponent.NOTIFICATIONS_APP_ID,
            );
            notificationActions.forEach(notificationAction => {
                const notificationName = notificationAction.staticProperties
                    .filter(
                        sp =>
                            sp.internalName ===
                            NotificationsComponent.NOTIFICATION_TITLE_KEY,
                    )
                    .map(sp => (sp as FreeTextStaticProperty).value)[0];
                const pipelineName = pipeline.name;
                existingNotifications.push({
                    notificationTitle: notificationName,
                    pipelineName,
                    pipelineId: pipeline._id,
                    notificationId: NotificationUtils.makeNotificationId(
                        pipeline._id,
                        notificationName,
                    ),
                });
            });
        });
        return existingNotifications;
    }

    getNotifications(
        notification: ExistingNotification,
        offset: number,
        count: number,
        scrollToBottom: boolean,
    ) {
        this.notificationService
            .getNotifications(notification, offset, count)
            .subscribe(notifications => {
                notifications.sort((a, b) => {
                    return a.createdAtTimestamp - b.createdAtTimestamp;
                });
                const notificationId = NotificationUtils.makeNotificationId(
                    notification.pipelineId,
                    notification.notificationTitle,
                );
                if (!this.allNotifications.has(notificationId)) {
                    this.allNotifications.set(notificationId, []);
                }
                this.allNotifications
                    .get(notificationId)
                    .unshift(...notifications);
                if (scrollToBottom) {
                    setTimeout(() => {
                        this.scrollToBottom();
                    }, 500);
                } else {
                    setTimeout(() => {
                        this.notificationContainer.nativeElement.scrollTop =
                            this.notificationContainer.nativeElement
                                .scrollHeight - this.previousScrollHeight;
                    });
                }
                this.updateUnreadNotifications(notifications);
            });
    }

    updateUnreadNotifications(notifications: NotificationItem[]) {
        notifications.forEach(n => {
            if (!n.read) {
                n.read = true;
                this.notificationCountService.decreaseNotificationCount();
                this.notificationService.updateNotification(n).subscribe();
            }
        });
    }

    selectNotification(notification: ExistingNotification) {
        this.currentOffset = 0;
        this.liveOffset = 0;
        this.currentlySelectedNotification = notification;
        this.currentlySelectedNotificationId =
            NotificationUtils.makeNotificationIdFromNotification(notification);
        if (this.allNotifications.has(this.currentlySelectedNotificationId)) {
            this.updateUnreadNotifications(
                this.allNotifications.get(this.currentlySelectedNotificationId),
            );
        }
        this.newNotificationInfo[this.currentlySelectedNotificationId] = false;
        this.notificationCountService.lockIncreaseUpdateForId(
            this.currentlySelectedNotificationId,
        );
        if (this.notificationContainer) {
            this.scrollToBottom();
        }
    }

    onScroll() {
        if (this.notificationContainer.nativeElement.scrollTop === 0) {
            this.currentOffset += 10;
            this.previousScrollHeight =
                this.notificationContainer.nativeElement.scrollHeight;
            const currentNotifications = this.allNotifications.get(
                this.currentlySelectedNotificationId,
            );
            this.getNotifications(
                this.currentlySelectedNotification,
                currentNotifications.length + this.liveOffset,
                10,
                false,
            );
        }
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        this.notificationCountService.unlockIncreaseUpdate();
    }
}
