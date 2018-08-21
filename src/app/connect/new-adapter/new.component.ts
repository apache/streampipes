import {Component, Output, OnInit, EventEmitter, Input} from '@angular/core';
import { ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import {RestService} from '../rest.service';
import {ProtocolDescription} from '../model/connect/grounding/ProtocolDescription';
import {FormatDescription} from '../model/connect/grounding/FormatDescription';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {DataSetDescription} from '../model/DataSetDescription';
import {EventSchema} from '../schema-editor/model/EventSchema';
import {AdapterDataSource} from '../all-adapters/adapter-data-source.service';
import {MatDialog} from '@angular/material';
import {AdapterStartedDialog} from './component/adapter-started-dialog.component';
import {Logger} from '../../shared/logger/default-log.service';
import {AdapterStreamDescription} from '../model/connect/AdapterStreamDescription';
import {AdapterSetDescription} from '../model/connect/AdapterSetDescription';
import {DataStreamDescription} from '../model/DataStreamDescription';


@Component({
    selector: 'sp-new-adapter',
    templateUrl: './new.component.html',
    styleUrls: ['./new.component.css']
})
export class NewComponent implements OnInit {
    @ViewChild('eschema') 
    eventSchemaComponent;
    @Output() newAdapterCreated = new EventEmitter();
    @Input() adapter: AdapterDescription;
    isLinear = false;
    setStreamFormGroup: FormGroup;
    firstFormGroup: FormGroup;
    secondFormGroup: FormGroup;

    setStreamSelected = false;
    selectedType: String;

    hasInputProtocol: Boolean;
    hasInputFormat: Boolean;
    hasInput: Boolean[];
    inputValue="";

    allProtocols: ProtocolDescription[];
    allFormats: FormatDescription[];
    

    // selectedProtocol: ProtocolDescription = new ProtocolDescription('');
    // selectedFormat: FormatDescription = new FormatDescription('');

    public newAdapterDescription: AdapterDescription;
    public selectedProtocol: ProtocolDescription;
    public selectedFormat: FormatDescription;

    constructor(private logger: Logger, private restService: RestService, private _formBuilder: FormBuilder, public dialog: MatDialog) {
        // console.log('constructor');
        // var protocol = new ProtocolDescription("id123456789");
        // protocol.description = "test"
        // this.allProtocols = [protocol];

    }

    ngOnInit() {

        this.newAdapterDescription = this.getNewAdapterDescription();

        this.setStreamFormGroup = this._formBuilder.group({
            condition: ["", Validators.required]
        })

        this.firstFormGroup = this._formBuilder.group({
            firstCtrl: ["", Validators.required]
        });
        this.secondFormGroup = this._formBuilder.group({
            secondCtrl: ['', Validators.required]
        });

        this.allProtocols = [];

        this.restService.getProtocols().subscribe(x => {
            this.allProtocols = x.list;
            this.allProtocols;
        });
        
        this.allFormats = [];
        this.restService.getFormats().subscribe(x => {
            this.allFormats = x.list;
            this.allFormats;
        });
    }

    private getNewAdapterDescription(): AdapterDescription {
        // TODO remove this is just that no errors occur on initila load of page
        const adapterDescription = new AdapterDescription('http://todo/ads1');
        //adapterDescription.protocol = new ProtocolDescription('http://todo/p1');
        //adapterDescription.format = new FormatDescription('http://todo/p2');

        const dataSet: DataSetDescription = new DataSetDescription('http://todo/ds2');
        dataSet.eventSchema = new EventSchema();
        adapterDescription['dataSet'] = dataSet;

        return adapterDescription;
    }
    protocolValueChanged(selectedProtocol) {
        this.selectedProtocol = selectedProtocol;
      }

    public protocolSelected() {
        var result: AdapterDescription;

        if (this.selectedProtocol.sourceType == "STREAM") {
            this.newAdapterDescription = new AdapterStreamDescription('http://todo/ads1');
            const dataStream: DataStreamDescription = new DataStreamDescription('http://todo/ds2');
            dataStream.eventSchema = new EventSchema();
            (this.newAdapterDescription as AdapterStreamDescription).dataStream = dataStream;
        } else if (this.selectedProtocol.sourceType == "SET") {
            this.newAdapterDescription = new AdapterSetDescription('http://todo/ads1');
            const dataSet: DataSetDescription = new DataSetDescription('http://todo/ds2');
            dataSet.eventSchema = new EventSchema();
            (this.newAdapterDescription as AdapterSetDescription).dataSet = dataSet;
        } else {
            this.logger.error('Currently just STREAM and SET are supported but the source type of the protocol was: ' +
                this.selectedProtocol.sourceType);
        }

        //this.newAdapterDescription.protocol = this.selectedProtocol;

    }

    formatSelected(selectedFormat) {
        //this.newAdapterDescription.format = selectedFormat;
      }

    public startAdapter() {
       let dialogRef = this.dialog.open(AdapterStartedDialog, {
            // width: '250px',
            // data: { name: this.name, animal: this.animal }
        });

        this.restService.addAdapter(this.newAdapterDescription);

        dialogRef.afterClosed().subscribe(result => {
           console.log('The dialog was closed');
            this.newAdapterCreated.emit()


        });
    }

    inputValueChangedProtocol(hasInput) {
        this.hasInputProtocol = hasInput;
    }

    inputValueChangedFormat(hasInput) {
        this.hasInputFormat = hasInput;
    }

    connectionSelected(selectedType) {
        this.setStreamSelected = true;
        this.selectedType = selectedType;
    }

}
