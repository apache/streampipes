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

import { Component, inject, Input } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    PipelineElementAssetService,
    PipelineElementIconTextService,
    SpDataStream,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-pipeline-element',
    templateUrl: './pipeline-element.component.html',
    styleUrls: ['./pipeline-element.component.scss'],
    standalone: false,
})
export class PipelineElementComponent {
    showImage: any;
    iconText: any;

    pipelineElement_:
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation;

    @Input()
    iconSize: any;

    @Input()
    iconStandSize: any;

    iconUrl: any;
    image: SafeUrl;

    private pipelineElementAssetService = inject(PipelineElementAssetService);
    private pipelineElementIconTextService = inject(
        PipelineElementIconTextService,
    );
    private sanitizer = inject(DomSanitizer);

    checkImageAvailable() {
        if (
            this.pipelineElement.includesAssets &&
            this.pipelineElement.includedAssets.indexOf('icon.png') > -1
        ) {
            this.image = this.sanitizer.bypassSecurityTrustUrl(
                this.makeAssetIconUrl(),
            );
            this.showImage = true;
        } else {
            this.showImage = false;
        }
    }

    makeAssetIconUrl() {
        return (
            this.pipelineElementAssetService.getAssetUrl(
                this.pipelineElement.appId,
            ) + '/icon'
        );
    }

    iconSizeCss() {
        if (this.iconSize) {
            return 'width:35px;height:35px;';
        } else if (this.iconStandSize) {
            return 'width:30px;height:30px;';
        } else {
            return 'width:50px;height:50px;';
        }
    }

    get pipelineElement() {
        return this.pipelineElement_;
    }

    @Input()
    set pipelineElement(
        pipelineElement:
            | SpDataStream
            | DataProcessorInvocation
            | DataSinkInvocation,
    ) {
        this.pipelineElement_ = pipelineElement;
        this.iconText = this.pipelineElementIconTextService.getElementIconText(
            this.pipelineElement.name,
        );
        this.checkImageAvailable();
    }
}
