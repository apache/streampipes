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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  EventProperty,
  EventRateTransformationRuleDescription,
  EventSchema,
  GenericAdapterSetDescription,
  RemoveDuplicatesTransformationRuleDescription,
  SpecificAdapterSetDescription
} from '../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { AdapterStartedDialog } from '../../dialog/adapter-started/adapter-started-dialog.component';
import { PanelType } from '../../../core-ui/dialog/base-dialog/base-dialog.model';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';
import { ConnectService } from '../../services/connect.service';

@Component({
  selector: 'sp-start-adapter-configuration',
  templateUrl: './start-adapter-configuration.component.html',
  styleUrls: ['./start-adapter-configuration.component.scss']
})
export class StartAdapterConfigurationComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  @Input() eventSchema: EventSchema;

  /**
   * Cancels the adapter configuration process
   */
  @Output() removeSelectionEmitter: EventEmitter<boolean> = new EventEmitter();

  /**
   * Is called when the adapter was created
   */
  @Output() adapterStartedEmitter: EventEmitter<void> = new EventEmitter<void>();

  /**
   * Go to next configuration step when this is complete
   */
  @Output() goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

  @Output() updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

  /**
   * The form group to validate the configuration for the format
   */
  startAdapterForm: FormGroup;

  startAdapterSettingsFormValid = false;

  timestampPropertiesInSchema: EventProperty[] = [];

  // preprocessing rule variables
  removeDuplicates = false;
  removeDuplicatesTime: number;

  eventRateReduction = false;
  eventRateTime: number;
  eventRateMode = 'none';

  saveInDataLake = false;
  dataLakeTimestampField: string;


  constructor(
    private dialogService: DialogService,
    private shepherdService: ShepherdService,
    private connectService: ConnectService,
    private _formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    // initialize form for validation
    this.startAdapterForm = this._formBuilder.group({});
    this.startAdapterForm.statusChanges.subscribe((status) => {
      this.startAdapterSettingsFormValid = this.startAdapterForm.valid;
    });

    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof SpecificAdapterSetDescription) {
    }

    // Auto selection of timestamp field for datalake
    // const eventSchema = this.connectService.getEventSchema(this.adapterDescription);
    // this.timestampPropertiesInSchema = this.timestampPipe.transform(eventSchema.eventProperties, '');
    // if (this.timestampPropertiesInSchema.length > 0) {
    //   this.dataLakeTimestampField = this.timestampPropertiesInSchema[0].runtimeName;
    // }

  }

  public triggerDialog(directlyStartAdapter: boolean) {
    if (this.removeDuplicates) {
      const removeDuplicates: RemoveDuplicatesTransformationRuleDescription = new RemoveDuplicatesTransformationRuleDescription();
      removeDuplicates['@class'] = 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription';
      removeDuplicates.filterTimeWindow = (this.removeDuplicatesTime) as any;
      this.adapterDescription.rules.push(removeDuplicates);
    }
    if (this.eventRateReduction) {
      const eventRate: EventRateTransformationRuleDescription = new EventRateTransformationRuleDescription();
      eventRate['@class'] = 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription';
      eventRate.aggregationTimeWindow = this.eventRateTime;
      eventRate.aggregationType = this.eventRateMode;
      this.adapterDescription.rules.push(eventRate);
    }

    const dialogRef = this.dialogService.open(AdapterStartedDialog, {
      panelType: PanelType.STANDARD_PANEL,
      title: 'Adapter generation',
      width: '70vw',
      data: {
        'adapter': this.adapterDescription,
        'directlyStartAdapter': directlyStartAdapter,
        'saveInDataLake': this.saveInDataLake,
        'dataLakeTimestampField': this.dataLakeTimestampField
      }
    });

    this.shepherdService.trigger('button-startAdapter');

    dialogRef.afterClosed().subscribe(result => {
      this.adapterStartedEmitter.emit();
    });
  }

  public startAdapter() {
    this.triggerDialog(true);
  }

  public createAdapter() {
    this.triggerDialog(false);
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public goBack() {
    this.goBackEmitter.emit();
  }

}
