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

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import {
  DataProcessorInvocation, DataSinkInvocation,
  Pipeline, PipelineElementMonitoringInfo,
  PipelineMonitoringInfo,
  SpDataSet, SpDataStream
} from '@streampipes/platform-services';
import { PipelineMonitoringService } from '@streampipes/platform-services';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';

@Component({
  selector: 'pipeline-monitoring',
  templateUrl: './pipeline-monitoring.component.html',
  styleUrls: ['./pipeline-monitoring.component.scss']
})
export class PipelineMonitoringComponent implements OnInit, OnDestroy {

  _pipeline: Pipeline;

  @Output()
  reloadPipelineEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

  pipelineMonitoringInfo: PipelineMonitoringInfo;
  pipelineMonitoringInfoAvailable = false;

  allElements: (SpDataSet | SpDataStream | DataProcessorInvocation | DataSinkInvocation)[] = [];

  autoRefresh = true;

  pipelineElementMonitoringInfo: Map<string, PipelineElementMonitoringInfo>;

  hasPipelineWritePrivileges = false;

  constructor(private pipelineMonitoringService: PipelineMonitoringService,
              private pipelineOperationsService: PipelineOperationsService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.hasPipelineWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_PIPELINE);
    });
    this.collectAllElements();
    this.checkMonitoringInfoCollection();
  }

  checkMonitoringInfoCollection() {
    if (this.pipeline.running) {
      this.refreshMonitoringInfo();
    }
  }

  collectAllElements() {
    this.allElements = this.allElements
        .concat(this.pipeline.streams)
        .concat(this.pipeline.sepas)
        .concat(this.pipeline.actions);
  }

  refreshMonitoringInfo() {
    this.pipelineMonitoringService
        .getPipelineMonitoringInfo(this.pipeline._id)
        .subscribe(monitoringInfo => {
          this.pipelineElementMonitoringInfo = new Map<string, PipelineElementMonitoringInfo>();
          this.pipelineMonitoringInfo = monitoringInfo;
          monitoringInfo.pipelineElementMonitoringInfo.forEach(info => {
            this.pipelineElementMonitoringInfo.set(info.pipelineElementId, info);
          });
          this.pipelineMonitoringInfoAvailable = true;
          if (this.autoRefresh) {
            setTimeout(() => {
              this.refreshMonitoringInfo();
            }, 5000);
          }
        });
  }

  selectElement(pipelineElement) {
    document.getElementById(pipelineElement.elementId).scrollIntoView();
  }

  ngOnDestroy(): void {
    this.autoRefresh = false;
  }

  startPipeline() {
    this.pipelineOperationsService.startPipeline(this.pipeline._id, this.reloadPipelineEmitter);
  }

  @Input()
  set pipeline(pipeline: Pipeline) {
    this._pipeline = pipeline;
    this.checkMonitoringInfoCollection();
  }

  get pipeline() {
    return this._pipeline;
  }

}
