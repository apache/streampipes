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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractStaticPropertyRenderer } from '../base/abstract-static-property';
import { StaticPropertyGroup } from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';

@Component({
    selector: 'sp-app-static-group',
    templateUrl: './static-group.component.html',
    styleUrls: ['./static-group.component.css'],
})
export class StaticGroupComponent
    extends AbstractStaticPropertyRenderer<StaticPropertyGroup>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    dependentStaticProperties: Map<string, boolean> = new Map<
        string,
        boolean
    >();

    handleConfigurationUpdate(event: ConfigurationInfo): void {
        this.dependentStaticProperties.set(
            event.staticPropertyInternalName,
            event.configured,
        );
        if (
            Array.from(this.dependentStaticProperties.values()).every(
                v => v === true,
            )
        ) {
            this.emitUpdate(true);
        } else {
            this.emitUpdate(false);
        }
    }

    ngOnInit(): void {
        this.staticProperty.staticProperties.forEach(sp => {
            this.dependentStaticProperties.set(sp.internalName, false);
        });
    }
}
