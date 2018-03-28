import { Component, OnInit } from '@angular/core';
import { Format } from '../format-form/format';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RdfmapperService} from '../rdfmapper/rdfmapper.service';
import {RestService} from '../rest.service';
import {ProtocolDescription} from '../model/ProtocolDescription';
import {FormatDescription} from '../model/FormatDescription';
import {AdapterDescription} from '../model/AdapterDescription';
import {DataSetDescription} from '../model/DataSetDescription';
import {Data} from '@angular/router';
import {EventSchema} from '../schema-editor/model/EventSchema';
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

    // selectedProtocol: ProtocolDescription = new ProtocolDescription('');
    // selectedFormat: FormatDescription = new FormatDescription('');

    public newAdapterDescription: AdapterDescription;
    constructor(private restService: RestService, private _formBuilder: FormBuilder) { }

    ngOnInit() {


        this.newAdapterDescription = this.getNewAdapterDescription();


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

    private getNewAdapterDescription(): AdapterDescription {
        const adapterDescription = new AdapterDescription('http://todo/ads1');
        adapterDescription.protocol = new ProtocolDescription('http://todo/p1');
        adapterDescription.format = new FormatDescription('http://todo/p2');
        const dataSet: DataSetDescription = new DataSetDescription('http://todo/ds2');
        dataSet.eventSchema = new EventSchema();
        adapterDescription.dataSet = dataSet;
        return adapterDescription;
    }

    public startAdapter() {
        console.log(this.newAdapterDescription);

        this.restService.addAdapter(this.newAdapterDescription);
        console.log('bbb');
    }

}
