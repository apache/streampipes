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
import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
    inject,
} from '@angular/core';
import {
    RuntimeResolvableTreeInputStaticProperty,
    TreeInputNode,
} from '@streampipes/platform-services';
import {
    MatNestedTreeNode,
    MatTree,
    MatTreeNestedDataSource,
    MatTreeNode,
    MatTreeNodeDef,
    MatTreeNodeOutlet,
    MatTreeNodeToggle,
} from '@angular/material/tree';
import { StaticTreeInputServiceService } from '../static-tree-input-service.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { NgClass } from '@angular/common';
import { DefaultClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-static-tree-input-browse-nodes',
    templateUrl: './static-tree-input-browse-nodes.component.html',
    styleUrls: [
        './static-tree-input-browse-nodes.component.scss',
        '../static-runtime-resolvable-tree-input.component.scss',
    ],
    imports: [
        LayoutAlignDirective,
        LayoutDirective,
        FlexDirective,
        MatIconButton,
        MatIcon,
        MatTree,
        NgClass,
        DefaultClassDirective,
        MatTreeNodeDef,
        MatTreeNode,
        MatTreeNodeToggle,
        MatTooltip,
        MatNestedTreeNode,
        MatTreeNodeOutlet,
        TranslatePipe,
    ],
})
export class StaticTreeInputBrowseNodesComponent implements OnInit {
    private staticTreeInputServiceService = inject(
        StaticTreeInputServiceService,
    );

    @Input()
    staticProperty: RuntimeResolvableTreeInputStaticProperty;

    @Output()
    showNodeDetailsEmitter: EventEmitter<TreeInputNode> =
        new EventEmitter<TreeInputNode>();

    @Output()
    performValidationEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    loadOptionsFromRestApiEmitter: EventEmitter<TreeInputNode> =
        new EventEmitter<TreeInputNode>();

    @ViewChild('tree')
    tree: MatTree<TreeInputNode>;

    largeView = false;
    childrenAccessor = node => node.children;
    dataSource = new MatTreeNestedDataSource<TreeInputNode>();

    selectedNodeId: string;

    hasChild = (_: number, node: TreeInputNode) => !node.dataNode;

    ngOnInit(): void {
        this.dataSource = new MatTreeNestedDataSource<TreeInputNode>();
        this.dataSource.data = [];
    }

    updateNodes(nodes: TreeInputNode[]) {
        this.dataSource.data = nodes || [];
    }

    refreshTree() {
        const data = this.dataSource.data.slice();
        this.dataSource.data = [];
        this.dataSource.data = [...data];
    }

    loadChildren(node: TreeInputNode, expanded: boolean): void {
        this.staticProperty.nextBaseNodeToResolve = node.internalNodeName;
        if (expanded) {
            this.loadOptionsFromRestApiEmitter.emit(node);
        }
    }

    addNode(node: TreeInputNode) {
        node.selected = true;
        this.staticProperty.selectedNodesInternalNames.push(
            node.internalNodeName,
        );
        this.performValidationEmitter.emit();
    }

    addAllDirectChildren(node: TreeInputNode) {
        node.children.forEach(child => {
            if (child.dataNode && !this.existsSelectedNode(child)) {
                this.staticProperty.selectedNodesInternalNames.push(
                    child.internalNodeName,
                );
            }
        });
        this.performValidationEmitter.emit();
    }

    removeNode(node: TreeInputNode) {
        node.selected = false;
        const index = this.staticTreeInputServiceService.getSelectedNodeIndex(
            this.staticProperty,
            node.internalNodeName,
        );
        this.staticProperty.selectedNodesInternalNames.splice(index, 1);
        this.performValidationEmitter.emit();
    }

    isNodeSelected(node: TreeInputNode) {
        return (
            this.staticTreeInputServiceService.getSelectedNodeIndex(
                this.staticProperty,
                node.internalNodeName,
            ) > -1
        );
    }

    showNodeDetails(node: TreeInputNode) {
        this.selectedNodeId = node.internalNodeName;
        this.showNodeDetailsEmitter.emit(node);
    }

    hideNodeDetails() {
        this.selectedNodeId = undefined;
        this.showNodeDetailsEmitter.emit(undefined);
    }

    hasDataChildren(node: TreeInputNode) {
        return (
            node.children.length > 0 &&
            node.children.find(c => c.dataNode) !== undefined
        );
    }

    existsSelectedNode(node: TreeInputNode) {
        return (
            this.staticProperty.selectedNodesInternalNames.find(
                nodeName => nodeName === node.internalNodeName,
            ) !== undefined
        );
    }
}
