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

import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {
    ContainerRuntime, ContainerRuntimeUnion, DockerContainerRuntime,
    FieldDeviceAccessResource,
    NodeInfoDescription,
    NvidiaContainerRuntime, UnnamedStreamPipesEntity
} from "../../core-model/gen/streampipes-model";
import {MatSnackBar} from "@angular/material/snack-bar";
import {zip} from "rxjs";
import {DialogService} from "../../core-ui/dialog/base-dialog/base-dialog.service";
import {PipelineService} from "../../platform-services/apis/pipeline.service";
import {DataMarketplaceService} from "../../connect/services/data-marketplace.service";
import {NodeService} from "../../platform-services/apis/node.service";
import {PanelType} from "../../core-ui/dialog/base-dialog/base-dialog.model";
import {NodeConfigurationDetailsComponent} from "./node-configuration-details/node-configuration-details.component";
import {NodeAddDetailsComponent} from "./node-add-details/node-add-details.component";

@Component({
    selector: 'node-configuration',
    encapsulation: ViewEncapsulation.None,
    templateUrl: './node-configuration.component.html',
    styleUrls: ['./node-configuration.component.scss']
})
export class NodeConfigurationComponent implements OnInit{

    loadingCompleted: boolean = false;
    nodes: NodeInfoDescription[];

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
    currentTimestamp: string;

    constructor(private nodeService: NodeService,
                private dataMarketplaceService: DataMarketplaceService,
                private DialogService: DialogService,
                private pipelineService: PipelineService,
                private _snackBar: MatSnackBar) { }

    ngOnInit() {
        this.getNodes();
        this.getDate();
    }

    getNodes() {
        this.nodeService.getNodes().subscribe(response => {
            this.nodes = response;
            this.nodes.forEach(x => {
                // Raspbian is too long -> shorten it
                let os = x.nodeResources.softwareResource.os;
                if (os.includes('Raspbian')) {
                    if (os.includes('buster')) {
                        if (os.includes('10')) {
                            x.nodeResources.softwareResource.os = 'Raspbian 10 (buster)'
                        }
                    }
                }
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
        const containerRuntime = node.nodeResources.softwareResource.containerRuntime;
        if(containerRuntime['@class'] === "org.apache.streampipes.model.node.resources.software.NvidiaContainerRuntime"){
            return true
        } else {
            return false
        }
    }

    async changeNodeState(node: NodeInfoDescription, desiredState: boolean) {
        if (node.active != desiredState) {
            var detectedProcessors = await this.checkNodeForProcessors(node.nodeControllerId);
            if (!detectedProcessors) {
                // No processors detected on this node. This means that no pipeline exists, that hosts processors on
                // this node. However, there can still be active adapters that running on that node that need be checked
                var detectedAdapters = await this.checkNodeForAdapters(node.nodeControllerId);
                if (!detectedAdapters) {
                    // No adapters detected on this node. This means that no adapter was created on this host. Thus
                    // we can safely proceed setting a new state, i.e. active = (true || false) this node
                    node.active = desiredState;
                    let stateService;
                    if (desiredState) {
                        stateService = this.nodeService.activateNode(node.nodeControllerId);
                    } else {
                        stateService = this.nodeService.deactivateNode(node.nodeControllerId);
                    }
                    stateService.subscribe(statusMessage => {
                        if(statusMessage.success) {
                            this.openSnackBar("Node successfully " + (desiredState ? "activated" : "deactivated"))
                        }
                    });
                } else {
                    this.openSnackBar("At least one adapter is executed on that node. Aborted!")
                }
            } else {
                this.openSnackBar("At least one processor is executed on that node. Aborted!")
            }
        }
    }

    checkNodeForProcessors(nodeControllerId: string) {
        return new Promise<boolean>(resolve => {
            var detectedProcessors = false;
            zip(this.pipelineService.getOwnPipelines(),
                this.pipelineService.getSystemPipelines()).subscribe(allPipelines => {
                allPipelines.forEach((pipelines, index) => {
                    pipelines.forEach(pipeline => {
                        detectedProcessors = pipeline.running && pipeline
                            .sepas.some(sepa => sepa.deploymentTargetNodeId === nodeControllerId);
                    })
                })
                resolve(detectedProcessors);
            });
        });
    };

    checkNodeForAdapters(nodeControllerId: string) {
        return new Promise<boolean>(resolve => {
            this.dataMarketplaceService.getAdapters().subscribe(allAdapters => {
                resolve(allAdapters.some(adapter => adapter.deploymentTargetNodeId === nodeControllerId));
            });
        })
    }

    openSnackBar(message: string) {
        this._snackBar.open(message, "close", {
            duration: 3000,
        });
    }

    evict(node: NodeInfoDescription) {
        // TODO: placeholder to migrate processors and adapters to another node
        this.openSnackBar("evict all adapters and processors");
    }

    settings(node: NodeInfoDescription) {
        this.DialogService.open(NodeConfigurationDetailsComponent,{
            panelType: PanelType.SLIDE_IN_PANEL,
            title: "Edit Node configuration",
            data: {
                "node": node
            }
        });
    }

    addNode() {
        this.DialogService.open(NodeAddDetailsComponent,{
            panelType: PanelType.SLIDE_IN_PANEL,
            title: "Add new node",
            data: {}
        });
    }

    getDate() {
        this.currentTimestamp = new Date().toLocaleTimeString(['en-US'],
            { hour: '2-digit', minute: "2-digit", second: "2-digit" });
    }
}