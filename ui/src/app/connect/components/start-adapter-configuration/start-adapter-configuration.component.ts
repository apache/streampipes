import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  EventRateTransformationRuleDescription, GenericAdapterSetDescription,
  RemoveDuplicatesTransformationRuleDescription, SpecificAdapterSetDescription
} from '../../../core-model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { AdapterStartedDialog } from '../../dialog/adapter-started/adapter-started-dialog.component';
import { PanelType } from '../../../core-ui/dialog/base-dialog/base-dialog.model';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';

@Component({
  selector: 'sp-start-adapter-configuration',
  templateUrl: './start-adapter-configuration.component.html',
  styleUrls: ['./start-adapter-configuration.component.css']
})
export class StartAdapterConfigurationComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Mat stepper to trigger next confifuration step when this is completed
   */
  @Input() stepper: MatStepper;

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


  // preprocessing rule variables
  removeDuplicates = false;
  removeDuplicatesTime: number;

  eventRateReduction = false;
  eventRateTime: number;
  eventRateMode = 'none';

  saveInDataLake = false;
  dataLakeTimestampField: string;

  isSetAdapter = false;


  constructor(
    private dialogService: DialogService,
    private shepherdService: ShepherdService,
    private _formBuilder: FormBuilder) { }

  ngOnInit(): void {
    // initialize form for validation
    this.startAdapterForm = this._formBuilder.group({});
    this.startAdapterForm.statusChanges.subscribe((status) => {
      this.startAdapterSettingsFormValid = this.startAdapterForm.valid;
    });

    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
                                              this.adapterDescription instanceof SpecificAdapterSetDescription) {
      this.isSetAdapter = true;
    }

  }
  public triggerDialog(storeAsTemplate: boolean) {
    if (this.removeDuplicates) {
      const removeDuplicates: RemoveDuplicatesTransformationRuleDescription = new RemoveDuplicatesTransformationRuleDescription();
      removeDuplicates['@class'] = 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription';
      removeDuplicates.filterTimeWindow = (this.removeDuplicatesTime) as any;
      this.adapterDescription.rules.push(removeDuplicates);
    }
    if (this.eventRateReduction) {
      const eventRate: EventRateTransformationRuleDescription = new EventRateTransformationRuleDescription();
      eventRate['@class'] = 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription';
      eventRate.aggregationTimeWindow = this.eventRateMode as any;
      eventRate.aggregationType = this.eventRateMode;
      this.adapterDescription.rules.push(eventRate);
    }

    const dialogRef = this.dialogService.open(AdapterStartedDialog, {
      panelType: PanelType.STANDARD_PANEL,
      title: 'Adapter generation',
      width: '70vw',
      data: {
        'adapter': this.adapterDescription,
        'storeAsTemplate': storeAsTemplate,
        'saveInDataLake': this.saveInDataLake,
        'dataLakeTimestampField': this.dataLakeTimestampField
      }
    });

    this.shepherdService.trigger('button-startAdapter');

    dialogRef.afterClosed().subscribe(result => {
      this.adapterStartedEmitter.emit();
    });
  }

  public saveTemplate() {
    this.triggerDialog(true);
  }

  public startAdapter() {
    this.triggerDialog(false);
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public goBack() {
    this.goBackEmitter.emit(this.stepper);
  }
}
