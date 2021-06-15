import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AdapterDescriptionUnion } from '../../../core-model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';

@Component({
  selector: 'sp-specific-adapter-configuration',
  templateUrl: './specific-adapter-configuration.component.html',
  styleUrls: ['./specific-adapter-configuration.component.css']
})
export class SpecificAdapterConfigurationComponent implements OnInit {

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

  specificAdapterSettingsFormValid: boolean;

  specificAdapterForm: FormGroup;

  constructor(
    private _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    // initialize form for validation
    this.specificAdapterForm = this._formBuilder.group({});
    this.specificAdapterForm.statusChanges.subscribe((status) => {
      this.specificAdapterSettingsFormValid = this.specificAdapterForm.valid;
    });
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public clickNext() {
    this.clickNextEmitter.emit(this.stepper);
  }
}
