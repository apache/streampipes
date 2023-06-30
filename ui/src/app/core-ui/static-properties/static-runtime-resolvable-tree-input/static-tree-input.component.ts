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
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTree, MatTreeNestedDataSource } from '@angular/material/tree';
import { UntypedFormControl } from '@angular/forms';

@Component({
    selector: 'sp-runtime-resolvable-tree-input',
    templateUrl: './static-tree-input.component.html',
    styleUrls: ['./static-tree-input.component.scss'],
})
export class StaticRuntimeResolvableTreeInputComponent
    extends BaseRuntimeResolvableInput<RuntimeResolvableTreeInputStaticProperty>
    implements OnInit
{
    treeControl = new NestedTreeControl<TreeInputNode>(node => node.children);
    dataSource = new MatTreeNestedDataSource<TreeInputNode>();

    selectedNodeMetadata: Record<string, string>;
    selectedNodeId: string;

    @ViewChild('tree') tree: MatTree<TreeInputNode>;

    constructor(runtimeResolvableService: RuntimeResolvableService) {
        super(runtimeResolvableService);
    }

    hasChild = (_: number, node: TreeInputNode) => !node.dataNode;

    ngOnInit(): void {
        this.treeControl = new NestedTreeControl<TreeInputNode>(
            node => node.children,
        );
        this.dataSource = new MatTreeNestedDataSource<TreeInputNode>();

        if (
            this.staticProperty.nodes.length === 0 &&
            (!this.staticProperty.dependsOn ||
                this.staticProperty.dependsOn.length === 0)
        ) {
            this.loadOptionsFromRestApi();
        } else if (this.staticProperty.nodes.length > 0) {
            this.dataSource.data = this.staticProperty.nodes;
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
            this.staticProperty.latestFetchedNodes =
                staticProperty.latestFetchedNodes;
            node.children = staticProperty.latestFetchedNodes;
        } else {
            this.staticProperty.nodes = staticProperty.nodes;
            this.dataSource.data = this.staticProperty.nodes;
        }
        const data = this.dataSource.data.slice();
        this.dataSource.data = null;
        this.dataSource = new MatTreeNestedDataSource<TreeInputNode>();
        this.dataSource.data = [...data];
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
        this.dataSource.data = [];
        this.performValidation();
    }

    resetOptionsAndReload(): void {
        this.staticProperty.nextBaseNodeToResolve = undefined;
        this.staticProperty.selectedNodesInternalNames = [];
        this.staticProperty.latestFetchedNodes = [];
        this.dataSource.data = [];
        this.loadOptionsFromRestApi();
    }

    loadChildren(node: TreeInputNode, expanded: boolean): void {
        this.staticProperty.nextBaseNodeToResolve = node.internalNodeName;
        if (expanded) {
            this.loadOptionsFromRestApi(node);
        }
    }

    addNode(node: TreeInputNode) {
        node.selected = true;
        this.staticProperty.selectedNodesInternalNames.push(
            node.internalNodeName,
        );
        this.performValidation();
    }

    removeNode(node: TreeInputNode) {
        node.selected = false;
        const index = this.getSelectedNodeIndex(node.internalNodeName);
        this.staticProperty.selectedNodesInternalNames.splice(index, 1);
        this.performValidation();
    }

    removeSelectedNode(selectedNodeInternalId: string): void {
        const index = this.getSelectedNodeIndex(selectedNodeInternalId);
        this.staticProperty.selectedNodesInternalNames.splice(index, 1);
    }

    isNodeSelected(node: TreeInputNode) {
        return this.getSelectedNodeIndex(node.internalNodeName) > -1;
    }

    getSelectedNodeIndex(internalNodeName: string) {
        return this.staticProperty.selectedNodesInternalNames.indexOf(
            internalNodeName,
        );
    }

    hasDataChildren(node: TreeInputNode) {
        return (
            node.children.length > 0 &&
            node.children.find(c => c.dataNode) !== undefined
        );
    }
}
