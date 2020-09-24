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

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";

import {
  DataProcessorInvocation, DataSinkInvocation,
  Pipeline,
  PipelineElementMonitoringInfo, SpDataSet, SpDataStream,
} from "../../../../core-model/gen/streampipes-model";
import {HistoricalMonitoringData} from "../../model/pipeline-details.model";

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
  consumedMessagesFirstInputStream: string = "";
  consumedMessagesSecondInputStream: string = "";

  producedMessages: string | number = "";

  cardColor: string = "rgb(27, 20, 100)";
  deactivatedCardColor: string = "rgb(241,241,241)";

  textColor: string = "rgb(208,208,208)";
  deactivatedTextColor: string = "rgb(205,205,205)";

  bandColor: string = "rgb(27, 20, 100)";
  deactivatedBandColor: string = "rgb(241,241,241)";
  okBandColor: string = "rgb(11,186,0)";
  warningBandColor: string = "rgb(253,144,0)";

  chartBackgroundColor: string = "rgb(27, 20, 100)";
  chartTextColor: string = "rgb(208,208,208)";

  consumedMessagesFirstStreamBandColor: string;
  consumedMessagesSecondStreamBandColor: string;

  notAvailable: string = "n/a";

  historicFirstConsumedInputValues: HistoricalMonitoringData[] = [];
  historicSecondConsumedInputValues: HistoricalMonitoringData[] = [];
  historicProducedOutputValues: HistoricalMonitoringData[] = [];

  consumedMessagesFirstStreamLastValue: number = -1;
  consumedMessagesSecondStreamLastValue: number = -1;
  producedMessageOutputLastValue: number = -1;

  ngOnInit(): void {

  }

  updateMonitoringInfo() {
    if (this.pipelineElementMonitoringInfo.inputTopicInfoExists) {
      let consumedMessages = this.pipelineElementMonitoringInfo.inputTopicInfo.map(info => {
        return {"count": (info.currentOffset - info.offsetAtPipelineStart),
          "lag": info.latestOffset - info.currentOffset,
          "currentOffset": info.currentOffset};
      });
      this.consumedMessagesFirstInputStream = consumedMessages[0].count + " / " + consumedMessages[0].lag;
      this.consumedMessagesSecondInputStream = consumedMessages.length > 1 ? consumedMessages[1].count + " / " + consumedMessages[1].lag : this.notAvailable;
      this.consumedMessagesFirstStreamBandColor = consumedMessages[0].lag > 10 ? this.warningBandColor : this.okBandColor;
      this.consumedMessagesSecondStreamBandColor = (consumedMessages.length > 1 ? (consumedMessages[1].lag > 10 ? this.warningBandColor : this.okBandColor) : this.deactivatedBandColor);

      this.makeHistoricData(consumedMessages[0], this.consumedMessagesFirstStreamLastValue, this.historicFirstConsumedInputValues);
      this.consumedMessagesFirstStreamLastValue = consumedMessages[0].count;
      this.historicFirstConsumedInputValues = [].concat(this.historicFirstConsumedInputValues);

      if (consumedMessages.length > 1) {
        this.makeHistoricData(consumedMessages[1], this.consumedMessagesSecondStreamLastValue, this.historicSecondConsumedInputValues);
        this.consumedMessagesSecondStreamLastValue = consumedMessages[1].count;
        this.historicSecondConsumedInputValues = [].concat(this.historicSecondConsumedInputValues);
      }
    } else {
      this.consumedMessagesFirstInputStream = this.notAvailable;
      this.consumedMessagesFirstStreamBandColor = this.deactivatedBandColor;
      this.consumedMessagesSecondInputStream = this.notAvailable;
      this.consumedMessagesSecondStreamBandColor = this.deactivatedBandColor;
    }
    if (this.pipelineElementMonitoringInfo.outputTopicInfoExists) {
      this.producedMessages = this.pipelineElementMonitoringInfo.outputTopicInfo.latestOffset - this.pipelineElementMonitoringInfo.outputTopicInfo.offsetAtPipelineStart;
      let producedMessage = {"count": this.producedMessages};
      this.makeHistoricData(producedMessage, this.producedMessageOutputLastValue, this.historicProducedOutputValues);
      this.producedMessageOutputLastValue = producedMessage.count;
      this.historicProducedOutputValues = [].concat(this.historicProducedOutputValues);
    } else {
      this.producedMessages = this.notAvailable;
    }
  }

  makeHistoricData(consumedMessage: any, lastValue: number, historicData: HistoricalMonitoringData[]) {
    console.log(consumedMessage);
    if (lastValue > -1) {
      let entry: HistoricalMonitoringData = {"name": new Date().toLocaleTimeString(), value: (consumedMessage.count - lastValue)};
      historicData.push(entry);
    }
    if (historicData.length > 10) {
      historicData.shift();
    } else {
      for (let i = 0; i < (10 - historicData.length); i++) {
        historicData.unshift({"name": i.toString(), "value": 0});
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