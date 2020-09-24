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

import { Component, OnInit } from "@angular/core";

@Component({
    templateUrl: './app-overview.component.html',
    styleUrls: ['./app-overview.component.css']
})
export class AppOverviewComponent implements OnInit {

    selectedIndex = 0;
    appOpen = false;
    currentlySelectedApp = '';

    apps: any[] = [
        {
            appName: 'Asset Dashboards',
            appDescription: 'Monitor measurements of your assets by placing visualizations on an image of your asset.',
            appId: 'asset-monitoring',
        }
        // ,
        // {
        //     appName: 'Image Labeling',
        //     appDescription: 'Label in data lake stored images.',
        //     appId: 'image-labeling',
        // },
        // {
        //     appName: 'New App',
        //     appDescription: 'An app that has some function',
        //     appId: 'new-app',
        // }
    ];

    constructor() {

    }

    ngOnInit() {

    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    appOpened(appOpen: boolean) {
        this.appOpen = appOpen;
    }

    appClosed() {
        this.appOpen = false;
        this.currentlySelectedApp = '';
    }

    selectApp(appId: string) {
        this.currentlySelectedApp = appId;
    }


}
