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
  FreeTextStaticProperty,
  GenericAdapterSetDescription,
  MappingPropertyUnary,
  Message,
  SpDataStream,
  SpecificAdapterSetDescription
} from '../../../core-model/gen/streampipes-model';
import { DialogRef } from '../../../core-ui/dialog/base-dialog/dialog-ref';
import { PipelineTemplateService } from '../../../platform-services/apis/pipeline-template.service';

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
        this.adapterInstalled = true;
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
            const templateName = 'org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate';
            // x.notifications[0].title
            this.pipelineTemplateService.getPipelineTemplateInvocation(this.adapter.adapterId, templateName)
              .subscribe(res => {

                let indexName;
                res.staticProperties.forEach(property => {
                  if (property instanceof FreeTextStaticProperty && 'domId2db_measurement' === property.internalName) {
                    indexName = this.adapter.name.toLowerCase().replace(' ', '_');
                    property.value = indexName;
                  } else if (property instanceof MappingPropertyUnary && 'domId2timestamp_mapping' === property.internalName) {
                    property.selectedProperty = 's0::' + this.dataLakeTimestampField;
                  }
                });

                res.pipelineTemplateId = templateName;
                // res.name = this.data.adapter.label;
                this.pipelineTemplateService.createPipelineTemplateInvocation(res, indexName);
              });
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
