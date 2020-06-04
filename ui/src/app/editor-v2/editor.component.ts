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
import {DataStreamDescription} from "../connect/model/DataStreamDescription";
import {DataSinkInvocation} from "../connect/model/DataSinkInvocation";
import {DataSourceDescription} from "../connect/model/DataSourceDescription";
import {DataProcessorInvocation} from "../core-model/gen/streampipes-model";

@Component({
    selector: 'editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

    selectedIndex: number = 1;
    activeType: string = "stream";

    availableDataStreams: DataStreamDescription[] = [];
    availableDataProcessors: DataProcessorInvocation[] = [];
    availableDataSinks: DataSinkInvocation[] = [];

    allElements: any = [];
    currentElements: any = [];

    tabs = [
        {
            title: 'Data Sets',
            type: 'set',
        },
        {
            title: 'Data Streams',
            type: 'stream',
        },
        {
            title: 'Data Processors',
            type: 'processor',
        },
        {
            title: 'Data Sinks',
            type: 'sink',
        }
    ];

    constructor(private editorService: EditorService) {
    }

    public ngOnInit() {
        this.editorService.getDataProcessors().subscribe(processors => {
            //processors.forEach(processor => processor.type = "processor");
            this.availableDataProcessors = processors;
            console.log(processors);
            this.allElements["processor"] = this.availableDataProcessors;
        });
        // this.editorService.getDataSources().subscribe(sources => {
        //     this.availableDataStreams = this.collectStreams(sources);
        //     this.allElements["stream"] = this.availableDataStreams;
        // });
        // this.editorService.getDataSinks().subscribe(sinks => {
        //     sinks.forEach(processor => processor.type = "sink");
        //     this.availableDataSinks = sinks;
        //     this.allElements["sink"] = this.availableDataSinks;
        // })
    }

    private collectStreams(sources: Array<DataSourceDescription>) {
        let streams: DataStreamDescription[] = [];
        sources.forEach(source => {
            if (!Array.isArray(source.spDataStreams)) {
                source.spDataStreams = [source.spDataStreams];
            }
            source.spDataStreams.forEach(stream => {
                stream.type = "stream";
                streams.push(stream);
            });
        });
        return streams;
    }


    selectPipelineElements(index : number) {
        this.selectedIndex = index;
        this.activeType = this.tabs[index].type;
        this.currentElements = this.allElements[this.activeType];
    }

}
