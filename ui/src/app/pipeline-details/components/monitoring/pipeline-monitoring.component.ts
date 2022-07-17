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

import { Component, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import {
  DataProcessorInvocation,
  DataSinkInvocation,
  PipelineElementMonitoringInfo,
  PipelineMonitoringInfo,
  PipelineMonitoringService,
  PipelineService,
  SpDataSet,
  SpDataStream
} from '@streampipes/platform-services';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';
import { AuthService } from '../../../services/auth.service';
import { SpPipelineDetailsDirective } from '../sp-pipeline-details.directive';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpPipelineRoutes } from '../../../pipelines/pipelines.routes';

@Component({
  selector: 'pipeline-monitoring',
  templateUrl: './pipeline-monitoring.component.html',
  styleUrls: ['./pipeline-monitoring.component.scss']
})
export class PipelineMonitoringComponent extends SpPipelineDetailsDirective implements OnInit, OnDestroy {

  pipelineMonitoringInfo: PipelineMonitoringInfo;
  pipelineMonitoringInfoAvailable = false;

  allElements: (SpDataSet | SpDataStream | DataProcessorInvocation | DataSinkInvocation)[] = [];

  autoRefresh = true;

  pipelineElementMonitoringInfo: Map<string, PipelineElementMonitoringInfo>;

  reloadPipelinesEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  reloadSubscription: Subscription;

  constructor(activatedRoute: ActivatedRoute,
              pipelineService: PipelineService,
              authService: AuthService,
              private pipelineMonitoringService: PipelineMonitoringService,
              private pipelineOperationsService: PipelineOperationsService,
              breadcrumbService: SpBreadcrumbService) {
    super(activatedRoute, pipelineService, authService, breadcrumbService);
  }

  ngOnInit(): void {
    super.onInit();
    this.reloadSubscription = this.reloadPipelinesEmitter.subscribe(reload => this.loadPipeline());
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
    this.reloadSubscription.unsubscribe();
  }

  startPipeline() {
    this.pipelineOperationsService.startPipeline(this.pipeline._id, this.reloadPipelinesEmitter);
  }

  onPipelineAvailable(): void {
    this.breadcrumbService.updateBreadcrumb([SpPipelineRoutes.BASE, {label: this.pipeline.name}, {label: 'Monitoring'} ]);
    this.collectAllElements();
    this.checkMonitoringInfoCollection();
  }

}
