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

import { Component, Input, OnInit } from '@angular/core';

import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import {
    HistoricalMonitoringData,
    ObservedMetricsStream,
} from '../../model/pipeline-details.model';

@Component({
    selector: 'sp-pipeline-element-statistics',
    templateUrl: './pipeline-element-statistics.component.html',
    styleUrls: ['./pipeline-element-statistics.component.scss'],
})
export class PipelineElementStatisticsComponent implements OnInit {
    @Input()
    allElements: (
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation
    )[];

    @Input()
    pipelineElement:
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation;

    _metricsInfo: SpMetricsEntry;

    currentPipelineElement:
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation;
    consumedMessagesFirstInputStream = '';
    consumedMessagesSecondInputStream = '';

    producedMessages: string | number = '';

    cardColor = 'rgb(27, 20, 100)';
    deactivatedCardColor = 'rgb(241,241,241)';

    textColor = 'rgb(208,208,208)';
    deactivatedTextColor = 'rgb(205,205,205)';

    bandColor = 'rgb(27, 20, 100)';
    deactivatedBandColor = 'rgb(241,241,241)';
    okBandColor = 'rgb(11,186,0)';
    warningBandColor = 'rgb(253,144,0)';

    chartBackgroundColor = 'rgb(27, 20, 100)';
    chartTextColor = 'rgb(208,208,208)';

    consumedMessagesFirstStreamBandColor: string;
    consumedMessagesSecondStreamBandColor: string;

    notAvailable = '-';

    historicFirstConsumedInputValues: HistoricalMonitoringData[] = [];
    historicSecondConsumedInputValues: HistoricalMonitoringData[] = [];
    historicProducedOutputValues: HistoricalMonitoringData[] = [];

    consumedMessagesFirstStreamLastValue = -1;
    consumedMessagesSecondStreamLastValue = -1;
    producedMessageOutputLastValue = -1;

    consumedMessagesFirstStreamAvailable = false;
    consumedMessagesSecondStreamAvailable = false;
    producedMessagesAvailable = false;

    observedInputStreams: ObservedMetricsStream[] = [];
    dataSink = false;

    ngOnInit(): void {
        this.dataSink = this.pipelineElement instanceof DataSinkInvocation;
        if (
            this.pipelineElement instanceof DataSinkInvocation ||
            this.pipelineElement instanceof DataProcessorInvocation
        ) {
            this.pipelineElement.inputStreams.forEach(is => {
                const identifier =
                    is.eventGrounding.transportProtocols[0].topicDefinition
                        .actualTopicName;
                this.observedInputStreams.push({
                    pipelineElementName: this.extractName(identifier),
                    identifier,
                });
            });
        }
    }

    extractName(outputTopic: string): string {
        return this.allElements
            .filter(el => !(el instanceof DataSinkInvocation))
            .find(el => {
                if (el instanceof DataProcessorInvocation) {
                    return (
                        el.outputStream.eventGrounding.transportProtocols[0]
                            .topicDefinition.actualTopicName === outputTopic
                    );
                } else {
                    return (
                        (el as SpDataStream).eventGrounding
                            .transportProtocols[0].topicDefinition
                            .actualTopicName === outputTopic
                    );
                }
            }).name;
    }

    updateMonitoringInfo() {}

    get metricsInfo() {
        return this._metricsInfo;
    }

    @Input()
    set metricsInfo(metricsInfo: SpMetricsEntry) {
        this._metricsInfo = metricsInfo;
        this.updateMonitoringInfo();
    }
}
