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

import {Component, Inject, OnInit} from "@angular/core";
import {EditorService} from "./services/editor.service";
import {
  DataProcessorInvocation,
  DataSinkInvocation,
  DataSourceDescription,
  SpDataSet,
  SpDataStream
} from "../core-model/gen/streampipes-model";
import {PipelineElementService} from "../platform-services/apis/pipeline-element.service";
import {
  PipelineElementConfig,
  PipelineElementType,
  PipelineElementUnion
} from "./model/editor.model";
import {PipelineElementTypeUtils} from "./utils/editor.utils";
import {AuthStatusService} from "../services/auth-status.service";
import {PanelType} from "../core-ui/dialog/base-dialog/base-dialog.model";
import {WelcomeTourComponent} from "./dialog/welcome-tour/welcome-tour.component";
import {DialogService} from "../core-ui/dialog/base-dialog/base-dialog.service";
import {MissingElementsForTutorialComponent} from "./dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component";
import {ShepherdService} from "../services/tour/shepherd.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {

    selectedIndex: number = 1;
    activeType: PipelineElementType = PipelineElementType.DataStream;

    availableDataSets: SpDataSet[] = [];
    availableDataStreams: SpDataStream[] = [];
    availableDataProcessors: DataProcessorInvocation[] = [];
    availableDataSinks: DataSinkInvocation[] = [];

    allElements: PipelineElementUnion[] = [];
    currentElements: Array<(SpDataStream | DataProcessorInvocation | DataSinkInvocation)> = [];

    rawPipelineModel: PipelineElementConfig[] = [];
    currentModifiedPipelineId: string;

    elementsLoaded = [false, false, false];
    allElementsLoaded: boolean = false;

    minimizedEditorStand: boolean = false;

    requiredStreamForTutorialAppId: any = "org.apache.streampipes.sources.simulator.flowrate1";
    requiredProcessorForTutorialAppId: any = "org.apache.streampipes.processors.filters.jvm.numericalfilter";
    requiredSinkForTutorialAppId: any = "org.apache.streampipes.sinks.internal.jvm.dashboard";
    missingElementsForTutorial: any = [];

    isTutorialOpen: boolean = false;

    tabs = [
        {
            title: 'Data Sets',
            type: PipelineElementType.DataSet
        },
        {
            title: 'Data Streams',
            type: PipelineElementType.DataStream
        },
        {
            title: 'Data Processors',
            type: PipelineElementType.DataProcessor
        },
        {
            title: 'Data Sinks',
            type: PipelineElementType.DataSink
        }
    ];

    constructor(private editorService: EditorService,
                private pipelineElementService: PipelineElementService,
                private AuthStatusService: AuthStatusService,
                private dialogService: DialogService,
                private shepherdService: ShepherdService,
                private ActivatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.ActivatedRoute.queryParams.subscribe(params => {
            if (params['pipeline']) {
                this.currentModifiedPipelineId = params['pipeline'];
            }
        });
        this.pipelineElementService.getDataProcessors().subscribe(processors => {
            this.availableDataProcessors = processors;
            this.allElements = this.allElements.concat(processors);
            this.afterPipelineElementLoaded(0);
        });
        this.pipelineElementService.getDataSources().subscribe(sources => {
            let allStreams = this.collectStreams(sources);
            this.availableDataStreams = allStreams.filter(s => !(s instanceof SpDataSet));
            this.availableDataSets = allStreams
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
        })

    }

    afterPipelineElementLoaded(index: number) {
        this.elementsLoaded[index] = true;
        if (this.elementsLoaded.every(e => e === true)) {
            this.allElementsLoaded = true;
            this.checkForTutorial();
        }
    }

    checkForTutorial() {
        if (this.AuthStatusService.email != undefined) {
            this.editorService
                .getUserDetails()
                .subscribe(user => {
                    if ((!user.hideTutorial) && !this.isTutorialOpen) {
                        if (this.requiredPipelineElementsForTourPresent()) {
                            this.isTutorialOpen = true;
                            this.dialogService.open(WelcomeTourComponent, {
                                panelType: PanelType.STANDARD_PANEL,
                                title: "Welcome to StreamPipes",
                                data: {
                                    "user": user
                                }
                            });
                        }
                    }
                });
        }
    }

    collectStreams(sources: Array<DataSourceDescription>): SpDataStream[] {
        let streams: SpDataStream[] = [];
        sources.forEach(source => {
            source.spDataStreams.forEach(stream => {
                streams.push(stream);
            });
        });
        return streams;
    }

    selectPipelineElements(index : number) {
        this.selectedIndex = index;
        this.activeType = this.tabs[index].type;
        this.currentElements = this.allElements
            .filter(pe => pe instanceof PipelineElementTypeUtils.toType(this.activeType))
            .sort((a, b) => {
                return a.name.localeCompare(b.name);
            });
        this.shepherdService.trigger("select-" +PipelineElementTypeUtils.toCssShortHand(this.activeType));
    }

    toggleEditorStand() {
        this.minimizedEditorStand = !this.minimizedEditorStand;
    }

    startCreatePipelineTour() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.shepherdService.startCreatePipelineTour();
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredStreamForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Flow Rate 1", "appId" : this.requiredStreamForTutorialAppId });
            }
            if (!this.requiredProcessorForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Numerical Filter", "appId" : this.requiredProcessorForTutorialAppId});
            }
            if (!this.requiredSinkForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Dashboard Sink", "appId" : this.requiredSinkForTutorialAppId});
            }

            this.dialogService.open(MissingElementsForTutorialComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: "Tutorial requires pipeline elements",
                data: {
                    "missingElementsForTutorial": this.missingElementsForTutorial
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
}
