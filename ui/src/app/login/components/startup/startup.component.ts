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

import { AuthService } from '../../../services/auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppConstants } from '../../../services/app.constants';

@Component({
    selector: 'sp-startup',
    templateUrl: './startup.component.html',
    styleUrls: ['./startup.component.scss'],
})
export class StartupComponent implements OnInit {
    progress = 0;
    currentStep = 0;
    maxLoadingTimeInSeconds = 100;
    loadingIntervalInSeconds = 1;

    constructor(
        private authService: AuthService,
        private router: Router,
        public appConstants: AppConstants,
    ) {}

    ngOnInit() {
        this.checkStatus();
    }

    checkStatus() {
        this.authService.checkConfiguration().subscribe(
            configured => {
                this.progress = 100;
                if (configured) {
                    this.router.navigate(['login']);
                } else {
                    setTimeout(() => {
                        this.checkStatus();
                    }, this.loadingIntervalInSeconds * 1000);
                }
            },
            () => {
                this.currentStep += this.loadingIntervalInSeconds;
                this.progress =
                    (this.currentStep / this.maxLoadingTimeInSeconds) * 100;
                setTimeout(() => {
                    this.checkStatus();
                }, this.loadingIntervalInSeconds * 1000);
            },
        );
    }
}
