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

import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { AbstractStaticPropertyRenderer } from '../base/abstract-static-property';
import {
    ExtensionDeploymentConfiguration,
    StaticPropertyGroup,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { NgTemplateOutlet } from '@angular/common';

export type GroupRenderCtx = {
    child: StaticPropertyUnion;
    index: number;
    onCompleted: (event: ConfigurationInfo) => void;
};

@Component({
    selector: 'sp-app-static-group',
    templateUrl: './static-group.component.html',
    styleUrls: ['./static-group.component.scss'],
    imports: [LayoutDirective, NgTemplateOutlet],
})
export class StaticGroupComponent
    extends AbstractStaticPropertyRenderer<StaticPropertyGroup>
    implements OnInit
{
    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    @Input({ required: true })
    renderStaticProperty!: TemplateRef<GroupRenderCtx>;

    dependentStaticProperties: Map<string, boolean> = new Map<
        string,
        boolean
    >();

    ctxFor(groupElement: StaticPropertyUnion, index: number): GroupRenderCtx {
        return {
            child: groupElement!,
            index,
            onCompleted: ev => this.handleConfigurationUpdate(ev),
        };
    }

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
            this.applyCompletedConfiguration(true);
        } else {
            this.applyCompletedConfiguration(false);
        }
    }

    ngOnInit(): void {
        this.staticProperty.staticProperties.forEach(sp => {
            this.dependentStaticProperties.set(sp.internalName, false);
        });
    }
}
