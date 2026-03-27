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
    OnChanges,
    OnInit,
    Output,
    TemplateRef,
} from '@angular/core';
import { BaseRuntimeResolvableInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-input';
import {
    RuntimeResolvableGroupStaticProperty,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import {
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { GroupRenderCtx } from '../static-group/static-group.component';
import { NgTemplateOutlet } from '@angular/common';

@Component({
    selector: 'sp-app-static-runtime-resolvable-group',
    templateUrl: './static-runtime-resolvable-group.component.html',
    imports: [
        LayoutDirective,
        MatButton,
        TranslatePipe,
        LayoutGapDirective,
        NgTemplateOutlet,
    ],
})
export class StaticRuntimeResolvableGroupComponent
    extends BaseRuntimeResolvableInput<RuntimeResolvableGroupStaticProperty>
    implements OnInit, OnChanges
{
    @Input({ required: true })
    renderStaticProperty!: TemplateRef<GroupRenderCtx>;

    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    handleConfigurationUpdate(event: ConfigurationInfo): void {}

    ngOnInit(): void {
        super.onInit();
        if (this.staticProperty.staticProperties.length === 0) {
            this.loadOptionsFromRestApi();
        }
        this.applyCompletedConfiguration(true);
    }

    ctxFor(groupElement: StaticPropertyUnion, index: number): GroupRenderCtx {
        return {
            child: groupElement!,
            index,
            onCompleted: ev => this.handleConfigurationUpdate(ev),
        };
    }

    afterErrorReceived() {}

    afterOptionsLoaded(staticProperty: RuntimeResolvableGroupStaticProperty) {
        this.staticProperty.staticProperties = staticProperty.staticProperties;
    }

    parse(
        staticProperty: StaticPropertyUnion,
    ): RuntimeResolvableGroupStaticProperty {
        return staticProperty as RuntimeResolvableGroupStaticProperty;
    }
}
