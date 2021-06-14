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

import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import {
  AdapterDescription,
  AdapterDescriptionUnion,
  EventProperty,
  EventRateTransformationRuleDescription,
  EventSchema,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  RemoveDuplicatesTransformationRuleDescription,
  SpecificAdapterSetDescription,
  SpecificAdapterStreamDescription,
  TransformationRuleDescriptionUnion
} from '../../../core-model/gen/streampipes-model';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Logger } from '../../../shared/logger/default-log.service';
import { ConnectService } from '../../services/connect.service';
import { TimestampPipe } from '../../filter/timestamp.pipe';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';
import { RestService } from '../../services/rest.service';
import { EventSchemaComponent } from '../schema-editor/event-schema/event-schema.component';
import { TransformationRuleService } from '../../services/transformation-rule.service';
import { AdapterStartedDialog } from '../../dialog/adapter-started/adapter-started-dialog.component';
import { IconService } from '../../services/icon.service';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';
import { PanelType } from '../../../core-ui/dialog/base-dialog/base-dialog.model';

@Component({
    selector: 'sp-new-adapter',
    templateUrl: './new-adapter.component.html',
    styleUrls: ['./new-adapter.component.css'],
})
export class NewAdapterComponent implements OnInit, AfterViewInit {

    selectedUploadFile: File;
    fileName;
    isGenericAdapter = false;
    isDataSetDescription = false;
    isDataStreamDescription = false;


    @Input()
    adapter: AdapterDescriptionUnion;

    @Output()
    removeSelectionEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

    @ViewChild('stepper', { static: true }) myStepper: MatStepper;


    protocolConfigurationValid: boolean;
    formatConfigurationValid: boolean;

    removeDuplicates = false;
    removeDuplicatesTime: number;

    eventRateReduction = false;
    eventRateTime: number;
    eventRateMode = 'none';

    saveInDataLake = false;
    dataLakeTimestampField: string;

    startAdapterFormGroup: FormGroup;

    eventSchema: EventSchema;
    oldEventSchema: EventSchema;

    timestampPropertiesInSchema: EventProperty[] = [];

    hasInput: boolean[];

    // indicates whether user uses a template or not
    fromTemplate = false;

    // deactivates all edit functions when user starts a template
    isEditable = true;

    @ViewChild(EventSchemaComponent, { static: true })
    private eventSchemaComponent: EventSchemaComponent;

    isSetAdapter = false;

    completedStaticProperty: ConfigurationInfo;

    isPreviewEnabled = false;

    parentForm: FormGroup;
    specificAdapterSettingsFormValid = false;
    genericAdapterSettingsFormValid = false;
    viewInitialized = false;

    constructor(
        private logger: Logger,
        private restService: RestService,
        private transformationRuleService: TransformationRuleService,
        private dialogService: DialogService,
        private shepherdService: ShepherdService,
        private connectService: ConnectService,
        private _formBuilder: FormBuilder,
        private iconService: IconService,
        private timestampPipe: TimestampPipe,
        private changeDetectorRef: ChangeDetectorRef) { }

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

        this.parentForm = this._formBuilder.group({
        });


        this.isGenericAdapter = this.connectService.isGenericDescription(this.adapter);
        this.isDataSetDescription = this.connectService.isDataSetDescription(this.adapter);
        this.isDataStreamDescription = this.connectService.isDataStreamDescription(this.adapter);
        this.formatConfigurationValid = false;


        this.startAdapterFormGroup = this._formBuilder.group({
            startAdapterFormCtrl: ['', Validators.required]
        });

        this.protocolConfigurationValid = false;

        this.eventSchema = this.getEventSchema(this.adapter);

        if (this.eventSchema.eventProperties.length > 0) {

            // Timeout is needed for stepper to work correctly. Without the stepper is frozen when initializing with
            // step 2. Can be removed when a better solution is founf.
            setTimeout(() => {
                this.goForward(this.myStepper);
                this.goForward(this.myStepper);
            }, 1);

            this.fromTemplate = true;
            this.isEditable = false;
            this.oldEventSchema = this.eventSchema;
        }

