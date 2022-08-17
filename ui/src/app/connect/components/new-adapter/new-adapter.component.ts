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

import { AfterViewInit, ChangeDetectorRef, Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import {
  AdapterDescriptionUnion,
  AdapterService,
  EventSchema,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  SpecificAdapterSetDescription,
  SpecificAdapterStreamDescription,
  TransformationRuleDescriptionUnion
} from '@streampipes/platform-services';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { ConnectService } from '../../services/connect.service';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';
import { RestService } from '../../services/rest.service';
import { EventSchemaComponent } from './schema-editor/event-schema/event-schema.component';
import { TransformationRuleService } from '../../services/transformation-rule.service';
import { IconService } from '../../services/icon.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpConnectRoutes } from '../../connect.routes';

@Component({
  selector: 'sp-new-adapter',
  templateUrl: './new-adapter.component.html',
  styleUrls: ['./new-adapter.component.scss']
})
export class NewAdapterComponent implements OnInit, AfterViewInit {

  selectedUploadFile: File;
  fileName;
  isGenericAdapter = false;
  isDataSetDescription = false;
  isDataStreamDescription = false;

  dataLakeTimestampField: string;

  @Input()
  adapter: AdapterDescriptionUnion;

  myStepper: MatStepper;

  protocolConfigurationValid: boolean;
  formatConfigurationValid: boolean;

  eventSchema: EventSchema;
  oldEventSchema: EventSchema;


  hasInput: boolean[];

  // indicates whether user uses a template or not
  fromTemplate = false;

  // deactivates all edit functions when user starts a template
  isEditable = true;

  private eventSchemaComponent: EventSchemaComponent;

  completedStaticProperty: ConfigurationInfo;

  isPreviewEnabled = false;

  parentForm: FormGroup;
  genericAdapterSettingsFormValid = false;
  viewInitialized = false;

  constructor(
    private restService: RestService,
    private transformationRuleService: TransformationRuleService,
    private shepherdService: ShepherdService,
    private connectService: ConnectService,
    private _formBuilder: FormBuilder,
    private iconService: IconService,
    private changeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private dataMarketplaceService: AdapterService,
    private router: Router,
    private breadcrumbService: SpBreadcrumbService) {
  }

  handleFileInput(files: any) {
    this.selectedUploadFile = files[0];
    this.fileName = this.selectedUploadFile.name;

    this.iconService.toBase64(this.selectedUploadFile)
      .then(
        data => {
          this.adapter.icon = (data as string);
        }
      );
  }

  ngOnInit() {

    this.dataMarketplaceService.getAdapterDescriptions().subscribe(adapters => {
      const adapter = adapters.find(a => a.appId === this.route.snapshot.params.appId);
      this.adapter = this.connectService.cloneAdapterDescription(adapter);
      this.breadcrumbService.updateBreadcrumb(this.breadcrumbService.makeRoute([SpConnectRoutes.BASE, SpConnectRoutes.CREATE], this.adapter.name));
      (this.adapter as any).templateTitle = this.adapter.name;
      this.adapter.name = '';
      this.adapter.description = '';
      this.parentForm = this._formBuilder.group({});


      this.isGenericAdapter = this.connectService.isGenericDescription(this.adapter);
      this.isDataSetDescription = this.connectService.isDataSetDescription(this.adapter);
      this.isDataStreamDescription = this.connectService.isDataStreamDescription(this.adapter);
      this.formatConfigurationValid = false;


      // this.startAdapterFormGroup = this._formBuilder.group({
      //     startAdapterFormCtrl: ['', Validators.required]
      // });

      this.protocolConfigurationValid = false;

      this.eventSchema = this.connectService.getEventSchema(this.adapter);

      if (this.eventSchema.eventProperties.length > 0) {

        // Timeout is needed for stepper to work correctly. Without the stepper is frozen when initializing with
        // step 2. Can be removed when a better solution is found.
        setTimeout(() => {
          this.goForward();
          this.goForward();
        }, 1);

        this.fromTemplate = true;
        this.isEditable = false;
        this.oldEventSchema = this.eventSchema;
      }
    });


    // this.parentForm.statusChanges.subscribe((status) => {
    //     this.genericadapterSettingsFormValid  = this.viewInitialized && this.parentForm.valid;
    // });
  }

  ngAfterViewInit() {
    this.viewInitialized = true;
    // this.genericAdapterSettingsFormValid  = this.viewInitialized && this.parentForm.valid;
    this.changeDetectorRef.detectChanges();
  }


  removeSelection() {
    this.router.navigate(['connect', 'create']);
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
    if (this.isEditable) {
      this.setSchema();
    }


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
    const transformationRules: TransformationRuleDescriptionUnion[] =
      this.transformationRuleService.getTransformationRuleDescriptions();
    this.adapter.rules = transformationRules;
  }

  goBack() {
    this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
  }

  goForward() {
    this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
  }

  public adapterWasStarted() {
    this.router.navigate(['connect']);
  }

  @ViewChild(EventSchemaComponent) set schemaComponent(eventSchemaComponent: EventSchemaComponent) {
    this.eventSchemaComponent = eventSchemaComponent;
  }

  @ViewChild('stepper') set stepperComponent(stepperComponent: MatStepper) {
    this.myStepper = stepperComponent;
  }
}
