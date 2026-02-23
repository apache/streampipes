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

import { Component, inject, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FlexFillDirective } from '@ngbracket/ngx-layout';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin } from 'rxjs';
import {
    AdapterDescription,
    AdapterService,
    AssetConstants,
    AssetLinkType,
    GenericStorageService,
    PipelineElementService,
    SpDataStream,
} from '@streampipes/platform-services';
import {
    DateFormatService,
    FeatureCardHeaderComponent,
    FeatureCardMetaSectionComponent,
    PipelineElementRuntimeInfoComponent,
    SpLabelComponent,
} from '@streampipes/shared-ui';

@Component({
    selector: 'sp-connect-feature-card',
    templateUrl: './connect-feature-card.component.html',
    styleUrls: ['./connect-feature-card.component.scss'],
    imports: [
        FlexFillDirective,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatIcon,
        TranslatePipe,
        FeatureCardHeaderComponent,
        FeatureCardMetaSectionComponent,
        SpLabelComponent,
        PipelineElementRuntimeInfoComponent,
    ],
})
export class ConnectFeatureCardComponent implements OnInit {
    @Input()
    resourceId: string;

    @Input()
    onClose?: () => void;

    adapter: AdapterDescription;
    assetLinkType: AssetLinkType;
    streamDescription: SpDataStream;

    private adapterService = inject(AdapterService);
    private genericStorageService = inject(GenericStorageService);
    private pipelineElementService = inject(PipelineElementService);
    private dateFormatService = inject(DateFormatService);
    private router = inject(Router);

    ngOnInit(): void {
        forkJoin([
            this.adapterService.getAdapter(this.resourceId),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
        ]).subscribe(([adapter, assetLinkTypes]) => {
            this.adapter = adapter;
            this.assetLinkType = assetLinkTypes.find(
                link => link.linkType === 'adapter',
            );

            if (adapter?.correspondingDataStreamElementId) {
                this.pipelineElementService
                    .getDataStreamByElementId(
                        adapter.correspondingDataStreamElementId,
                    )
                    .subscribe(stream => {
                        this.streamDescription = stream;
                    });
            }
        });
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    getFieldCount(): number {
        return (
            this.streamDescription?.eventSchema?.eventProperties?.length ?? 0
        );
    }

    getOutputStreamName(): string {
        return (
            this.streamDescription?.name ||
            this.adapter?.dataStream?.name ||
            this.adapter?.correspondingDataStreamElementId ||
            '–'
        );
    }

    getStatusLabel(): string {
        return this.adapter?.running ? 'Running' : 'Stopped';
    }

    getStatusTone(): 'success' | 'error' {
        return this.adapter?.running ? 'success' : 'error';
    }

    navigateToAdapter(): void {
        this.onClose?.();
        this.router.navigate(['connect', 'details', this.resourceId, 'data']);
    }
}
