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

import {AuthService} from "../../../services/auth.service";
import {AuthStatusService} from "../../../services/auth-status.service";
import {Component, Inject, OnInit} from "@angular/core";

@Component({
    selector: 'startup',
    templateUrl: './startup.component.html'
})
export class StartupComponent implements OnInit {

    progress: number = 0;
    currentStep = 0;
    maxLoadingTimeInSeconds = 300;
    loadingIntervalInSeconds = 1;

    constructor(private AuthService: AuthService,
                private AuthStatusService: AuthStatusService,
                @Inject("$state") private $state: any) {
    }

    ngOnInit() {
        this.checkStatus();
    }

    checkStatus() {
        this.AuthService.checkConfiguration().subscribe(() => {
            this.progress = 100;
            this.$state.go("setup");
        }, () => {
            this.currentStep += this.loadingIntervalInSeconds;
            this.progress = (this.currentStep / this.maxLoadingTimeInSeconds) * 100;
            setTimeout(() => {
                this.checkStatus();
            }, this.loadingIntervalInSeconds*1000);
        });
    }
}