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
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { RestService } from '../../services/rest.service';
import {
  AdapterDescriptionUnion,
  GenericAdapterSetDescription,
  Message,
  PipelineOperationStatus,
  SpDataStream,
  SpecificAdapterSetDescription
} from '../../../core-model/gen/streampipes-model';
import { DialogRef } from '../../../core-ui/dialog/base-dialog/dialog-ref';
import { PipelineTemplateService } from '../../../platform-services/apis/pipeline-template.service';
import { PipelineInvocationBuilder } from '../../../core-services/template/PipelineInvocationBuilder';

@Component({
  selector: 'sp-dialog-adapter-started-dialog',
  templateUrl: './adapter-started-dialog.component.html',
  styleUrls: ['./adapter-started-dialog.component.scss']
})
export class AdapterStartedDialog implements OnInit {

  adapterInstalled = false;
  public adapterStatus: Message;
  public streamDescription: SpDataStream;
  private pollingActive = false;
  public runtimeData: any;
  public isSetAdapter = false;
  public isTemplate = false;
  public pipelineOperationStatus: PipelineOperationStatus;

  @Input()
  storeAsTemplate: boolean;

  @Input()
  adapter: AdapterDescriptionUnion;

  @Input()
  saveInDataLake: boolean;

  @Input()
  dataLakeTimestampField: string;

  constructor(
    public dialogRef: DialogRef<AdapterStartedDialog>,
    private restService: RestService,
    private shepherdService: ShepherdService,
    private pipelineTemplateService: PipelineTemplateService) {
  }

  ngOnInit() {
    this.startAdapter();
  }

  startAdapter() {
    if (this.storeAsTemplate) {

      this.restService.addAdapterTemplate(this.adapter).subscribe(x => {
        this.adapterStatus = x as Message;
        this.isTemplate = true;
        this.adapterInstalled = true;
      });

    } else {

      const newAdapter = this.adapter;
      this.restService.addAdapter(this.adapter).subscribe(x => {
        this.adapterStatus = x;
        if (x.success) {

          // Start preview on streams and message for sets
          if (newAdapter instanceof GenericAdapterSetDescription || newAdapter instanceof SpecificAdapterSetDescription) {
            this.isSetAdapter = true;
          } else {
            this.restService.getSourceDetails(x.notifications[0].title).subscribe(st => {
              this.streamDescription = st;
              this.pollingActive = true;
            });
          }

          if (this.saveInDataLake) {
            const pipelineId = 'org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate';
            this.pipelineTemplateService.getPipelineTemplateInvocation(this.adapter.adapterId, pipelineId)
              .subscribe(res => {

                const pipelineName = 'Persist ' + this.adapter.name;

                let indexName = this.adapter.name
                  .toLowerCase()
                  .replace(/ /g, '')
                  .replace(/\./g, '');

                // Ensure that index name is no number
                if (!Number.isNaN(indexName)) {
                  indexName = 'sp' + indexName;
                }
                const pipelineInvocation = PipelineInvocationBuilder
                  .create(res)
                  .setName(pipelineName)
                  .setTemplateId(pipelineId)
                  .setFreeTextStaticProperty('db_measurement', indexName)
                  .setMappingPropertyUnary('timestamp_mapping', 's0::' + this.dataLakeTimestampField)
                  .build();

                this.pipelineTemplateService.createPipelineTemplateInvocation(pipelineInvocation).subscribe(pipelineOperationStatus => {
                  this.pipelineOperationStatus = pipelineOperationStatus;
                  this.adapterInstalled = true;
                });
              });
          } else {
            this.adapterInstalled = true;
          }
        }
      });

    }
  }

  onCloseConfirm() {
    this.pollingActive = false;
    this.dialogRef.close('Confirm');
    this.shepherdService.trigger('confirm_adapter_started_button');
  }

}
