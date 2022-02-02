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
  DataProcessorInvocation, DataSinkInvocation,
  Pipeline,
  PipelineElementMonitoringInfo, SpDataSet, SpDataStream,
} from '@streampipes/platform-services';
import { HistoricalMonitoringData } from '../../model/pipeline-details.model';

@Component({
  selector: 'pipeline-element-statistics',
  templateUrl: './pipeline-element-statistics.component.html',
  styleUrls: ['./pipeline-element-statistics.component.scss']
})
export class PipelineElementStatisticsComponent implements OnInit {

  @Input()
  pipeline: Pipeline;

  @Input()
  pipelineElement:  (SpDataSet | SpDataStream | DataProcessorInvocation | DataSinkInvocation);

  _pipelineElementMonitoringInfo: PipelineElementMonitoringInfo;

  currentPipelineElement: SpDataSet | SpDataStream | DataProcessorInvocation | DataSinkInvocation;
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

  ngOnInit(): void {
    this.producedMessagesAvailable = this.pipelineElementMonitoringInfo.producedMessageInfoExists;
    this.consumedMessagesFirstStreamAvailable = this.pipelineElementMonitoringInfo.consumedMessageInfoExists;
    this.consumedMessagesSecondStreamAvailable =
        this.pipelineElementMonitoringInfo.consumedMessageInfoExists && this.pipelineElementMonitoringInfo.consumedMessagesInfos.length > 1;
  }

  updateMonitoringInfo() {
    if (this.pipelineElementMonitoringInfo.consumedMessageInfoExists) {
      const consumedMessages = this.pipelineElementMonitoringInfo.consumedMessagesInfos;
      this.consumedMessagesFirstInputStream = consumedMessages[0].consumedMessagesSincePipelineStart + ' / ' + consumedMessages[0].lag;
      this.consumedMessagesSecondInputStream =
          consumedMessages.length > 1 ? consumedMessages[1].consumedMessagesSincePipelineStart +
              ' / ' + consumedMessages[1].lag : this.notAvailable;
      this.consumedMessagesFirstStreamBandColor = consumedMessages[0].lag > 10 ? this.warningBandColor : this.okBandColor;
      this.consumedMessagesSecondStreamBandColor =
          (consumedMessages.length > 1 ? (consumedMessages[1].lag > 10 ? this.warningBandColor : this.okBandColor) :
              this.deactivatedBandColor);

      const consumedMessage = {'count': consumedMessages[0].consumedMessagesSincePipelineStart};
      this.makeHistoricData(consumedMessage, this.consumedMessagesFirstStreamLastValue, this.historicFirstConsumedInputValues);
      this.consumedMessagesFirstStreamLastValue = consumedMessages[0].consumedMessagesSincePipelineStart;
      this.historicFirstConsumedInputValues = [].concat(this.historicFirstConsumedInputValues);

      if (consumedMessages.length > 1) {
        this.makeHistoricData(consumedMessages[1], this.consumedMessagesSecondStreamLastValue, this.historicSecondConsumedInputValues);
        this.consumedMessagesSecondStreamLastValue = consumedMessages[1].consumedMessagesSincePipelineStart;
        this.historicSecondConsumedInputValues = [].concat(this.historicSecondConsumedInputValues);
      }
    } else {
      this.consumedMessagesFirstInputStream = this.notAvailable;
      this.consumedMessagesFirstStreamBandColor = this.deactivatedBandColor;
      this.consumedMessagesSecondInputStream = this.notAvailable;
      this.consumedMessagesSecondStreamBandColor = this.deactivatedBandColor;
    }
    if (this.pipelineElementMonitoringInfo.producedMessageInfoExists) {
      this.producedMessages = this.pipelineElementMonitoringInfo.producedMessagesInfo.totalProducedMessagesSincePipelineStart;
      const producedMessage = {'count': this.producedMessages};
      this.makeHistoricData(producedMessage, this.producedMessageOutputLastValue, this.historicProducedOutputValues);
      this.producedMessageOutputLastValue = producedMessage.count;
      this.historicProducedOutputValues = [].concat(this.historicProducedOutputValues);
    } else {
      this.producedMessages = this.notAvailable;
    }
  }

  makeHistoricData(consumedMessage: any, lastValue: number, historicData: HistoricalMonitoringData[]) {
    if (lastValue > -1) {
      const entry: HistoricalMonitoringData = {'name': new Date().toLocaleTimeString(), value: (consumedMessage.count - lastValue)};
      historicData.push(entry);
    }
    if (historicData.length > 10) {
      historicData.shift();
    } else {
      for (let i = 0; i < (10 - historicData.length); i++) {
        historicData.unshift({'name': i.toString(), 'value': 0});
      }
    }
  }

  get pipelineElementMonitoringInfo() {
    return this._pipelineElementMonitoringInfo;
  }

  @Input()
  set pipelineElementMonitoringInfo(pipelineElementMonitoringInfo: PipelineElementMonitoringInfo) {
    this._pipelineElementMonitoringInfo = pipelineElementMonitoringInfo;
    this.updateMonitoringInfo();
  }


}
