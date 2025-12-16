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
import {
    AssetConstants,
    AssetLinkType,
    GenericStorageService,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineService,
} from '@streampipes/platform-services';
import { forkJoin } from 'rxjs';
import { PipelineDetailsModule } from '../../../pipeline-details/pipeline-details.module';
import {
    DefaultFlexDirective,
    DefaultLayoutDirective,
    FlexFillDirective,
} from '@ngbracket/ngx-layout';
import { SharedUiModule } from '@streampipes/shared-ui';
import { PipelinePreviewMetaComponent } from './pipeline-preview-meta/pipeline-preview-meta.component';
import { MatDivider } from '@angular/material/list';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-pipeline-feature-card',
    templateUrl: './pipeline-feature-card.component.html',
    styleUrls: ['./pipeline-feature-card.component.scss'],
    imports: [
        PipelineDetailsModule,
        DefaultFlexDirective,
        DefaultLayoutDirective,
        SharedUiModule,
        PipelinePreviewMetaComponent,
        FlexFillDirective,
        MatDivider,
    ],
})
export class PipelineFeatureCardComponent implements OnInit {
    @Input()
    resourceId: string;

    @Input() onClose?: () => void;

    pipeline: Pipeline;
    pipelineCanvasMetadata: PipelineCanvasMetadata;
    assetLink: AssetLinkType;

    private pipelineService = inject(PipelineService);
    private pipelineCanvasService = inject(PipelineCanvasMetadataService);
    private genericStorageService = inject(GenericStorageService);
    private router = inject(Router);

    ngOnInit() {
        forkJoin([
            this.pipelineService.getPipelineById(this.resourceId),
            this.pipelineCanvasService.getPipelineCanvasMetadata(
                this.resourceId,
            ),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
        ]).subscribe(p => {
            this.pipeline = p[0];
            this.pipelineCanvasMetadata = p[1]
                ? p[1]
                : new PipelineCanvasMetadata();
            this.assetLink = p[2].find(a => a.linkType === 'pipeline');
        });
    }

    navigateToPipelines(): void {
        this.onClose();
        this.router.navigate(['pipelines', 'details', this.resourceId]);
    }
}
