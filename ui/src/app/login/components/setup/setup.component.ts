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

import { Component, ElementRef, ViewChild } from '@angular/core';
import { RestApi } from '../../../services/rest-api.service';
import { LoginService } from '../../services/login.service';
import { Router } from '@angular/router';
import { AppConstants } from '../../../services/app.constants';

@Component({
    selector: 'sp-setup',
    templateUrl: './setup.component.html',
    styleUrls: ['./setup.component.scss'],
})
export class SetupComponent {
    @ViewChild('scroll') private scrollContainer: ElementRef;

    installationFinished: boolean;
    installationSuccessful: boolean;
    installationResults: any;
    loading: any;
    setup: any = {
        adminEmail: '',
        adminPassword: '',
        installPipelineElements: true,
    };
    installationRunning: any;
    nextTaskTitle: any;

    constructor(
        private loginService: LoginService,
        private restApi: RestApi,
        private router: Router,
        public appConstants: AppConstants,
    ) {
        this.installationFinished = false;
        this.installationSuccessful = false;
        this.installationResults = [];
        this.loading = false;
    }

    configure(currentInstallationStep) {
        this.installationRunning = true;
        this.loading = true;

        this.loginService
            .setupInstall(this.setup, currentInstallationStep)
            .subscribe(data => {
                this.installationResults = this.installationResults.concat(
                    data.statusMessages,
                );
                this.nextTaskTitle = data.nextTaskTitle;
                this.scrollContainer.nativeElement.scrollTop =
                    this.scrollContainer.nativeElement.scrollHeight;
                const nextInstallationStep = currentInstallationStep + 1;
                if (nextInstallationStep > data.installationStepCount - 1) {
                    // eslint-disable-next-line no-sequences
                    this.restApi.configured().subscribe(res => {
                        if (res.configured) {
                            this.installationFinished = true;
                            this.loading = false;
                        }
                        // eslint-disable-next-line @typescript-eslint/no-unused-expressions
                    }),
                        () => {
                            this.loading = false;
                            // this.showToast("Fatal error, contact administrator");
                        };
                } else {
                    this.configure(nextInstallationStep);
                }
            });
    }

    openLoginPage() {
        this.router.navigate(['login']);
    }
}
