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

import { Component, OnInit, ViewChild } from '@angular/core';
import { BaseRuntimeResolvableInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-input';
import {
    RuntimeResolvableTreeInputStaticProperty,
    StaticPropertyUnion,
    TreeInputNode,
} from '@streampipes/platform-services';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';
import { UntypedFormControl } from '@angular/forms';
import { StaticTreeInputServiceService } from './static-tree-input-service.service';
import { StaticTreeInputBrowseNodesComponent } from './static-tree-input-browse-nodes/static-tree-input-browse-nodes.component';

@Component({
    selector: 'sp-static-runtime-resolvable-tree-input',
    templateUrl: './static-runtime-resolvable-tree-input.component.html',
    styleUrls: ['./static-runtime-resolvable-tree-input.component.scss'],
})
export class StaticRuntimeResolvableTreeInputComponent
    extends BaseRuntimeResolvableInput<RuntimeResolvableTreeInputStaticProperty>
    implements OnInit
{
    nodeDetails: TreeInputNode;

    editorMode: 'tree' | 'text' = 'tree';

    // The following two arrays store the fetched nodes from the backend to
    // present them to the user in the UI. For performance reasons, the nodes
    // should not be stored in the static property object
    latestFetchedNodes = [];
    nodes = [];

    @ViewChild('staticTreeInputBrowseNodesComponent')
    private staticTreeInputBrowseNodesComponent: StaticTreeInputBrowseNodesComponent;

    constructor(
        runtimeResolvableService: RuntimeResolvableService,
        private staticTreeInputServiceService: StaticTreeInputServiceService,
    ) {
        super(runtimeResolvableService);
    }

    ngOnInit(): void {
        this.resetStaticPropertyState();

        if (
            this.staticProperty.nodes.length === 0 &&
            (!this.staticProperty.dependsOn ||
                this.staticProperty.dependsOn.length === 0)
        ) {
            this.loadOptionsFromRestApi();
        } else if (this.staticProperty.nodes.length > 0) {
            this.staticTreeInputBrowseNodesComponent?.updateNodes(
                this.staticProperty.nodes,
            );
            this.showOptions = true;
        }
        super.onInit();
        this.parentForm.addControl(
            this.staticProperty.internalName,
            new UntypedFormControl(this.staticProperty.nodes, []),
        );
    }

    parse(
        staticProperty: StaticPropertyUnion,
    ): RuntimeResolvableTreeInputStaticProperty {
        return staticProperty as RuntimeResolvableTreeInputStaticProperty;
    }

    afterOptionsLoaded(
        staticProperty: RuntimeResolvableTreeInputStaticProperty,
        node: TreeInputNode,
    ) {
        if (
            staticProperty.latestFetchedNodes &&
            staticProperty.latestFetchedNodes.length > 0
        ) {
            this.latestFetchedNodes = staticProperty.latestFetchedNodes;
            if (node) {
                node.children = staticProperty.latestFetchedNodes;
            }
        } else {
            this.nodes = staticProperty.nodes;
            this.staticTreeInputBrowseNodesComponent?.updateNodes(this.nodes);
        }
        this.staticTreeInputBrowseNodesComponent?.refreshTree();

        this.performValidation();
    }

    performValidation() {
        let error = { error: true };
        if (this.anyNodeSelected()) {
            error = undefined;
        }
        this.parentForm.controls[this.staticProperty.internalName].setErrors(
            error,
        );
    }

    anyNodeSelected(): boolean {
        return this.staticProperty.selectedNodesInternalNames.length > 0;
    }

    anySelected(node: TreeInputNode): boolean {
        if (node.selected) {
            return true;
        } else {
            return node.children.find(n => this.anySelected(n)) !== undefined;
        }
    }

    afterErrorReceived() {
        this.staticProperty.nodes = [];
        this.staticTreeInputBrowseNodesComponent?.updateNodes([]);
        this.performValidation();
    }

    showNodeDetails(node: TreeInputNode) {
        this.nodeDetails = node;
    }

    resetOptionsAndReload(): void {
        this.staticProperty.nextBaseNodeToResolve = undefined;
        this.staticProperty.selectedNodesInternalNames = [];
        this.staticProperty.latestFetchedNodes = [];
        this.staticTreeInputBrowseNodesComponent?.updateNodes([]);
        this.loadOptionsFromRestApi();
    }

    reload(): void {
        this.loadOptionsFromRestApi();
    }

    removeSelectedNode(selectedNodeInternalId: string): void {
        const index = this.staticTreeInputServiceService.getSelectedNodeIndex(
            this.staticProperty,
            selectedNodeInternalId,
        );
        this.staticProperty.selectedNodesInternalNames.splice(index, 1);
    }

    changeEditorMode(mode: 'tree' | 'text') {
        this.editorMode = mode;

        if (mode === 'tree') {
            this.resetStaticPropertyStateAndReload();
        }
    }

    /**
     * The static property keeps the state of the last fetched nodes to be able
     * to set the subtree to the right node. When a user switches the editor
     * this state should be reset
     */
    private resetStaticPropertyStateAndReload() {
        this.resetStaticPropertyState();
        this.reload();
    }

    private resetStaticPropertyState(): void {
        this.staticProperty.latestFetchedNodes = [];
        this.latestFetchedNodes = [];
        this.staticProperty.nextBaseNodeToResolve = undefined;
    }
}
