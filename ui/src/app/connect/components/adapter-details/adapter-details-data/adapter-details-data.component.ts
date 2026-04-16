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

import { Component, OnInit, inject } from '@angular/core';
import { SpAbstractAdapterDetailsDirective } from '../abstract-adapter-details.directive';
import {
    PipelineElementRuntimeInfoComponent,
    SpBasicHeaderTitleComponent,
    SpBasicNavTabsComponent,
} from '@streampipes/shared-ui';
import {
    PipelineElementService,
    SpDataStream,
} from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
    selector: 'sp-adapter-details-data',
    templateUrl: './adapter-details-data.component.html',
    styleUrl: './adapter-details-data.component.scss',
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        SpBasicHeaderTitleComponent,
        PipelineElementRuntimeInfoComponent,
        TranslatePipe,
        MatIcon,
        MatIconButton,
        CdkCopyToClipboard,
        MatTooltip,
    ],
})
export class AdapterDetailsDataComponent
    extends SpAbstractAdapterDetailsDirective
    implements OnInit
{
    private pipelineElementService = inject(PipelineElementService);

    stream: SpDataStream;

    ngOnInit(): void {
        super.onInit();
    }

    onAdapterLoaded(): void {
        const streamId = this.adapter.correspondingDataStreamElementId;

        this.pipelineElementService
            .getDataStreamByElementId(streamId)
            .subscribe(stream => (this.stream = stream));
    }
}
