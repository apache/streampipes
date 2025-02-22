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
import { RouterOutlet } from '@angular/router';
import { slideInAnimation } from './animation';
import { Title } from '@angular/platform-browser';
import { AppConstants } from './services/app.constants';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-app-root',
    templateUrl: './app.component.html',
    animations: [slideInAnimation],
})
export class AppComponent implements OnInit {
    constructor(
        private titleService: Title,
        private appConstants: AppConstants,
        private translate: TranslateService,
    ) {
        const supportedLanguages = ['de', 'en'];
        const defaultLanguage = 'en';
        this.translate.addLangs(supportedLanguages);
        this.translate.setDefaultLang(defaultLanguage);
        const browserLang = translate.getBrowserLang();

        this.translate.use(
            supportedLanguages.includes(browserLang)
                ? browserLang
                : defaultLanguage,
        );
    }

    ngOnInit(): void {
        this.titleService.setTitle(this.appConstants.APP_TITLE);
    }

    prepareRoute(outlet: RouterOutlet) {
        return (
            outlet &&
            outlet.activatedRouteData &&
            outlet.activatedRouteData.animation
        );
    }
}
