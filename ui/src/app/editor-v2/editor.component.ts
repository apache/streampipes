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

import {Component, OnInit} from "@angular/core";
import {EditorService} from "./services/editor.service";
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    DataSourceDescription, SpDataSet,
    SpDataStream
} from "../core-model/gen/streampipes-model";
import {PipelineElementService} from "../platform-services/apis/pipeline-element.service";
import {PipelineElementHolder} from "./model/editor.model";

@Component({
    selector: 'editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

    static readonly DATA_STREAM_IDENTIFIER = "org.apache.streampipes.model.SpDataStream";
    static readonly DATA_SET_IDENTIFIER = "org.apache.streampipes.model.SpDataSet";
    static readonly DATA_PROCESSOR_IDENTIFIER = "org.apache.streampipes.model.graph.DataProcessorInvocation";
    static readonly DATA_SINK_IDENTIFIER = "org.apache.streampipes.model.graph.DataSinkInvoation";

    selectedIndex: number = 1;
    activeType: string = EditorComponent.DATA_STREAM_IDENTIFIER;

    availableDataStreams: SpDataStream[] = [];
    availableDataProcessors: DataProcessorInvocation[] = [];
    availableDataSinks: DataSinkInvocation[] = [];

    allElements: PipelineElementHolder[] = [];
    currentElements: (SpDataStream | DataProcessorInvocation | DataSinkInvocation)[] = [];

    rawPipelineModel: any = [];
    currentModifiedPipelineId: any;

    tabs = [
        {
            title: 'Data Sets',
            type: EditorComponent.DATA_SET_IDENTIFIER
        },
        {
            title: 'Data Streams',
            type: EditorComponent.DATA_STREAM_IDENTIFIER
        },
        {
            title: 'Data Processors',
            type: EditorComponent.DATA_PROCESSOR_IDENTIFIER
        },
        {
            title: 'Data Sinks',
            type: EditorComponent.DATA_SINK_IDENTIFIER
        }
    ];

    constructor(private editorService: EditorService,
                private pipelineElementService: PipelineElementService) {
    }

    ngOnInit() {
        this.pipelineElementService.getDataProcessors().subscribe(processors => {
            this.availableDataProcessors = processors;
            this.allElements[EditorComponent.DATA_PROCESSOR_IDENTIFIER] = this.availableDataProcessors;
        });
        this.pipelineElementService.getDataSources().subscribe(sources => {
            this.availableDataStreams = this.collectStreams(sources);
            this.allElements[EditorComponent.DATA_STREAM_IDENTIFIER] = this.availableDataStreams
                .filter(ds => ds["@class"] == EditorComponent.DATA_STREAM_IDENTIFIER );
            this.allElements[EditorComponent.DATA_SET_IDENTIFIER] = this.availableDataStreams
                .filter(ds => ds["@class"] == EditorComponent.DATA_SET_IDENTIFIER );
            this.currentElements = this.allElements[EditorComponent.DATA_STREAM_IDENTIFIER];
        });
        this.pipelineElementService.getDataSinks().subscribe(sinks => {
            this.availableDataSinks = sinks;
            this.allElements[EditorComponent.DATA_SINK_IDENTIFIER] = this.availableDataSinks;
        })

    }

    private collectStreams(sources: Array<DataSourceDescription>) {
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
        this.currentElements = this.allElements[this.activeType];
        this.makeDraggable();
    }

    makeDraggable() {
        console.log("makuing drabbg");
        (<any>$('.draggable-icon')).draggable({
            revert: 'invalid',
            helper: 'clone',
            stack: '.draggable-icon',
            start: function (el, ui) {
                ui.helper.appendTo('#content');
                $('#outerAssemblyArea').css('border', '3px dashed #39b54a');
            },
            stop: function (el, ui) {
                $('#outerAssemblyArea').css('border', '3px solid rgb(156, 156, 156)');
            }
        });
    };

}
