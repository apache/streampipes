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
import { SpBreadcrumbService, SpNavigationItem } from '@streampipes/shared-ui';
import { SpPipelineRoutes } from '../../pipelines.breadcrumb';

@Directive()
export abstract class AbstractFunctionDetailsDirective {
    public activeFunction: FunctionDefinition;

    contentReady = false;
    tabs: SpNavigationItem[] = [];
    streamNames: Record<string, string> = {};
    functionNotFound = false;
    refreshing = false;

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
                itemIcon: 'monitoring',
            },
            {
                itemId: 'logs',
                itemTitle: 'Logs',
                itemLink: ['pipelines', 'functions', functionId, 'logs'],
                itemIcon: 'receipt_long',
            },
        ];
        this.loadFunctions(functionId);
    }

    loadFunctions(functionId: string) {
        this.functionsService.getActiveFunctions().subscribe(functions => {
            this.activeFunction = functions.find(
                f => f.functionId.id === functionId,
            );
            if (!this.activeFunction) {
                this.functionNotFound = true;
                return;
            }
            this.loadStreams(this.activeFunction.consumedStreams);
        });
    }

    loadStreams(relatedStreams: string[]) {
        this.streamNames = {};
        if (relatedStreams.length === 0) {
            this.afterFunctionLoaded();
            return;
        }
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
        this.refreshing = true;
        this.adapterMonitoringService.triggerMonitoringUpdate().subscribe({
            next: () => this.afterFunctionLoaded(),
            error: () => (this.refreshing = false),
        });
    }

    abstract afterFunctionLoaded(): void;

    abstract getBreadcrumbLabel(): string;
}
