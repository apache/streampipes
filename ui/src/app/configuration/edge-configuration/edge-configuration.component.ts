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
import {
    FieldDeviceAccessResource,
    NodeInfoDescription, NvidiaContainerRuntime
} from "../../core-model/gen/streampipes-model";

@Component({
    selector: 'edge-configuration',
    templateUrl: './edge-configuration.component.html',
    styleUrls: ['./edge-configuration.component.css']
})
export class EdgeConfigurationComponent {

    loadingCompleted: boolean = false;
    availableEdgeNodes: NodeInfoDescription[];

    os: String;
    serverVersion: String;
    availableGPU: boolean = false;
    cpuCores: number;
    cpuArch: string;
    memTotal: number;
    diskTotal: number;
    gpuCores: number;
    gpuType: string;
    locationTags: String[];
    fieldDevices: FieldDeviceAccessResource[];

    constructor(private configurationService: ConfigurationService) {

    }

    ngOnInit() {
        this.getAvailableEdgeNodes();
    }

    getAvailableEdgeNodes() {
        this.configurationService.getAvailableEdgeNodes().subscribe(response => {
            this.availableEdgeNodes = response;
            this.availableEdgeNodes.forEach(x => {
                // Raspbian is too long -> shorten it
                let os = x.nodeResources.softwareResource.os;
                if (os.includes('Raspbian')) {
                    if (os.includes('buster')) {
                        if (os.includes('10')) {
                            x.nodeResources.softwareResource.os = 'Raspbian 10 (buster)'
                        }
                    }
                }
                let memTotal = x.nodeResources.hardwareResource.memory.memTotal;
                let diskTotal = x.nodeResources.hardwareResource.disk.diskTotal;
                x.nodeResources.hardwareResource.memory.memTotal = this.bytesToGB(memTotal);
                x.nodeResources.hardwareResource.disk.diskTotal = this.bytesToGB(diskTotal)
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

    nvidiaRuntime(node: NodeInfoDescription) {
        let nvidiaRuntime = false;
        if (node.nodeResources.softwareResource.containerRuntime instanceof NvidiaContainerRuntime) {
            nvidiaRuntime = true;
        }
        return nvidiaRuntime;
    }
}