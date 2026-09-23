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
    inject,
    Input,
    OnInit,
    Output,
    ViewChild,
    ChangeDetectionStrategy,
} from '@angular/core';
import {
    RuntimeResolvableTreeInputStaticProperty,
    TreeInputNode,
} from '@streampipes/platform-services';
import {
    MatNestedTreeNode,
    MatTree,
    MatTreeNestedDataSource,
    MatTreeNodeDef,
    MatTreeNodeOutlet,
} from '@angular/material/tree';
import { StaticTreeInputServiceService } from '../static-tree-input-service.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-static-tree-input-browse-nodes',
    templateUrl: './static-tree-input-browse-nodes.component.html',
    styleUrls: [
        './static-tree-input-browse-nodes.component.scss',
        '../static-runtime-resolvable-tree-input.component.scss',
    ],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        LayoutAlignDirective,
        LayoutDirective,
        FlexDirective,
        MatIconButton,
        MatIcon,
        MatTree,
        MatTreeNodeDef,
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

    childrenAccessor = node => node.children;
    dataSource = new MatTreeNestedDataSource<TreeInputNode>();

    selectedNodeId: string;

    /**
     * Nodes are fetched lazily and any node, including a data node, can have
     * children. A node is rendered without an expand toggle only after its
     * children were requested and came back empty.
     */
    private leafNodeIds = new Set<string>();

    ngOnInit(): void {
        this.dataSource = new MatTreeNestedDataSource<TreeInputNode>();
        this.dataSource.data = [];
    }

    updateNodes(nodes: TreeInputNode[]) {
        this.leafNodeIds.clear();
        this.dataSource.data = nodes || [];
    }

    onChildrenLoaded(node: TreeInputNode) {
        if (node.children?.length > 0) {
            this.leafNodeIds.delete(node.internalNodeName);
            this.tree?.expand(node);
        } else {
            this.leafNodeIds.add(node.internalNodeName);
        }
    }

    isLeaf(node: TreeInputNode): boolean {
        return this.leafNodeIds.has(node.internalNodeName);
    }

    refreshTree() {
        const data = this.dataSource.data.slice();
        this.dataSource.data = [];
        this.dataSource.data = [...data];
    }

    /**
     * A collapsed node is expanded in onChildrenLoaded once its children
     * arrived, so that no empty group is shown while the request is pending.
     */
    toggleNode(node: TreeInputNode): void {
        if (this.tree?.isExpanded(node)) {
            this.tree.collapse(node);
            return;
        }
        this.staticProperty.nextBaseNodeToResolve = node.internalNodeName;
        this.loadOptionsFromRestApiEmitter.emit(node);
    }

    addNode(node: TreeInputNode) {
        if (this.staticProperty.multiSelection) {
            node.selected = true;
            this.staticProperty.selectedNodesInternalNames.push(
                node.internalNodeName,
            );
        } else {
            this.clearSelectedFlags(this.dataSource.data);
            node.selected = true;
            this.staticProperty.selectedNodesInternalNames = [
                node.internalNodeName,
            ];
        }
        this.performValidationEmitter.emit();
    }

    addAllDirectChildren(node: TreeInputNode) {
        if (!this.staticProperty.multiSelection) {
            return;
        }

        node.children.forEach(child => {
            if (this.isSelectable(child) && !this.existsSelectedNode(child)) {
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
            node.children.find(c => this.isSelectable(c)) !== undefined
        );
    }

    existsSelectedNode(node: TreeInputNode) {
        return (
            this.staticProperty.selectedNodesInternalNames.find(
                nodeName => nodeName === node.internalNodeName,
            ) !== undefined
        );
    }

    isSelectable(node: TreeInputNode) {
        return (
            node.dataNode ||
            node.nodeMetadata?.Selectable === true ||
            node.nodeMetadata?.selectable === true
        );
    }

    private clearSelectedFlags(nodes: TreeInputNode[]) {
        nodes.forEach(currentNode => {
            currentNode.selected = false;
            this.clearSelectedFlags(currentNode.children ?? []);
        });
    }
}
