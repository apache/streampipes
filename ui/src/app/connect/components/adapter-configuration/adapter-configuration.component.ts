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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import {
  AdapterDescriptionUnion,
  EventSchema,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  SpecificAdapterSetDescription,
  SpecificAdapterStreamDescription
} from '@streampipes/platform-services';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { ConnectService } from '../../services/connect.service';
import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';
import { TransformationRuleService } from '../../services/transformation-rule.service';
import { Router } from '@angular/router';

@Component({
  selector: 'sp-adapter-configuration',
  templateUrl: './adapter-configuration.component.html',
  styleUrls: ['./adapter-configuration.component.scss']
})
export class AdapterConfigurationComponent implements OnInit {

  /**
   * Used to display the type of the configured adapter
   */
  @Input() displayName = '';
  @Input() adapter: AdapterDescriptionUnion;
  @Input() isEditMode;

  /**
   * Required to render the corresponding components
   */
  isDataStreamDescription = true;
  isGenericAdapter = false;

  myStepper: MatStepper;
  parentForm: UntypedFormGroup;

  eventSchema: EventSchema;
  oldEventSchema: EventSchema;

  private eventSchemaComponent: EventSchemaComponent;

  constructor(
    private transformationRuleService: TransformationRuleService,
    private shepherdService: ShepherdService,
    private connectService: ConnectService,
    private _formBuilder: UntypedFormBuilder,
    private router: Router,
  ) {
  }

  ngOnInit() {
    this.parentForm = this._formBuilder.group({});

    this.isDataStreamDescription = this.connectService.isDataStreamDescription(this.adapter);
    this.isGenericAdapter = this.connectService.isGenericDescription(this.adapter);

    this.eventSchema = this.connectService.getEventSchema(this.adapter);

  }

  removeSelection() {
    this.router.navigate(['connect', 'create']).then();
  }

  clickProtocolSettingsNextButton() {
    this.shepherdService.trigger('specific-settings-next-button');
    this.goForward();
  }

  clickSpecificSettingsNextButton() {
    this.shepherdService.trigger('specific-settings-next-button');
    this.guessEventSchema();
    this.goForward();
  }

  clickEventSchemaNextButtonButton() {
    this.setSchema();

    this.shepherdService.trigger('event-schema-next-button');
    this.goForward();
  }

  clickFormatSelectionNextButton() {
    this.shepherdService.trigger('format-selection-next-button');
    this.guessEventSchema();
    this.goForward();
  }

  guessEventSchema() {
    const eventSchema: EventSchema = this.connectService.getEventSchema(this.adapter);
    if (eventSchema.eventProperties.length === 0) {
      this.eventSchemaComponent.guessSchema();
    } else {
      this.oldEventSchema = eventSchema;
    }


  }

  public setSchema() {

    if (this.adapter instanceof GenericAdapterSetDescription) {
      (this.adapter as GenericAdapterSetDescription).dataSet.eventSchema = this.eventSchema;
    } else if (this.adapter instanceof SpecificAdapterSetDescription) {
      (this.adapter as SpecificAdapterSetDescription).dataSet.eventSchema = this.eventSchema;
    } else if (this.adapter instanceof GenericAdapterStreamDescription) {
      (this.adapter as GenericAdapterStreamDescription).dataStream.eventSchema = this.eventSchema;
    } else if (this.adapter instanceof SpecificAdapterStreamDescription) {
      (this.adapter as SpecificAdapterStreamDescription).dataStream.eventSchema = this.eventSchema;
    } else {
      console.log('Error: Adapter type is unknown');
    }

    this.transformationRuleService.setOldEventSchema(this.oldEventSchema);

    this.transformationRuleService.setNewEventSchema(this.eventSchema);
    this.adapter.rules = this.transformationRuleService.getTransformationRuleDescriptions();
  }

  goBack() {
    this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
  }

  goForward() {
    this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
  }

  public adapterWasStarted() {
    this.router.navigate(['connect']).then();
  }

  @ViewChild(EventSchemaComponent) set schemaComponent(eventSchemaComponent: EventSchemaComponent) {
    this.eventSchemaComponent = eventSchemaComponent;
  }

  @ViewChild('stepper') set stepperComponent(stepperComponent: MatStepper) {
    this.myStepper = stepperComponent;
  }
}
