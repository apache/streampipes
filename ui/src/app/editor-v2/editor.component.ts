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
import {
    PipelineElementConfig,
    PipelineElementHolder,
    PipelineElementType,
    PipelineElementUnion
} from "./model/editor.model";
import {EditorConstants} from "./constants/editor.constants";
import {PipelineElementTypeUtils} from "./utils/editor.utils";

@Component({
    selector: 'editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.css']
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
    currentModifiedPipelineId: any;

    elementsLoaded = [false, false, false];
    allElementsLoaded: boolean = false;

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
                private pipelineElementService: PipelineElementService) {
    }

    ngOnInit() {
        this.pipelineElementService.getDataProcessors().subscribe(processors => {
            this.availableDataProcessors = processors;
            this.allElements = this.allElements.concat(processors);
            this.afterPipelineElementLoaded(0);
        });
        this.pipelineElementService.getDataSources().subscribe(sources => {
            let allStreams = this.collectStreams(sources);
            console.log(allStreams);
            this.availableDataStreams = allStreams.filter(s => !(s instanceof SpDataSet));
            this.availableDataSets = allStreams
                .filter(s => s instanceof SpDataSet)
                .map(s => s as SpDataSet);
            this.allElements = this.allElements.concat(this.availableDataStreams);

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
            .filter(pe => pe instanceof PipelineElementTypeUtils.toType(this.activeType));
    }
}
