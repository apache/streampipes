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

import {Component, Inject} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {ShepherdService} from '../../../services/tour/shepherd.service';
import {RestService} from "../../rest.service";
import {StatusMessage} from "../../model/message/StatusMessage";
import {GenericAdapterSetDescription} from '../../model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from '../../model/connect/SpecificAdapterSetDescription';
import {PipelineTemplateService} from '../../../platform-services/apis/pipeline-template.service';
import {FreeTextStaticProperty} from '../../model/FreeTextStaticProperty';
import {StaticProperty} from '../../model/StaticProperty';
import {MappingPropertyUnary} from '../../model/MappingPropertyUnary';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './dialog-adapter-started.html',
    styleUrls: ['./adapter-started-dialog.component.css'],
})
export class AdapterStartedDialog {

    adapterInstalled: boolean = false;
    private adapterStatus: StatusMessage;
    private streamDescription: any;
    private pollingActive: boolean = false;
    private runtimeData: any;
    private isSetAdapter: boolean = false;
    private isTemplate: boolean = false;

    private saveInDataLake: boolean;

    constructor(
        public dialogRef: MatDialogRef<AdapterStartedDialog>,
        private restService: RestService,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private ShepherdService: ShepherdService,
        private pipelineTemplateService: PipelineTemplateService) { }

    ngOnInit() {
        this.startAdapter();
    }

    startAdapter() {
        if (this.data.storeAsTemplate) {

            this.restService.addAdapterTemplate(this.data.adapter).subscribe(x => {
                this.adapterStatus = x;
                this.isTemplate = true;
                this.adapterInstalled = true;
            });

        } else {

            var newAdapter = this.data.adapter;
            this.restService.addAdapter(this.data.adapter).subscribe(x => {
                this.adapterInstalled = true;
                this.adapterStatus = x;
                if (x.success) {

                    // Start preview on streams and message for sets
                    if (newAdapter instanceof GenericAdapterSetDescription || newAdapter instanceof SpecificAdapterSetDescription) {
                        this.isSetAdapter = true;
                    } else {
                        this.restService.getSourceDetails(x.notifications[0].title).subscribe(x => {
                            this.streamDescription = x.spDataStreams[0];
                            this.pollingActive = true;
                            this.getLatestRuntimeInfo();
                        });
                    }

                    if (this.data.saveInDataLake) {
                        const templateName = "org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate";
                        x.notifications[0].title
                        this.pipelineTemplateService.getPipelineTemplateInvocation(x.notifications[0].title + "/streams", templateName)
                            .subscribe(res => {

                                res.list.forEach(property => {
                                    if (property instanceof FreeTextStaticProperty && "domId2db_measurement" == property.internalName) {
                                        property.value = this.data.adapter.label.toLowerCase().replace(" ", "_");
                                    } else if (property instanceof MappingPropertyUnary && "domId2timestamp_mapping" == property.internalName) {
                                        property.selectedProperty = "s0::" + this.data.dataLakeTimestampField;
                                    }


                                });

                                res.pipelineTemplateId = templateName;
                                res.name = this.data.adapter.label;

                                this.pipelineTemplateService.createPipelineTemplateInvocation(res);

                            });
                    }


                }
            });

        }
    }

    getLatestRuntimeInfo() {
        this.restService.getRuntimeInfo(this.streamDescription).subscribe(data => {
            if (!(Object.keys(data).length === 0 && data.constructor === Object)) {
                this.runtimeData = data;
            }

            if (this.pollingActive) {
                setTimeout(() => {
                    this.getLatestRuntimeInfo();
                }, 1000);
            }
        });
    }

    isPropertyType(property, type) {
      return property.properties.domainProperties !== undefined && property.properties.domainProperties.length === 1 &&
        property.properties.domainProperties[0] === type;
    }

    isImage(property) {
        return this.isPropertyType(property, 'https://image.com');
    }

    isTimestamp(property) {
      return this.isPropertyType(property, 'http://schema.org/DateTime');
    }

    hasNoDomainProperty(property) {
        if (this.isTimestamp(property) || this.isImage(property)) {
            return false;
        } else {
            return true;
        }
    }

    onCloseConfirm() {
        this.pollingActive = false;
        this.dialogRef.close('Confirm');
        this.ShepherdService.trigger("confirm_adapter_started_button");
    }

}