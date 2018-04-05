import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';
import {DragulaService} from 'ng2-dragula/ng2-dragula';
import {RestService} from '../../rest.service';
import {EventSchema} from '../model/EventSchema';
import {AdapterDescription} from '../../model/AdapterDescription';
import {ProtocolDescription} from '../../model/ProtocolDescription';
import {FormatDescription} from '../../model/FormatDescription';
import {EventProperty} from '../model/EventProperty';
import {GuessSchema} from '../model/GuessSchema';

@Component({
    selector: 'app-event-schema',
    templateUrl: './event-schema.component.html',
    styleUrls: ['./event-schema.component.css']
})

export class EventSchemaComponent implements OnInit {

    @Input() adapterDescription;
    @Output() adapterChange = new EventEmitter<AdapterDescription>();

    public eventSchema: EventSchema = null;

    public schemaGuess: GuessSchema = new GuessSchema();

    constructor(private restService: RestService,
                private dragulaService: DragulaService) {
    }


    public guessSchema(): void {
        this.restService.getGuessSchema(this.adapterDescription).subscribe(x => {
            this.adapterDescription.dataSet.eventSchema  = x.eventSchema;
            this.schemaGuess = x;
        });
    }

    ngOnInit() {
        if (this.adapterDescription.dataSet.eventSchema == null) {
            this.adapterDescription.dataSet.eventSchema = new EventSchema();
        }
    }



}
