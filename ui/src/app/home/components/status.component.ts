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

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationCountService } from '../../services/notification-count-service';
import {
    PipelineService,
    PipelineElementService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.css'],
})
export class StatusComponent implements OnInit {
    pipelines = 0;
    runningPipelines = 0;
    installedPipelineElements = 0;
    unreadNotificationCount = 0;

    constructor(
        private pipelineElementService: PipelineElementService,
        private router: Router,
        public notificationCountService: NotificationCountService,
        private pipelineService: PipelineService,
    ) {}

    ngOnInit() {
        this.getPipelines();
        this.getStreams();
        this.getProcessors();
        this.getSinks();
        this.notificationCountService.unreadNotificationCount$.subscribe(
            count => (this.unreadNotificationCount = count),
        );
    }

    getPipelines() {
        this.pipelineService.getPipelines().subscribe(pipelines => {
            this.pipelines = pipelines.length;
            this.runningPipelines = pipelines.filter(p => p.running).length;
        });
    }

    getStreams() {
        this.pipelineElementService.getDataStreams().subscribe(streams => {
            this.addPipelineElementList(streams);
        });
    }

    getProcessors() {
        this.pipelineElementService.getDataProcessors().subscribe(msg => {
            this.addPipelineElementList(msg);
        });
    }

    getSinks() {
        this.pipelineElementService.getDataSinks().subscribe(msg => {
            this.addPipelineElementList(msg);
        });
    }

    addPipelineElementList(msg) {
        this.installedPipelineElements += msg.length;
    }

    navigate(url: string) {
        this.router.navigate([url]);
    }
}
