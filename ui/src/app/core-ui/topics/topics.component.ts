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
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineElementUnion } from '../../editor/model/editor.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
} from '@angular/material/table';
import { MatButton, MatIconButton } from '@angular/material/button';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'sp-pipeline-element-topics',
    templateUrl: './topics.component.html',
    styleUrls: ['./topics.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatTabGroup,
        MatTab,
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        MatIconButton,
        CdkCopyToClipboard,
        MatTooltip,
        MatIcon,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class TopicsComponent implements OnInit {
    translateService = inject(TranslateService);
    selectedTabIndex = 0;

    availableTabs = [
        this.translateService.instant('Topics'),
        this.translateService.instant('Code'),
    ];

    tabs: string[] = [];

    @Input()
    pipelineElement: PipelineElementUnion;
    isDataStream: boolean;

    constructor(private dialogRef: DialogRef<TopicsComponent>) {}

    ngOnInit() {
        if (
            this.pipelineElement instanceof SpDataStream ||
            this.pipelineElement instanceof DataProcessorInvocation ||
            this.pipelineElement instanceof DataSinkInvocation
        ) {
            this.tabs = this.availableTabs;
        } else {
            this.tabs = [this.availableTabs[1]];
            this.selectedTabIndex = 1;
        }
    }

    isSpDataStream(): boolean {
        return this.pipelineElement instanceof SpDataStream;
    }

    isDataProcessorInvocation(): boolean {
        return this.pipelineElement instanceof DataProcessorInvocation;
    }

    isDataSinkInvocation(): boolean {
        return this.pipelineElement instanceof DataSinkInvocation;
    }

    close() {
        setTimeout(() => {
            this.dialogRef.close();
        });
    }

    protected readonly SpDataStream = SpDataStream;
}
