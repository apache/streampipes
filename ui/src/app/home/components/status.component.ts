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

import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationCountService } from '../../services/notification-count-service';
import {
    PipelineElementService,
    PipelineService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.scss'],
})
export class StatusComponent implements OnInit {
    unreadNotificationCount = 0;

    @Input()
    availablePipelines = 0;

    @Input()
    availablePipelineElements = 0;

    @Input()
    runningPipelines = 0;

    constructor(
        private pipelineElementService: PipelineElementService,
        private router: Router,
        public notificationCountService: NotificationCountService,
        private pipelineService: PipelineService,
    ) {}

    ngOnInit() {
        this.notificationCountService.unreadNotificationCount$.subscribe(
            count => (this.unreadNotificationCount = count),
        );
    }

    navigate(url: string) {
        this.router.navigate([url]);
    }
}
