import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  ProtocolDescription
} from '../../../core-model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';

@Component({
  selector: 'sp-generic-adapter-configuration',
  templateUrl: './generic-adapter-configuration.component.html',
  styleUrls: ['./generic-adapter-configuration.component.css']
})
export class GenericAdapterConfigurationComponent implements OnInit {

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
   * Go to next configuration step when this is complete
   */
  @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();


  genericAdapterSettingsFormValid: boolean;

  genericAdapterForm: FormGroup;

  protocolDescription: ProtocolDescription;

  constructor(
    private _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {

    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof GenericAdapterStreamDescription) {
      this.protocolDescription = this.adapterDescription.protocolDescription;
    }

    // initialize form for validation
    this.genericAdapterForm = this._formBuilder.group({});
    this.genericAdapterForm.statusChanges.subscribe((status) => {
      this.genericAdapterSettingsFormValid = this.genericAdapterForm.valid;
    });
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public clickNext() {
    this.clickNextEmitter.emit(this.stepper);
  }
}
