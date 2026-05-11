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
    ElementRef,
    HostListener,
    inject,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import { BaseRuntimeResolvableInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-input';
import {
    RuntimeResolvableTreeInputStaticProperty,
    StaticPropertyUnion,
    TreeInputNode,
} from '@streampipes/platform-services';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormControl,
} from '@angular/forms';
import { StaticTreeInputServiceService } from './static-tree-input-service.service';
import { StaticTreeInputBrowseNodesComponent } from './static-tree-input-browse-nodes/static-tree-input-browse-nodes.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { CdkDrag, CdkDragEnd, CdkDragMove } from '@angular/cdk/drag-drop';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { StaticTreeInputButtonMenuComponent } from './static-tree-input-button-menu/static-tree-input-button-menu.component';
import { SpExceptionMessageComponent } from '@streampipes/shared-ui';
import { StaticTreeInputNodeDetailsComponent } from './static-tree-input-node-details/static-tree-input-node-details.component';
import { StaticTreeInputSelectedNodesComponent } from './static-tree-input-selected-nodes/static-tree-input-selected-nodes.component';
import { StaticTreeInputTextEditorComponent } from './static-tree-input-text-editor/static-tree-input-text-editor.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-static-runtime-resolvable-tree-input',
    templateUrl: './static-runtime-resolvable-tree-input.component.html',
    styleUrls: ['./static-runtime-resolvable-tree-input.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        StaticTreeInputButtonMenuComponent,
        SpExceptionMessageComponent,
        LayoutGapDirective,
        CdkDrag,
        MatIconButton,
        MatIcon,
        MatTooltip,
        TranslatePipe,
        StaticTreeInputBrowseNodesComponent,
        StaticTreeInputNodeDetailsComponent,
        StaticTreeInputSelectedNodesComponent,
        StaticTreeInputTextEditorComponent,
    ],
})
export class StaticRuntimeResolvableTreeInputComponent
    extends BaseRuntimeResolvableInput<RuntimeResolvableTreeInputStaticProperty>
    implements OnInit, OnDestroy
{
    private staticTreeInputServiceService = inject(
        StaticTreeInputServiceService,
    );
    private host = inject(ElementRef<HTMLElement>);

    nodeDetails: TreeInputNode;

    editorMode: 'tree' | 'text' = 'tree';

    // The following two arrays store the fetched nodes from the backend to
    // present them to the user in the UI. For performance reasons, the nodes
    // should not be stored in the static property object
    latestFetchedNodes = [];
    nodes = [];
    treeFullscreen = false;
    browsePanelWidth = 50;
    isResizingPanels = false;
    private readonly minPanelWidthPercent = 30;
    private readonly maxPanelWidthPercent = 70;

    @ViewChild('staticTreeInputBrowseNodesComponent')
    private staticTreeInputBrowseNodesComponent: StaticTreeInputBrowseNodesComponent;

    @ViewChild('treeWorkspace')
    private treeWorkspace: ElementRef<HTMLDivElement>;

    private placeholderNode?: Comment;

    ngOnInit(): void {
        // if a node is selected it is assumed the adapter was opened in edit mode
        // when that is the case, the browse tree should be reloaded
        // if the adapter is created, the reload should only be triggered if all previous configurations are set correctly
        if (
            this.staticProperty.selectedNodesInternalNames &&
            this.staticProperty.selectedNodesInternalNames.length > 0
        ) {
            this.resetStaticPropertyStateAndReload();
        } else {
            this.resetStaticPropertyState();
        }

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

    ngOnDestroy(): void {
        this.restoreHostPosition();
    }

    afterOptionsLoaded(
        staticProperty: RuntimeResolvableTreeInputStaticProperty,
        node: TreeInputNode,
    ) {
        if (
            staticProperty.latestFetchedNodes &&
            staticProperty.nextBaseNodeToResolve !== null
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
        } else {
            this.closeTreeFullscreen();
        }
    }

    toggleTreeFullscreen() {
        if (this.treeFullscreen) {
            this.closeTreeFullscreen();
        } else {
            this.detachHostToOverlay();
            this.treeFullscreen = true;
        }
    }

    onPanelResizeStarted() {
        this.isResizingPanels = true;
    }

    onPanelResizeMoved(event: CdkDragMove) {
        const workspaceElement = this.treeWorkspace?.nativeElement;

        if (!workspaceElement) {
            return;
        }

        const rect = workspaceElement.getBoundingClientRect();
        const relativeLeft = event.pointerPosition.x - rect.left;
        const widthPercent = (relativeLeft / rect.width) * 100;

        this.browsePanelWidth = Math.min(
            Math.max(widthPercent, this.minPanelWidthPercent),
            this.maxPanelWidthPercent,
        );

        event.source.element.nativeElement.style.transform = 'none';
    }

    onPanelResizeEnded(event: CdkDragEnd) {
        this.isResizingPanels = false;
        event.source.element.nativeElement.style.transform = 'none';
    }

    @HostListener('window:resize')
    onWindowResize() {
        if (this.treeFullscreen) {
            this.updateDetachedHostBounds();
        }
    }

    @HostListener('window:keydown.escape')
    onEscapePressed() {
        if (this.treeFullscreen) {
            this.closeTreeFullscreen();
        }
    }

    private closeTreeFullscreen() {
        this.treeFullscreen = false;
        this.restoreHostPosition();
    }

    private detachHostToOverlay() {
        const hostEl = this.host.nativeElement;

        if (this.placeholderNode || hostEl.parentNode === document.body) {
            this.updateDetachedHostBounds();
            return;
        }

        const parent = hostEl.parentNode;

        if (!parent) {
            return;
        }

        this.placeholderNode = document.createComment(
            'sp-static-tree-input-placeholder',
        );
        parent.insertBefore(this.placeholderNode, hostEl);
        document.body.appendChild(hostEl);
        hostEl.classList.add('tree-detached-host');
        this.updateDetachedHostBounds();
    }

    private restoreHostPosition() {
        const hostEl = this.host.nativeElement;

        if (this.placeholderNode?.parentNode) {
            this.placeholderNode.parentNode.insertBefore(
                hostEl,
                this.placeholderNode,
            );
            this.placeholderNode.parentNode.removeChild(this.placeholderNode);
        }

        this.placeholderNode = undefined;
        hostEl.classList.remove('tree-detached-host');
        hostEl.style.removeProperty('--tree-overlay-top');
        hostEl.style.removeProperty('--tree-overlay-left');
        hostEl.style.removeProperty('--tree-overlay-width');
        hostEl.style.removeProperty('--tree-overlay-height');
    }

    private updateDetachedHostBounds() {
        const hostEl = this.host.nativeElement;
        const mainSection = document.querySelector(
            '.main-section',
        ) as HTMLElement | null;
        const targetRect =
            mainSection?.getBoundingClientRect() ??
            document.documentElement.getBoundingClientRect();

        hostEl.style.setProperty('--tree-overlay-top', `${targetRect.top}px`);
        hostEl.style.setProperty('--tree-overlay-left', `${targetRect.left}px`);
        hostEl.style.setProperty(
            '--tree-overlay-width',
            `${targetRect.width}px`,
        );
        hostEl.style.setProperty(
            '--tree-overlay-height',
            `${targetRect.height}px`,
        );
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
