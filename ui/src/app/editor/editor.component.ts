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
import {
  DataProcessorInvocation,
  DataSinkInvocation,
  DataSourceDescription,
  SpDataSet,
  SpDataStream
} from '../core-model/gen/streampipes-model';
import { PipelineElementService } from '../platform-services/apis/pipeline-element.service';
import {
  PipelineElementConfig,
  PipelineElementIdentifier,
  PipelineElementUnion,
  TabsModel
} from './model/editor.model';
import { PanelType } from '../core-ui/dialog/base-dialog/base-dialog.model';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { DialogService } from '../core-ui/dialog/base-dialog/base-dialog.service';
import { MissingElementsForTutorialComponent } from './dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { ShepherdService } from '../services/tour/shepherd.service';
import { ActivatedRoute } from '@angular/router';
import { EditorConstants } from './constants/editor.constants';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {

    selectedIndex = 1;
    activeType: PipelineElementIdentifier = EditorConstants.DATA_STREAM_IDENTIFIER;
    activeShorthand: string;

    availableDataSets: SpDataSet[] = [];
    availableDataStreams: SpDataStream[] = [];
    availableDataProcessors: DataProcessorInvocation[] = [];
    availableDataSinks: DataSinkInvocation[] = [];

    allElements: PipelineElementUnion[] = [];
    currentElements: (SpDataStream | DataProcessorInvocation | DataSinkInvocation)[] = [];

    rawPipelineModel: PipelineElementConfig[] = [];
    currentModifiedPipelineId: string;

    elementsLoaded = [false, false, false];
    allElementsLoaded = false;

    requiredStreamForTutorialAppId: any = 'org.apache.streampipes.sources.simulator.flowrate1';
    requiredProcessorForTutorialAppId: any = 'org.apache.streampipes.processors.filters.jvm.numericalfilter';
    requiredSinkForTutorialAppId: any = 'org.apache.streampipes.sinks.internal.jvm.dashboard';
    missingElementsForTutorial: any = [];

    pipelineCanvasMaximized = false;

    isTutorialOpen = false;

    tabs: TabsModel[] = [
        {
            title: 'Data Sets',
            type: EditorConstants.DATA_SET_IDENTIFIER,
            shorthand: 'set'
        },
        {
            title: 'Data Streams',
            type: EditorConstants.DATA_STREAM_IDENTIFIER,
            shorthand: 'stream'
        },
        {
            title: 'Data Processors',
            type: EditorConstants.DATA_PROCESSOR_IDENTIFIER,
            shorthand: 'sepa'
        },
        {
            title: 'Data Sinks',
            type: EditorConstants.DATA_SINK_IDENTIFIER,
            shorthand: 'action'
        }
    ];

    constructor(private editorService: EditorService,
                private pipelineElementService: PipelineElementService,
                private authService: AuthService,
                private dialogService: DialogService,
                private shepherdService: ShepherdService,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.activatedRoute.queryParams.subscribe(params => {
            if (params['pipeline']) {
                this.currentModifiedPipelineId = params['pipeline'];
            }
        });
        this.pipelineElementService.getDataProcessors().subscribe(processors => {
            this.availableDataProcessors = processors;
            this.allElements = this.allElements.concat(processors);
            this.afterPipelineElementLoaded(0);
        });
        this.pipelineElementService.getDataStreams().subscribe(streams => {
            // let allStreams = this.collectStreams(sources);
            this.availableDataStreams = streams.filter(s => !(s instanceof SpDataSet));
            this.availableDataSets = streams
                .filter(s => s instanceof SpDataSet)
                .map(s => s as SpDataSet);
            this.allElements = this.allElements.concat(this.availableDataStreams);
            this.allElements = this.allElements.concat(this.availableDataSets);

            this.selectPipelineElements(1);
            this.afterPipelineElementLoaded(1);
        });
        this.pipelineElementService.getDataSinks().subscribe(sinks => {
            this.availableDataSinks = sinks;
            this.allElements = this.allElements.concat(this.availableDataSinks);
            this.afterPipelineElementLoaded(2);
        });

    }

    afterPipelineElementLoaded(index: number) {
        this.elementsLoaded[index] = true;
        if (this.elementsLoaded.every(e => e === true)) {
            this.allElementsLoaded = true;
            this.checkForTutorial();
        }
    }

    checkForTutorial() {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser.showTutorial && !this.isTutorialOpen) {
            if (this.requiredPipelineElementsForTourPresent()) {
                this.isTutorialOpen = true;
                this.dialogService.open(WelcomeTourComponent, {
                    panelType: PanelType.STANDARD_PANEL,
                    title: 'Welcome to StreamPipes',
                    data: {
                        'user': currentUser.displayName
                    }
                });
            }
        }
    }

    collectStreams(sources: DataSourceDescription[]): SpDataStream[] {
        const streams: SpDataStream[] = [];
        sources.forEach(source => {
            source.spDataStreams.forEach(stream => {
                streams.push(stream);
            });
        });
        return streams;
    }

    selectPipelineElements(index: number) {
        this.selectedIndex = index;
        this.activeType = this.tabs[index].type;
        this.activeShorthand = this.tabs[index].shorthand;
        this.currentElements = this.allElements
            .filter(pe => pe['@class'] === this.activeType)
            .sort((a, b) => {
                return a.name.localeCompare(b.name);
            });
        this.shepherdService.trigger('select-' + this.activeShorthand);
    }

    startCreatePipelineTour() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.shepherdService.startCreatePipelineTour();
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredStreamForTourPresent()) {
                this.missingElementsForTutorial.push({'name' : 'Flow Rate 1', 'appId' : this.requiredStreamForTutorialAppId });
            }
            if (!this.requiredProcessorForTourPresent()) {
                this.missingElementsForTutorial.push({'name' : 'Numerical Filter', 'appId' : this.requiredProcessorForTutorialAppId});
            }
            if (!this.requiredSinkForTourPresent()) {
                this.missingElementsForTutorial.push({'name' : 'Dashboard Sink', 'appId' : this.requiredSinkForTutorialAppId});
            }

            this.dialogService.open(MissingElementsForTutorialComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Tutorial requires pipeline elements',
                data: {
                    'missingElementsForTutorial': this.missingElementsForTutorial
                }
            });
        }
    }

    requiredPipelineElementsForTourPresent() {
        return this.requiredStreamForTourPresent() &&
            this.requiredProcessorForTourPresent() &&
            this.requiredSinkForTourPresent();
    }

    requiredStreamForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements,
            this.requiredStreamForTutorialAppId);
    }

    requiredProcessorForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements,
            this.requiredProcessorForTutorialAppId);
    }

    requiredSinkForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements,
            this.requiredSinkForTutorialAppId);
    }

    requiredPeForTourPresent(list, appId) {
        return list && list.some(el => {
            return el.appId === appId;
        });
    }

    togglePipelineCanvasMode(maximize: boolean) {
        this.pipelineCanvasMaximized = maximize;
    }
}
