/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { TreeNode } from '../model/TreeNode';

export class TreeNodeBuilder {
    private readonly node: TreeNode;

    constructor(name: string) {
        this.node = { name, children: [] };
    }

    addChildren(...childrenBuilders: TreeNodeBuilder[]): TreeNodeBuilder {
        for (const childBuilder of childrenBuilders) {
            this.node.children!.push(childBuilder.build());
        }
        return this;
    }

    static create(
        name: string,
        ...children: TreeNodeBuilder[]
    ): TreeNodeBuilder {
        const builder = new TreeNodeBuilder(name);
        if (children.length > 0) {
            builder.addChildren(...children);
        }
        return builder;
    }

    build(): TreeNode {
        return this.node;
    }
}
