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
    Input,
    OnInit,
    OnChanges,
    SimpleChanges,
    TemplateRef,
    ChangeDetectionStrategy,
} from '@angular/core';
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
    completedConfigurations: ConfigurationInfo[];
    onCompleted: (event: ConfigurationInfo) => void;
};

@Component({
    selector: 'sp-app-static-group',
    templateUrl: './static-group.component.html',
    styleUrls: ['./static-group.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [LayoutDirective, NgTemplateOutlet],
})
export class StaticGroupComponent
    extends AbstractStaticPropertyRenderer<StaticPropertyGroup>
    implements OnInit, OnChanges
{
    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    @Input({ required: true })
    renderStaticProperty!: TemplateRef<GroupRenderCtx>;

    completedGroupConfigurations: ConfigurationInfo[] = [];
    childCompletedConfigurations: ConfigurationInfo[] = [];

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['completedConfigurations']) {
            this.updateChildConfigurations();
        }
    }

    private updateChildConfigurations(): void {
        this.childCompletedConfigurations =
            this.staticPropertyUtils.mergeCompletedConfigurations(
                this.completedConfigurations,
                this.completedGroupConfigurations,
            );
    }

    ctxFor(groupElement: StaticPropertyUnion, index: number): GroupRenderCtx {
        return {
            child: groupElement!,
            index,
            completedConfigurations: this.childCompletedConfigurations,
            onCompleted: ev => this.handleConfigurationUpdate(ev),
        };
    }

    handleConfigurationUpdate(event: ConfigurationInfo): void {
        this.staticPropertyUtils.updateCompletedConfiguration(
            event,
            this.completedGroupConfigurations,
        );
        this.updateChildConfigurations();
        this.applyCompletedConfiguration(
            this.completedGroupConfigurations.every(
                config => config.configured,
            ),
        );
    }

    ngOnInit(): void {
        this.completedGroupConfigurations =
            this.staticProperty.staticProperties.map(sp => ({
                staticPropertyInternalName: sp.internalName,
                configured: false,
            }));
        this.updateChildConfigurations();
        this.applyCompletedConfiguration(
            this.completedGroupConfigurations.length === 0,
        );
    }
}
