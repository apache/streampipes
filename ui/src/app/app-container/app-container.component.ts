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

import { Component, ChangeDetectorRef } from '@angular/core';

import { AppContainerService } from './shared/app-container.service';
import { InstalledApp } from './shared/installed-app.model';


@Component({
    templateUrl: './app-container.component.html',
    styleUrls: ['./app-container.component.css']
})
export class AppContainerComponent {

    installedApps: InstalledApp[];
    activeApp: InstalledApp;
    isAppActive = false;

    constructor(private appContainerService: AppContainerService, private changeDetector: ChangeDetectorRef) {
        this.appContainerService.getInstalledApps().subscribe(installedApps => {
            this.installedApps = installedApps;
        });
    }

    activateApp(installedApp: InstalledApp): void {
        this.isAppActive = false;
        this.changeDetector.detectChanges();
        this.activeApp = installedApp;
        this.isAppActive = true;
    }

    appClosed() {
        this.isAppActive = false;
    }

}