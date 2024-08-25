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

import { Component, Input } from '@angular/core';
import {
    CollectionStaticProperty,
    ExtensionDeploymentConfiguration,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';

@Component({
    selector: 'sp-static-collection',
    templateUrl: './static-collection.component.html',
    styleUrls: ['./static-collection.component.scss'],
})
export class StaticCollectionComponent extends AbstractValidatedStaticPropertyRenderer<CollectionStaticProperty> {
    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    constructor() {
        super();
    }

    add(property: StaticPropertyUnion) {
        if (!this.staticProperty.members) {
            this.staticProperty.members = [];
        }
        this.staticProperty.members.push(property);
    }

    remove(i) {
        this.staticProperty.members.splice(i, 1).slice(0);
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {}
}
