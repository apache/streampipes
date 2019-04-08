///<reference path="../model/connect/AdapterDescription.ts"/>
import {Component, OnInit, Input, Output, EventEmitter, ViewChild, PipeTransform} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { RestService } from '../rest.service';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { MatDialog } from '@angular/material';
import { MatStepper } from '@angular/material';
import { AdapterStartedDialog } from './component/adapter-started-dialog.component';
import { Logger } from '../../shared/logger/default-log.service';
import { GenericAdapterSetDescription } from '../model/connect/GenericAdapterSetDescription';
import { GenericAdapterStreamDescription } from '../model/connect/GenericAdapterStreamDescription';
import {EventSchema} from '../schema-editor/model/EventSchema';
import {SpecificAdapterSetDescription} from '../model/connect/SpecificAdapterSetDescription';
import {SpecificAdapterStreamDescription} from '../model/connect/SpecificAdapterStreamDescription';
import {TransformationRuleDescription} from '../model/connect/rules/TransformationRuleDescription';
import {TransformationRuleService} from '../transformation-rule.service';
import {ShepherdService} from '../../services/tour/shepherd.service';
import {EventSchemaComponent} from '../schema-editor/event-schema/event-schema.component';
import {ConnectService} from "../connect.service";
import {RemoveDuplicatesRuleDescription} from '../model/connect/rules/RemoveDuplicatesRuleDescription';
import {IconService} from './icon.service';
import {TimestampPipe} from '../filter/timestamp.pipe';
import {EventProperty} from '../schema-editor/model/EventProperty';

@Component({
    selector: 'sp-new-adapter',
    templateUrl: './new-adapter.component.html',
    styleUrls: ['./new-adapter.component.css'],
})
export class NewAdapterComponent implements OnInit {


    selectedUploadFile: File;
    fileName;

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;

