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

import * as dagre from 'dagre';
import { JsplumbBridge } from './jsplumb-bridge.service';
import { Injectable } from '@angular/core';
import {
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
} from '../model/editor.model';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
} from '@streampipes/platform-services';
import { JsplumbFactoryService } from './jsplumb-factory.service';
import { UserErrorMessage } from '../../core-model/base/UserErrorMessage';
import { Connection } from '@jsplumb/browser-ui';

@Injectable({ providedIn: 'root' })
export class PipelineValidationService {
    errorMessages: any = [];
    pipelineValid = false;

    availableErrorMessages: UserErrorMessage[] = [
        new UserErrorMessage(
            'Did you add a data stream?',
            'Any pipeline needs at least one data stream.',
        ),
        new UserErrorMessage(
            'Did you add a data sink?',
            'Any pipeline needs at least one data sink.',
        ),
        new UserErrorMessage(
            'Did you connect all elements?',
            'No orphaned elements are allowed within a pipeline, make sure to connect all elements.',
        ),
        new UserErrorMessage(
            'Separate pipelines',
            "It seems you've created more than one pipeline at once. Create only one pipeline at a time!",
        ),
        new UserErrorMessage(
            'Did you configure all elements?',
            "There's a pipeline element which is missing some configuration.",
        ),
    ];

    constructor(private jsplumbFactoryService: JsplumbFactoryService) {}

    isValidPipeline(rawPipelineModel, previewConfig: boolean) {
        const jsplumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(previewConfig);
        const streamInAssembly = this.isStreamInAssembly(rawPipelineModel);
        const sepaInAssembly = this.isSepaInAssembly(rawPipelineModel);
        const actionInAssembly = this.isActionInAssembly(rawPipelineModel);
        let allElementsConnected = true;
        let onlyOnePipelineCreated = true;
        let allElementsConfigured = true;

        if (streamInAssembly && (sepaInAssembly || actionInAssembly)) {
            allElementsConnected = this.allElementsConnected(
                rawPipelineModel,
                jsplumbBridge,
            );
            allElementsConfigured =
                this.allElementsConfigured(rawPipelineModel);
        }

        if (streamInAssembly && actionInAssembly && allElementsConnected) {
            onlyOnePipelineCreated = this.onlyOnePipelineCreated(
                rawPipelineModel,
                jsplumbBridge,
            );
        }

        if (!this.isEmptyPipeline(rawPipelineModel)) {
            this.buildErrorMessages(
                streamInAssembly,
                actionInAssembly,
                allElementsConnected,
                onlyOnePipelineCreated,
                allElementsConfigured,
            );
        } else {
            this.errorMessages = [];
        }

        this.pipelineValid =
            streamInAssembly &&
            actionInAssembly &&
            allElementsConnected &&
            onlyOnePipelineCreated &&
            allElementsConfigured;
        return this.pipelineValid;
    }

    isEmptyPipeline(rawPipelineModel) {
        return (
            !this.isActionInAssembly(rawPipelineModel) &&
            !this.isStreamInAssembly(rawPipelineModel) &&
            !this.isInAssembly(rawPipelineModel, 'sepa')
        );
    }

    buildErrorMessages(
        streamInAssembly,
        actionInAssembly,
        allElementsConnected,
        onlyOnePipelineCreated,
        allElementsConfigured,
    ) {
        this.errorMessages = [];
        if (!streamInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[0]);
        }
        if (!actionInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[1]);
        }
        if (!allElementsConnected) {
            this.errorMessages.push(this.availableErrorMessages[2]);
        }
        if (!onlyOnePipelineCreated) {
            this.errorMessages.push(this.availableErrorMessages[3]);
        }
        if (!allElementsConfigured) {
            this.errorMessages.push(this.availableErrorMessages[4]);
        }
    }

    allElementsConfigured(rawPipelineModel: PipelineElementConfig[]): boolean {
        return rawPipelineModel
            .filter(
                config =>
                    config.payload instanceof DataProcessorInvocation ||
                    config.payload instanceof DataSinkInvocation,
            )
            .every(
                config =>
                    config.settings.completed ===
                    PipelineElementConfigurationStatus.OK,
            );
    }

    allElementsConnected(rawPipelineModel, jsplumbBridge) {
        const g = this.makeGraph(rawPipelineModel, jsplumbBridge);
        return g.nodes().every(node => this.isFullyConnected(g, node));
    }

    isFullyConnected(g, node) {
        const nodeProperty = g.node(node);
        return g.outEdges(node).length >= nodeProperty.endpointCount;
    }

    isStreamInAssembly(rawPipelineModel) {
        return (
            this.isInAssembly(rawPipelineModel, 'stream') ||
            this.isInAssembly(rawPipelineModel, 'set')
        );
    }

    isActionInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, 'action');
    }

    isSepaInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, 'sepa');
    }

    onlyOnePipelineCreated(rawPipelineModel, jsplumbBridge: JsplumbBridge) {
        const g = this.makeGraph(rawPipelineModel, jsplumbBridge);
        const tarjan = dagre.graphlib.alg.tarjan(g);

        return tarjan.length === 1;
    }

    isInAssembly(rawPipelineModel: PipelineElementConfig[], type) {
        let isElementInAssembly = false;
        rawPipelineModel.forEach(pe => {
            if (pe.type === type && !pe.settings.disabled) {
                isElementInAssembly = true;
            }
        });
        return isElementInAssembly;
    }

    makeGraph(
        rawPipelineModel: PipelineElementConfig[],
        jsplumbBridge: JsplumbBridge,
    ) {
        const g = new dagre.graphlib.Graph();
        g.setGraph({ rankdir: 'LR' });
        g.setDefaultEdgeLabel(() => {
            return {};
        });
        const nodes = $('#assembly').find("div[id^='jsplumb']").get();
        for (let i = 0; i < nodes.length; i++) {
            const n = nodes[i];
            const elementOptions = this.getElementOptions(
                n.id,
                rawPipelineModel,
            );
            if (!elementOptions.settings.disabled) {
                g.setNode(n.id, {
                    label: n.id,
                    type: elementOptions.type,
                    name: elementOptions.payload.name,
                    endpointCount: jsplumbBridge.getEndpointCount(n.id),
                });
            }
        }
        const edges = jsplumbBridge.getAllConnections() as Connection[];
        edges.forEach((edge, i) => {
            const c = edges[i];
            g.setEdge(c.source.id, c.target.id);
            g.setEdge(c.target.id, c.source.id);
        });
        return g;
    }

    getElementOptions(id, rawPipelineModel: PipelineElementConfig[]) {
        return rawPipelineModel.find(pe => pe.payload.dom === id);
    }
}
