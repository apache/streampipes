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

import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {PipelineElementStatus, PipelineOperationStatus} from "../../../core-model/gen/streampipes-model";
import {Component, Input} from "@angular/core";


@Component({
    selector: 'pipeline-status-dialog',
    templateUrl: './pipeline-status-dialog.component.html',
    styleUrls: ['./pipeline-status-dialog.component.scss']
})
export class PipelineStatusDialogComponent {

    statusDetailsVisible: any;
    elementStati : Array<Array<PipelineElementStatus>>;

    @Input()
    pipelineOperationStatus: PipelineOperationStatus;

    constructor(private DialogRef: DialogRef<PipelineStatusDialogComponent>) {
        this.statusDetailsVisible = false;
        this.elementStati = [];
    }

    ngOnInit(){
        let nodes = [];
        this.pipelineOperationStatus.elementStatus.forEach(stat => {
            if (!nodes.includes(stat.elementNode)){
                nodes.push(stat.elementNode)
            }
        })

        nodes.forEach(node =>{
            let nodeStati = [];
            this.pipelineOperationStatus.elementStatus.forEach(stat =>{
                if(stat.elementNode == node){
                    nodeStati.push(stat);
                }
            })
            this.elementStati.push(nodeStati);
        })
    }

    close() {
        this.DialogRef.close();
    };

    toggleStatusDetailsVisible() {
        this.statusDetailsVisible = !(this.statusDetailsVisible);
    }
}