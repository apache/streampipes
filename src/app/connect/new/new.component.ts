import { Component, OnInit } from '@angular/core';
import { Format } from '../format-form/format';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RdfmapperService} from '../rdfmapper/rdfmapper.service';
import {RestService} from '../rest.service';
import {ProtocolDescription} from '../model/ProtocolDescription';
import {FormatDescription} from '../model/FormatDescription';
// import {AdapterDescription} from '../model/AdapterDescription';

// import { TsonLd } from '../tsonld';

@Component({
  selector: 'app-new',
  templateUrl: './new.component.html',
  styleUrls: ['./new.component.css']
})
export class NewComponent implements OnInit {

  isLinear = false;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;


  allProtocols: ProtocolDescription[];
  allFormats: FormatDescription[];

  selectedProtocol: ProtocolDescription = new ProtocolDescription('');
  selectedFormat: FormatDescription = new FormatDescription('');

  myFormat: Format;

  constructor(private restService: RestService, private _formBuilder: FormBuilder, private rdfMapperService:
    // constructor(private _formBuilder: FormBuilder, private rdfMapperService:
    RdfmapperService) { }

  ngOnInit() {

    this.rdfMapperService.testRdfMapper();

    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });

    this.restService.getProtocols().subscribe(x => {
        this.allProtocols = x.list;
    });

    this.restService.getFormats().subscribe(x => {
        this.allFormats = x.list;
    });
  }

}