        // this.parentForm.statusChanges.subscribe((status) => {
        //     this.genericadapterSettingsFormValid  = this.viewInitialized && this.parentForm.valid;
        // });
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
        // this.genericAdapterSettingsFormValid  = this.viewInitialized && this.parentForm.valid;
        this.changeDetectorRef.detectChanges();
    }

    public showPreview(isPreviewEnabled) {
        this.isPreviewEnabled = isPreviewEnabled;
    }

    public triggerDialog(storeAsTemplate: boolean) {
        if (this.removeDuplicates) {
            const removeDuplicates: RemoveDuplicatesTransformationRuleDescription = new RemoveDuplicatesTransformationRuleDescription();
            removeDuplicates['@class'] = 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription';
            removeDuplicates.filterTimeWindow = (this.removeDuplicatesTime) as any;
            this.adapter.rules.push(removeDuplicates);
        }
        if (this.eventRateReduction) {
            const eventRate: EventRateTransformationRuleDescription = new EventRateTransformationRuleDescription();
            eventRate['@class'] = 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription';
            eventRate.aggregationTimeWindow = this.eventRateMode as any;
            eventRate.aggregationType = this.eventRateMode;
            this.adapter.rules.push(eventRate);
        }

        const dialogRef = this.dialogService.open(AdapterStartedDialog, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Adapter generation',
            width: '70vw',
            data: {
                'adapter': this.adapter,
                'storeAsTemplate': storeAsTemplate,
                'saveInDataLake': this.saveInDataLake,
                'dataLakeTimestampField': this.dataLakeTimestampField
            }
        });

        this.shepherdService.trigger('button-startAdapter');

        dialogRef.afterClosed().subscribe(result => {
            this.updateAdapterEmitter.emit();
            this.removeSelectionEmitter.emit();
        });
    }

    public saveTemplate() {
        this.triggerDialog(true);
    }

    public startAdapter() {
        this.triggerDialog(false);
    }

    validateFormat(valid) {
        this.formatConfigurationValid = valid;
    }

    validateSpecificAdapterForm(valid) {
        this.specificAdapterSettingsFormValid = valid;
    }

    validateGenericAdapterForm(valid) {
        this.genericAdapterSettingsFormValid = valid;
    }

    removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    clickProtocolSettingsNextButton(stepper: MatStepper) {
        this.shepherdService.trigger('specific-settings-next-button');
        this.goForward(stepper);
    }

    clickSpecificSettingsNextButton(stepper: MatStepper) {
        this.shepherdService.trigger('specific-settings-next-button');
        this.guessEventSchema();
        this.goForward(stepper);
    }

    clickEventSchemaNextButtonButton(stepper: MatStepper) {
        if (this.isEditable) {
            this.setSchema();
        }

        // Auto selection of timestamp field for datalake
        this.timestampPropertiesInSchema = this.timestampPipe.transform(this.eventSchema.eventProperties, '');
        if (this.timestampPropertiesInSchema.length > 0) {
            this.dataLakeTimestampField = this.timestampPropertiesInSchema[0].runtimeName;
        }

        this.shepherdService.trigger('event-schema-next-button');
        this.goForward(stepper);

        if (this.adapter instanceof GenericAdapterSetDescription || this.adapter instanceof SpecificAdapterSetDescription) {
            this.isSetAdapter = true;
        }

    }

    clickFormatSelectionNextButton(stepper: MatStepper) {
        this.shepherdService.trigger('format-selection-next-button');
        this.guessEventSchema();
        this.goForward(stepper);
    }

    guessEventSchema() {
        const eventSchema: EventSchema = this.getEventSchema(this.adapter);
        if (eventSchema.eventProperties.length === 0) {
            this.eventSchemaComponent.guessSchema();
        } else {
            this.oldEventSchema = eventSchema;
        }
    }

    getEventSchema(adapter: AdapterDescription): EventSchema {
        let eventSchema: EventSchema;

        if (adapter instanceof GenericAdapterSetDescription) {
            eventSchema = (adapter as GenericAdapterSetDescription).dataSet.eventSchema || new EventSchema();
        } else if (adapter instanceof SpecificAdapterSetDescription) {
            eventSchema = (adapter as SpecificAdapterSetDescription).dataSet.eventSchema || new EventSchema();
        } else if (adapter instanceof GenericAdapterStreamDescription) {
            eventSchema = (adapter as GenericAdapterStreamDescription).dataStream.eventSchema || new EventSchema();
        } else if (adapter instanceof SpecificAdapterStreamDescription) {
            eventSchema = (adapter as SpecificAdapterStreamDescription).dataStream.eventSchema || new EventSchema();
        } else {
            eventSchema = new EventSchema();
        }

        if (eventSchema && eventSchema.eventProperties && eventSchema.eventProperties.length > 0) {
            return eventSchema;
        } else {
            eventSchema.eventProperties = [];
            return eventSchema;
        }
    }

    public setSchema() {

        if (this.adapter instanceof  GenericAdapterSetDescription) {
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

    goBack(stepper: MatStepper) {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
    }
    goForward(stepper: MatStepper) {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
    }

    triggerUpdate(configurationInfo: ConfigurationInfo) {
        this.completedStaticProperty = {...configurationInfo};
    }
}
