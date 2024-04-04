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
import { HomeService } from './home.service';
import { Router } from '@angular/router';
import { AppConstants } from '../services/app.constants';
import {
    CurrentUserService,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { UserRole } from '../_enums/user-role.enum';
import { MissingElementsForTutorialComponent } from '../editor/dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { ShepherdService } from '../services/tour/shepherd.service';
import {
    AdapterDescription,
    AdapterService,
    NamedStreamPipesEntity,
    Pipeline,
    PipelineElementService,
    PipelineService,
} from '@streampipes/platform-services';
import { zip } from 'rxjs';
import { UserInfo } from '../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
    serviceLinks = [];
    showStatus = false;

    availablePipelineElements: NamedStreamPipesEntity[] = [];
    availableAdapters: AdapterDescription[] = [];
    availablePipelines: Pipeline[] = [];
    runningPipelines: Pipeline[] = [];

    requiredAdapterForTutorialAppId: any =
        'org.apache.streampipes.connect.iiot.adapters.simulator.machine';
    requiredProcessorForTutorialAppId: any =
        'org.apache.streampipes.processors.filters.jvm.numericalfilter';
    requiredSinkForTutorialAppId: any =
        'org.apache.streampipes.sinks.internal.jvm.datalake';
    missingElementsForTutorial: any = [];

    isTutorialOpen = false;
    currentUser: UserInfo;

    constructor(
        private homeService: HomeService,
        private currentUserService: CurrentUserService,
        private router: Router,
        public appConstants: AppConstants,
        private breadcrumbService: SpBreadcrumbService,
        private dialogService: DialogService,
        private shepherdService: ShepherdService,
        private pipelineService: PipelineService,
        private pipelineElementService: PipelineElementService,
        private adapterService: AdapterService,
    ) {
        this.serviceLinks = this.homeService.getFilteredServiceLinks();
    }

    ngOnInit() {
        this.currentUser = this.currentUserService.getCurrentUser();
        const isAdmin = this.hasRole(UserRole.ROLE_ADMIN);
        this.showStatus = isAdmin || this.hasRole(UserRole.ROLE_PIPELINE_ADMIN);
        if (isAdmin) {
            this.loadResources();
        }
        this.breadcrumbService.updateBreadcrumb([]);
    }

    hasRole(role: UserRole): boolean {
        return this.currentUser.roles.indexOf(role) > -1;
    }

    openLink(link) {
        if (link.link.newWindow) {
            window.open(link.link.value);
        } else {
            this.router.navigate([link.link.value]);
        }
    }

    checkForTutorial() {
        if (this.currentUser.showTutorial) {
            if (this.requiredPipelineElementsForTourPresent()) {
                this.isTutorialOpen = true;
                const dialogRef = this.dialogService.open(
                    WelcomeTourComponent,
                    {
                        panelType: PanelType.STANDARD_PANEL,
                        title: 'Welcome to ' + this.appConstants.APP_NAME,
                        data: {
                            userInfo: this.currentUser,
                        },
                    },
                );
                dialogRef.afterClosed().subscribe(startTutorial => {
                    if (startTutorial) {
                        this.startTutorial();
                    }
                });
            }
        }
    }

    startTutorial() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.router.navigate(['connect']).then(() => {
                this.shepherdService.startAdapterTour();
            });
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredAdapterForTutorialAppId()) {
                this.missingElementsForTutorial.push({
                    name: 'Machine Data Simulator',
                    appId: this.requiredAdapterForTutorialAppId,
                });
            }
            if (!this.requiredProcessorForTourPresent()) {
                this.missingElementsForTutorial.push({
                    name: 'Numerical Filter',
                    appId: this.requiredProcessorForTutorialAppId,
                });
            }
            if (!this.requiredSinkForTourPresent()) {
                this.missingElementsForTutorial.push({
                    name: 'Dashboard Sink',
                    appId: this.requiredSinkForTutorialAppId,
                });
            }

            this.dialogService.open(MissingElementsForTutorialComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Tutorial requires pipeline elements',
                data: {
                    missingElementsForTutorial: this.missingElementsForTutorial,
                },
            });
        }
    }

    requiredPipelineElementsForTourPresent() {
        return (
            this.requiredAdapterForTourPresent() &&
            this.requiredProcessorForTourPresent() &&
            this.requiredSinkForTourPresent()
        );
    }

    requiredAdapterForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availableAdapters,
            this.requiredAdapterForTutorialAppId,
        );
    }

    requiredProcessorForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availablePipelineElements,
            this.requiredProcessorForTutorialAppId,
        );
    }

    requiredSinkForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availablePipelineElements,
            this.requiredSinkForTutorialAppId,
        );
    }

    requiredPeForTourPresent(list: NamedStreamPipesEntity[], appId: string) {
        return (
            list &&
            list.some(el => {
                return el.appId === appId;
            })
        );
    }

    loadResources(): void {
        zip(
            this.pipelineService.getPipelines(),
            this.adapterService.getAdapterDescriptions(),
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ).subscribe(res => {
            this.availablePipelines = res[0];
            this.runningPipelines = res[0].filter(p => p.running);
            this.availableAdapters = res[1];
            this.availablePipelineElements = this.availablePipelineElements
                .concat(...res[2])
                .concat(...res[3])
                .concat(...res[4]);

            this.checkForTutorial();
        });
    }
}
