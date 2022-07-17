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
import { DomSanitizer } from '@angular/platform-browser';
import { HomeService } from './home.service';
import { Router } from '@angular/router';
import { AppConstants } from '../services/app.constants';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

    serviceLinks = [];

    constructor(private homeService: HomeService,
                private sanitizer: DomSanitizer,
                private router: Router,
                public appConstants: AppConstants,
                private breadcrumbService: SpBreadcrumbService) {
        this.serviceLinks = this.homeService.getFilteredServiceLinks();
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb([]);
    }

    getBackground(url) {
        return this.sanitizer.bypassSecurityTrustStyle(`url(${url})`);
    }

    openLink(link) {
        if (link.link.newWindow) {
            window.open(link.link.value);
        } else {
            this.router.navigate([link.link.value]);
        }
    }

}
