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

import { Component, OnInit } from '@angular/core';
import { BaseRuntimeResolvableInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-input';
import {
    RuntimeResolvableTreeInputStaticProperty,
    StaticPropertyUnion,
    TreeInputNode,
} from '@streampipes/platform-services';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import {
    AbstractControl,
    UntypedFormControl,
    ValidationErrors,
    ValidatorFn,
} from '@angular/forms';

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

    constructor(runtimeResolvableService: RuntimeResolvableService) {
        super(runtimeResolvableService);
    }

    hasChild = (_: number, node: TreeInputNode) =>
        !!node.children && node.children.length > 0;

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
    ) {
        this.staticProperty.nodes = staticProperty.nodes;
        this.dataSource.data = this.staticProperty.nodes;
        this.performValidation();
    }

    toggleNodeSelection(node: TreeInputNode) {
        node.selected = !node.selected;
        this.performValidation();
    }

    toggleAllNodeSelection(node: any) {
        const descendants = this.treeControl.getDescendants(node);
        const newState = !node.selected;
        node.selected = newState;
        descendants.forEach(d => (d.selected = newState));
        this.performValidation();
    }

    descendantsAllSelected(node: TreeInputNode) {
        const descendants = this.treeControl.getDescendants(node);
        const allSelected =
            descendants.length > 0 &&
            descendants.every(child => {
                return child.selected;
            });
        node.selected = allSelected;
        return allSelected;
    }

    descendantsPartiallySelected(node: TreeInputNode) {
        const descendants = this.treeControl.getDescendants(node);
        const result = descendants.some(child => child.selected);
        return result && !this.descendantsAllSelected(node);
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
        return (
            this.dataSource.data.find(d => this.anySelected(d)) !== undefined
        );
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
}
