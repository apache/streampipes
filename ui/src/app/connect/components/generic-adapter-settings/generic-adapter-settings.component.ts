import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AdapterDescriptionUnion,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  ProtocolDescription
} from '../../../core-model/gen/streampipes-model';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'sp-generic-adapter-settings',
  templateUrl: './generic-adapter-settings.component.html',
  styleUrls: ['./generic-adapter-settings.component.css']
})
export class GenericAdapterSettingsComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Returns whether the user input for the format configuration is valid or not
   */
  @Output() validateEmitter: EventEmitter<boolean> = new EventEmitter();

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
      this.validateEmitter.emit(this.genericAdapterForm.valid);
    });
  }

}
