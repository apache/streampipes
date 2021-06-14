import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  FormatDescription,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription
} from '../../../core-model/gen/streampipes-model';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RestService } from '../../services/rest.service';

@Component({
  selector: 'sp-select-format',
  templateUrl: './select-format.component.html',
  styleUrls: ['./select-format.component.css']
})
export class SelectFormatComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Returns whether the user input for the format configuration is valid or not
   */
  @Output() validateEmitter: EventEmitter<boolean> = new EventEmitter();

  /**
   * Local reference of the format description from the adapter description
   */
  selectedFormat: FormatDescription;

  /**
   * Contains all the available formats that a user can select from
   */
  allFormats: FormatDescription[] = [];

  /**
   * The form group to validate the configuration for the foramt
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
      this.validateEmitter.emit(this.formatForm.valid);
    });

    // ensure that adapter description is a generic adapter
    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof GenericAdapterStreamDescription) {
      this.selectedFormat = this.adapterDescription.formatDescription;
    }
    if (this.adapterDescription instanceof GenericAdapterSetDescription) {
      if ((this.adapterDescription as GenericAdapterSetDescription).formatDescription !== undefined) {
        this.validateEmitter.emit(true);
      }
    }
    if (this.adapterDescription instanceof GenericAdapterStreamDescription) {
      if ((this.adapterDescription as GenericAdapterStreamDescription).formatDescription !== undefined) {
        this.validateEmitter.emit(true);
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
        this.validateEmitter.emit(true);
      }
    }
  }

}
