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

import { ActivatedRoute } from '@angular/router';
import {
    AdapterMonitoringService,
    FunctionDefinition,
    FunctionsService,
    PipelineElementService,
    SpDataStream,
} from '@streampipes/platform-services';
import { Directive } from '@angular/core';
import { Observable, zip } from 'rxjs';
import { SpBreadcrumbService } from '../../../../../dist/streampipes/shared-ui';
import { SpPipelineRoutes } from '../../pipelines.routes';

@Directive()
export abstract class AbstractFunctionDetailsDirective {
    public activeFunction: FunctionDefinition;

    contentReady = false;
    tabs = [];
    streamNames: Record<string, string> = {};

    constructor(
        private route: ActivatedRoute,
        protected functionsService: FunctionsService,
        private pipelineElementService: PipelineElementService,
        private adapterMonitoringService: AdapterMonitoringService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    onInit() {
        const functionId = this.route.snapshot.params.functionId;
        this.breadcrumbService.updateBreadcrumb([
            SpPipelineRoutes.BASE,
            { label: functionId },
            { label: this.getBreadcrumbLabel() },
        ]);
        this.tabs = [
            {
                itemId: 'metrics',
                itemTitle: 'Metrics',
                itemLink: ['pipelines', 'functions', functionId, 'metrics'],
            },
            {
                itemId: 'logs',
                itemTitle: 'Logs',
                itemLink: ['pipelines', 'functions', functionId, 'logs'],
            },
        ];
        this.loadFunctions(functionId);
    }

    loadFunctions(functionId: string) {
        this.functionsService.getActiveFunctions().subscribe(functions => {
            this.activeFunction = functions.find(
                f => f.functionId.id === functionId,
            );
            this.loadStreams(this.activeFunction.consumedStreams);
        });
    }

    loadStreams(relatedStreams: string[]) {
        this.streamNames = {};
        const observables = this.getStreamObservables(relatedStreams);
        zip(...observables).subscribe(streams => {
            streams.forEach(
                stream => (this.streamNames[stream.elementId] = stream.name),
            );
            this.afterFunctionLoaded();
        });
    }

    getStreamObservables(relatedStreams: string[]): Observable<SpDataStream>[] {
        return relatedStreams.map(s =>
            this.pipelineElementService.getDataStreamByElementId(s),
        );
    }

    triggerUpdate() {
        this.adapterMonitoringService
            .triggerMonitoringUpdate()
            .subscribe(() => {
                this.afterFunctionLoaded();
            });
    }

    abstract afterFunctionLoaded(): void;

    abstract getBreadcrumbLabel(): string;
}
