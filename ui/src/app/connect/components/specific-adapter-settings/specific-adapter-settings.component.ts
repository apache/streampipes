import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AdapterDescriptionUnion } from '../../../core-model/gen/streampipes-model';
import { ConfigurationInfo } from '../../model/ConfigurationInfo';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'sp-specific-adapter-settings',
  templateUrl: './specific-adapter-settings.component.html',
  styleUrls: ['./specific-adapter-settings.component.css']
})
export class SpecificAdapterSettingsComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Returns whether the user input for the format configuration is valid or not
   */
  @Output() validateEmitter: EventEmitter<boolean> = new EventEmitter();


  completedStaticProperty: ConfigurationInfo;

  specificAdapterForm: FormGroup;

  constructor(
    private _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    // initialize form for validation
    this.specificAdapterForm = this._formBuilder.group({});
    this.specificAdapterForm.statusChanges.subscribe((status) => {
      this.validateEmitter.emit(this.specificAdapterForm.valid);
    });
  }

  triggerUpdate(configurationInfo: ConfigurationInfo) {
    this.completedStaticProperty = {...configurationInfo};
  }


}
