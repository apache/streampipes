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

import {Component} from "@angular/core";
import {RestApi} from "../../services/rest-api.service";
import {Router} from "@angular/router";
import {NotificationCountService} from "../../services/notification-count-service";

@Component({
    selector: 'status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.css']
})
export class StatusComponent {

    pipelines: number = 0;
    runningPipelines: number = 0;
    installedPipelineElements: number = 0;

    constructor(private RestApi: RestApi,
                private Router: Router,
                public NotificationCountService: NotificationCountService) {

    }

    ngOnInit() {
        this.getPipelines();
        this.getStreams();
        this.getProcessors();
        this.getSinks();
    }

    getPipelines() {
        this.RestApi.getOwnPipelines().subscribe(pipelines => {
           this.pipelines = pipelines.length;
           this.runningPipelines = pipelines.filter(p => p.running).length;
        });
    }

    getStreams() {
        this.RestApi.getOwnSources()
            .subscribe((sources) => {
                sources.forEach((source, i, sources) => {
                    this.installedPipelineElements += source.spDataStreams.length;
                });
            });
    };

    getProcessors() {
        this.RestApi.getOwnSepas()
            .subscribe(msg => {
                this.addPipelineElementList(msg);
            });
    };

    getSinks() {
        this.RestApi.getOwnActions()
            .subscribe(msg => {
               this.addPipelineElementList(msg);
            });
    };

    addPipelineElementList(msg) {
        this.installedPipelineElements += msg.length;
    }

    navigate(url: string) {
        this.Router.navigate([url]);
    }
}