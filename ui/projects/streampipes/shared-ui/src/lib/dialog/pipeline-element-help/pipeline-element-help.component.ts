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

import { Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { DialogRef } from '../base-dialog/dialog-ref';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { PipelineElementRuntimeInfoComponent } from '../../components/pipeline-element-runtime-info/pipeline-element-runtime-info.component';
import { PipelineElementDocumentationComponent } from '../../components/pipeline-element-documentation/pipeline-element-documentation.component';
import { SpElementIdComponent } from '../../components/element-id/element-id.component';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SpLabelComponent } from '../../components/sp-label/sp-label.component';
import { SpAssetBrowserService } from '../../components/asset-browser/asset-browser.service';
import { SpTableAssetContextService } from '../../components/sp-table/sp-asset-context/sp-table-asset-context.service';
import { SpTableResolvedAssetContext } from '../../components/sp-table/sp-table.model';
import { MatTooltip } from '@angular/material/tooltip';
import { map } from 'rxjs';

@Component({
    selector: 'sp-pipeline-element-help',
    templateUrl: './pipeline-element-help.component.html',
    styleUrls: ['./pipeline-element-help.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatTabGroup,
        MatTab,
        PipelineElementRuntimeInfoComponent,
        PipelineElementDocumentationComponent,
        SpElementIdComponent,
        MatDivider,
        MatButton,
        MatTooltip,
        SpLabelComponent,
        TranslatePipe,
    ],
})
export class PipelineElementHelpComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<PipelineElementHelpComponent>>(DialogRef);
    private assetBrowserService = inject(SpAssetBrowserService);
    private assetContextService = inject(SpTableAssetContextService);
    private destroyRef = inject(DestroyRef);

    selectedTabIndex = 0;

    translateService = inject(TranslateService);

    availableTabs = [
        this.translateService.instant('Preview'),
        this.translateService.instant('Documentation'),
    ];

    tabs: string[] = [];

    @Input()
    pipelineElement:
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation;

    isDataStream: boolean;
    assetContext?: SpTableResolvedAssetContext;

    ngOnInit() {
        if (this.pipelineElement instanceof SpDataStream) {
            this.tabs = this.availableTabs;
            this.isDataStream = true;
            this.assetBrowserService.assetData$
                .pipe(
                    map(assetData =>
                        this.assetContextService.resolveDataStreamAssetContext(
                            assetData,
                            this.pipelineElement as SpDataStream,
                        ),
                    ),
                    takeUntilDestroyed(this.destroyRef),
                )
                .subscribe(assetContext => this.assetContext = assetContext);
        } else {
            this.tabs.push(this.availableTabs[1]);
            this.selectedTabIndex = 1;
        }
    }

    close() {
        setTimeout(() => {
            this.dialogRef.close();
        });
    }

    protected readonly SpDataStream = SpDataStream;
}
