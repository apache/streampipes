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
    HostListener,
    Input,
    OnDestroy,
    OnInit,
} from '@angular/core';
import {
    DataType,
    EventPropertyList,
    EventPropertyPrimitive,
    EventPropertyUnion,
    SemanticType,
    SpDataStream,
} from '@streampipes/platform-services';
import { RestService } from '../../connect/services/rest.service';
import { Subscription } from 'rxjs';
import { HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';
import { LivePreviewService } from '../../services/live-preview.service';
import { RuntimeInfo } from './pipeline-element-runtime-info.model';

@Component({
    selector: 'sp-pipeline-element-runtime-info',
    templateUrl: './pipeline-element-runtime-info.component.html',
    styleUrls: ['./pipeline-element-runtime-info.component.scss'],
})
export class PipelineElementRuntimeInfoComponent implements OnInit, OnDestroy {
    @Input()
    streamDescription: SpDataStream;

    @Input()
    showTitle = true;

    runtimeData: { runtimeName: string; value: any }[];
    runtimeInfo: RuntimeInfo[];
    timer: any;
    runtimeDataError = false;
    runtimeSub: Subscription;

    constructor(
        private restService: RestService,
        private livePreviewService: LivePreviewService,
    ) {}

    ngOnInit(): void {
        this.runtimeInfo = this.makeRuntimeInfo();
        this.getLatestRuntimeInfo();
    }

    makeRuntimeInfo(): RuntimeInfo[] {
        return this.streamDescription.eventSchema.eventProperties
            .map(ep => {
                return {
                    label: ep.label || 'n/a',
                    description: ep.description || 'n/a',
                    runtimeType: this.getFriendlyRuntimeType(ep),
                    runtimeName: ep.runtimeName,
                    value: undefined,
                    isTimestamp: this.isTimestamp(ep),
                    isImage: this.isImage(ep),
                    hasNoDomainProperty: this.hasNoDomainProperty(ep),
                    valueChanged: false,
                };
            })
            .sort((a, b) => a.runtimeName.localeCompare(b.runtimeName));
    }

    getFriendlyRuntimeType(ep: EventPropertyUnion) {
        if (ep instanceof EventPropertyPrimitive) {
            if (DataType.isNumberType(ep.runtimeType)) {
                return 'Number';
            } else if (DataType.isBooleanType(ep.runtimeType)) {
                return 'Boolean';
            } else {
                return 'Text';
            }
        } else if (ep instanceof EventPropertyList) {
            return 'List';
        } else {
            return 'Nested';
        }
    }

    private isImage(ep: EventPropertyUnion) {
        return SemanticType.isImage(ep);
    }

    private isTimestamp(ep: EventPropertyUnion) {
        return SemanticType.isTimestamp(ep);
    }

    private hasNoDomainProperty(ep: EventPropertyUnion) {
        return !(this.isTimestamp(ep) || this.isImage(ep));
    }

    getLatestRuntimeInfo() {
        this.runtimeSub = this.restService
            .getRuntimeInfo(this.streamDescription)
            .subscribe(event => {
                if (event.type === HttpEventType.DownloadProgress) {
                    try {
                        const responseJson = this.livePreviewService.convert(
                            event as HttpDownloadProgressEvent,
                        );
                        const [firstKey] = Object.keys(responseJson);
                        const json = responseJson[firstKey];
                        this.runtimeDataError = !json;
                        this.runtimeInfo.forEach(r => {
                            const previousValue = r.value;
                            r.value = json[r.runtimeName];
                            r.valueChanged = r.value !== previousValue;
                        });
                    } catch (error) {
                        this.runtimeDataError = true;
                        this.runtimeData = [];
                    }
                }
            });
    }

    ngOnDestroy(): void {
        this.runtimeSub?.unsubscribe();
    }

    @HostListener('window:beforeunload')
    closeSubscription() {
        this.runtimeSub?.unsubscribe();
    }
}
