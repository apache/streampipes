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
import { EditorService } from './services/editor.service';
import { PipelineElementService } from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from './model/editor.model';
import {
    CurrentUserService,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { MissingElementsForTutorialComponent } from './dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { ShepherdService } from '../services/tour/shepherd.service';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { zip } from 'rxjs';
import { AppConstants } from '../services/app.constants';
import { SpPipelineRoutes } from '../pipelines/pipelines.routes';

@Component({
    selector: 'sp-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss'],
})
export class EditorComponent implements OnInit {
    allElements: PipelineElementUnion[] = [];

    rawPipelineModel: PipelineElementConfig[] = [];
    currentModifiedPipelineId: string;

    allElementsLoaded = false;

    requiredStreamForTutorialAppId: any =
        'org.apache.streampipes.sources.simulator.flowrate1';
    requiredProcessorForTutorialAppId: any =
        'org.apache.streampipes.processors.filters.jvm.numericalfilter';
    requiredSinkForTutorialAppId: any =
        'org.apache.streampipes.sinks.internal.jvm.datalake';
    missingElementsForTutorial: any = [];

    isTutorialOpen = false;

    constructor(
        private editorService: EditorService,
        private pipelineElementService: PipelineElementService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private dialogService: DialogService,
        private shepherdService: ShepherdService,
        private activatedRoute: ActivatedRoute,
        private appConstants: AppConstants,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.activatedRoute.params.subscribe(params => {
            if (params.pipelineId) {
                this.currentModifiedPipelineId = params.pipelineId;
            } else {
                this.breadcrumbService.updateBreadcrumb([
                    SpPipelineRoutes.BASE,
                    { label: 'New Pipeline' },
                ]);
            }
        });
        zip(
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ).subscribe(response => {
            this.allElements = this.allElements
                .concat(response[0])
                .concat(response[1])
                .concat(response[2])
                .sort((a, b) => {
                    return a.name.localeCompare(b.name);
                });
            this.allElementsLoaded = true;
            this.checkForTutorial();
        });
    }

    checkForTutorial() {
        const currentUser = this.currentUserService.getCurrentUser();
        if (currentUser.showTutorial && !this.isTutorialOpen) {
            if (this.requiredPipelineElementsForTourPresent()) {
                this.isTutorialOpen = true;
                this.dialogService.open(WelcomeTourComponent, {
                    panelType: PanelType.STANDARD_PANEL,
                    title: 'Welcome to ' + this.appConstants.APP_NAME,
                    data: {
                        userInfo: currentUser,
                    },
                });
            }
        }
    }

    startCreatePipelineTour() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.shepherdService.startCreatePipelineTour();
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredStreamForTourPresent()) {
                this.missingElementsForTutorial.push({
                    name: 'Flow Rate 1',
                    appId: this.requiredStreamForTutorialAppId,
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
            this.requiredStreamForTourPresent() &&
            this.requiredProcessorForTourPresent() &&
            this.requiredSinkForTourPresent()
        );
    }

    requiredStreamForTourPresent() {
        return this.requiredPeForTourPresent(
            this.allElements,
            this.requiredStreamForTutorialAppId,
        );
    }

    requiredProcessorForTourPresent() {
        return this.requiredPeForTourPresent(
            this.allElements,
            this.requiredProcessorForTutorialAppId,
        );
    }

    requiredSinkForTourPresent() {
        return this.requiredPeForTourPresent(
            this.allElements,
            this.requiredSinkForTutorialAppId,
        );
    }

    requiredPeForTourPresent(list, appId) {
        return (
            list &&
            list.some(el => {
                return el.appId === appId;
            })
        );
    }
}
