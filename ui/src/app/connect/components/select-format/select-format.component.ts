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


  @Input() adapterDescription: AdapterDescriptionUnion;

  // What is this for?
  @Input() completedStaticProperty: ConfigurationInfo;

  @Output() public selectedFormatEmitter = new EventEmitter<FormatDescription>();
  @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();
  @Output() validateEmitter: EventEmitter<boolean> = new EventEmitter();

  // local reference of the format description from the adapter description
  selectedFormat: FormatDescription;

  allFormats: FormatDescription[] = [];

  /**
   * The form group to validate format configuration
   */
  formatForm: FormGroup;


  constructor(
    private restService: RestService,
    private _formBuilder: FormBuilder) { }

  ngOnInit(): void {

    this.restService.getFormats().subscribe(x => {
      this.allFormats = x.list;
    });

    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
                this.adapterDescription instanceof GenericAdapterStreamDescription) {
      this.selectedFormat = this.adapterDescription.formatDescription;
    }

    this.formatForm = this._formBuilder.group({
    });

    this.formatForm.statusChanges.subscribe((status) => {
      this.validateEmitter.emit(this.formatForm.valid);
    });

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

  triggerUpdate(configurationInfo: ConfigurationInfo) {
    this.updateEmitter.emit(configurationInfo);
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
