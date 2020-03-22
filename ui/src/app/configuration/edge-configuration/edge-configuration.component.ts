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

import {Component} from "@angular/core";
import {ConfigurationService} from "../shared/configuration.service";
import {NodeInfo} from "../model/NodeInfo.model";

@Component({
    selector: 'edge-configuration',
    templateUrl: './edge-configuration.component.html',
    styleUrls: ['./edge-configuration.component.css']
})
export class EdgeConfigurationComponent {

    loadingCompleted: boolean = false;
    availableEdgeNodes: Array<NodeInfo>;

    constructor(private configurationService: ConfigurationService) {

    }

    ngOnInit() {
        this.getAvailableEdgeNodes();
    }

    getAvailableEdgeNodes() {
        this.configurationService.getAvailableEdgeNodes().subscribe(response => {
            this.availableEdgeNodes = response;
            this.availableEdgeNodes.forEach(x => {
                x.nodeResources.hardwareResource.memory.memTotal = this.bytesToGB(x.nodeResources.hardwareResource.memory.memTotal);
                x.nodeResources.hardwareResource.disk.diskTotal = this.bytesToGB(x.nodeResources.hardwareResource.disk.diskTotal)
            });
        })
    }

    bytesToGB(bytes) {
        var b = 1;
        var kb = b * 1024;
        var mb = kb * 1024;
        var gb = mb * 1024;
        return (Math.round((bytes / gb) * 100) / 100);
    };

}