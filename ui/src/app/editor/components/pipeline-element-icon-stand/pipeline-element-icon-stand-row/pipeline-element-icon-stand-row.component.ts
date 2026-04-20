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

import { Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import {
    PipelineElementType,
    PipelineElementUnion,
} from '../../../model/editor.model';
import { PipelineElementTypeUtils } from '../../../utils/editor.utils';
import { EditorService } from '../../../services/editor.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTooltip } from '@angular/material/tooltip';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import {
    PipelineElementComponent,
    SpLabelComponent,
    SpAssetBrowserService,
    SpTableAssetContextService,
} from '@streampipes/shared-ui';
import { MatButton } from '@angular/material/button';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SpDataStream } from '@streampipes/platform-services';
import { SpTableResolvedAssetContext } from '@streampipes/shared-ui';
import { map } from 'rxjs';

@Component({
    selector: 'sp-pe-icon-stand-row',
    templateUrl: './pipeline-element-icon-stand-row.component.html',
    styleUrls: ['./pipeline-element-icon-stand-row.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        MatTooltip,
        NgClass,
        ClassDirective,
        PipelineElementComponent,
        SpLabelComponent,
        LayoutAlignDirective,
        MatButton,
    ],
})
export class PipelineElementIconStandRowComponent implements OnInit {
    private editorService = inject(EditorService);
    private assetBrowserService = inject(SpAssetBrowserService);
    private assetContextService = inject(SpTableAssetContextService);
    private destroyRef = inject(DestroyRef);

    @Input()
    element: PipelineElementUnion;

    activeCssClass: string;
    cypressName: string;

    currentMouseOver = false;
    assetContext?: SpTableResolvedAssetContext;

    ngOnInit(): void {
        const activeType = PipelineElementTypeUtils.fromClassName(
            this.element['@class'],
        );
        this.activeCssClass = this.makeActiveCssClass(activeType);
        this.cypressName = this.element.name.toLowerCase().replace(' ', '_');

        if (this.element instanceof SpDataStream) {
            this.assetBrowserService.assetData$
                .pipe(
                    map(assetData =>
                        this.assetContextService.resolveDataStreamAssetContext(
                            assetData,
                            this.element as SpDataStream,
                        ),
                    ),
                    takeUntilDestroyed(this.destroyRef),
                )
                .subscribe(assetContext => (this.assetContext = assetContext));
        }
    }

    makeActiveCssClass(elementType: PipelineElementType): string {
        return PipelineElementTypeUtils.toCssShortHand(elementType);
    }

    updateMouseOver(e: string) {
        this.currentMouseOver = !this.currentMouseOver;
    }

    openHelpDialog(pipelineElement) {
        this.editorService.openHelpDialog(pipelineElement);
    }
}
