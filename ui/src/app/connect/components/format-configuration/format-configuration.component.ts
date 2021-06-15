import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  FormatDescription,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription
} from '../../../core-model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RestService } from '../../services/rest.service';
import { MatStepper } from '@angular/material/stepper';

@Component({
  selector: 'sp-format-configuration',
  templateUrl: './format-configuration.component.html',
  styleUrls: ['./format-configuration.component.css']
})
export class FormatConfigurationComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Mat stepper to trigger next confifuration step when this is completed
   */
  @Input() stepper: MatStepper;


  @Output() goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

  /**
   * Cancels the adapter configuration process
   */
  @Output() removeSelectionEmitter: EventEmitter<boolean> = new EventEmitter();

  /**
   * Go to next configuration step when this is complete
   */
  @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();

  formatConfigurationValid: boolean;

  /**
   * Local reference of the format description from the adapter description
   */
  selectedFormat: FormatDescription;

  /**
   * Contains all the available formats that a user can select from
   */
  allFormats: FormatDescription[] = [];

  /**
   * The form group to validate the configuration for the format
   */
  formatForm: FormGroup;

  constructor(
    private restService: RestService,
    private _formBuilder: FormBuilder) { }

  ngOnInit(): void {

    // fetch all available formats from backend
    this.restService.getFormats().subscribe(res => {
      this.allFormats = res.list;
    });

    // initialize form for validation
    this.formatForm = this._formBuilder.group({});
    this.formatForm.statusChanges.subscribe((status) => {
      this.formatConfigurationValid = this.formatForm.valid;
    });

    // ensure that adapter description is a generic adapter
    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof GenericAdapterStreamDescription) {
      this.selectedFormat = this.adapterDescription.formatDescription;
    }
    if (this.adapterDescription instanceof GenericAdapterSetDescription) {
      if ((this.adapterDescription as GenericAdapterSetDescription).formatDescription !== undefined) {
        this.formatConfigurationValid = this.formatForm.valid;
      }
    }
    if (this.adapterDescription instanceof GenericAdapterStreamDescription) {
      if ((this.adapterDescription as GenericAdapterStreamDescription).formatDescription !== undefined) {
        this.formatConfigurationValid = this.formatForm.valid;
      }
    }
  }

  formatSelected(selectedFormat) {
    if (
      this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof GenericAdapterStreamDescription
    ) {
      this.adapterDescription.formatDescription = selectedFormat;
      this.selectedFormat = selectedFormat;
      if (selectedFormat.config.length === 0) {
        this.formatConfigurationValid = this.formatForm.valid;
      }
    }
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public clickNext() {
    this.clickNextEmitter.emit(this.stepper);
  }

  public goBack() {
    this.goBackEmitter.emit(this.stepper);
  }
}