        this.iconService.toBase64(this.selectedUploadFile)
            .then(
                data => {
                    this.adapter.icon = (<string> data);
                }
            );
    }


    @Input()
    adapter: AdapterDescription;

    @Output()
    removeSelectionEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

    @ViewChild('stepper') myStepper: MatStepper;

    allFormats: FormatDescription[] = [];

    protocolConfigurationValid: boolean;
    formatConfigurationValid: boolean;

    removeDuplicates: boolean = false;
    removeDuplicatesTime: number;

    saveInDataLake: boolean = false;
    dataLakeTimestampField: string;

    startAdapterFormGroup: FormGroup;

    eventSchema: EventSchema;
    oldEventSchema: EventSchema;

    timestampPropertiesInSchema: EventProperty[] = [];

    hasInput: Boolean[];

    // indicates whether user uses a template or not
    fromTemplate: Boolean = false;

    // deactivates all edit functions when user starts a template
    isEditable: Boolean = true;

    @ViewChild(EventSchemaComponent)
    private eventSchemaComponent: EventSchemaComponent;


    constructor(
        private logger: Logger,
        private restService: RestService,
        private transformationRuleService: TransformationRuleService,
        public dialog: MatDialog,
        private ShepherdService: ShepherdService,
        private connectService: ConnectService,
        private _formBuilder: FormBuilder,
        private iconService: IconService,
        private timestampPipe: TimestampPipe,
    ) {}

    ngOnInit() {

        this.formatConfigurationValid = false;

        if (this.adapter instanceof GenericAdapterSetDescription) {
            if ((<GenericAdapterSetDescription> this.adapter).format != undefined) {
                this.formatConfigurationValid = true;
            }
        }

        if (this.adapter instanceof GenericAdapterStreamDescription) {
            if ((<GenericAdapterStreamDescription> this.adapter).format != undefined) {
                this.formatConfigurationValid = true;
            }
        }

        this.restService.getFormats().subscribe(x => {
            this.allFormats = x.list;
        });

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
    }




    public triggerDialog(storeAsTemplate: boolean) {
        if (this.removeDuplicates) {
            this.adapter.rules.push(new RemoveDuplicatesRuleDescription(this.removeDuplicatesTime));
        }

        let dialogRef = this.dialog.open(AdapterStartedDialog, {
            width: '70%',
            data: { adapter: this.adapter,
                storeAsTemplate: storeAsTemplate,
                saveInDataLake: this.saveInDataLake,
                dataLakeTimestampField: this.dataLakeTimestampField
            },
            panelClass: 'sp-no-padding-dialog'
        });

        this.ShepherdService.trigger("button-startAdapter");

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

    validateProtocol(valid) {
        this.protocolConfigurationValid = valid;
    }

    validateFormat(valid) {
        this.formatConfigurationValid = valid;
    }

    removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    clickProtocolSettingsNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger("specific-settings-next-button");
        this.goForward(stepper);
    }

    clickSpecificSettingsNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger("specific-settings-next-button");
        this.guessEventSchema();
        this.goForward(stepper);
    }

    clickEventSchemaNextButtonButton(stepper: MatStepper) {
        if (this.isEditable) {
            this.setSchema();
        }

        // Auto selection of timestamp field for datalake
        this.timestampPropertiesInSchema = this.timestampPipe.transform(this.eventSchema.eventProperties, "");
        if (this.timestampPropertiesInSchema.length > 0) {
            this.dataLakeTimestampField = this.timestampPropertiesInSchema[0].runTimeName;
        }

        this.ShepherdService.trigger("event-schema-next-button");
        this.goForward(stepper);
    }

    clickFormatSelectionNextButton(stepper: MatStepper) {
        this.ShepherdService.trigger("format-selection-next-button");
        this.guessEventSchema();
        this.goForward(stepper);
    }

    guessEventSchema() {
        var eventSchema: EventSchema = this.getEventSchema(this.adapter);
        if (eventSchema.eventProperties.length == 0) {
            this.eventSchemaComponent.guessSchema();
        } else {
            this.oldEventSchema = eventSchema;
        }
    }

    getEventSchema(adapter: AdapterDescription): EventSchema {
        var eventSchema : EventSchema;

        if (adapter.constructor.name == 'GenericAdapterSetDescription') {
            eventSchema = (<GenericAdapterSetDescription> adapter).dataSet.eventSchema;
        } else if (adapter.constructor.name == 'SpecificAdapterSetDescription'){
            eventSchema = (<SpecificAdapterSetDescription> adapter).dataSet.eventSchema;
        } else if (adapter.constructor.name == 'GenericAdapterStreamDescription'){
            eventSchema = (<GenericAdapterStreamDescription> adapter).dataStream.eventSchema;
        } else if (adapter.constructor.name == 'SpecificAdapterStreamDescription'){
            eventSchema = (<SpecificAdapterStreamDescription> adapter).dataStream.eventSchema;
        } else {
            return new EventSchema();
        }

        if (eventSchema && eventSchema.eventProperties && eventSchema.eventProperties.length > 0) {
            return eventSchema;
        } else {
            return new EventSchema();
        }
    }

    public setSchema() {

        if (this.adapter.constructor.name == 'GenericAdapterSetDescription') {
            (<GenericAdapterSetDescription> this.adapter).dataSet.eventSchema = this.eventSchema;
        } else if (this.adapter.constructor.name == 'SpecificAdapterSetDescription'){
            (<SpecificAdapterSetDescription> this.adapter).dataSet.eventSchema = this.eventSchema;
        } else if (this.adapter.constructor.name == 'GenericAdapterStreamDescription'){
            (<GenericAdapterStreamDescription> this.adapter).dataStream.eventSchema = this.eventSchema;
        } else if (this.adapter.constructor.name == 'SpecificAdapterStreamDescription'){
            (<SpecificAdapterStreamDescription> this.adapter).dataStream.eventSchema = this.eventSchema;
        }


        this.transformationRuleService.setOldEventSchema(this.oldEventSchema);

        this.transformationRuleService.setNewEventSchema(this.eventSchema);
        const transformationRules: TransformationRuleDescription[] = this.transformationRuleService.getTransformationRuleDescriptions();
        this.adapter.rules = transformationRules;
    }

    formatSelected(selectedFormat) {
        if (
            this.adapter instanceof GenericAdapterSetDescription ||
            this.adapter instanceof GenericAdapterStreamDescription
        ) {
            this.adapter.format = selectedFormat;
            if (selectedFormat.config.length == 0) {
                this.validateFormat(true);
            }
        }
    }

    isGenericAdapter() {
        return this.connectService.isGenericDescription(this.adapter);
    }

    goBack(stepper: MatStepper) {
        console.log(this.myStepper.selectedIndex);
        this.myStepper.selectedIndex = this.myStepper.selectedIndex - 1;
        // this.myStepper.previous();
    }
    goForward(stepper: MatStepper) {
        this.myStepper.selectedIndex = this.myStepper.selectedIndex + 1;
        // this.myStepper.selectedIndex++;
        // this.myStepper.next();
    }
}
